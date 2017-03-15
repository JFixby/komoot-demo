
package com.jfixby.komoot.digest;

import com.jfixby.komoot.separator.Notification;

public class NotificationHandler {

	private final Notification notification;
	private final String inputMessageReceiptHandle;

	public NotificationHandler (final Notification notification, final String inputMessageReceiptHandle) {
		this.notification = notification;
		this.inputMessageReceiptHandle = inputMessageReceiptHandle;
	}

	public Notification getNotification () {
		return this.notification;
	}

	public String getMessageReceiptID () {
		return this.inputMessageReceiptHandle;
	}

}
