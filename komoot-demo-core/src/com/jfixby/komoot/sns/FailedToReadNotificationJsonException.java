
package com.jfixby.komoot.sns;

public class FailedToReadNotificationJsonException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -4998111802688769436L;

	public FailedToReadNotificationJsonException () {
		super();
	}

	public FailedToReadNotificationJsonException (final String message, final Throwable cause, final boolean enableSuppression,
		final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FailedToReadNotificationJsonException (final String message, final Throwable cause) {
		super(message, cause);
	}

	public FailedToReadNotificationJsonException (final String message) {
		super(message);
	}

	public FailedToReadNotificationJsonException (final Throwable cause) {
		super(cause);
	}

}
