
package com.jfixby.komoot.separator;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Set;

public class QueueRegistry {
	final Set<String> list = Collections.newSet();

	public void add (final String q) {
		this.list.add(q);
	}

	public boolean contains (final String q) {
		return this.list.contains(q);
	}

}
