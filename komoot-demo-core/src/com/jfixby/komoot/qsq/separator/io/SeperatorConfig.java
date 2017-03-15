
package com.jfixby.komoot.qsq.separator.io;

public class SeperatorConfig {

	public String inputQueueURL = "";
	public String digestBotEmailAdress = "";
	public String sqsMailboxPrefix = "";
	public long digestSleepBeforeStartTime = -1;
	public long separatorStartProcessingDelay = -1;
	public long maxEventsPerDigest;

}
