package de.oppermann.bastian.spleef.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import de.oppermann.bastian.spleef.util.PlayerManager;
import de.oppermann.bastian.spleef.util.gui.GuiInventory;

/**
 * Prevents a player from opening an other inventory than its own.
 * 
 * @author Bastian Oppermann
 */
public class InventoryOpenListener implements Listener {
	
	private static boolean allow = false;
	
	@EventHandler(ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (PlayerManager.getArena(event.getPlayer().getUniqueId()) != null) {
			if (allow) {
				return;
			}
			if (event.getPlayer().getInventory() != event.getInventory()) {
				event.setCancelled(true);
				GuiInventory.onInventoryClose(event.getPlayer().getUniqueId());
			}
		}
	}
	
	/**
	 * Adds an inventory that should not be listened to.
	 */
	public static void allowOpening(boolean allow) {
		InventoryOpenListener.allow = allow;
	}

}
