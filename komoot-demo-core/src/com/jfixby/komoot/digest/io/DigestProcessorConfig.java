
package com.jfixby.komoot.digest.io;

public class DigestProcessorConfig {
	public String digestBotEmailAdress = "";
	public long digestSleepBeforeStartTime = -1;
	public long maxEventsPerDigest;
	public String sqsMailboxPrefix = "";
	public long digestSendPeriod;
}
