
package com.jfixby.komoot.run;

import java.io.IOException;

import com.jfixby.komoot.credentials.AWSCredentials;
import com.jfixby.komoot.digest.DigestProducersPool;
import com.jfixby.komoot.mail.io.MailServerConfig;
import com.jfixby.scarabei.amazon.aws.RedAWS;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.json.JsonString;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.aws.api.AWS;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.aws.api.ses.AmazonSimpleEmail;
import com.jfixby.scarabei.aws.api.ses.AmazonSimpleEmailSpecs;
import com.jfixby.scarabei.aws.api.ses.SES;
import com.jfixby.scarabei.aws.api.ses.SESClient;
import com.jfixby.scarabei.aws.api.ses.SESClientSpecs;
import com.jfixby.scarabei.aws.api.ses.SendEmailResult;
import com.jfixby.scarabei.gson.GoogleGson;

public class RunSendEmailTest {

	public static void main (final String[] args) throws IOException {
		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleGson());
		AWS.installComponent(new RedAWS());

		final File awsCredentialsFile = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("credentials")
			.child("aws-credentials.json");
		final JsonString credentialsJson = Json.newJsonString(awsCredentialsFile.readToString());
		final AWSCredentialsProvider awsKeys = Json.deserializeFromString(AWSCredentials.class, credentialsJson);

		final File mailServerConfigFile = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("configs")
			.child("mail-server-config.json");
		final JsonString mailJson = Json.newJsonString(mailServerConfigFile.readToString());
		final MailServerConfig mailConfig = Json.deserializeFromString(MailServerConfig.class, mailJson);

		final SES ses = AWS.getSES();

		final SESClientSpecs clSpec = ses.newClientSpecs();
		clSpec.setAWSCredentialsProvider(awsKeys);
		clSpec.setSESRegionName(mailConfig.sesRegion);
		final SESClient client = ses.newClient(clSpec);

// final String to = "hello@jfixby.com";
		final String to = DigestProducersPool.debugWrapEmail("komoot+Travis@jfixby.com", "komoot", "jfixby.com");
		final String from = "komoot@jfixby.com";
		final String subject = "test";
		final String body = "hi!";

// final String bcc = to.replaceAll("@", "++") + "@jfixby.com";
		L.d("Sending e-mail:");
		L.d("      from", from);
		L.d("        to", to);
		L.d("   subject", subject);
		L.d("          ", body);
		L.d();

		final AmazonSimpleEmailSpecs specs = ses.newEmailSpecs();
		specs.setSubject(subject);
		specs.setFrom(from);
		specs.addTo(to);
		specs.setBody(body.toString());

		final AmazonSimpleEmail email = ses.newEmail(specs);
		final SendEmailResult sendResult = client.send(email);

	}

}
