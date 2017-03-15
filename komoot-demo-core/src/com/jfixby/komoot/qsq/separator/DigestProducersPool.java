
package com.jfixby.komoot.qsq.separator;

import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.math.IntegerMath;
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
	final boolean debug;
	final String debugEmailSender;
	final String debugEmailDomain;
	public long sleep_before_start;
	public long max_messages_per_digest;

	public DigestProducersPool (final SQSClient client, final String digestBotEmailAdress, final AWSCredentialsProvider awsKeys,
		final NotificationsSeparatorSpecs specs) {
		this.client = client;
		this.digestBotEmailAdress = Debug.checkNull("digestBotEmailAdress", digestBotEmailAdress);
		Debug.checkEmpty("digestBotEmailAdress", digestBotEmailAdress);

		final SES ses = AWS.getSES();
		final SESClientSpecs mailSpec = ses.newClientSpecs();
		mailSpec.setSESRegionName(specs.getSESRegionName());
		mailSpec.setAWSCredentialsProvider(awsKeys);
		this.mailClient = ses.newClient(mailSpec);
		this.debug = specs.getDebugMode();
		this.debugEmailSender = specs.getDebugEmailSender();
		this.debugEmailDomain = specs.getDebugEmailDomain();
		this.sleep_before_start = specs.getDigestSleepBeforeStartTime();
		this.max_messages_per_digest = IntegerMath.limit(10, specs.getMaxEventsPerDigest(), Integer.MAX_VALUE);

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

	public void deployPool (final String prefix) {
		L.d("Deploy digest pool");
		final Collection<String> allQueues = this.client.listAllSQSUrls().filter(val -> val.startsWith(prefix));
// allQueues.print("allQueues");

		for (final String q : allQueues) {
			this.ensureProcessing(q);
		}

// Sys.exit();
	}

	public static String debugWrapEmail (final String inputEmail, final String debugEmailSender, final String debugEmailDomain) {
		return debugEmailSender + "+" + inputEmail.replaceAll("@", "_").replaceAll("\\+", "__") + "@" + debugEmailDomain;
	}

	public String debugWrapEmail (final String inputEmail) {
		return debugWrapEmail(inputEmail, this.debugEmailSender, this.debugEmailDomain);
	}

}
