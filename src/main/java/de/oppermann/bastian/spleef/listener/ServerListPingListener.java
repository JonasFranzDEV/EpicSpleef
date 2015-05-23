package de.oppermann.bastian.spleef.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.arena.SpleefArena;

public class ServerListPingListener implements Listener {
	
	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		FileConfiguration config = SpleefMain.getInstance().getBungeeConfigAccessor().getConfig();
		if (config.getBoolean("supportBungeecord")) {
			if (config.getBoolean("epicSpleefBungee.enabled")) {
				String name = config.getString("instantJoinLobbyOrArena");
				for (SpleefArena arena : SpleefArena.getSpleefArenas()) {
					if (arena.getName().equals(name)) {
						String[] lines = arena.getSignText();
						for (int i = 0; i < lines.length; i++) {
							if (lines[i] == null) {
								lines[i] = "";
							}
							lines[i] = lines[i].replace("=!=", "");
						}
						event.setMotd(lines[0] + "=!=" + lines[1] + "=!=" + lines[2] + "=!=" + lines[3]);
						return;
					}
				}
			}
		}
	}

}
