
package com.jfixby.komoot.separator;

import java.util.Date;

import com.jfixby.scarabei.api.err.Err;

public class Notification {

	private NotificationTimestamp timeStamp;
	private String eventString;
	private String userName;
	private String email;

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.email == null) ? 0 : this.email.hashCode());
		result = prime * result + ((this.eventString == null) ? 0 : this.eventString.hashCode());
		result = prime * result + ((this.timeStamp == null) ? 0 : this.timeStamp.hashCode());
		result = prime * result + ((this.userName == null) ? 0 : this.userName.hashCode());
		return result;
	}

	@Override
	public boolean equals (final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Notification other = (Notification)obj;
		if (this.email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!this.email.equals(other.email)) {
			return false;
		}
		if (this.eventString == null) {
			if (other.eventString != null) {
				return false;
			}
		} else if (!this.eventString.equals(other.eventString)) {
			return false;
		}
		if (this.timeStamp == null) {
			if (other.timeStamp != null) {
				return false;
			}
		} else if (!this.timeStamp.slightlyEquals(other.timeStamp)) {
			return false;
		}
		if (this.userName == null) {
			if (other.userName != null) {
				return false;
			}
		} else if (!this.userName.equals(other.userName)) {
			return false;
		}
		return true;
	}

	public NotificationTimestamp getTimeStamp () {
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
		this.timeStamp = new NotificationTimestamp(parseAwsDate(awsTimeStamp));
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
