
package com.jfixby.komoot.digest;

import com.jfixby.komoot.separator.DigestProducersPool;
import com.jfixby.komoot.separator.PROCESSOR_STATE;
import com.jfixby.komoot.separator.QueueRegistry;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.api.util.JUtils;
import com.jfixby.scarabei.api.util.StateSwitcher;
import com.jfixby.scarabei.aws.api.AWS;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.aws.api.sqs.SQS;
import com.jfixby.scarabei.aws.api.sqs.SQSClienSpecs;
import com.jfixby.scarabei.aws.api.sqs.SQSClient;

public class DigestProcessor {

	@SuppressWarnings("unused")
	private DigestProcessor () {
		Err.reportError("Invalid constructor");
		this.digestProducers = null;
		this.startDelay = -1;
	}

	AWSCredentialsProvider awsKeys;
	private StateSwitcher<PROCESSOR_STATE> state;
	private SQSClient client;
	private final DigestProducersPool digestProducers;
	private String notification_system_mailbox_prefix;
	private final long startDelay;
	final QueueRegistry queueRegistry = new QueueRegistry();

	DigestProcessor (final DigestProcessorSpecs specs) {
		this.awsKeys = specs.getAWSCredentialsProvider();
		this.state = JUtils.newStateSwitcher(PROCESSOR_STATE.NEW);
		final SQS sqs = AWS.getSQS();
		final SQSClienSpecs sqsspecs = sqs.newSQSClienSpecs();
		this.awsKeys = Debug.checkNull("AWSCredentialsProvider", specs.getAWSCredentialsProvider());
		sqsspecs.setAWSCredentialsProvider(this.awsKeys);

		this.client = sqs.newClient(sqsspecs);
		this.digestProducers = new DigestProducersPool(this.client, specs.getDigestBotEmailAdress(), this.awsKeys, specs);
		this.startDelay = specs.getDigestSleepBeforeStartTime();
		this.notification_system_mailbox_prefix = Debug.checkNull("SQSMailboxPrefix", specs.getSQSMailboxPrefix());
		Debug.checkEmpty("SQSMailboxPrefix", specs.getSQSMailboxPrefix());
		this.notification_system_mailbox_prefix = "nts-" + this.notification_system_mailbox_prefix;

	}

	public Thread mainThread = new Thread() {
		@Override
		public void run () {
			DigestProcessor.this.process();
		}

	};

	public static DigestProcessorSpecs newDigestProcessorSpecs () {
		return new DigestProcessorSpecs();
	}

	protected void process () {
		L.d("Starting digest server");
		{

			final Collection<String> currentQueues = this.listUserQueues();
			currentQueues.print("current queues");
		}
		while (true) {
			final Collection<String> currentQueues = this.listUserQueues();

			final List<String> notRegistered = currentQueues.filter(q -> !this.queueRegistry.contains(q));

			for (final String q : notRegistered) {
				this.register(q);
			}
			if (notRegistered.size() == 0) {
				Sys.sleep(1000);
			}
		}
	}

	private Collection<String> listUserQueues () {
		final Collection<String> all = this.client.listAllSQSUrls();
		final Collection<String> currentQueues = all.filter(val -> val.contains(this.notification_system_mailbox_prefix));
		return currentQueues;
	}

	private void register (final String q) {
		L.d("register queue", q);
		this.queueRegistry.add(q);
		final boolean isAlreadyProcessing = !this.digestProducers.ensureProcessing(q);
		if (isAlreadyProcessing) {
			L.e("queueRegistry is corupted", q);
		}

	}

	public static DigestProcessor newDigestProcessor (final DigestProcessorSpecs specs) {
		return new DigestProcessor(specs);
	}

	public void start () {
		this.state.expectState(PROCESSOR_STATE.NEW);
		this.state.switchState(PROCESSOR_STATE.RUNNING);
		this.mainThread.start();
	}

}
