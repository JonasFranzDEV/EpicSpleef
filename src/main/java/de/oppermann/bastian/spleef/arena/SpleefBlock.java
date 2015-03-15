package de.oppermann.bastian.spleef.arena;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import de.oppermann.bastian.spleef.util.Validator;

/**
 * Represents a block of the SpleefArena.
 * 
 * @author Bastian Oppermann
 */
public class SpleefBlock {

	private final int X;
	private final int Y;
	private final int Z;
	private final Material TYPE;
	private final byte DATA;
	
	/**
	 * Class constructor.
	 * 
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 */
	public SpleefBlock(int x, int y, int z, Material type, byte data) {
		this.X = x;
		this.Y = y;
		this.Z = z;
		this.TYPE = type;
		this.DATA = data;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SpleefBlock)) {
			return false;
		}
		
		SpleefBlock block = (SpleefBlock) obj;
		
		if (block.getX() == this.getX()) {
			if (block.getY() == this.getY()) {
				if (block.getZ() == this.getZ()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Gets the real block.
	 */
	public Block toBlock(World world) {
		Validator.validateNotNull(world, "world");
		return world.getBlockAt(getX(), getY(), getZ());
	}
	
	/* Getter */

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
	
	/**
	 * Gets the material of the block.
	 */
	public Material getType() {
		return TYPE;
	}

	/**
	 * Gets the data of the block.
	 */
	public byte getData() {
		return DATA;
	}
	
}
