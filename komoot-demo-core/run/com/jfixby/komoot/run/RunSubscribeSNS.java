
package com.jfixby.komoot.run;

import java.io.IOException;

import com.jfixby.komoot.credentials.AWSCredentials;
import com.jfixby.scarabei.amazon.aws.RedAWS;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.json.JsonString;
import com.jfixby.scarabei.aws.api.AWS;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.aws.api.s3.S3;
import com.jfixby.scarabei.aws.api.sns.SNS;
import com.jfixby.scarabei.aws.api.sns.SNSClient;
import com.jfixby.scarabei.aws.api.sns.SNSClientSpecs;
import com.jfixby.scarabei.aws.api.sns.SNSTopicSunscribeRequest;
import com.jfixby.scarabei.aws.api.sns.SNSTopicSunscribeRequestParams;
import com.jfixby.scarabei.gson.GoogleGson;

public class RunSubscribeSNS {

	public static void main (final String[] args) throws IOException {
		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleGson());
		AWS.installComponent(new RedAWS());
		final S3 s3 = AWS.getS3();

		final SNS sns = AWS.getSNS();

		final SNSClientSpecs cientSpecs = sns.newClientSpecs();

		final File awsCredentialsFile = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("credentials")
			.child("aws-credentials.json");
		final JsonString credentialsJson = Json.newJsonString(awsCredentialsFile.readToString());
		final AWSCredentialsProvider awsKeys = Json.deserializeFromString(AWSCredentials.class, credentialsJson);

		cientSpecs.setAWSCredentialsProvider(awsKeys);

		final SNSClient snsClient = sns.newClient(cientSpecs);

		final SNSTopicSunscribeRequestParams params = snsClient.newSubscribeParams();
		params.setRegion(awsKeys.getRegionName());
		params.setTopicARN("arn:aws:sns:eu-west-1:963797398573:challenge-notifications");
// params.setTopicARN("arn:aws:sns:eu-central-1:642548582501:test");

		params.setProtocol("https");
		params.setEndPoint("https://sqs.eu-central-1.amazonaws.com/642548582501/komoot");

		final SNSTopicSunscribeRequest requrest = snsClient.subscribe(params);

	}

}
