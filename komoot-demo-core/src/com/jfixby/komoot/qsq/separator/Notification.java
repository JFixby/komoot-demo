
package com.jfixby.komoot.qsq.separator;

import java.util.Date;

import com.jfixby.scarabei.api.err.Err;

public class Notification {

	private long timeStamp;
	private String eventString;
	private String userName;
	private String email;

	public long getTimeStamp () {
		return this.timeStamp;
	}

	public static long parseAwsDate (final String awsTimeStamp) {
		try {
			final Date date = javax.xml.bind.DatatypeConverter.parseDateTime(awsTimeStamp).getTime();
			return date.getTime();
		} catch (final Throwable e) {
			Err.reportError(e);
		}
		return -1;
	}

	public void setTimeStamp (final String awsTimeStamp) {
		this.timeStamp = parseAwsDate(awsTimeStamp);
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

	@Override
	public String toString () {
		return "Notification [timeStamp=" + this.timeStamp + ", eventString=" + this.eventString + ", userName=" + this.userName
			+ ", email=" + this.email + "]";
	}

}
