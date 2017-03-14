
package com.jfixby.komoot.qsq.separator;

import com.jfixby.scarabei.api.debug.Debug;

public class UserID {

	private final String user_id;

	@Override
	public String toString () {
		return "UserID[" + this.user_id + "]";
	}

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.user_id == null) ? 0 : this.user_id.hashCode());
		return result;
	}

	@Override
	public boolean equals (final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final UserID other = (UserID)obj;
		if (this.user_id == null) {
			if (other.user_id != null) {
				return false;
			}
		} else if (!this.user_id.equals(other.user_id)) {
			return false;
		}
		return true;
	}

	public UserID (final String user_id) {
		this.user_id = Debug.checkNull("user_id", user_id);
	}

}
