
package com.jfixby.komoot.digest;

import com.jfixby.komoot.separator.Notification;
import com.jfixby.komoot.separator.NotificationTimestamp;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Set;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.aws.api.AWS;
import com.jfixby.scarabei.aws.api.ses.AmazonSimpleEmail;
import com.jfixby.scarabei.aws.api.ses.AmazonSimpleEmailSpecs;
import com.jfixby.scarabei.aws.api.ses.SES;
import com.jfixby.scarabei.aws.api.ses.SESClient;
import com.jfixby.scarabei.aws.api.ses.SendEmailResult;

public class DigestEmail {

	private final String from;
	private final Collection<String> to;
	private final Collection<String> bcc;
	private final String subject;
	final StringBuilder body = new StringBuilder();

	public DigestEmail (final DigestEmailSpecs specs) {
		this.from = specs.getFromEmailAdress();
		this.to = specs.getToEmailAdress();
		this.bcc = specs.getBccEmailAdress();
		this.subject = specs.getSubject();

	}

	final Set<Notification> list = Collections.newSet();

	public void addNotification (final Notification notification) {
		Debug.checkNull("notification", notification);
		this.list.add(notification);
	}

	public void seal () {
		this.body.setLength(0);
		this.body.append("Hi, " + this.list.getLast().getUserName() + " your friends are active!");
		this.body.append("\n");
		this.body.append("\n");

		final Set<Notification> sorted = Collections.newSet(this.list);
		sorted.sort( (a, b) -> Long.compare(a.getTimeStamp().getTime(), b.getTimeStamp().getTime()));
		for (final Notification n : sorted) {
			this.body.append(formatDate(n.getTimeStamp()) + " " + n.getEventString());
			this.body.append("\n");
		}

		L.d("Ready to send e-mail:");
		L.d("      from", this.from);
		L.d("        to", this.to);
		L.d("       bcc", this.bcc);
		L.d("   subject", this.subject);
		L.d(this.body);
		L.d();
	}

	public static String formatDate (final NotificationTimestamp t) {
		return padRight(padRight(t.day() + ",", 8) + " " + t.time(), 20);
	}

	public static String fixedLengthString (final String string, final int length) {
		return padRight(string, length);
	}

	public static String padRight (final String s, final int n) {
		return String.format("%1$-" + n + "s", s);
	}

	public static String padLeft (final String s, final int n) {
		return String.format("%1$" + n + "s", s);
	}

	public void send (final SESClient sesClient) {

		final SES ses = AWS.getSES();

		final AmazonSimpleEmailSpecs specs = ses.newEmailSpecs();
		specs.setSubject(this.subject);
		specs.setFrom(this.from);
		specs.addTo(this.to);
		specs.addBcc(this.bcc);
		specs.setBody(this.body.toString());

		final AmazonSimpleEmail email = ses.newEmail(specs);
		final SendEmailResult sendResult = sesClient.send(email);

	}

}
