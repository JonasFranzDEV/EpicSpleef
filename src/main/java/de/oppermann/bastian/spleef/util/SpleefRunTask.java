package de.oppermann.bastian.spleef.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.oppermann.bastian.spleef.arena.SpleefArena;

public class SpleefRunTask implements Runnable {

	@Override
	public void run() {
		for (SpleefArena arena : SpleefArena.getSpleefArenas()) {
			if (arena.getConfiguration().getMode() != SpleefMode.SPLEEF_RUN) {
				continue;
			}
			
			if (arena.getStatus() == GameStatus.ACTIVE && !arena.countdownIsActive()) {	// for spleef run mode
				for (UUID uuidPlayer : arena.getPlayers()) {
					Player player = Bukkit.getPlayer(uuidPlayer);
					final double DIFFERENCE = 0.15;
					Location[] locs = new Location[] {
						player.getLocation().add(DIFFERENCE, 0, DIFFERENCE),
						player.getLocation().add(-DIFFERENCE, 0, -DIFFERENCE),
						player.getLocation().add(-DIFFERENCE, 0, DIFFERENCE),
						player.getLocation().add(DIFFERENCE, 0, -DIFFERENCE),
					};
						
					for (Location loc : locs) {				
						Block block = loc.getBlock().getRelative(0, -1, 0);
						if (block.getType() != Material.AIR && !block.hasMetadata("BlockRemover") && arena.isArenaBlock(block)) {
							new BlockRemover(block);
						}
					}
				}				
				
			}
		}
	}
	
}
