
package com.jfixby.komoot.demo;

import java.io.IOException;

import com.jfixby.komoot.demo.credentials.AWSCredentials;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.gson.GoogleGson;

public class CreateCredentials {

	public static void main (final String[] args) throws IOException {
		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleGson());

		final AWSCredentials credentials = new AWSCredentials();

		credentials.secretKeyID = "secretKeyID";
		credentials.regionName = "regionName";
		credentials.accessKeyID = "accessKeyID-" + Sys.SystemTime().currentTimeMillis();

		final File awsCredentialsFile = LocalFileSystem.ApplicationHome().parent().child("komoot-demo-config").child("credentials")
			.child("aws-credentials.json");
		final String data = Json.serializeToString(credentials).toString();
		awsCredentialsFile.writeString(data);

		L.d("writing", awsCredentialsFile);
		L.d(data);

	}

}
