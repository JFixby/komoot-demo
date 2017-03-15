
package com.jfixby.komoot.demo.run;

import java.io.IOException;

import com.jfixby.komoot.demo.credentials.AWSCredentials;
import com.jfixby.komoot.separator.NotificationsSeparator;
import com.jfixby.komoot.separator.NotificationsSeparatorSpecs;
import com.jfixby.komoot.separator.io.SeperatorConfig;
import com.jfixby.scarabei.amazon.aws.RedAWS;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.json.JsonString;
import com.jfixby.scarabei.aws.api.AWS;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.gson.GoogleGson;

public class RunSeparator {

	public static void main (final String[] args) throws IOException {

		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleGson());
		AWS.installComponent(new RedAWS());

		final File awsCredentialsFile = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("credentials")
			.child("aws-credentials.json");
		final JsonString credentialsJson = Json.newJsonString(awsCredentialsFile.readToString());
		final AWSCredentialsProvider awsKeys = Json.deserializeFromString(AWSCredentials.class, credentialsJson);

		final File separatorConfigFile = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("configs")
			.child("separator-config.json");
		final JsonString configJson = Json.newJsonString(separatorConfigFile.readToString());
		final SeperatorConfig separatorConfig = Json.deserializeFromString(SeperatorConfig.class, configJson);

		final NotificationsSeparatorSpecs specs = NotificationsSeparator.newNotificationsSeparatorSpecs();
		specs.setInputQueueURL(separatorConfig.inputQueueURL);

		specs.setAWSCredentialsProvider(awsKeys);
		specs.setDebugMode(true);
		specs.setSQSMailboxPrefix(separatorConfig.sqsMailboxPrefix);
		specs.setSeparatorStartProcessingDelay(separatorConfig.separatorStartProcessingDelay);
		final NotificationsSeparator separator = NotificationsSeparator.newNotificationsSeparator(specs);
		separator.start();

	}

}
