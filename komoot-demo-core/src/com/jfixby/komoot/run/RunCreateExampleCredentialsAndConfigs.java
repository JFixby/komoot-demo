
package com.jfixby.komoot.run;

import java.io.IOException;

import com.jfixby.komoot.credentials.AWSCredentials;
import com.jfixby.komoot.digest.io.DigestProcessorConfig;
import com.jfixby.komoot.mail.io.MailServerConfig;
import com.jfixby.komoot.separator.io.SeperatorConfig;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.api.time.TimeStream;
import com.jfixby.scarabei.gson.GoogleGson;

public class RunCreateExampleCredentialsAndConfigs {

	public static void main (final String[] args) throws IOException {
		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleGson());
		{
			final AWSCredentials credentials = new AWSCredentials();

			credentials.secretKeyID = "secretKeyID";
			credentials.regionName = "regionName";
			credentials.accessKeyID = "accessKeyID-" + Sys.SystemTime().currentTimeMillis();

			final File file = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("credentials")
				.child("aws-credentials.example.json");
			final String data = Json.serializeToString(credentials).toString();
			file.writeString(data);

			L.d("writing", file);
			L.d(data);
		}

		{
			final MailServerConfig config = new MailServerConfig();

			config.sesRegion = "us-east-1";

			final File file = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("configs")
				.child("mail-server-config.example.json");
			final String data = Json.serializeToString(config).toString();
			file.writeString(data);

			L.d("writing", file);
			L.d(data);
		}

		{
			final DigestProcessorConfig config = new DigestProcessorConfig();

			config.digestBotEmailAdress = "example@example.com";
			config.digestSendPeriod = TimeStream.HOUR;
			config.digestSleepBeforeStartTime = 10;
			config.maxEventsPerDigest = 500;
			config.sqsMailboxPrefix = "kns-usr-nbox";

			final File file = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("configs")
				.child("digest-processor-config.example.json");
			final String data = Json.serializeToString(config).toString();
			file.writeString(data);

			L.d("writing", file);
			L.d(data);
		}

		{
			final SeperatorConfig config = new SeperatorConfig();

			config.inputQueueURL = "http://inout.queue";
			config.separatorStartProcessingDelay = 1000;
			config.sqsMailboxPrefix = "kns-usr-nbox";

			final File file = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("configs")
				.child("separator-config.example.json");
			final String data = Json.serializeToString(config).toString();
			file.writeString(data);

			L.d("writing", file);
			L.d(data);
		}

	}

}
