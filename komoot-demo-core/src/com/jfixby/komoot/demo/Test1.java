
package com.jfixby.komoot.demo;

import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.json.JsonString;
import com.jfixby.scarabei.gson.GoogleGson;

public class Test1 {

	public static void main (final String[] args) {
		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleGson());
		final JsonString string = Json.serializeToString(new Object());
		Json.printPretty(string);
	}

}
