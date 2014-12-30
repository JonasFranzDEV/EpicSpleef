package de.oppermann.bastian.spleef.hooks;

import org.bukkit.Bukkit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class WorldEditHook {
	
	public static WorldEditPlugin getWorldEditPlugin() {
		WorldEditPlugin worldEditPlugin = null;
		worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		return worldEditPlugin;
	}

}
