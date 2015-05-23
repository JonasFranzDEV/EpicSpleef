package de.oppermann.bastian.spleef.listener;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.PlayerManager;

public class PlayerPickupItemListener implements Listener {
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		SpleefArena arena = PlayerManager.getArena(player.getUniqueId());
		if (arena == null) {
			return;
		}
		Item item = event.getItem();
		if (item.hasMetadata("EpicSpleef:" + arena.getName() + ":Grenade")) {
			event.setCancelled(true);
			item.remove();
			player.getInventory().addItem(new ItemStack(Material.EGG));
		}
		if (item.hasMetadata("EpicSpleef:" + arena.getName() + ":Speed")) {
			event.setCancelled(true);
			item.remove();
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*10, 1));
		}
		if (item.hasMetadata("EpicSpleef:" + arena.getName() + ":Jump")) {
			event.setCancelled(true);
			item.remove();
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20*10, 1));
		}
		if (item.hasMetadata("EpicSpleef:" + arena.getName() + ":Invisibility")) {
			event.setCancelled(true);
			item.remove();
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*3, 1));
		}
	}

}
