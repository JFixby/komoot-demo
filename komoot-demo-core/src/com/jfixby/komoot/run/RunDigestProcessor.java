
package com.jfixby.komoot.run;

import java.io.IOException;

import com.jfixby.komoot.credentials.AWSCredentials;
import com.jfixby.komoot.digest.DigestProcessor;
import com.jfixby.komoot.digest.DigestProcessorSpecs;
import com.jfixby.komoot.digest.io.DigestProcessorConfig;
import com.jfixby.komoot.mail.io.MailServerConfig;
import com.jfixby.scarabei.amazon.aws.RedAWS;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.json.JsonString;
import com.jfixby.scarabei.aws.api.AWS;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.gson.GoogleGson;

public class RunDigestProcessor {

	public static void main (final String[] args) throws IOException {

		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleGson());
		AWS.installComponent(new RedAWS());

		final File awsCredentialsFile = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("credentials")
			.child("aws-credentials.json");
		final JsonString credentialsJson = Json.newJsonString(awsCredentialsFile.readToString());
		final AWSCredentialsProvider awsKeys = Json.deserializeFromString(AWSCredentials.class, credentialsJson);

		final File digestConfigFile = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("configs")
			.child("digest-processor-config.json");
		final JsonString configJson = Json.newJsonString(digestConfigFile.readToString());
		final DigestProcessorConfig digestConfig = Json.deserializeFromString(DigestProcessorConfig.class, configJson);

		final File mailServerConfigFile = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("configs")
			.child("mail-server-config.json");
		final JsonString mailJson = Json.newJsonString(mailServerConfigFile.readToString());
		final MailServerConfig mailConfig = Json.deserializeFromString(MailServerConfig.class, mailJson);

		final DigestProcessorSpecs specs = DigestProcessor.newDigestProcessorSpecs();
		specs.setDigestBotEmailAdress(digestConfig.digestBotEmailAdress);

		specs.setAWSCredentialsProvider(awsKeys);
		specs.setDebugMode(digestConfig.debugMode);
		specs.setDebugEmailDomain("jfixby.com");
		specs.setDebugEmailSender("komoot");
		specs.setDigestSendPeriod(digestConfig.digestSendPeriod);
		specs.setMaxEventsPerDigest(digestConfig.maxEventsPerDigest);
		specs.setSESRegionName(mailConfig.sesRegion);
		specs.setSQSMailboxPrefix(digestConfig.sqsMailboxPrefix);
		specs.setDigestSleepBeforeStartTime(digestConfig.digestSleepBeforeStartTime);
		final DigestProcessor separator = DigestProcessor.newDigestProcessor(specs);
		separator.start();

	}

}
