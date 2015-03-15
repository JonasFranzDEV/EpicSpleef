package de.oppermann.bastian.spleef.util;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import de.oppermann.bastian.spleef.SpleefMain;

public class BlockRemover {
	
	private static final ArrayList<BlockRemover> REMOVERS = new ArrayList<>();
	
	private int x;
	private int y;
	private int z;
	private World world;
	
	private BlockRemover instance;
	
	@SuppressWarnings("deprecation")
	public BlockRemover(Block block) {		
		this.instance = this;
		
		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
		this.world = block.getWorld();
		
		if (REMOVERS.contains(this)) {
			return;
		}
		
		block.setMetadata("BlockRemover", new FixedMetadataValue(SpleefMain.getInstance(), 1));
		
		block.setTypeIdAndData(Material.STAINED_CLAY.getId(), DyeColor.GREEN.getWoolData(), true);
		
		Bukkit.getScheduler().runTaskLater(SpleefMain.getInstance(), new Runnable() {			
			@Override
			public void run() {
				Block block = world.getBlockAt(x, y, z);
				if (block.hasMetadata("BlockRemover")) {
					block.setTypeIdAndData(Material.STAINED_CLAY.getId(), DyeColor.ORANGE.getWoolData(), true);
				}
			}
		}, 10 * 1);
		
		Bukkit.getScheduler().runTaskLater(SpleefMain.getInstance(), new Runnable() {			
			@Override
			public void run() {
				Block block = world.getBlockAt(x, y, z);
				if (block.hasMetadata("BlockRemover")) {
					block.setTypeIdAndData(Material.STAINED_CLAY.getId(), DyeColor.RED.getWoolData(), true);
				}
			}
		}, 10 * 2);
		
		Bukkit.getScheduler().runTaskLater(SpleefMain.getInstance(), new Runnable() {			
			@Override
			public void run() {
				Block block = world.getBlockAt(x, y, z);
				if (block.hasMetadata("BlockRemover")) {
					block.setType(Material.AIR);
				}
				REMOVERS.remove(instance);
				block.removeMetadata("BlockRemover", SpleefMain.getInstance());
			}
		}, 10 * 3);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BlockRemover)) {
			return false;
		}		
		BlockRemover remover = (BlockRemover) obj;
		
		if (remover.x == x) {
			if (remover.y == y) {
				if (remover.z == z) {
					if (remover.world.getName().equals(world.getName())) {
						return true;
					}
				}
			}
		}		
		return false;
	}

}
