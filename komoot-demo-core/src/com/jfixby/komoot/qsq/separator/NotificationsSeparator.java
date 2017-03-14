
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
import com.jfixby.scarabei.aws.api.sqs.SQSCreateQueueParams;
import com.jfixby.scarabei.aws.api.sqs.SQSCreateQueueResult;
import com.jfixby.scarabei.aws.api.sqs.SQSDeleteMessageParams;
import com.jfixby.scarabei.aws.api.sqs.SQSDeleteMessageResult;
import com.jfixby.scarabei.aws.api.sqs.SQSMessage;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageParams;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageRequest;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageResult;
import com.jfixby.scarabei.aws.api.sqs.SQSSendMessageParams;

public class NotificationsSeparator {

	private NotificationsSeparator () {
	}

	AWSCredentialsProvider awsKeys;
	private StateSwitcher<SEPARATOR_STATE> state;
	private SQSClient client;
	private String inputQueueURL;

	private NotificationsSeparator (final NotificationsSeparatorSpecs specs) {
		this.awsKeys = specs.getAWSCredentialsProvider();
		this.state = JUtils.newStateSwitcher(SEPARATOR_STATE.NEW);
		final SQS sqs = AWS.getSQS();
		final SQSClienSpecs sqsspecs = sqs.newSQSClienSpecs();
		this.awsKeys = Debug.checkNull("AWSCredentialsProvider", specs.getAWSCredentialsProvider());
		sqsspecs.setAWSCredentialsProvider(this.awsKeys);
		this.inputQueueURL = Debug.checkNull("queueURL", specs.getInputQueueURL());

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

		while (true) {
			final SQS sqs = AWS.getSQS();
			final SQSReceiveMessageParams params = sqs.newReceiveMessageParams();
			params.setQueueURL(this.inputQueueURL);
			final SQSReceiveMessageRequest request = sqs.newReceiveMessageRequest(params);

			final SQSReceiveMessageResult result = this.client.receive(request);

			final Collection<SQSMessage> messages = result.listMessages();
			for (final SQSMessage m : messages) {
// m.print();
				try {
					this.processBody(m);
				} catch (final FailedToReadNotificationJsonException e) {
					L.e(e);
				}

			}
		}

	}

	private void processBody (final SQSMessage inputMessage) throws FailedToReadNotificationJsonException {
		final String inputMessageBody = inputMessage.getBody();
		final String inputMessageReceiptHandle = inputMessage.getReceiptHandle();

		final SrlzNotification srlzd_notification = readNotification(inputMessageBody);
		final SQS sqs = AWS.getSQS();
		final Notification notification = new Notification();
		notification.put("user_id", srlzd_notification.user_id);
		notification.put("timestamp", srlzd_notification.timestamp);
		notification.put("name", srlzd_notification.name);
		notification.put("email", srlzd_notification.email);
		notification.put("message", srlzd_notification.message);
// L.d("notification", Json.serializeToString(srlzd_notification));
		notification.print("messagess processed: " + this.messagessProcessed);
		this.messagessProcessed++;

		final String queueName = this.queueName(srlzd_notification.user_id);
		L.d("new queue", queueName + " " + queueName.length());
		final SQSCreateQueueParams createQueueRequestParams = sqs.newCreateQueueParams();
		createQueueRequestParams.setName(queueName);

		final SQSCreateQueueResult queueCreateResult = this.client.createQueue(createQueueRequestParams);
		final String queuURL = queueCreateResult.getQueueURL();
		L.d("creating queue", queuURL);

		final SQSSendMessageParams sendParams = sqs.newSendMessageParams();
		sendParams.setQueueURL(queuURL);
		final String messageText = inputMessageBody;
		sendParams.setBody(messageText);

		this.client.sendMessage(sendParams);

		final SQSDeleteMessageParams delete = sqs.newDeleteMessageParams();
		delete.setQueueURL(this.inputQueueURL);
		delete.setMessageReceiptHandle(inputMessageReceiptHandle);
		final SQSDeleteMessageResult deleteResult = this.client.deleteMessage(delete);

	}

	private String queueName (final String user_id) {
		Debug.checkNull("user_id", user_id);
		if (user_id == null) {
			return null;
		}
		final String result = "komoot-usr-mailbox-"
			+ user_id.replaceAll(":", "").replaceAll("@", "-").replaceAll("\\+", "-").replaceAll("\\.", "-");
		return result;
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
