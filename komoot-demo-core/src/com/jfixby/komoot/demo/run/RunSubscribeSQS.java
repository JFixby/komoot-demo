
package com.jfixby.komoot.demo.run;

import java.io.IOException;

import com.jfixby.komoot.demo.credentials.AWSCredentials;
import com.jfixby.komoot.io.SrlzMessageBody;
import com.jfixby.komoot.io.SrlzNotification;
import com.jfixby.komoot.separator.NotificationsSeparator;
import com.jfixby.komoot.sns.FailedToReadNotificationJsonException;
import com.jfixby.scarabei.amazon.aws.RedAWS;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.json.JsonString;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.aws.api.AWS;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.aws.api.sqs.SQS;
import com.jfixby.scarabei.aws.api.sqs.SQSClienSpecs;
import com.jfixby.scarabei.aws.api.sqs.SQSClient;
import com.jfixby.scarabei.aws.api.sqs.SQSMessage;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageParams;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageRequest;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageResult;
import com.jfixby.scarabei.gson.GoogleGson;

public class RunSubscribeSQS {

	public static void main (final String[] args) throws IOException {
		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleGson());
		AWS.installComponent(new RedAWS());

		final SQS sqs = AWS.getSQS();

		final File awsCredentialsFile = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("credentials")
			.child("aws-credentials.json");
		final JsonString credentialsJson = Json.newJsonString(awsCredentialsFile.readToString());
		final AWSCredentialsProvider awsKeys = Json.deserializeFromString(AWSCredentials.class, credentialsJson);

		final SQSClienSpecs specs = sqs.newSQSClienSpecs();
		specs.setAWSCredentialsProvider(awsKeys);

		final SQSClient client = sqs.newClient(specs);
		long i = 0;
		while (true) {
			final SQSReceiveMessageParams params = sqs.newReceiveMessageParams();
			params.setQueueURL("https://sqs.eu-central-1.amazonaws.com/642548582501/komoot");
			final SQSReceiveMessageRequest request = sqs.newReceiveMessageRequest(params);

			final SQSReceiveMessageResult result = client.receive(request);

			final Collection<SQSMessage> messages = result.listMessages();
			for (final SQSMessage m : messages) {
// m.print();
				final String body = m.getBody();
				try {
					final SrlzNotification srlzd_notification = readNotification(body);
					i++;
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
				notification.user_id = NotificationsSeparator.generateFakeUserID(notification.email);
			}
			return notification;
		} catch (final Throwable e) {
			throw new FailedToReadNotificationJsonException("failed to read json: " + msgBody.Message, e);
		}

	}

}
