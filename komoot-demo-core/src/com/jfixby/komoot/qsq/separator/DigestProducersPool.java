
package com.jfixby.komoot.qsq.separator;

import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.aws.api.sqs.SQSClient;

public class DigestProducersPool {
	final Map<String, DigestProducer> pool = Collections.newMap();
	private final SQSClient client;
	private final String digestBotEmailAdress;

	public DigestProducersPool (final SQSClient client, final String digestBotEmailAdress) {
		this.client = client;
		this.digestBotEmailAdress = Debug.checkNull("digestBotEmailAdress", digestBotEmailAdress);
		Debug.checkEmpty("digestBotEmailAdress", digestBotEmailAdress);
	}

	public void ensureProcessing (final String queuURL) {
		DigestProducer producer = this.pool.get(queuURL);
		if (producer != null) {
			return;
		}
		producer = new DigestProducer(queuURL, this);
		this.pool.put(queuURL, producer);
		producer.start();
	}

	public SQSClient getSQSClient () {
		return this.client;
	}

	public String getDigestBotEmailAdress () {
		return this.digestBotEmailAdress;
	}

	public void deployPool () {
		L.d("Deploy digest pool");
		final Collection<String> allQueues = this.client.listAllSQSUrls()
			.filter(val -> val.contains(NotificationsSeparator.MAILBOX_PREFIX));
		allQueues.print("allQueues");

		for (final String q : allQueues) {
			this.ensureProcessing(q);
		}
		Sys.sleep(5000);
// Sys.exit();
	}

}
