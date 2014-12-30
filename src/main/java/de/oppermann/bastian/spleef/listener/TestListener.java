package de.oppermann.bastian.spleef.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class TestListener implements Listener {
	
	@EventHandler
	public void onTest(PlayerToggleSneakEvent event) {
		if (event.isSneaking()) {
			event.setCancelled(true);	// cause i want to test it o.O
		}
	}

}
