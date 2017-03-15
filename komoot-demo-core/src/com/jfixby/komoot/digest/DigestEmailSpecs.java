
package com.jfixby.komoot.digest;

import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;

public class DigestEmailSpecs {

	private String fromEmailAdress;
	final List<String> toEmailAdress = Collections.newList();
	final List<String> bccEmailAdress = Collections.newList();
	private String subject;

	public void setFrom (final String fromEmailAdress) {
		this.fromEmailAdress = fromEmailAdress;
	}

	public void addTo (final String toEmailAdress) {
		this.toEmailAdress.add(toEmailAdress);
	}

	public void setSubject (final String subject) {
		this.subject = subject;
	}

	public String getFromEmailAdress () {
		return this.fromEmailAdress;
	}

	public Collection<String> getToEmailAdress () {
		return this.toEmailAdress;
	}

	public Collection<String> getBccEmailAdress () {
		return this.bccEmailAdress;
	}

	public String getSubject () {
		return this.subject;
	}

	public void addBcc (final String debugWrapEmail) {
		this.bccEmailAdress.add(debugWrapEmail);
	}

}
