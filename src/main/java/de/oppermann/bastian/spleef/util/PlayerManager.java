package de.oppermann.bastian.spleef.util;

import java.util.UUID;

import de.oppermann.bastian.spleef.arena.SpleefArena;

public class PlayerManager {

	/**
	 * Gets the arena of a player.
	 */
	public static SpleefArena getArena(UUID player) {
		for (SpleefArena arena : SpleefArena.getSpleefArenas()) {
			if (arena.getPlayers().contains(player)) {
				return arena;
			}
		}		
		return null;
	}
	
}
