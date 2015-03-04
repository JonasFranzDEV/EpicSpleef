package de.oppermann.bastian.spleef.util;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.UnknownDependencyException;

import de.oppermann.bastian.spleef.SpleefMain;

/**
 * Simple class to reload the plugin.
 */
public class Reloader implements Runnable {

	final File FILE;
	final String PLUGIN_RELOADED;
	final Player PLAYER;
	
	/**
	 * Class constructor
	 */
	public Reloader(File file, String pluginReloadedMessage, Player messageReciever) {
		FILE = file;
		PLUGIN_RELOADED = pluginReloadedMessage;
		PLAYER = messageReciever;
	}
	
	/**
	 * Reloads the plugin.
	 */
	public void reload() {
		Bukkit.getPluginManager().disablePlugin(SpleefMain.getInstance());
		new Thread(this).start();;
	}

	/**
	 * Don't call this on your own!
	 */
	@Override
	public void run() {
		try {
			Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().loadPlugin(FILE));
			PLAYER.sendMessage(PLUGIN_RELOADED);
		} catch (UnknownDependencyException e) {
			// should not happen (EpicSpleef does not have dependencies!)
			e.printStackTrace();
		} catch (InvalidPluginException e) {
			// should not happen
			e.printStackTrace();
		} catch (InvalidDescriptionException e) {
			// should not happen
			e.printStackTrace();
		}
	}
	
}
