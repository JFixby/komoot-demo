
package com.jfixby.komoot.qsq.separator;

import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;

public class NotificationsSeparatorSpecs {
	private String inputQueue;
	private AWSCredentialsProvider awsKeys;

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

}
