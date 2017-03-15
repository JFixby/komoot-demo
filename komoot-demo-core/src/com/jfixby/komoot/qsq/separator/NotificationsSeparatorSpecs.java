
package com.jfixby.komoot.qsq.separator;

import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;

public class NotificationsSeparatorSpecs {
	private String inputQueue;
	private AWSCredentialsProvider awsKeys;
	private String digestBotEmailAdress;
	private boolean debugMode;
	private String debugEmailDomain;
	private String debugEmailSender;
	private String sesRegion;
	private String fromAdress;
	private String sQSMailboxPrefix;
	private long digestSleepBeforeStartTime;
	private long separatorStartProcessingDelay;
	private long maxMessagesPerDigest = 500;

	NotificationsSeparatorSpecs () {
	}

	public void setAWSCredentialsProvider (final AWSCredentialsProvider awsKeys) {
		this.awsKeys = awsKeys;
	}

	public void setInputQueueURL (final String inputQueue) {
		this.inputQueue = inputQueue;
	}

	public AWSCredentialsProvider getAWSCredentialsProvider () {
		return this.awsKeys;
	}

	public String getInputQueueURL () {
		return this.inputQueue;
	}

	public String getDigestBotEmailAdress () {
		return this.digestBotEmailAdress;
	}

	public void setDigestBotEmailAdress (final String digestBotEmailAdress) {
		this.digestBotEmailAdress = digestBotEmailAdress;
	}

	public void setDebugMode (final boolean debugMode) {
		this.debugMode = debugMode;
	}

	public void setDebugEmailDomain (final String debugEmailDomain) {
		this.debugEmailDomain = debugEmailDomain;
	}

	public void setDebugEmailSender (final String debugEmailSender) {
		this.debugEmailSender = debugEmailSender;
	}

	public boolean getDebugMode () {
		return this.debugMode;
	}

	public String getDebugEmailSender () {
		return this.debugEmailSender;
	}

	public String getDebugEmailDomain () {
		return this.debugEmailDomain;
	}

	public void setSESRegionName (final String sesRegion) {
		this.sesRegion = sesRegion;
	}

	public String getSESRegionName () {
		return this.sesRegion;
	}

	public String getSQSMailboxPrefix () {
		return this.sQSMailboxPrefix;
	}

	public void setSQSMailboxPrefix (final String sQSMailboxPrefix) {
		this.sQSMailboxPrefix = sQSMailboxPrefix;
	}

	public long getDigestSleepBeforeStartTime () {
		return this.digestSleepBeforeStartTime;
	}

	public void setDigestSleepBeforeStartTime (final long digestSleepBeforeStartTime) {
		this.digestSleepBeforeStartTime = digestSleepBeforeStartTime;
	}

	public void setSeparatorStartProcessingDelay (final long separatorStartProcessingDelay) {
		this.separatorStartProcessingDelay = separatorStartProcessingDelay;
	}

	public long getSeparatorStartProcessingDelay () {
		return this.separatorStartProcessingDelay;
	}

	public long getMaxEventsPerDigest () {
		return this.maxMessagesPerDigest;
	}

	public void setMaxEventsPerDigest (final long maxEventsPerDigest) {
		this.maxMessagesPerDigest = maxEventsPerDigest;
	}

}
