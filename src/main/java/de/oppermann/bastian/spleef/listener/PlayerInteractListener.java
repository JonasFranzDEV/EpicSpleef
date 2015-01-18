package de.oppermann.bastian.spleef.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.GameStatus;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.PlayerManager;
import de.oppermann.bastian.spleef.util.gui.GuiInventory;
import de.oppermann.bastian.spleef.util.gui.GuiItem;

public class PlayerInteractListener implements Listener {
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		SpleefArena arena = PlayerManager.getArena(event.getPlayer().getUniqueId());
		if (arena != null) {
			
			if (event.getAction() == Action.LEFT_CLICK_BLOCK && arena.isArenaBlock(event.getClickedBlock()) && arena.getStatus() == GameStatus.ACTIVE) {
				//event.getClickedBlock().setType(Material.AIR);	// TODO option to enable instant remove
				//if (arena.getConfiguration().isEnableSnowballs()) {
					//event.getPlayer().getInventory().addItem(new ItemStack(Material.SNOW_BALL));
				//}
			}
			
			// TODO check status and don't cancel it if the game is running
			if (arena.getStatus() != GameStatus.ACTIVE) {
				event.setCancelled(true);				
			}
			if (event.hasItem() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
				if (arena.getStatus() == GameStatus.ACTIVE) {
					if (event.getItem().getType() == Material.DIAMOND_HOE) {	// TODO configurable item
						event.getPlayer().launchProjectile(Egg.class);	// launch epic egg
					}
				}
				GuiItem item = GuiItem.getByItemStack(event.getItem());
				if (item != null) {
					Player player = event.getPlayer();
					switch (item) {
						case LEAVE_ARENA:
							arena.removePlayer(player);
							break;
						case STATISTICS:
							InventoryOpenListener.allowOpening(true);
							player.openInventory(GuiInventory.STATISTICS.createInventory(player));
							InventoryOpenListener.allowOpening(false);
							break;
						case HIDE_PLAYERS:
							player.sendMessage(Language.PREFIX.toString() + ChatColor.RED + "You should better hide yourself... (not implementet yet)");
							break;
						case SHOP:
							InventoryOpenListener.allowOpening(true);
							player.openInventory(GuiInventory.SHOP.createInventory(player));
							InventoryOpenListener.allowOpening(false);
							break;
						default:
							break;
					}
				}
			}			
		} else {	// check if player interacts with sign
			if (event.hasBlock()) {
				if (event.getClickedBlock().getState() instanceof Sign) {
					for (SpleefArena arenas : SpleefArena.getSpleefArenas()) {
						if (arenas.isJoinSign(event.getClickedBlock())) {
							arenas.join(event.getPlayer());	// TODO check some stuff
						}
					}
				}
			}
		}
	}

}
