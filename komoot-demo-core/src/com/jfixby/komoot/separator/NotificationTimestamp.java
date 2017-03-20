
package com.jfixby.komoot.separator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationTimestamp {

	private final long timestamp;

	public NotificationTimestamp (final long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean slightlyEquals (final NotificationTimestamp timeStamp) {
		if (!timeStamp.day().equals(this.day())) {
			return false;
		}
		if (!timeStamp.time().equals(this.time())) {
			return false;
		}
		return true;
	}

	public String day () {
		return day(this.timestamp);
	}

	public String time () {
		return time(this.timestamp);
	}

	public static String day (final long timestamp) {
		final Date date = new Date(timestamp);
		final SimpleDateFormat format = new SimpleDateFormat("EEEE");
		return format.format(date);
	}

	public static String time (final long timestamp) {
		final Date date = new Date(timestamp);
		final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(date);
	}

	public long getTime () {
		return this.timestamp;
	}

}
