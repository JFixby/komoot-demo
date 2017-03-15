
package com.jfixby.komoot.qsq.separator;

import com.jfixby.komoot.io.SrlzNotification;
import com.jfixby.komoot.sns.FailedToReadNotificationJsonException;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Queue;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.log.L;
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
	private final Queue<Notification> localMessagesQueue;

	public DigestProducer (final String queuURL, final DigestProducersPool owner) {
		this.state = JUtils.newStateSwitcher(DIGEST_PRODUCER_STATE.NEW);
		this.owner = owner;
		this.inputQueueURL = Debug.checkNull(queuURL);
		this.localMessagesQueue = Collections.newQueue();

	}

	final Thread workerThread = new Thread() {
		@Override
		public void run () {
			DigestProducer.this.work();
		}

	};
	private long messagessProcessed = 0;
	private final boolean deleteInputMessages = false;

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

			final SQSReceiveMessageParams params = sqs.newReceiveMessageParams();
			params.setQueueURL(this.inputQueueURL);
			final SQSReceiveMessageRequest request = sqs.newReceiveMessageRequest(params);

			final SQSClient client = this.owner.getSQSClient();
			final SQSReceiveMessageResult result = client.receive(request);

			final Collection<SQSMessage> messages = result.listMessages();
			if (this.localMessagesQueue.size() >= this.owner.max_messages_per_digest) {
				this.processDigest();
			} else if (messages.size() == 0) {// no more input
				// notifications
				this.processDigest();
				L.d("Digest[" + this + "] is going to sleep.");
				Sys.sleep(TimeStream.HOUR);
				return;
			}
			for (final SQSMessage m : messages) {
				try {
					this.processBody(m);
// Sys.sleep(1500);
				} catch (final FailedToReadNotificationJsonException e) {
					L.e(e);
				}

			}
// Sys.sleep(150);
		}
	}

	private void processDigest () {
		if (this.localMessagesQueue.size() == 0) {// no digest
			return;
		}

		final DigestEmailSpecs specs = new DigestEmailSpecs();

		if (!this.owner.debug) {
			final Notification notification = this.localMessagesQueue.getLast();
			specs.setTo(notification.getEmail());
		} else {// debug mode
			final Notification notification = this.localMessagesQueue.getLast();
			specs.setTo(this.owner.debugWrapEmail(notification.getEmail()));
		}
		specs.setSubject("Komoot updates");
		specs.setFrom(this.owner.getDigestBotEmailAdress());

		final DigestEmail emailToUser = new DigestEmail(specs);
		while (this.localMessagesQueue.size() > 0) {
			final Notification notification = this.localMessagesQueue.dequeue();
			emailToUser.addNotification(notification);
		}
		emailToUser.seal();
		if (!this.owner.debug) {
			emailToUser.send(this.owner.getMailClient());
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

		final SQS sqs = AWS.getSQS();

		this.localMessagesQueue.enqueue(notification);
		L.d("received notification", notification);

		this.messagessProcessed++;

		if (this.deleteInputMessages) {
			final SQSDeleteMessageParams delete = sqs.newDeleteMessageParams();
			delete.setQueueURL(this.inputQueueURL);
			delete.setMessageReceiptHandle(inputMessageReceiptHandle);
			final SQSClient client = this.owner.getSQSClient();
			final SQSDeleteMessageResult deleteResult = client.deleteMessage(delete);
		}

	}

}
