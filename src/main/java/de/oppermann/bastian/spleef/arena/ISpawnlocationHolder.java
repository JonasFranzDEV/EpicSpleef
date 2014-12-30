package de.oppermann.bastian.spleef.arena;

import java.util.ArrayList;

public interface ISpawnlocationHolder {

	/**
	 * Adds a spawn location.
	 * 
	 * @param spawnLocation The {@link SpleefSpawnLocation} to add.
	 */
	public void addSpawnLocation(SpleefSpawnLocation spawnLocation);
	
	/**
	 * Gets the spawnlocations.
	 */
	public ArrayList<SpleefSpawnLocation> getSpawnLocations();
	
}
