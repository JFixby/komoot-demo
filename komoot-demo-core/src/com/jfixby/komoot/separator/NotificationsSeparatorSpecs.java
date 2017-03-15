
package com.jfixby.komoot.separator;

import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;

public class NotificationsSeparatorSpecs {
	private String inputQueue;
	private AWSCredentialsProvider awsKeys;

	private boolean debugMode;

	private String sQSMailboxPrefix;

	private long separatorStartProcessingDelay;

	NotificationsSeparatorSpecs () {
	}

	public AWSCredentialsProvider getAWSCredentialsProvider () {
		return this.awsKeys;
	}

	public void setAWSCredentialsProvider (final AWSCredentialsProvider awsKeys) {
		this.awsKeys = awsKeys;
	}

	public void setInputQueueURL (final String inputQueue) {
		this.inputQueue = inputQueue;
	}

	public String getInputQueueURL () {
		return this.inputQueue;
	}

	public void setDebugMode (final boolean debugMode) {
		this.debugMode = debugMode;
	}

	public boolean getDebugMode () {
		return this.debugMode;
	}

	public String getSQSMailboxPrefix () {
		return this.sQSMailboxPrefix;
	}

	public void setSQSMailboxPrefix (final String sQSMailboxPrefix) {
		this.sQSMailboxPrefix = sQSMailboxPrefix;
	}

	public void setSeparatorStartProcessingDelay (final long separatorStartProcessingDelay) {
		this.separatorStartProcessingDelay = separatorStartProcessingDelay;
	}

	public long getSeparatorStartProcessingDelay () {
		return this.separatorStartProcessingDelay;
	}

}
