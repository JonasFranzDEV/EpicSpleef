package de.oppermann.bastian.spleef.util;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Strange class, but necessary cause bukkit sucks. (no EntityDismountEvent, only a VehicleExitEvent...)
 * <p>
 * (Threadsafe class)
 */
public class PlayerDismountCheckTask implements Runnable {

	private static final HashMap<UUID, Entity> PLAYERS = new HashMap<>();
	
	@Override
	public void run() {
		synchronized (PLAYERS) {
			for (UUID playerUUID : PLAYERS.keySet()) {
				Player player = Bukkit.getPlayer(playerUUID);
				if (player != null) {
					if (player.getVehicle() == null) {
						PLAYERS.get(playerUUID).setPassenger(player);
					}
				}
			}
		}
	}
	
	public static void addDisallowedPlayer(Player player) {
		synchronized (PLAYERS) {
			if (player.getVehicle() == null) {
				return;
			}
			PLAYERS.put(player.getUniqueId(), player.getVehicle());
		}
	}
	
	public static void removePlayer(Player player) {
		synchronized (PLAYERS) {
			PLAYERS.remove(player.getUniqueId());
		}
	}

}
