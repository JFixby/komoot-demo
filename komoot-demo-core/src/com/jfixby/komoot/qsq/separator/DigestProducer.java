
package com.jfixby.komoot.qsq.separator;

import com.jfixby.komoot.demo.Notification;
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
import com.jfixby.scarabei.aws.api.sqs.SQSMessage;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageParams;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageRequest;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageResult;

public class DigestProducer {

	private final StateSwitcher<DIGEST_PRODUCER_STATE> state;
	private final UserID uid;
	private final String inputQueueURL;
	private final DigestProducersPool owner;
	private final Queue<Notification> localMessagesQueue;

	public DigestProducer (final UserID uid, final String queuURL, final DigestProducersPool owner) {
		this.state = JUtils.newStateSwitcher(DIGEST_PRODUCER_STATE.NEW);
		this.uid = Debug.checkNull(uid);
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

	public void start () {
		this.state.expectState(DIGEST_PRODUCER_STATE.NEW);
		this.state.switchState(DIGEST_PRODUCER_STATE.WORKING);
	}

	private void work () {
		L.d("Digest producer is listening", this.inputQueueURL);
		final SQS sqs = AWS.getSQS();
		while (true) {

			final SQSReceiveMessageParams params = sqs.newReceiveMessageParams();
			params.setQueueURL(this.inputQueueURL);
			final SQSReceiveMessageRequest request = sqs.newReceiveMessageRequest(params);

			final SQSClient client = this.owner.getSQSClient();
			final SQSReceiveMessageResult result = client.receive(request);

			final Collection<SQSMessage> messages = result.listMessages();
			if (messages.size() == 0) {// no more input notifications
				this.processDigest();
				Sys.sleep(TimeStream.HOUR);
				return;
			}
			for (final SQSMessage m : messages) {
				try {
					this.processBody(m);
				} catch (final FailedToReadNotificationJsonException e) {
					L.e(e);
				}

			}
			Sys.sleep(150);
		}
	}

	private void processDigest () {
		if (this.localMessagesQueue.size() == 0) {// no digest
			return;
		}

		final DigestEmailSpecs specs = new DigestEmailSpecs();
		{
			final Notification notification = this.localMessagesQueue.getLast();
			specs.setTo(notification.getToEmailAdress());
		}

		specs.setFrom(this.owner.getDigestBotEmailAdress());
		final DigestEmail emailToUser = new DigestEmail(specs);
		while (this.localMessagesQueue.size() > 0) {
			final Notification notification = this.localMessagesQueue.dequeue();
			emailToUser.addNotification(notification);
		}
		emailToUser.seal();
		emailToUser.send();

	}

	private void processBody (final SQSMessage m) throws FailedToReadNotificationJsonException {
		final Notification nextNotification = null;
		this.localMessagesQueue.enqueue(nextNotification);
	}

}
