package de.oppermann.bastian.spleef.util;

import org.bukkit.Bukkit;

public class PluginChecker {

	public static boolean worldeditIsLoaded() {
		return Bukkit.getPluginManager().getPlugin("WorldEdit") != null;
	}
	
	public static boolean vaultIsLoaded() {
		return Bukkit.getPluginManager().getPlugin("Vault") != null;
	}
	
}
