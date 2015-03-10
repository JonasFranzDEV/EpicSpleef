package de.oppermann.bastian.spleef.util;

import java.util.UUID;

import de.oppermann.bastian.spleef.arena.SpleefArena;

public class PlayerManager {

	/**
	 * Gets the arena of a player. (player || spectator)
	 */
	public static SpleefArena getArena(UUID player) {
		for (SpleefArena arena : SpleefArena.getSpleefArenas()) {
			if (arena.getPlayers().contains(player)) {
				return arena;
			}
			if (arena.getSpectators().contains(player)) {
				return arena;
			}
		}		
		return null;
	}
	

	/**
	 * Gets the arena of a player. (only if he is a spectator)
	 */
	public static SpleefArena getSpectateArena(UUID player) {
		for (SpleefArena arena : SpleefArena.getSpleefArenas()) {
			if (arena.getSpectators().contains(player)) {
				return arena;
			}
		}		
		return null;
	}
	

	/**
	 * Gets the arena of a player. (only if he is a player)
	 */
	public static SpleefArena getPlayerArena(UUID player) {
		for (SpleefArena arena : SpleefArena.getSpleefArenas()) {
			if (arena.getPlayers().contains(player)) {
				return arena;
			}
		}		
		return null;
	}
	
}
