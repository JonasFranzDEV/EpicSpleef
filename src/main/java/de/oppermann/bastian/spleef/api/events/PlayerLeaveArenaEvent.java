package de.oppermann.bastian.spleef.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.oppermann.bastian.spleef.api.ISpleefArena;

/**
 * Called when a player leaves an arena.
 */
public class PlayerLeaveArenaEvent extends PlayerArenaEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	/**
	 * Class constructor.
	 * 
	 * @param who The player.
	 * @param arena The arena.
	 */
	public PlayerLeaveArenaEvent(Player who, ISpleefArena arena) {
		super(who, arena);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bukkit.event.Event#getHandlers()
	 */
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}	

}