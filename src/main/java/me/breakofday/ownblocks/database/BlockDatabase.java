package me.breakofday.ownblocks.database;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;

import me.breakofday.ownblocks.database.ConnectionWrapper.StatementWrapper;

public class BlockDatabase {

	private static final Logger logger = Logger.getLogger(BlockDatabase.class.getName());
	
	private final ConnectionWrapper connection;
	private final StatementWrapper INSERT;
	private final StatementWrapper SELECT_BY_LOCATION;
	private final StatementWrapper SELECT_COUNT_BY_OWNER;
	private final StatementWrapper DELETE_BY_LOCATION;

	public BlockDatabase(File file) throws IOException, SQLException {
		this.connection = new ConnectionWrapper(file);
		
		connection.prepareStatement("CREATE TABLE IF NOT EXISTS OWNBLOCKS ("
				+ "world TEXT,"
				+ "x INTEGER,"
				+ "y INTEGER,"
				+ "z INTEGER,"
				+ "owner TEXT)").execute();
		
		this.INSERT = connection.prepareStatement("INSERT INTO OWNBLOCKS (world, x, y, z, owner) VALUES (?, ?, ?, ?, ?)");
		this.SELECT_BY_LOCATION = connection.prepareStatement("SELECT * FROM OWNBLOCKS WHERE world = ? AND x = ? AND y = ? AND z = ?");
		this.SELECT_COUNT_BY_OWNER = connection.prepareStatement("SELECT COUNT(*) FROM OWNBLOCKS WHERE owner = ?");
		this.DELETE_BY_LOCATION = connection.prepareStatement("DELETE FROM OWNBLOCKS WHERE world = ? AND x = ? AND y = ? AND z = ?");
	}

	public int getBlocksCount(UUID uuid) {
		try {
			ResultSet result = SELECT_COUNT_BY_OWNER.executeQuery(uuid.toString());
			return result.getInt(1);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "데이터베이스에서 플레이어의 블록 개수를 가져오는 도중 오류가 발생하였습니다.");
		}
		return -1;
	}

	public UUID getOwner(Location loc) {
		try {
			String world = loc.getWorld().getName();
			int x = (int) loc.getX(), y = (int) loc.getY(), z = (int) loc.getZ();
			ResultSet result = SELECT_BY_LOCATION.executeQuery(world, x, y, z);
			if (result.next()) {
				return UUID.fromString(result.getString("owner"));
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "데이터베이스에서 블록의 주인을 가져오는 도중 오류가 발생하였습니다.");
		}
		return null;
	}

	public boolean hasOwner(Location loc) {
		try {
			String world = loc.getWorld().getName();
			int x = (int) loc.getX(), y = (int) loc.getY(), z = (int) loc.getZ();
			ResultSet result = SELECT_BY_LOCATION.executeQuery(world, x, y, z);
			return result.next();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "데이터베이스에서 블록의 주인을 가져오는 도중 오류가 발생하였습니다.");
		}
		return false;
	}

	public void setOwner(Location loc, UUID uuid) {
		try {
			if(!hasOwner(loc)) {
				String world = loc.getWorld().getName();
				int x = (int) loc.getX(), y = (int) loc.getY(), z = (int) loc.getZ();
				INSERT.execute(world, x, y, z, uuid.toString());
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "데이터베이스에 블록의 주인 정보를 넣는 도중 오류가 발생하였습니다.");
		}
	}

	public void deleteOwner(Location loc) {
		try {
			String world = loc.getWorld().getName();
			int x = (int) loc.getX(), y = (int) loc.getY(), z = (int) loc.getZ();
			DELETE_BY_LOCATION.execute(world, x, y, z);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "데이터베이스에서 블록의 주인을 제거하는 도중 오류가 발생하였습니다.");
		}
	}

}
