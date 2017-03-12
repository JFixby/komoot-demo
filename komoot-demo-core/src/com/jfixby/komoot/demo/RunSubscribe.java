
package com.jfixby.komoot.demo;

import com.jfixby.scarabei.amazon.aws.RedAWS;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.aws.api.AWS;
import com.jfixby.scarabei.aws.api.s3.S3;
import com.jfixby.scarabei.aws.api.sns.SNS;
import com.jfixby.scarabei.aws.api.sns.SNSClient;
import com.jfixby.scarabei.aws.api.sns.SNSClientSpecs;
import com.jfixby.scarabei.aws.api.sns.SNSTopicSunscribeRequest;
import com.jfixby.scarabei.aws.api.sns.SNSTopicSunscribeRequestParams;
import com.jfixby.scarabei.gson.GoogleGson;

public class RunSubscribe {

	public static void main (final String[] args) {
		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleGson());
		AWS.installComponent(new RedAWS());
		final S3 s3 = AWS.getS3();

		final SNS sns = AWS.getSNS();

		final SNSClientSpecs cientSpecs = sns.newSunscribeSpecs();
		final SNSClient snsClient = sns.newClient(cientSpecs);

		final SNSTopicSunscribeRequestParams params = snsClient.newSunscribeParams();
		final SNSTopicSunscribeRequest requrest = snsClient.sunscribe(params);

	}

}
