
package com.jfixby.komoot.qsq.separator;

import com.jfixby.komoot.sns.FailedToReadNotificationJsonException;
import com.jfixby.komoot.sns.Notification;
import com.jfixby.komoot.sns.io.SrlzMessageBody;
import com.jfixby.komoot.sns.io.SrlzNotification;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.md5.MD5;
import com.jfixby.scarabei.api.util.JUtils;
import com.jfixby.scarabei.api.util.StateSwitcher;
import com.jfixby.scarabei.aws.api.AWS;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.aws.api.sqs.SQS;
import com.jfixby.scarabei.aws.api.sqs.SQSClienSpecs;
import com.jfixby.scarabei.aws.api.sqs.SQSClient;
import com.jfixby.scarabei.aws.api.sqs.SQSMessage;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageRequest;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageRequestParams;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageRequestResult;

public class NotificationsSeparator {

	private NotificationsSeparator () {
	}

	AWSCredentialsProvider awsKeys;
	private StateSwitcher<SEPARATOR_STATE> state;
	private SQSClient client;
	private String queueURL;

	private NotificationsSeparator (final NotificationsSeparatorSpecs specs) {
		this.awsKeys = specs.getAWSCredentialsProvider();
		this.state = JUtils.newStateSwitcher(SEPARATOR_STATE.NEW);
		final SQS sqs = AWS.getSQS();
		final SQSClienSpecs sqsspecs = sqs.newSQSClienSpecs();
		this.awsKeys = Debug.checkNull("AWSCredentialsProvider", specs.getAWSCredentialsProvider());
		sqsspecs.setAWSCredentialsProvider(this.awsKeys);
		this.queueURL = Debug.checkNull("queueURL", specs.getInputQueueURL());

		this.client = sqs.newClient(sqsspecs);
	}

	public static NotificationsSeparatorSpecs newNotificationsSeparatorSpecs () {
		return new NotificationsSeparatorSpecs();
	}

	public static NotificationsSeparator newNotificationsSeparator (final NotificationsSeparatorSpecs specs) {
		return new NotificationsSeparator(specs);
	}

	public Thread mainThread = new Thread() {
		@Override
		public void run () {
			NotificationsSeparator.this.separate();
		}

	};
	long messagessProcessed = 0;

	private void separate () {

		final SQS sqs = AWS.getSQS();
		while (true) {
			final SQSReceiveMessageRequestParams params = sqs.newReceiveMessageRequestParams();
			params.setQueueURL(this.queueURL);
			final SQSReceiveMessageRequest request = sqs.newReceiveMessageRequest(params);

			final SQSReceiveMessageRequestResult result = this.client.receive(request);

			final Collection<SQSMessage> messages = result.listMessages();
			for (final SQSMessage m : messages) {
// m.print();
				final String body = m.getBody();

				try {
					final SrlzNotification srlzd_notification = readNotification(body);

					final Notification notification = new Notification();
					notification.put("user_id", srlzd_notification.user_id);

					notification.put("timestamp", srlzd_notification.timestamp);
					notification.put("name", srlzd_notification.name);
					notification.put("email", srlzd_notification.email);
					notification.put("message", srlzd_notification.message);
// L.d("notification", Json.serializeToString(srlzd_notification));
					notification.print("messagess processed: " + this.messagessProcessed);
					this.messagessProcessed++;

				} catch (final FailedToReadNotificationJsonException e) {
					L.e(e);
				}

			}
		}

	}

	private static SrlzNotification readNotification (final String body) throws FailedToReadNotificationJsonException {
		SrlzMessageBody msgBody;
		try {
			msgBody = Json.deserializeFromString(SrlzMessageBody.class, body);
		} catch (final Throwable e) {
			throw new FailedToReadNotificationJsonException("failed to read json: " + body, e);
		}

		try {
			final SrlzNotification notification = Json.deserializeFromString(SrlzNotification.class, msgBody.Message);
			if (notification.user_id == null || "".equals(notification.user_id)) {
				notification.user_id = generateFakeUserID(notification.email);
			}
			return notification;
		} catch (final Throwable e) {
			throw new FailedToReadNotificationJsonException("failed to read json: " + msgBody.Message, e);
		}

	}

	public static String generateFakeUserID (final String email) {
		return MD5.md5String(email).getHumanReadableMD5HashHexString().toUpperCase() + "-" + email;
	}

	public void start () {
		this.state.expectState(SEPARATOR_STATE.NEW);
		this.state.switchState(SEPARATOR_STATE.RUNNING);
		this.mainThread.start();
	}
}
