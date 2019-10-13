package me.breakofday.ownblocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class Language {

	private final Properties language = new Properties();

	Language(String fileName) throws IOException {
		language.load(new BufferedReader(new InputStreamReader(Language.class.getResourceAsStream("/" + fileName), "UTF-8")));
	}

	public String get(String key) {
		return language.getProperty(key, "");
	}

}
