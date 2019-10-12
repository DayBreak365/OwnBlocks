package me.breakofday.ownblocks;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.breakofday.ownblocks.configuration.OwnBlocksConfig;
import me.breakofday.ownblocks.database.BlockDatabase;
import me.breakofday.ownblocks.database.ConnectionWrapper;

public class OwnBlocks extends JavaPlugin implements Listener {

	private static final Logger logger = Logger.getLogger(OwnBlocks.class.getName());
	private static final File mainDirectory = new File("plugins/OwnBlocks");
	static {
		if (!mainDirectory.exists()) {
			mainDirectory.mkdirs();
		}
	}

	private final Messager messager = new Messager();
	private final BlockDatabase database;
	private final BlockHandler blockHandler;
	private final Language language;
	
	public OwnBlocks() {
		BlockDatabase database = null;
		Language language = null;
		try {
			database = new BlockDatabase(new File(mainDirectory.getPath() + "/database.db"));
			OwnBlocksConfig.load();
			language = new Language(OwnBlocksConfig.getLanguage());
		} catch (SQLException | IOException | InvalidConfigurationException ex) {
			logger.log(Level.SEVERE, "An error occurred while loading the plugin: " + ex.getClass().getSimpleName());
		}
		this.database = database;
		this.blockHandler = new BlockHandler(database);
		this.language = language;
	}

	@Override
	public void onEnable() {
		if (database != null) {
			Bukkit.getPluginManager().registerEvents(blockHandler, this);
			messager.sendConsoleMessage(language.getString("test"));
			messager.sendConsoleMessage("플러그인이 활성화되었습니다.");
		} else {
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(blockHandler);
		ConnectionWrapper.closeAll();
		messager.sendConsoleMessage("플러그인이 비활성화되었습니다.");
	}

}
