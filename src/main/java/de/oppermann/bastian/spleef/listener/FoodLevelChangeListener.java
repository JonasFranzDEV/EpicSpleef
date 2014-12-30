package de.oppermann.bastian.spleef.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import de.oppermann.bastian.spleef.util.PlayerManager;

public class FoodLevelChangeListener implements Listener {
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (PlayerManager.getArena(event.getEntity().getUniqueId()) != null) {
			event.setCancelled(true);
		}
	}

}
