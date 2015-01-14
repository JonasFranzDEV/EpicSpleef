package de.oppermann.bastian.spleef.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.PlayerManager;

public class PlayerCommandPreprocessListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		SpleefArena arena = PlayerManager.getArena(player.getUniqueId());
		if (arena != null) {
			String command = event.getMessage().contains(" ") ? event.getMessage().substring(0, event.getMessage().indexOf(" ")) : event.getMessage();
			String[] args = event.getMessage().contains(" ") ? event.getMessage().substring(event.getMessage().indexOf(" ") + 1).split(" ") : new String[0];
			
			if (command.equalsIgnoreCase("/spleef")) {	// TODO configurable /spleef command
				if (args.length == 1 && args[0].equals(Language.COMMAND_LEAVE.toString())) {
					return;
				}
			}
			event.setCancelled(true);
			player.sendMessage(Language.NO_COMMANDS_IN_ARENA.toString());
		}
	}
	
}
