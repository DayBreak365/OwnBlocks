package me.breakofday.ownblocks.configuration;

public enum ConfigNodes {

	LANGUAGE("language", "english.properties");

	private final String path;
	private final Object defaultValue;
	private final String[] comments;

	private ConfigNodes(String path, Object defaultValue, String... comments) {
		this.path = path;
		this.defaultValue = defaultValue;
		this.comments = comments;
	}

	public String getPath() {
		return path;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public String[] getComments() {
		return comments;
	}

}
