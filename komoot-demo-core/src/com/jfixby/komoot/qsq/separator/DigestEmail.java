
package com.jfixby.komoot.qsq.separator;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.log.L;

public class DigestEmail {

	private final String from;
	private final String to;
	private final String subject;
	final StringBuilder body = new StringBuilder();

	public DigestEmail (final DigestEmailSpecs specs) {
		this.from = specs.getFromEmailAdress();
		this.to = specs.getToEmailAdress();
		this.subject = specs.getSubject();

	}

	final List<Notification> list = Collections.newList();

	public void addNotification (final Notification notification) {
		Debug.checkNull("notification", notification);
		this.list.add(notification);
	}

	public void seal () {
		this.body.setLength(0);
		this.body.append("Hi, " + this.list.getLast().getUserName() + " your friends are active!");
		this.body.append("\n");
		for (final Notification n : this.list) {
			this.body.append(this.date(n) + "        " + n.getEventString());
			this.body.append("\n");
		}
	}

	private String date (final Notification n) {
		final long timestamp = n.getTimeStamp();
		final Date date = new Date(timestamp);
		final SimpleDateFormat format = new SimpleDateFormat("EEEE,HH:mm");
		return format.format(date);
	}

	public void send () {
		L.d("Sending e-mail:");
		L.d("      from", this.from);
		L.d("        to", this.to);
		L.d("   subject", this.subject);
		L.d("          ", this.body);
		L.d();
	}

}
