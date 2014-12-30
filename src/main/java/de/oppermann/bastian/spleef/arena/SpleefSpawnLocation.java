package de.oppermann.bastian.spleef.arena;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents a spawn location.
 * 
 * @author Bastian Oppermann
 */
public class SpleefSpawnLocation {

	private final double X;
	private final double Y;
	private final double Z;
	private final float YAW;
	private final float PITCH;
	
	/**
	 * Class constructor.
	 * 
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 */
	public SpleefSpawnLocation(double x, double y, double z, float yaw, float pitch) {
		this.X = x;
		this.Y = y;
		this.Z = z;
		this.YAW = yaw;
		this.PITCH = pitch;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SpleefSpawnLocation)) {
			return false;
		}
		
		SpleefSpawnLocation location = (SpleefSpawnLocation) obj;
		
		if (location.getX() == this.getX()) {
			if (location.getY() == this.getY()) {
				if (location.getZ() == this.getZ()) {
					if (location.getYaw() == this.getYaw()) {
						if (location.getPitch() == this.getPitch()) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Creates a location.
	 * @param world The world of the location.
	 */
	public Location toLocation(World world) {
		return new Location(world, X, Y, Z, YAW, PITCH);
	}
	 
	/* Getter */

	/**
	 * Gets the x coordinate.
	 */
	public double getX() {
		return X;
	}

	/**
	 * Gets the y coordinate.
	 */
	public double getY() {
		return Y;
	}

	/**
	 * Gets the z coordinate.
	 */
	public double getZ() {
		return Z;
	}
	
	/**
	 * Gets the yaw.
	 */
	public float getYaw() {
		return YAW;
	}

	/**
	 * Gets the pitch.
	 */
	public float getPitch() {
		return PITCH;
	}
	
}
