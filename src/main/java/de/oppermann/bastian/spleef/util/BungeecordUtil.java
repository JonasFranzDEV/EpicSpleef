package de.oppermann.bastian.spleef.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.oppermann.bastian.spleef.SpleefMain;

public class BungeecordUtil {
	
	public static void init() {
		Bukkit.getMessenger().registerOutgoingPluginChannel(SpleefMain.getInstance(), "BungeeCord");
	}
	
	public static void teleportToServer(Player player, String server) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);
		player.sendPluginMessage(SpleefMain.getInstance(), "BungeeCord", out.toByteArray());
	}
	
}
