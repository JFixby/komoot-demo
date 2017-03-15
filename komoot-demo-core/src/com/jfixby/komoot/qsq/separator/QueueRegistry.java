
package com.jfixby.komoot.qsq.separator;

import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Set;

public class QueueRegistry {
	final Set<String> list = Collections.newSet();

	public void addExisting (final Collection<String> allQueues) {
		this.list.addAll(allQueues);
	}

}
