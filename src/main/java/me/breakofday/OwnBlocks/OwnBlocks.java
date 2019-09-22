package me.breakofday.OwnBlocks;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.breakofday.OwnBlocks.ConnectionWrapper.StatementWrapper;

public class OwnBlocks extends JavaPlugin implements Listener {

	private static final Logger logger = Logger.getLogger(OwnBlocks.class.getName());
	private static final File mainDirectory = new File("plugins/OwnBlocks");
	static {
		if (!mainDirectory.exists()) {
			mainDirectory.mkdirs();
		}
	}

	private final Messager messager = new Messager();
	private final ConnectionWrapper connection;
	private final StatementWrapper INSERT;
	private final StatementWrapper SELECT_LOCATION;
	private final StatementWrapper DELETE_LOCATION;

	public OwnBlocks() {
		ConnectionWrapper connection = null;
		try {
			connection = new ConnectionWrapper(new File(mainDirectory.getPath() + "/database.db"));
		} catch (IOException | SQLException e) {
			logger.log(Level.SEVERE, "Database에 연결하는 도중 오류가 발생하였습니다.");
		}
		this.connection = connection;

		StatementWrapper INSERT = null;
		StatementWrapper SELECT_LOCATION = null;
		StatementWrapper DELETE_LOCATION = null;
		try {
			connection.prepareStatement(
					"CREATE TABLE IF NOT EXISTS OWNERS ("
							+ "world TEXT,"
							+ "x INTEGER,"
							+ "y INTEGER,"
							+ "z INTEGER,"
							+ "owner TEXT"
							+ ")").execute();
			INSERT = connection.prepareStatement(
					"INSERT INTO OWNERS (world, x, y, z, owner)"
							+ " VALUES "
							+ "(?, ?, ?, ?, ?)");
			SELECT_LOCATION = connection.prepareStatement(
					"SELECT * FROM OWNERS WHERE world = ? AND x = ? AND y = ? AND z = ?");
			DELETE_LOCATION = connection.prepareStatement(
					"DELETE FROM OWNERS WHERE world = ? AND x = ? AND y = ? AND z = ?");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Statement를 준비하는 도중 오류가 발생하였습니다.");
		}
		this.INSERT = INSERT;
		this.SELECT_LOCATION = SELECT_LOCATION;
		this.DELETE_LOCATION = DELETE_LOCATION;
	}

	@Override
	public void onEnable() {
		if(connection != null && INSERT != null && SELECT_LOCATION != null) {
			Bukkit.getPluginManager().registerEvents(this, this);
			messager.sendConsoleMessage("플러그인이 활성화되었습니다.");
		} else {
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

	@EventHandler
	private void onBlockPlace(BlockPlaceEvent e) {
		try {
			String playerUUID = e.getPlayer().getUniqueId().toString();
			Location l = e.getBlock().getLocation();
			String world = l.getWorld().getName();
			int x = (int) l.getX();
			int y = (int) l.getY();
			int z = (int) l.getZ();
			if(!SELECT_LOCATION.executeQuery(world, x, y, z).next()) {
				INSERT.execute(world, x, y, z, playerUUID);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler
	private void onBlockBreak(BlockBreakEvent e) {
		try {
			Player p = e.getPlayer();
			String playerUUID = p.getUniqueId().toString();
			Location l = e.getBlock().getLocation();
			String world = l.getWorld().getName();
			int x = (int) l.getX();
			int y = (int) l.getY();
			int z = (int) l.getZ();
			ResultSet rs = SELECT_LOCATION.executeQuery(world, x, y, z);
			if(rs.next()) {
				String owner = rs.getString("owner");
				if(owner.equalsIgnoreCase(playerUUID)) {
					DELETE_LOCATION.execute(world, x, y, z);
				} else {
					e.setCancelled(true);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c저런! &7부술 수 없는 블록입니다."));
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		ConnectionWrapper.closeAll();
		messager.sendConsoleMessage("플러그인이 비활성화되었습니다.");
	}

}
