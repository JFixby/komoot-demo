
package com.jfixby.komoot.digest;

public class DigestEmailSpecs {

	private String fromEmailAdress;
	private String toEmailAdress;
	private String subject;

	public void setFrom (final String fromEmailAdress) {
		this.fromEmailAdress = fromEmailAdress;
	}

	public void setTo (final String toEmailAdress) {
		this.toEmailAdress = toEmailAdress;
	}

	public void setSubject (final String subject) {
		this.subject = subject;
	}

	public String getFromEmailAdress () {
		return this.fromEmailAdress;
	}

	public String getToEmailAdress () {
		return this.toEmailAdress;
	}

	public String getSubject () {
		return this.subject;
	}

}
