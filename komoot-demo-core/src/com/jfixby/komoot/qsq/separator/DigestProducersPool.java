
package com.jfixby.komoot.qsq.separator;

import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.aws.api.AWS;
import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;
import com.jfixby.scarabei.aws.api.ses.SES;
import com.jfixby.scarabei.aws.api.ses.SESClient;
import com.jfixby.scarabei.aws.api.ses.SESClientSpecs;
import com.jfixby.scarabei.aws.api.sqs.SQSClient;

public class DigestProducersPool {
	final Map<String, DigestProducer> pool = Collections.newMap();
	private final SQSClient client;
	private final String digestBotEmailAdress;
	private final SESClient mailClient;

	public DigestProducersPool (final SQSClient client, final String digestBotEmailAdress, final AWSCredentialsProvider awsKeys) {
		this.client = client;
		this.digestBotEmailAdress = Debug.checkNull("digestBotEmailAdress", digestBotEmailAdress);
		Debug.checkEmpty("digestBotEmailAdress", digestBotEmailAdress);

		final SES ses = AWS.getSES();
		final SESClientSpecs mailSpec = ses.newClientSpecs();
		mailSpec.setAWSCredentialsProvider(awsKeys);
		this.mailClient = ses.newClient(mailSpec);
	}

	public SESClient getMailClient () {
		return this.mailClient;
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
