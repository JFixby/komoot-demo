
package com.jfixby.komoot.demo.run;

import java.io.IOException;

import com.jfixby.komoot.demo.credentials.AWSCredentials;
import com.jfixby.komoot.qsq.separator.NotificationsSeparator;
import com.jfixby.komoot.qsq.separator.NotificationsSeparatorSpecs;
import com.jfixby.komoot.qsq.separator.io.SeperatorConfig;
import com.jfixby.komoot.ses.io.MailServerConfig;
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

		final File mailServerConfigFile = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("configs")
			.child("mail-server-config.json");
		final JsonString mailJson = Json.newJsonString(mailServerConfigFile.readToString());
		final MailServerConfig mailConfig = Json.deserializeFromString(MailServerConfig.class, mailJson);

		final NotificationsSeparatorSpecs specs = NotificationsSeparator.newNotificationsSeparatorSpecs();
		specs.setInputQueueURL(separatorConfig.inputQueueURL);
		specs.setDigestBotEmailAdress(separatorConfig.digestBotEmailAdress);

		specs.setAWSCredentialsProvider(awsKeys);
		specs.setDebugMode(true);
		specs.setDebugEmailDomain("jfixby.com");
		specs.setDebugEmailSender("komoot");
		specs.setMaxEventsPerDigest(separatorConfig.maxEventsPerDigest);
		specs.setSESRegionName(mailConfig.sesRegion);
		specs.setSQSMailboxPrefix(separatorConfig.sqsMailboxPrefix);
		specs.setDigestSleepBeforeStartTime(separatorConfig.digestSleepBeforeStartTime);
		specs.setSeparatorStartProcessingDelay(separatorConfig.separatorStartProcessingDelay);
		final NotificationsSeparator separator = NotificationsSeparator.newNotificationsSeparator(specs);
		separator.start();

	}

}
