
package com.jfixby.komoot.demo;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Map;

public class Notification {

	final Map<String, String> values = Collections.newMap();
	private String email;

	public void print (final String tag) {
		this.values.print(tag);
	}

	public void put (final String tag, final String value) {
		this.values.put(tag, value);
	}

	public String getToEmailAdress () {
		return this.email;
	}

	public void setToEmailAdress (final String email) {
		this.email = email;
		this.values.put("email", email);
	}

}
