package de.oppermann.bastian.spleef.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.oppermann.bastian.spleef.util.gui.GuiInventory;

/**
 * Prevents a player from opening an other inventory than its own.
 * 
 * @author Bastian Oppermann
 */
public class InventoryCloseListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent event) {
		GuiInventory.onInventoryClose(event.getPlayer().getUniqueId());
	}

}
