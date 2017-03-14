
package com.jfixby.komoot.qsq.separator;

import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;

public class NotificationsSeparatorSpecs {
	private String inputQueue;
	private AWSCredentialsProvider awsKeys;
	private String digestBotEmailAdress;

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

}
