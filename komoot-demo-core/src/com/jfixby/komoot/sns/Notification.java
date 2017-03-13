
package com.jfixby.komoot.sns;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Map;

public class Notification {

	final Map<String, String> values = Collections.newMap();

	public void print (final String tag) {
// L.d(this.toString());
		this.values.print(tag);
	}

	public void put (final String tag, final String value) {
		this.values.put(tag, value);
	}

}
