package de.oppermann.bastian.spleef.storage;

import java.sql.DriverManager;
import java.sql.SQLException;

import de.oppermann.bastian.spleef.util.Validator;

/**
 * Class to connect to a MySQL-database.
 */
public class MySQLConnector extends SQLConnector {

	private String host;
	private String port;
	private String database;
	private String user;
	private String pass;

	/**
	 * Class constructor.
	 * 
	 * @param host The server host.
	 * @param port The server port.
	 * @param database The name of the database.
	 * @param user The name of the user.
	 * @param password The password.
	 */
	public MySQLConnector(String host, String port, String database, String user, String password) throws SQLException {
		Validator.validateNotNull(host, "host");
		Validator.validateNotNull(database, "database");
		Validator.validateNotNull(user, "user");
		Validator.validateNotNull(password, "password");
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.pass = password;
		connect();
		succeeded = connection != null;
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.lib.storage.SQLConnector#connect()
	 */
	@Override
	protected void connect() throws SQLException{
		connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + user + "&password=" + pass);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					closeConnection();
				} catch (SQLException e) {
					// should not happen
					e.printStackTrace();
				}
			}
		});
	}

}
