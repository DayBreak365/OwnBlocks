package me.breakofday.ownblocks;

import java.io.IOException;
import java.util.Properties;

public class Language {

	private final Properties language = new Properties();

	Language(String fileName) throws IOException {
		language.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
	}

	public String getString(String key) {
		return language.getProperty(key, "");
	}

}
