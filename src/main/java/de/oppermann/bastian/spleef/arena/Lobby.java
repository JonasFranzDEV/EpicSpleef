package de.oppermann.bastian.spleef.arena;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;

import de.oppermann.bastian.spleef.api.ILobby;
import de.oppermann.bastian.spleef.util.Validator;

public class Lobby implements ILobby {
	
	private static final HashMap<String, Lobby> LOBBIES = new HashMap<>();	// stores the arenas with the unique name as key
	
	private final ArrayList<SpleefSpawnLocation> SPAWNLOCATIONS = new ArrayList<>();	// stores the spawn locations
	
	private final String NAME;	// an unique name for the arena
	private final String WORLD;	// the world of the arena

	public Lobby(String name, String world) {
		// validate parameters
		Validator.validateNotNull(name, "name");
		Validator.validateNotNull(world, "world");
		
		// initialize the final fields
		this.NAME = name;
		this.WORLD = world;
		
		
		if (LOBBIES.containsKey(NAME)) {	// check if the name is unique
			throw new IllegalArgumentException("There's already a lobby with the name " + NAME);
		}
		
		LOBBIES.put(name, this);
		
		if (Bukkit.getWorld(WORLD) == null) {	// check if the world exists
			throw new IllegalArgumentException("The world " + NAME + " does not exist or is not loaded yet");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.api.ILobby#addSpawnLocation(de.oppermann.bastian.spleef.arena.SpleefSpawnLocation)
	 */
	@Override
	public void addSpawnLocation(SpleefSpawnLocation spawnLocation) {
		Validator.validateNotNull(spawnLocation, "spawnLocation");	// validate the parameter
		
		if (!SPAWNLOCATIONS.contains(spawnLocation)) {	// would be very annoying to have spawn locations at the same location
			SPAWNLOCATIONS.add(spawnLocation);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.api.ILobby#getName()
	 */
	public String getName() {
		return this.NAME;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.api.ILobby#getWorldName()
	 */
	public String getWorldName() {
		return this.WORLD;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.api.ILobby#getWorld()
	 */
	public World getWorld() {
		return Bukkit.getWorld(WORLD);
	}	
	
	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.api.ILobby#getSpawnLocations()
	 */
	@Override
	public ArrayList<SpleefSpawnLocation> getSpawnLocations() {
		return new ArrayList<SpleefSpawnLocation>(SPAWNLOCATIONS);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.api.ILobby#getRandomSpawnLocation()
	 */
	public SpleefSpawnLocation getRandomSpawnLocation() {
		if (SPAWNLOCATIONS.size() <= 0) {
			throw new IllegalArgumentException("the lobby has no spawn locations at the moment");
		}
		
		return SPAWNLOCATIONS.get((int) (Math.random() * SPAWNLOCATIONS.size()));
	}
	
	/**
	 * Gets a lobby by its name.
	 * 
	 * @param name The name of the lobby.
	 */
	public static Lobby getLobbyByName(String name) {
		Validator.validateNotNull(name, "name");
		return LOBBIES.get(name);
	}
	
	/**
	 * Gets the names of all lobbies.
	 */
	public static String[] getLobbyNames() {
		return LOBBIES.keySet().toArray(new String[LOBBIES.keySet().size()]);
	}
	
	/**
	 * Gets all lobbies.
	 */
	public static Lobby[] getLobbies() {
		return LOBBIES.values().toArray(new Lobby[LOBBIES.values().size()]);
	}
	
}
