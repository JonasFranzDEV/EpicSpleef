package de.oppermann.bastian.spleef.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import de.oppermann.bastian.spleef.api.ISpleefArena;

/**
 * Called when a player joins an arena.
 */
public class PlayerJoinArenaEvent extends PlayerArenaEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();
	
	private boolean cancelled;

	/**
	 * Class constructor.
	 * 
	 * @param who The player.
	 * @param arena The arena.
	 */
	public PlayerJoinArenaEvent(Player who, ISpleefArena arena) {
		super(who, arena);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bukkit.event.Cancellable#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bukkit.event.Cancellable#setCancelled(boolean)
	 */
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
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