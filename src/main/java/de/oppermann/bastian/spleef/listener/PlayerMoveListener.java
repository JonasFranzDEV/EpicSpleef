package de.oppermann.bastian.spleef.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.google.common.util.concurrent.FutureCallback;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.GameStatus;
import de.oppermann.bastian.spleef.util.PlayerManager;
import de.oppermann.bastian.spleef.util.SpleefPlayer;

public class PlayerMoveListener implements Listener {
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {		
		Player player = event.getPlayer();
		SpleefArena arena = PlayerManager.getArena(player.getUniqueId());
		if (arena == null) {
			return;
		}
		
		if (arena.getStatus() != GameStatus.ACTIVE && !arena.playersAreInLobby() && arena.getConfiguration().freezePlayers()) {	// freeze players in arena
			int toX = (int) event.getFrom().getX();
			int toY = (int) event.getFrom().getY();
			int toZ = (int) event.getFrom().getZ();
			int fromX = (int) event.getTo().getX();
			int fromY = (int) event.getTo().getY();
			int fromZ = (int) event.getTo().getZ();
			
			boolean xMatch = toX == fromX;
			boolean yMatch = toY == fromY;
			boolean zMatch = toZ == fromZ;
			
			if (!xMatch || !yMatch || !zMatch) {
				Location from = event.getFrom();
				double x = Math.floor(from.getX());
				double z = Math.floor(from.getZ());
				x += 0.5;
				z += 0.5;
				player.teleport(new Location(from.getWorld(), x, from.getY(), z, from.getYaw(), from.getPitch()));
			}			
		}
		
		if (arena.getStatus() == GameStatus.ACTIVE) {
			int fromY = event.getFrom().getBlockY();
			int toY = event.getTo().getBlockY();
			
			if (fromY == toY) {
				return;
			}
			
			if (arena.getLowestBlock() > toY) {
				arena.onLose(player);
				return;
			}

			// check if player is jumping
			if (fromY < toY) {	// up
				if (event.getFrom().getBlock().getRelative(0, -1, 0).getType().isBlock()) {
					final SpleefArena ARENA = arena;
					SpleefPlayer.getPlayer(player.getUniqueId(), new FutureCallback<SpleefPlayer>() {					
						@Override
						public void onSuccess(SpleefPlayer stats) {
							stats.addJumps(ARENA.getName(), 1);
						}
						
						@Override
						public void onFailure(Throwable e) {
							e.printStackTrace();
						}
					});
				}
			}
		}		
	}

}
