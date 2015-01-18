package de.oppermann.bastian.spleef.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import de.oppermann.bastian.spleef.util.Validator;

/**
 * Represents a simple block.
 * 
 * @author Bastian Oppermann
 */
public class SimpleBlock {

	private final String WORLD;
	private final int X;
	private final int Y;
	private final int Z;
	
	/**
	 * Class constructor.
	 * 
	 * @param world The world of the block.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 */
	public SimpleBlock(String world, int x, int y, int z) {
		Validator.validateNotNull(world, "world");
		this.WORLD = world;
		this.X = x;
		this.Y = y;
		this.Z = z;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SimpleBlock)) {
			return false;
		}
		
		SimpleBlock block = (SimpleBlock) obj;
		
		if (block.getX() == this.getX()) {
			if (block.getY() == this.getY()) {
				if (block.getZ() == this.getZ()) {
					if (block.getWorldName().equals(this.getWorldName())) {
						return true;						
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean equalsRealBlock(Block block) {
		return toBlock().equals(block);
	}
	
	/**
	 * Gets the real block. Could be null if the world does not exist.
	 */
	public Block toBlock() {
		World world = Bukkit.getWorld(WORLD);
		if (world == null) {
			return null;
		}
		return world.getBlockAt(getX(), getY(), getZ());
	}
	
	/* Getter */
	
	/**
	 * Gets the world of the block. Could be null!
	 */
	public World getWorld() {
		return Bukkit.getWorld(WORLD);
	}
	
	/**
	 * Gets the name of the world.
	 */
	public String getWorldName() {
		return WORLD;
	}

	/**
	 * Gets the x coordinate.
	 */
	public int getX() {
		return X;
	}

	/**
	 * Gets the y coordinate.
	 */
	public int getY() {
		return Y;
	}

	/**
	 * Gets the z coordinate.
	 */
	public int getZ() {
		return Z;
	}
	
}
