package de.oppermann.bastian.spleef.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.PlayerManager;

public class EntityDamageByEntityListener implements Listener {
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity().getPassenger() == null) {
			return;
		}
		SpleefArena arena = PlayerManager.getArena(event.getEntity().getPassenger().getUniqueId());
		if (arena != null) {
			event.setCancelled(true);
		}
	}

}
