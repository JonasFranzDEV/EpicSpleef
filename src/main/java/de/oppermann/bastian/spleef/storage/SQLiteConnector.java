package de.oppermann.bastian.spleef.storage;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;

import de.oppermann.bastian.spleef.util.Validator;

/**
 * Class to connect to a SQLite-database.
 */
public class SQLiteConnector extends SQLConnector {

	private final String DB_PATH;

	/**
	 * Class constructor.
	 * 
	 * @param plugin The plugin.
	 * @param filename The file name.
	 * @throws SQLException
	 */
	public SQLiteConnector(JavaPlugin plugin, String filename) throws SQLException {
		Validator.validateNotNull(plugin, "plugin");
		Validator.validateNotNull(filename, "filename");
		DB_PATH = plugin.getDataFolder().getPath() + "/" + filename;
		File f = new File(plugin.getDataFolder().getPath());
		f.mkdirs();
		connect();
		succeeded = connection != null;
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.lib.storage.SQLConnector#connect()
	 */
	@Override
	protected void connect() throws SQLException {
		if (connection != null)
			return;
		connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					closeConnection();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
