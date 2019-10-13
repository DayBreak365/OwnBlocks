package me.breakofday.ownblocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class Language {

	private final Properties language = new Properties();

	Language(String fileName) throws IOException {
		language.load(
				new BufferedReader(new InputStreamReader(Language.class.getResourceAsStream("/" + fileName), "UTF-8")));
	}

	public String get(LanguageNode node) {
		return language.getProperty(node.key, "");
	}

	public static enum LanguageNode {

		PLUGIN_ENABLED("plugin.enabled"),
		PLUGIN_DISABLED("plugin.disabled"),

		BLOCK_PROHIBITED("block.prohibited");

		private final String key;

		private LanguageNode(String key) {
			this.key = key;
		}

	}

}
