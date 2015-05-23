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
import org.bukkit.inventory.ItemStack;

import com.google.common.util.concurrent.FutureCallback;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaIsDisabledException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaIsFullException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaMisconfiguredException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaNotWaitingForPlayersException;
import de.oppermann.bastian.spleef.util.GameStatus;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.PlayerManager;
import de.oppermann.bastian.spleef.util.SpleefPlayer;
import de.oppermann.bastian.spleef.util.gui.GuiInventory;
import de.oppermann.bastian.spleef.util.gui.GuiItem;

public class PlayerInteractListener implements Listener {
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		SpleefArena arena = PlayerManager.getArena(event.getPlayer().getUniqueId());
		if (arena != null) {
			Player player = event.getPlayer();
			if (arena.getConfiguration().instanstBlockDestroy() && event.getAction() == Action.LEFT_CLICK_BLOCK && arena.isArenaBlock(event.getClickedBlock()) && arena.getStatus() == GameStatus.ACTIVE) {
				event.getClickedBlock().setType(Material.AIR);
				if (arena.getConfiguration().isEnableSnowballs()) {
					int snowballs = 0;
					for (ItemStack itemStack : player.getInventory().getContents()) {
						if (itemStack != null && itemStack.getType() == Material.SNOW_BALL) {
							snowballs += itemStack.getAmount();
						}
					}
					if (snowballs < arena.getConfiguration().getMaxSnowballs() || arena.getConfiguration().getMaxSnowballs() < 0) {
						player.getInventory().addItem(new ItemStack(Material.SNOW_BALL));						
					}
				}
				
				final SpleefArena ARENA = arena;
				SpleefPlayer.getPlayer(player.getUniqueId(), new FutureCallback<SpleefPlayer>() {
					@Override
					public void onFailure(Throwable e) {
						e.printStackTrace();
					}

					@Override
					public void onSuccess(SpleefPlayer stats) {
						stats.addDestroyedBlocks(ARENA.getName(), 1);
					}
				});
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
							player.sendMessage(ChatColor.RED + "Not implemented yet, sorry ...");
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
							Player player = event.getPlayer();
							try {
								arenas.join(player);
							} catch (SpleefArenaNotWaitingForPlayersException e) {
								player.sendMessage(Language.CAN_NOT_JOIN_GAME_ACTIVE.toString().replace("%arena%", arenas.getName()));
							} catch (SpleefArenaIsFullException e) {
								player.sendMessage(Language.CAN_NOT_JOIN_ARENA_FULL.toString().replace("%arena%", arenas.getName()));
							} catch (SpleefArenaIsDisabledException e) {
								player.sendMessage(Language.CAN_NOT_JOIN_ARENA_DISABLED.toString().replace("%arena%", arenas.getName()));
							} catch (SpleefArenaMisconfiguredException e) {
								player.sendMessage(Language.CAN_NOT_JOIN_ARENA_MISCONFIGURED.toString().replace("%arena%", arenas.getName()));
							}
						}
					}
				}
			}
		}
	}

}
