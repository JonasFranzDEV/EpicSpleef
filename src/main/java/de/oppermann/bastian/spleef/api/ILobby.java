package de.oppermann.bastian.spleef.api;

import java.util.ArrayList;

import org.bukkit.World;

import de.oppermann.bastian.spleef.arena.SpleefSpawnLocation;

/**
 * An interface representing a lobby.
 */
public interface ILobby {

	/**
	 * Adds a {@link SpleefSpawnLocation} to the lobby.
	 * 
	 * @param spawnLocation The spawn location to add.
	 */
	public void addSpawnLocation(SpleefSpawnLocation spawnLocation);
	
	/**
	 * Gets a copy of the spawn locations.
	 * 
	 * @return A copy of the spawn locations.
	 */
	public ArrayList<SpleefSpawnLocation> getSpawnLocations();
	
	/**
	 * Gets a random spawn location.
	 * 
	 * @return A random spawn location.
	 */
	public SpleefSpawnLocation getRandomSpawnLocation();
	
	/**
	 * Gets the name of the arena.
	 * 
	 * @return The name of the arena.
	 */
	public String getName();
	
	/**
	 * Gets the name of the arena's world.
	 * 
	 * @return The name of the arena's world.
	 */
	public String getWorldName();
	
	/**
	 * Gets the arena's world.
	 * 
	 * @return The arena's world.
	 */
	public World getWorld();
	
}
