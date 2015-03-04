package de.oppermann.bastian.spleef.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.oppermann.bastian.spleef.util.Validator;

/**
 * Class to create other YAML-files than the <code>config.yml</code>.
 */
public class ConfigAccessor {

	private final String FILE_NAME;
	private final JavaPlugin PLUGIN;
	private final File DATA_FOLDER;

	private File configFile;
	private FileConfiguration fileConfiguration;

	/**
	 * Class constructor.
	 * 
	 * @param plugin The plugin.
	 * @param fileName The name of the file.
	 * @param dataFolder The data folder.
	 */
	public ConfigAccessor(JavaPlugin plugin, String fileName, File dataFolder) {
		Validator.validateNotNull(plugin, "plugin");
		Validator.validateNotNull(fileName, "fileName");
		
		this.PLUGIN = plugin;
		this.FILE_NAME = fileName;
		this.DATA_FOLDER = dataFolder;
		reloadConfig();
	}

	/**
	 * Reloads the config.
	 * 
	 * @see JavaPlugin#reloadConfig()
	 */
	public void reloadConfig() {
		if (configFile == null) {
			File dataFolder = DATA_FOLDER;
			if (dataFolder == null) {
				dataFolder = PLUGIN.getDataFolder();
			}
			if (dataFolder == null) {
				throw new IllegalStateException();
			}
			configFile = new File(dataFolder, FILE_NAME);
		}
		fileConfiguration = YamlConfiguration.loadConfiguration(configFile);

		// Look for defaults in the jar
		InputStream defConfigStream = PLUGIN.getResource(FILE_NAME);
		if (defConfigStream != null) {
			@SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			fileConfiguration.setDefaults(defConfig);
		}
	}

	/**
	 * Gets the {@link FileConfiguration}.
	 * 
	 * @return The FileConfiguration.
	 * @see JavaPlugin#getConfig()
	 */
	public FileConfiguration getConfig() {
		if (fileConfiguration == null) {
			this.reloadConfig();
		}
		return fileConfiguration;
	}

	/**
	 * Saves the config.
	 * 
	 * @see JavaPlugin#saveConfig()
	 */
	public void saveConfig() {
		if (fileConfiguration == null || configFile == null) {
			return;
		} else {
			try {
				getConfig().save(configFile);
			} catch (IOException ex) {
				PLUGIN.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
			}
		}
	}

	/**
	 * Saves the default config.
	 * 
	 * @see JavaPlugin#saveDefaultConfig()
	 */
	public void saveDefaultConfig() {
		if (!configFile.exists()) {
			this.PLUGIN.saveResource(FILE_NAME, false);
		}
	}
	
	/**
	 * Gets the File.
	 */
	public File getFile() {
		return configFile;
	}

}