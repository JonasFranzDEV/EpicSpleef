package de.oppermann.bastian.spleef.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.PlayerManager;

public class EntityDamageListener implements Listener {

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		SpleefArena arena = PlayerManager.getArena(event.getEntity().getUniqueId());
		if (arena != null) {
			if (arena.playersAreInLobby()) {
				event.setCancelled(true);
				return;
			}
			if (!arena.getConfiguration().hurtPlayers()) {
				event.setCancelled(true);
				return;
			}
			event.setDamage(0D);	// hurt but not damage 
		}
	}
	
}
