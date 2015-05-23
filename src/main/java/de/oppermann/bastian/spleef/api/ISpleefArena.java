package de.oppermann.bastian.spleef.api;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.oppermann.bastian.spleef.arena.SpleefBlock;
import de.oppermann.bastian.spleef.arena.SpleefSpawnLocation;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaIsDisabledException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaIsFullException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaMisconfiguredException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaNotWaitingForPlayersException;
import de.oppermann.bastian.spleef.util.GameStatus;
import de.oppermann.bastian.spleef.util.GameStopReason;
import de.oppermann.bastian.spleef.util.SimpleBlock;
import de.oppermann.bastian.spleef.util.SpleefArenaConfiguration;

/**
 * An interface representing an arena.
 */
public interface ISpleefArena {

	/**
	 * Broadcasts a message to all players in the arena.
	 * 
	 * @param message The message to broadcast.
	 */
	public void broadcastMessage(String message);
	
	/**
	 * Attempts to add a player to the arena.
	 * 
	 * @param player The player to add.
	 * @throws SpleefArenaNotWaitingForPlayersException if the arena is not waiting for players (e.g. the game is already active)
	 * @throws SpleefArenaIsFullException if the arena is full.
	 * @throws SpleefArenaIsDisabledException if the arena is disabled.
	 * @throws SpleefArenaMisconfiguredException if the configuration of the arena is wrong.
	 */
	public void join(Player player) throws SpleefArenaNotWaitingForPlayersException, SpleefArenaIsFullException, SpleefArenaIsDisabledException, SpleefArenaMisconfiguredException;
	
	/**
	 * Stops the game immediately (e.g. because of a reload)
	 * 
	 * @param reason The reason for stopping the game.
	 */
	public void stopImmediately(GameStopReason reason);
	
	/**
	 * Removes a player from the arena.
	 * 
	 * @param player The player to remove.
	 */
	public void removePlayer(Player player);
	
	/**
	 * Removes a player from the arena.
	 * 
	 * @param player The player to remove.
	 * @param spectate Whether the player should be set as a spectator or not.
	 */
	public void removePlayer(Player player, boolean spectate);
	
	/**
	 * Checks if a block is part of the arena.
	 * 
	 * @param block The {@link Block} to check.
	 * @return Whether it is a block, or not.
	 */
	public boolean isArenaBlock(Block block);
	
	/**
	 * Adds a block to the arena. Blocks that are already part of the arena will be ignored.
	 * 
	 * @param block The block to add.
	 */
	public void addSpleefBlock(SpleefBlock block);
	
	/**
	 * Adds a join sign to the arena.
	 * 
	 * @param block The sign to add.
	 * @return <code>False</code> if the block couldn't be added, because it is already a join sign of the arena, otherwise <code>true</code>.
	 */
	public boolean addJoinSign(Block block);
	
	/**
	 * Checks if a block is a join sign.
	 * 
	 * @param block The block to check.
	 * @return Whether the block is a join sign or not.
	 */
	public boolean isJoinSign(Block block);
	
	/**
	 * Adds a {@link SpleefSpawnLocation} to the arena.
	 * 
	 * @param spawnLocation The spawn location to add.
	 */
	public void addSpawnLocation(SpleefSpawnLocation spawnLocation);
	
	/**
	 * Gets the y-axis of the lowest block.
	 * 
	 * @return The y-axis of the lowest block.
	 */
	public int getLowestBlock();
	
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
	
	/**
	 * Gets the configuration of the arena.
	 * 
	 * @return The configuration of the arena.
	 */
	public SpleefArenaConfiguration getConfiguration();
	
	/**
	 * Gets a copy of the player list.
	 * 
	 * @return A copy of the player list.
	 */
	public ArrayList<UUID> getPlayers();
	
	/**
	 * Gets a copy of the spectator list.
	 * 
	 * @return A copy of the spectator list.
	 */	
	public ArrayList<UUID> getSpectators();
	
	/**
	 * Gets a copy of the spawn locations.
	 * 
	 * @return A copy of the spawn locations.
	 */
	public ArrayList<SpleefSpawnLocation> getSpawnLocations();
	
	/**
	 * Gets a copy of the blocks of the arena.
	 * 
	 * @return A copy of the blocks of the arena.
	 */
	public ArrayList<SpleefBlock> getBlocks();
	
	/**
	 * Gets a copy of the join signs of the arena.
	 * 
	 * @return A copy of the join signs of the arena.
	 */
	public ArrayList<SimpleBlock> getJoinSigns();
	
	/**
	 * Gets the status of the arena.
	 * 
	 * @return The status of the arena.
	 */
	public GameStatus getStatus();
	
	/**
	 * Checks if the countdown is active.
	 * 
	 * @return Whether the countdown is active or not.
	 */
	public boolean countdownIsActive();
	
	/**
	 * Checks if the players are still in the lobby.
	 * 
	 * @return Whether the players are in the lobby or not.
	 */
	public boolean playersAreInLobby();
	
	/**
	 * Deletes the arena.
	 * 
	 * @param deleteStats Whether the statistics should be deleted, too, or not.
	 */
	public void delete(boolean deleteStats);
	
}
