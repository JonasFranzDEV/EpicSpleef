package de.oppermann.bastian.spleef.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.util.concurrent.FutureCallback;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.PlayerManager;
import de.oppermann.bastian.spleef.util.SpleefPlayer;

public class BlockBreakListener implements Listener {
	
	// priorities cause plugins like worldguard
	
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreakLow(BlockBreakEvent event) {
		Player player = event.getPlayer();
		SpleefArena arena = PlayerManager.getArena(player.getUniqueId());
		if (arena != null) {
			event.setCancelled(true);	// cancel event. Other plugins like worldguard won't listen to cancelled events
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreakHighest(BlockBreakEvent event) {
		Player player = event.getPlayer();
		SpleefArena arena = PlayerManager.getArena(player.getUniqueId());
		if (arena != null) {
			if (arena.isArenaBlock(event.getBlock())) {
				event.setCancelled(false);	// allow breaking again
				event.getBlock().setType(Material.AIR);	// no drop
				
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
		}
	}

}
