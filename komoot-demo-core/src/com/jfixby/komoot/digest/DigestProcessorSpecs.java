
package com.jfixby.komoot.digest;

import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;

public class DigestProcessorSpecs {

	private String digestBotEmailAdress;
	private AWSCredentialsProvider awsKeys;
	private boolean debugMode;
	private String debugEmailDomain;
	private String debugEmailSender;
	private String sesRegion;
	private String fromAdress;
	private long maxMessagesPerDigest = 500;
	private long digestSleepBeforeStartTime;
	private String sQSMailboxPrefix;

	public long getMaxEventsPerDigest () {
		return this.maxMessagesPerDigest;
	}

	public void setMaxEventsPerDigest (final long maxEventsPerDigest) {
		this.maxMessagesPerDigest = maxEventsPerDigest;
	}

	public String getSQSMailboxPrefix () {
		return this.sQSMailboxPrefix;
	}

	public long getDigestSleepBeforeStartTime () {
		return this.digestSleepBeforeStartTime;
	}

	public void setDigestSleepBeforeStartTime (final long digestSleepBeforeStartTime) {
		this.digestSleepBeforeStartTime = digestSleepBeforeStartTime;
	}

	public void setSQSMailboxPrefix (final String sQSMailboxPrefix) {
		this.sQSMailboxPrefix = sQSMailboxPrefix;
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

	public String getDigestBotEmailAdress () {
		return this.digestBotEmailAdress;
	}

	public void setDigestBotEmailAdress (final String digestBotEmailAdress) {
		this.digestBotEmailAdress = digestBotEmailAdress;
	}

	public AWSCredentialsProvider getAWSCredentialsProvider () {
		return this.awsKeys;
	}

	public void setAWSCredentialsProvider (final AWSCredentialsProvider awsKeys) {
		this.awsKeys = awsKeys;
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

}
