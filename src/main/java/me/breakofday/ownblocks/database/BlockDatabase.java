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

	public int getOwningBlockCount(UUID uuid) {
		try {
			ResultSet result = SELECT_COUNT_BY_OWNER.executeQuery(uuid.toString());
			return result.getInt(1);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "An error has occurred while getting the number of blocks that player own from the database.");
		}
		return -1;
	}

	public UUID getOwner(Location loc) {
		try {
			ResultSet result = SELECT_BY_LOCATION.executeQuery(loc.getWorld().getName(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
			if (result.next()) {
				return UUID.fromString(result.getString("owner"));
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "An error has occurred while getting the owner of the block from the database.");
		}
		return null;
	}

	public boolean hasOwner(Location loc) {
		try {
			ResultSet result = SELECT_BY_LOCATION.executeQuery(loc.getWorld().getName(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
			return result.next();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "An error has occurred while getting the owner of the block from the database.");
		}
		return false;
	}

	public void setOwner(Location loc, UUID uuid) {
		try {
			if(!hasOwner(loc)) {
				INSERT.execute(loc.getWorld().getName(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), uuid.toString());
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "An error has occurred while inserting the owner information of the block into the database.");
		}
	}

	public void deleteOwner(Location loc) {
		try {
			DELETE_BY_LOCATION.execute(loc.getWorld().getName(), (int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "An error has occurred while deleting the owner of the block from the database.");
		}
	}

}
