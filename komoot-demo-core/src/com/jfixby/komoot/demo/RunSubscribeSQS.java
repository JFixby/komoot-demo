
package com.jfixby.komoot.demo;

import java.io.IOException;

import com.jfixby.komoot.demo.credentials.AWSCredentials;
import com.jfixby.scarabei.amazon.aws.RedAWS;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.json.JsonString;
import com.jfixby.scarabei.aws.api.AWS;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.aws.api.sqs.SQS;
import com.jfixby.scarabei.aws.api.sqs.SQSClienSpecs;
import com.jfixby.scarabei.aws.api.sqs.SQSClient;
import com.jfixby.scarabei.aws.api.sqs.SQSMessage;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageRequest;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageRequestParams;
import com.jfixby.scarabei.aws.api.sqs.SQSReceiveMessageRequestResult;
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

		final SQSReceiveMessageRequestParams params = sqs.newReceiveMessageRequestParams();
		params.setQueueURL("https://sqs.eu-central-1.amazonaws.com/642548582501/komoot");
		final SQSReceiveMessageRequest request = sqs.newReceiveMessageRequest(params);

		final SQSReceiveMessageRequestResult result = client.receive(request);

		final Collection<SQSMessage> messages = result.listMessages();
		for (final SQSMessage m : messages) {
			m.print();
		}

	}

}
