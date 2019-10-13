package me.breakofday.OwnBlocks;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.breakofday.OwnBlocks.database.BlockDatabase;
import me.breakofday.OwnBlocks.database.ConnectionWrapper;

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
	
	public OwnBlocks() {
		BlockDatabase database = null;
		try {
			database = new BlockDatabase(new File(mainDirectory.getPath() + "/database.db"));
			
		} catch (SQLException | IOException ex) {
			logger.log(Level.SEVERE, "데이터베이스에 연결하는 도중 오류가 발생하였습니다.");
		}
		this.database = database;
		this.blockHandler = new BlockHandler(database);
	}

	@Override
	public void onEnable() {
		if (database != null) {
			Bukkit.getPluginManager().registerEvents(blockHandler, this);
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
