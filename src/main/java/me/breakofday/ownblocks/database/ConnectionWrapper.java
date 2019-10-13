package me.breakofday.ownblocks.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sqlite JDBC Simple Wrapper
 * @author Daybreak 새벽
 */
public class ConnectionWrapper {

	private static final Logger logger = Logger.getLogger(ConnectionWrapper.class.getName());
	private static final ArrayList<Connection> connections = new ArrayList<>();
	private static final ArrayList<PreparedStatement> statements = new ArrayList<>();

	public static void closeAll() {
		for (Connection connection : connections) {
			try {
				connection.close();
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "An error has occurred while closing the connection.");
			}
		}
		for (PreparedStatement statement : statements) {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "An error has occurred while closing the statement.");
			}
		}
	}

	private final Connection connection;

	public ConnectionWrapper(File file) throws IOException, SQLException {
		if (!file.exists()) {
			file.createNewFile();
		}
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
		connection.setAutoCommit(true);
		connections.add(connection);
	}

	public Connection getConnection() {
		return connection;
	}

	public StatementWrapper prepareStatement(String sql) throws SQLException {
		return new StatementWrapper(sql);
	}

	public class StatementWrapper {

		private final PreparedStatement statement;

		private StatementWrapper(String sql) throws SQLException {
			this.statement = connection.prepareStatement(sql);
			statements.add(statement);
		}

		public boolean execute(Object... objects) throws SQLException {
			for (int i = 1; i < objects.length + 1; i++) {
				Object parameterObj = objects[i - 1];
				if (parameterObj == null) {
					statement.setNull(i, java.sql.Types.OTHER);
				} else {
					if (parameterObj instanceof Byte) {
						statement.setInt(i, ((Byte) parameterObj).intValue());
					} else if (parameterObj instanceof String) {
						statement.setString(i, (String) parameterObj);
					} else if (parameterObj instanceof Short) {
						statement.setShort(i, ((Short) parameterObj).shortValue());
					} else if (parameterObj instanceof Integer) {
						statement.setInt(i, ((Integer) parameterObj).intValue());
					} else if (parameterObj instanceof Long) {
						statement.setLong(i, ((Long) parameterObj).longValue());
					} else if (parameterObj instanceof Float) {
						statement.setFloat(i, ((Float) parameterObj).floatValue());
					} else if (parameterObj instanceof Double) {
						statement.setDouble(i, ((Double) parameterObj).doubleValue());
					} else if (parameterObj instanceof byte[]) {
						statement.setBytes(i, (byte[]) parameterObj);
					} else if (parameterObj instanceof Boolean) {
						statement.setBoolean(i, ((Boolean) parameterObj).booleanValue());
					}
				}
			}
			final boolean bool = statement.execute();
			statement.clearParameters();
			return bool;
		}

		public ResultSet executeQuery(Object... objects) throws SQLException {
			for (int i = 1; i < objects.length + 1; i++) {
				Object parameterObj = objects[i - 1];
				if (parameterObj == null) {
					statement.setNull(i, java.sql.Types.OTHER);
				} else {
					if (parameterObj instanceof Byte) {
						statement.setInt(i, ((Byte) parameterObj).intValue());
					} else if (parameterObj instanceof String) {
						statement.setString(i, (String) parameterObj);
					} else if (parameterObj instanceof Short) {
						statement.setShort(i, ((Short) parameterObj).shortValue());
					} else if (parameterObj instanceof Integer) {
						statement.setInt(i, ((Integer) parameterObj).intValue());
					} else if (parameterObj instanceof Long) {
						statement.setLong(i, ((Long) parameterObj).longValue());
					} else if (parameterObj instanceof Float) {
						statement.setFloat(i, ((Float) parameterObj).floatValue());
					} else if (parameterObj instanceof Double) {
						statement.setDouble(i, ((Double) parameterObj).doubleValue());
					} else if (parameterObj instanceof byte[]) {
						statement.setBytes(i, (byte[]) parameterObj);
					} else if (parameterObj instanceof Boolean) {
						statement.setBoolean(i, ((Boolean) parameterObj).booleanValue());
					}
				}
			}
			final ResultSet rs = statement.executeQuery();
			statement.clearParameters();
			return rs;
		}

	}

}
