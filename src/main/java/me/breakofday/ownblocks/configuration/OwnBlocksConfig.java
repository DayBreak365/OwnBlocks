package me.breakofday.ownblocks.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;

import me.breakofday.ownblocks.utils.FileUtil;

public class OwnBlocksConfig {

	private OwnBlocksConfig() {}

	private static final Logger logger = Logger.getLogger(OwnBlocksConfig.class.getName());

	public static boolean isLoaded() {
		return file != null && config != null;
	}

	public static void load() throws IOException, InvalidConfigurationException {
		if (!isLoaded()) {
			file = FileUtil.createFile("config.yml");
			lastModified = file.lastModified();
			config = new CommentedConfiguration(file);
			update();
		}
	}

	private static void update() throws FileNotFoundException, IOException, InvalidConfigurationException {
		config.load();
		cache.clear();
		for (ConfigNodes node : ConfigNodes.values()) {
			Object value = config.get(node.getPath());
			if (value != null) {
				cache.put(node, value);
			} else {
				config.set(node.getPath(), node.getDefaultValue());
				cache.put(node, node.getDefaultValue());
			}
		}
		config.save();
	}

	private static File file;
	private static long lastModified;
	private static CommentedConfiguration config;
	private static EnumMap<ConfigNodes, Object> cache = new EnumMap<>(ConfigNodes.class);

	private static String getString(ConfigNodes node) {
		return get(node, String.class);
	}

	@SuppressWarnings("unchecked") // Private only method
	private static <T> T get(ConfigNodes configNode, Class<T> clazz) throws IllegalStateException {
		if (!isLoaded()) {
			logger.log(Level.SEVERE, "An attempt was made to access a configuration without loading it.");
			throw new IllegalStateException("The configuration has not been loaded yet.");
		}
		if (lastModified != file.lastModified()) {
			try {
				update();
			} catch (IOException | InvalidConfigurationException e) {
				logger.log(Level.SEVERE, "An error has occurred while reloading the configuration.");
			}
		}
		return (T) cache.get(configNode);
	}

	/* UNUSED
	@SuppressWarnings("unchecked") // Private only method
	private static <T> List<T> getList(ConfigNodes configNode, Class<T> clazz) throws IllegalStateException {
		List<?> list = (List<?>) get(configNode, List.class);
		List<T> newList = new ArrayList<>();
		for (Object object : list) {
			if (object != null && clazz.isAssignableFrom(object.getClass())) {
				newList.add((T) object);
			}
		}
		return newList;
	}
	*/

	// Config Wrappers

	public static String getLanguage() {
		return getString(ConfigNodes.LANGUAGE);
	}

}
