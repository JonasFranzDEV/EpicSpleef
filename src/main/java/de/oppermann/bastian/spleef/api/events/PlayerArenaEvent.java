package de.oppermann.bastian.spleef.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import de.oppermann.bastian.spleef.api.ISpleefArena;

/**
 * An abstract class. (life would be better if everything would be abstract ._.)
 */
public abstract class PlayerArenaEvent extends PlayerEvent {

	private ISpleefArena arena;
	
	/**
	 * Class constructor.
	 * 
	 * @param who The player.
	 * @param arena The arena.
	 */
	public PlayerArenaEvent(Player who, ISpleefArena arena) {
		super(who);
		this.arena = arena;
	}
	
	/**
	 * Gets the arena.
	 * 
	 * @return The arena.
	 */
	public ISpleefArena getArena() {
		return this.arena;
	}

}
