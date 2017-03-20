
package com.jfixby.komoot.digest;

import com.jfixby.komoot.io.SrlzNotification;
import com.jfixby.komoot.separator.Notification;
import com.jfixby.komoot.separator.NotificationsSeparator;
import com.jfixby.komoot.sns.FailedToReadNotificationJsonException;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.collections.Queue;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.math.FloatMath;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.api.time.TimeStream;
import com.jfixby.scarabei.api.util.JUtils;
import com.jfixby.scarabei.api.util.StateSwitcher;
import com.jfixby.scarabei.aws.api.AWS;
import com.jfixby.scarabei.aws.api.sqs.SQS;
import com.jfixby.scarabei.aws.api.sqs.SQSClient;
import com.jfixby.scarabei.aws.api.sqs.SQSDeleteMessageParams;
import com.jfixby.scarabei.aws.api.sqs.SQSDeleteMessageResult;
import com.jfixby.scarabei.aws.api.sqs.SQSMessage;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageParams;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageRequest;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageResult;

public class DigestProducer {

	@Override
	public String toString () {
		return "DigestProducer[" + this.inputQueueURL + "]";
	}

	private final StateSwitcher<DIGEST_PRODUCER_STATE> state;
	private final String inputQueueURL;
	private final DigestProducersPool owner;
	private final Queue<NotificationHandler> localMessagesQueue;

	public DigestProducer (final String queuURL, final DigestProducersPool owner) {
		this.state = JUtils.newStateSwitcher(DIGEST_PRODUCER_STATE.NEW);
		this.owner = owner;
		this.inputQueueURL = Debug.checkNull(queuURL);
		this.localMessagesQueue = Collections.newQueue();
		this.digestSendPeriod = owner.digestSendPeriod;

	}

	final Thread workerThread = new Thread() {
		@Override
		public void run () {
			DigestProducer.this.work();
		}

	};
	private long messagessProcessed = 0;
	private final boolean deleteInputMessages = true;
	private final long digestSendPeriod;
	private final long waitMorePeriod = 10000;

	public void start () {
		this.state.expectState(DIGEST_PRODUCER_STATE.NEW);
		this.state.switchState(DIGEST_PRODUCER_STATE.WORKING);
		this.workerThread.start();
	}

	private void work () {
		Sys.sleep(this.owner.sleep_before_start);
		L.d("Digest producer is listening", this.inputQueueURL);
		final SQS sqs = AWS.getSQS();
		while (true) {
			try {

				final SQSReceiveMessageParams params = sqs.newReceiveMessageParams();
				params.setQueueURL(this.inputQueueURL);
				final SQSReceiveMessageRequest request = sqs.newReceiveMessageRequest(params);

				final SQSClient client = this.owner.getSQSClient();
				final SQSReceiveMessageResult result = client.receive(request);

				final Collection<SQSMessage> messages = result.listMessages();
				if (this.localMessagesQueue.size() >= this.owner.max_messages_per_digest) {// input overflow
					this.tryToDropDigest();// drop a chunk
				} else if (messages.size() == 0) {// no more input

					final boolean done = this.tryToDropDigest();
					if (done) {// sleep till next drop
						L.d("Digest[" + this + "] is going to sleep for " + this.timeValueString(this.digestSendPeriod) + " minutes");
						Sys.sleep(this.digestSendPeriod);
					} else {// wait for messages
						Sys.sleep(this.waitMorePeriod);
					}
				} else {// more input available
					this.consumeMessages(messages);
				}
			} catch (final Throwable r) {
				L.e(r);
				Sys.sleep(this.waitMorePeriod);
			}
		}
	}

	private void consumeMessages (final Collection<SQSMessage> messages) {
		for (final SQSMessage m : messages) {
			try {
				this.processBody(m);
			} catch (final FailedToReadNotificationJsonException e) {
				L.e(e);
			}
		}
	}

	private String timeValueString (final long periodMS) {
		return "" + FloatMath.roundToDigit(periodMS * 1f / TimeStream.MINUTE, 2);

	}

	private boolean tryToDropDigest () {
		if (this.localMessagesQueue.size() == 0) {// no digest
			return false;
		}

		final DigestEmailSpecs specs = new DigestEmailSpecs();
		String user;
		if (!this.owner.debug) {
			final Notification notification = this.localMessagesQueue.getLast().getNotification();
			specs.addTo(notification.getEmail());
			specs.addBcc(this.owner.debugWrapEmail(notification.getEmail()));
			user = notification.getUserName();

		} else {// debug mode
			final Notification notification = this.localMessagesQueue.getLast().getNotification();
			user = notification.getUserName();
// specs.addTo(notification.getEmail());
			specs.addTo(this.owner.debugWrapEmail(notification.getEmail()));
		}
		specs.setSubject("Komoot updates for " + user);
		specs.setFrom(this.owner.getDigestBotEmailAdress());

		final DigestEmail emailToUser = new DigestEmail(specs);
		final List<String> processedMessages = Collections.newList();
		while (this.localMessagesQueue.size() > 0) {
			final NotificationHandler notification = this.localMessagesQueue.dequeue();
			emailToUser.addNotification(notification.getNotification());
			processedMessages.add(notification.getMessageReceiptID());
		}
		emailToUser.seal();
		{
			emailToUser.send(this.owner.getMailClient());
			if (this.deleteInputMessages) {
				this.deleteProcessedMessages(processedMessages);
			}
		}
		return true;
	}

	private void deleteProcessedMessages (final List<String> messagesToDelete) {
		final SQS sqs = AWS.getSQS();

		for (final String toDelete : messagesToDelete) {
			final SQSDeleteMessageParams delete = sqs.newDeleteMessageParams();
			delete.setQueueURL(this.inputQueueURL);
			delete.setMessageReceiptHandle(toDelete);
			final SQSClient client = this.owner.getSQSClient();
			final SQSDeleteMessageResult deleteResult = client.deleteMessage(delete);
		}
	}

	private void processBody (final SQSMessage inputMessage) throws FailedToReadNotificationJsonException {
// final Notification nextNotification = null;
// this.localMessagesQueue.enqueue(nextNotification);

		final String inputMessageBody = inputMessage.getBody();
		final String inputMessageReceiptHandle = inputMessage.getReceiptHandle();

		final SrlzNotification srlzd_notification = NotificationsSeparator.readNotification(inputMessageBody);
		final Notification notification = new Notification();
		notification.setEmail(srlzd_notification.email);
		notification.setEventString(srlzd_notification.message);
		notification.setTimeStamp(srlzd_notification.timestamp);
		notification.setUserName(srlzd_notification.name);

		final NotificationHandler notificationHandler = new NotificationHandler(notification, inputMessageReceiptHandle);
		this.localMessagesQueue.enqueue(notificationHandler);
		L.d("      received notification", notification);

		this.messagessProcessed++;

	}

}
