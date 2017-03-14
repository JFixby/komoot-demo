
package com.jfixby.komoot.demo.run;

import java.util.Date;

import com.jfixby.komoot.qsq.separator.DigestEmail;
import com.jfixby.komoot.qsq.separator.Notification;
import com.jfixby.scarabei.amazon.aws.RedAWS;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.aws.api.AWS;
import com.jfixby.scarabei.gson.GoogleGson;

public class ParseDateTest {

	public static void main (final String[] args) {
		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleGson());
		AWS.installComponent(new RedAWS());
		final String dateString = "2017-03-14T10:55:29";

		final long timestamp = Notification.parseAwsDate(dateString);
		L.d("timestamp", timestamp);
		L.d("    input", dateString);
		L.d("         ", new Date(timestamp));
		L.d("         ", DigestEmail.formatDate(timestamp));

	}

}
