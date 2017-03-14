
package com.jfixby.komoot.qsq.separator;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.aws.api.sqs.SQSClient;

public class DigestProducersPool {
	final Map<UserID, DigestProducer> pool = Collections.newMap();
	private final SQSClient client;
	private final String digestBotEmailAdress;

	public DigestProducersPool (final SQSClient client, final String digestBotEmailAdress) {
		this.client = client;
		this.digestBotEmailAdress = Debug.checkNull("digestBotEmailAdress", digestBotEmailAdress);
		Debug.checkEmpty("digestBotEmailAdress", digestBotEmailAdress);
	}

	public void ensureProcessing (final String user_id, final String queuURL) {
		final UserID uid = new UserID(user_id);
		DigestProducer producer = this.pool.get(uid);
		if (producer != null) {
			return;
		}

		producer = new DigestProducer(uid, queuURL, this);
		this.pool.put(uid, producer);
		producer.start();
	}

	public SQSClient getSQSClient () {
		return this.client;
	}

	public String getDigestBotEmailAdress () {
		return this.digestBotEmailAdress;
	}

}
