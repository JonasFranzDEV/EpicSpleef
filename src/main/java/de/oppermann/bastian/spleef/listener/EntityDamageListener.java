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
		if (arena != null) { // is the entity is a player who is in an arena
			if (arena.playersAreInLobby()) {
				event.setCancelled(true);
				return;
			}
			if (!arena.getConfiguration().hurtPlayers()) {
				event.setCancelled(true);
				return;
			}
			event.setDamage(0D);	// hurt but not damage 
		} else { // something that's not in an arena - maybe even a player :o
			if (event.getEntity().getPassenger() != null) {	// if the entity has a passenger
				arena = PlayerManager.getArena(event.getEntity().getPassenger().getUniqueId());
				if (arena != null) {	// if the passenger is in an arena
					event.setCancelled(true); // vehicles should be immortal, shouldn't they? :D
				}
			}
		}
	}
	
}
