
package com.jfixby.komoot.qsq.separator;

import java.text.ParsePosition;
import java.util.Date;

import com.google.gson.internal.bind.util.ISO8601Utils;
import com.jfixby.scarabei.api.err.Err;

public class Notification {

	private long timeStamp;
	private String eventString;
	private String userName;
	private String email;

	public long getTimeStamp () {
		return this.timeStamp;
	}

	public void setTimeStamp (final String awsTimeStamp) {
		try {
			final Date date = ISO8601Utils.parse(awsTimeStamp, new ParsePosition(0));
			this.timeStamp = date.getTime();
		} catch (final Throwable e) {
			Err.reportError(e);
		}
	}

	public String getEventString () {
		return this.eventString;
	}

	public void setEventString (final String eventString) {
		this.eventString = eventString;
	}

	public String getUserName () {
		return this.userName;
	}

	public void setUserName (final String userName) {
		this.userName = userName;
	}

	public String getEmail () {
		return this.email;
	}

	public void setEmail (final String email) {
		this.email = email;
	}

}
