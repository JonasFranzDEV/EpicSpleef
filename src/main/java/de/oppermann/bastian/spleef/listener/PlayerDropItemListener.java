package de.oppermann.bastian.spleef.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import de.oppermann.bastian.spleef.util.PlayerManager;

/**
 * Prevents a player from dropping items.
 * 
 * @author Bastian Oppermann
 */
public class PlayerDropItemListener implements Listener {

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (PlayerManager.getArena(player.getUniqueId()) != null) {
			int slot = player.getInventory().getHeldItemSlot();
			if (player.getInventory().getItem(slot) != null && player.getInventory().getItem(slot).getType() != Material.AIR) {
				event.setCancelled(true);
			} else {
				ItemStack itemdropped = event.getItemDrop().getItemStack().clone();
                event.getItemDrop().remove();
                player.getInventory().setItem(slot, itemdropped);
                player.updateInventory();
			}
		}
	}
	
}
