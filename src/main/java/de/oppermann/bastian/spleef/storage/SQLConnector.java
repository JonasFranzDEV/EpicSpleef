package de.oppermann.bastian.spleef.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import de.oppermann.bastian.spleef.util.Validator;

/**
 * Class for SQL.
 * 
 * @author Bastian Oppermann
 */
public abstract class SQLConnector {

	protected Connection connection;
	protected boolean succeeded = false;
	protected String database;

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find JDBC-driver!");
			e.printStackTrace();
		}
	}

	/**
	 * Gets a {@link Statement}.
	 * <p>
	 * Beware of SQL injection! Use a PreparedStatement instead.
	 * 
	 * @return A Statement.
	 * @throws SQLException
	 * @see {@link Connection#createStatement()}
	 */
	public Statement getStatement() throws SQLException {
		return connection.createStatement();
	}

	/**
	 * Prepares a statement.
	 * 
	 * @param sql The sql-command.
	 * @return A prepared statement.
	 * @throws SQLException
	 * @see {@link Connection#prepareStatement(String)}
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		Validator.validateNotNull(sql, "sql");
		return connection.prepareStatement(sql);
	}

	/**
	 * Closes a connection.
	 * 
	 * @throws SQLException 
	 */
	public void closeConnection() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
	}
	
	/**
	 * Connects to the database.
	 * 
	 * @throws SQLException
	 */
	protected abstract void connect() throws SQLException;

}
