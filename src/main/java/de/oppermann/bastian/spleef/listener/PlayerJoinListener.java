package de.oppermann.bastian.spleef.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.util.EpicSpleefVersion;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.UpdateChecker;

public class PlayerJoinListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player PLAYER = event.getPlayer();
		if (PLAYER.hasPermission("spleef.getupdateinfos") && SpleefMain.getInstance().getConfigAccessor().getConfig().getBoolean("auto-update.check")) {
			Bukkit.getScheduler().runTaskAsynchronously(SpleefMain.getInstance(), new Runnable() {				
				@Override
				public void run() {
					EpicSpleefVersion version = UpdateChecker.getNewestVersion(true);
					if (version != null) {
						String updateMessage = Language.UPDATE_YOUR_VERSION_IS_OUTDATED.toString();
						updateMessage = updateMessage.replace("%oldVersion%", SpleefMain.getInstance().getDescription().getVersion());
						String simpleVersion = version.getVersionName().replace("EpicSpleef-", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace(".jar", "");
						updateMessage = updateMessage.replace("%version%", simpleVersion);
						updateMessage = updateMessage.replace("%mcVersion%", version.getVersionGameVersion());
						String updateCommand = "/spleef " + Language.COMMAND_UPDATE + " " + Language.COMMAND_UPDATE_ARGUMENT_INSTALL + " " + simpleVersion;	// TODO custom /spleef command
						updateMessage = updateMessage.replace("%updateCommand%", updateCommand);
						PLAYER.sendMessage(updateMessage);
					}
				}
			});
		}
	}

}
