package de.oppermann.bastian.spleef.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.PlayerManager;
import de.oppermann.bastian.spleef.util.gui.GuiInventory;

/**
 * Prevents a player from clicking items (moving them around).
 * 
 * @author Bastian Oppermann
 */
public class InventoryClickListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (PlayerManager.getArena(event.getWhoClicked().getUniqueId()) != null) {
			event.setCancelled(true);
		}
		if (GuiInventory.getLastCreatedInventoryType(event.getWhoClicked().getUniqueId()) == GuiInventory.JOIN) {
			if (compare(event.getWhoClicked().getOpenInventory().getTopInventory(), GuiInventory.getLastCreatedInventory(event.getWhoClicked().getUniqueId()))) {
				event.setCancelled(true);
				if (compare(event.getClickedInventory(), GuiInventory.getLastCreatedInventory(event.getWhoClicked().getUniqueId()))) {
					SpleefArena arena = GuiInventory.getArena(event.getSlot());
					if (arena != null) {
						// TODO check some stuff
						arena.join((Player) event.getWhoClicked());
					}
				}
			}
		}
		if (GuiInventory.getLastCreatedInventoryType(event.getWhoClicked().getUniqueId()) == GuiInventory.STATISTICS) {
			if (compare(event.getWhoClicked().getOpenInventory().getTopInventory(), GuiInventory.getLastCreatedInventory(event.getWhoClicked().getUniqueId()))) {
				event.setCancelled(true);
			}
		}
	}
	
	private boolean compare(Inventory inv, Inventory inv2) {
		if (inv == null || inv2 == null) {
			return false;
		}
		if (!inv.getTitle().equals(inv2.getTitle())) {
			return false;
		}
		if (inv.getMaxStackSize() != inv2.getMaxStackSize()) {
			return false;
		}
		if (!inv.getViewers().equals(inv2.getViewers())) {
			return false;
		}
		return true;		
	}
	
}