package de.oppermann.bastian.spleef.util;

import org.bukkit.Bukkit;

public class PluginChecker {

	public static boolean worldeditIsLoaded() {
		return Bukkit.getPluginManager().getPlugin("WorldEdit") != null;
	}
	
}
