package de.oppermann.bastian.spleef.util;

import java.util.ArrayList;

import org.bukkit.entity.Item;
import org.bukkit.metadata.FixedMetadataValue;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.arena.SpleefBlock;

/**
 * Schedule delay: 1 second (20 ticks)
 */
public class SuperModesTask implements Runnable {

	@Override
	public void run() {
		for (SpleefArena arena : SpleefArena.getSpleefArenas()) {
			if (arena.getConfiguration().getMode() == SpleefMode.SUPER_SPLEEF && arena.getStatus() == GameStatus.ACTIVE && !arena.countdownIsActive()) {
				// grenade
				if (Math.random() < 0.05) {
					ArrayList<SpleefBlock> blocks = arena.getBlocks();
					SpleefBlock block = blocks.get((int) (Math.random() * blocks.size()));
					int counter = blocks.size() / 2;
					while (block.toBlock(arena.getWorld()).getType() != block.getType() && !block.getType().isSolid() && counter > 0) {
						 block = blocks.get((int) (Math.random() * blocks.size()));
						 counter--;
					}
					if (counter > 0) {
						Item item = arena.getWorld().dropItem(block.toBlock(arena.getWorld()).getLocation().add(0, 1, 0), SuperModeItems.GRENADE.getItemStack());
						item.setMetadata("EpicSpleef:" + arena.getName() + ":Grenade", new FixedMetadataValue(SpleefMain.getInstance(), 1));
					}
				}
				// speed boost
				if (Math.random() < 0.05) {
					ArrayList<SpleefBlock> blocks = arena.getBlocks();
					SpleefBlock block = blocks.get((int) (Math.random() * blocks.size()));
					int counter = blocks.size() / 2;
					while (block.toBlock(arena.getWorld()).getType() != block.getType() && !block.getType().isSolid() && counter > 0) {
						 block = blocks.get((int) (Math.random() * blocks.size()));
						 counter--;
					}
					if (counter > 0) {
						Item item = arena.getWorld().dropItem(block.toBlock(arena.getWorld()).getLocation().add(0, 1, 0), SuperModeItems.SPEED_BOOST.getItemStack());
						item.setMetadata("EpicSpleef:" + arena.getName() + ":Speed", new FixedMetadataValue(SpleefMain.getInstance(), 1));
					}
				}
				// jump boost
				if (Math.random() < 0.05) {
					ArrayList<SpleefBlock> blocks = arena.getBlocks();
					SpleefBlock block = blocks.get((int) (Math.random() * blocks.size()));
					int counter = blocks.size() / 2;
					while (block.toBlock(arena.getWorld()).getType() != block.getType() && !block.getType().isSolid() && counter > 0) {
						 block = blocks.get((int) (Math.random() * blocks.size()));
						 counter--;
					}
					if (counter > 0) {
						Item item = arena.getWorld().dropItem(block.toBlock(arena.getWorld()).getLocation().add(0, 1, 0), SuperModeItems.JUMP_BOOST.getItemStack());
						item.setMetadata("EpicSpleef:" + arena.getName() + ":Jump", new FixedMetadataValue(SpleefMain.getInstance(), 1));
					}
				}
				// invisibility
				if (Math.random() < 0.05) {
					ArrayList<SpleefBlock> blocks = arena.getBlocks();
					SpleefBlock block = blocks.get((int) (Math.random() * blocks.size()));
					int counter = blocks.size() / 2;
					while (block.toBlock(arena.getWorld()).getType() != block.getType() && !block.getType().isSolid() && counter > 0) {
						 block = blocks.get((int) (Math.random() * blocks.size()));
						 counter--;
					}
					if (counter > 0) {
						Item item = arena.getWorld().dropItem(block.toBlock(arena.getWorld()).getLocation().add(0, 1, 0), SuperModeItems.INVISIBILITY.getItemStack());
						item.setMetadata("EpicSpleef:" + arena.getName() + ":Invisibility", new FixedMetadataValue(SpleefMain.getInstance(), 1));
					}
				}
			}
		}
	}

}
