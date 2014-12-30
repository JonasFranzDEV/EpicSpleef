package de.oppermann.bastian.spleef.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.storage.StorageManager;

/**
 * Class that represents the stats of a player.
 * 
 * @author Bastian Oppermann
 */
public class SpleefPlayerStats {
	
	static {	// write the changes every 2 minutes into database
		Bukkit.getScheduler().runTaskTimer(SpleefMain.getInstance(), new Runnable() {			
			@Override
			public void run() {
				for (SpleefPlayerStats stats : STATS.values()) {
					stats.writeChangesToDatabase();
				}
			}
		}, 20*60*2, 20*60*2);	// TODO configurable interval
	}
	
	private static final HashMap<UUID, SpleefPlayerStats> STATS = new HashMap<>();
	
	private final UUID PLAYER;
	
	private boolean existsInMainTable;
	private ArrayList<String> existsInTable = new ArrayList<>(); // if the list contains the arena name it exists in the table for the arena
	
	private final HashMap<String, Integer> WINS = new HashMap<>();
	private final HashMap<String, Integer> LOSSES = new HashMap<>();
	private final HashMap<String, Integer> POINTS = new HashMap<>();
	private final HashMap<String, Integer> JUMPS = new HashMap<>();
	private final HashMap<String, Integer> DESTROYED_BLOCKS = new HashMap<>();
	private final ArrayList<String> NOT_IN_SYNC_WITH_DATABASE = new ArrayList<String>(); // every arena name in the list is different from the database
	private boolean notInSyncWithMainTable = false;
	private int totalPoints;
	
	private SpleefPlayerStats(UUID player, boolean existsInMainTable, int totalPoints) {
		Validator.validateNotNull(player, "player");
		this.PLAYER = player;
		
		this.existsInMainTable = existsInMainTable;
		this.totalPoints = totalPoints;
		
		STATS.put(PLAYER, this);
	}
	
	/**
	 * Adds wins to the stats of the player.
	 * 
	 * @param arenaName The name of the arena.
	 * @param wins The wins to add. Could also be negative to remove wins.
	 */
	public void addWins(String arenaName, int wins) {
		Validator.validateNotNull(arenaName, "arenaName");
		Integer oldWins = WINS.get(arenaName);
		int newWins = oldWins == null ? wins : (oldWins + wins);		
		WINS.put(arenaName, newWins);
		if (wins != 0 && !NOT_IN_SYNC_WITH_DATABASE.contains(arenaName)){
			NOT_IN_SYNC_WITH_DATABASE.add(arenaName);
		}
		// TODO option to write data immediately into database
	}
	
	/**
	 * Adds jumps to the stats of the player.
	 * 
	 * @param arenaName The name of the arena.
	 * @param jumps The jumps to add. Could also be negative to remove jumps.
	 */
	public void addJumps(String arenaName, int jumps) {
		Validator.validateNotNull(arenaName, "arenaName");
		Integer oldJumps = JUMPS.get(arenaName);
		int newJumps = oldJumps == null ? jumps : (oldJumps + jumps);		
		JUMPS.put(arenaName, newJumps);
		if (jumps != 0 && !NOT_IN_SYNC_WITH_DATABASE.contains(arenaName)){
			NOT_IN_SYNC_WITH_DATABASE.add(arenaName);
		}
		// TODO option to write data immediately into database
	}
	
	/**
	 * Adds destroyed blocks to the stats of the player.
	 * 
	 * @param arenaName The name of the arena.
	 * @param destroyedBlocks The amount of destroyed blocks to add. Could also be negative to decrease the amount.
	 */
	public void addDestroyedBlocks(String arenaName, int destroyedBlocks) {
		Validator.validateNotNull(arenaName, "arenaName");
		Integer oldDestroyedBlocks = DESTROYED_BLOCKS.get(arenaName);
		int newDestroyedBlocks = oldDestroyedBlocks == null ? destroyedBlocks : (oldDestroyedBlocks + destroyedBlocks);		
		DESTROYED_BLOCKS.put(arenaName, newDestroyedBlocks);
		if (destroyedBlocks != 0 && !NOT_IN_SYNC_WITH_DATABASE.contains(arenaName)){
			NOT_IN_SYNC_WITH_DATABASE.add(arenaName);
		}
		// TODO option to write data immediately into database
	}
	
	/**
	 * Adds losses to the stats of the player.
	 * 
	 * @param arenaName The name of the arena.
	 * @param losses The losses to add. Could also be negative to remove losses.
	 */
	public void addLosses(String arenaName, int losses) {
		Validator.validateNotNull(arenaName, "arenaName");
		Integer oldLosses = LOSSES.get(arenaName);
		int newLosses = oldLosses == null ? losses : (oldLosses + losses);		
		LOSSES.put(arenaName, newLosses);		
		if (losses != 0 && !NOT_IN_SYNC_WITH_DATABASE.contains(arenaName)){
			NOT_IN_SYNC_WITH_DATABASE.add(arenaName);
		}
		// TODO option to write data immediately into database
	}
	
	/**
	 * Adds points for the player.
	 * 
	 * @param arenaName The name of the arena.
	 * @param losses The points to add. Could also be negative to remove points.
	 */
	public void addPoints(String arenaName, int points) {
		Validator.validateNotNull(arenaName, "arenaName");
		Integer oldPoints = POINTS.get(arenaName);
		int newPoints = oldPoints == null ? points : (oldPoints + points);		
		POINTS.put(arenaName, newPoints);
		
		if (points != 0 && !NOT_IN_SYNC_WITH_DATABASE.contains(arenaName)){
			NOT_IN_SYNC_WITH_DATABASE.add(arenaName);
		}
		
		// special for points
		totalPoints += points;
		if (points != 0) {
			notInSyncWithMainTable = true;
		}
		// TODO option to write data immediately into database
	}
	
	/**
	 * Gets the wins of the player.
	 * 
	 * @param arenaName The name of the arena.
	 */
	public int getWins(String arenaName) {
		Validator.validateNotNull(arenaName, "arenaName");
		Integer wins = WINS.get(arenaName);
		return wins == null ? 0 : wins;
	}
	
	/**
	 * Gets the amount of wins earned in all arenas together.
	 */
	public int getTotalWins() {
		int wins = 0;
		for (Integer win : WINS.values()) {
			wins += win;
		}
		return wins;
	}
	
	/**
	 * Gets the losses of the player.
	 * 
	 * @param arenaName The name of the arena.
	 */
	public int getLosses(String arenaName) {
		Validator.validateNotNull(arenaName, "arenaName");
		Integer losses = LOSSES.get(arenaName);
		return losses == null ? 0 : losses;
	}
	
	/**
	 * Gets the amount of losses earned in all arenas together.
	 */
	public int getTotalLosses() {
		int losses = 0;
		for (Integer loss : LOSSES.values()) {
			losses += loss;
		}
		return losses;
	}
	
	/**
	 * Gets the jumps of the player.
	 * 
	 * @param arenaName The name of the arena.
	 */
	public int getJumps(String arenaName) {
		Validator.validateNotNull(arenaName, "arenaName");
		Integer jumps = JUMPS.get(arenaName);
		return jumps == null ? 0 : jumps;
	}
	
	/**
	 * Gets the amount of jumps in all arenas together.
	 */
	public int getTotalJumps() {
		int jumps = 0;
		for (Integer jump : JUMPS.values()) {
			jumps += jump;
		}
		return jumps;
	}
	
	/**
	 * Gets the amount of destroyed blocks of the player.
	 * 
	 * @param arenaName The name of the arena.
	 */
	public int getDestroyedBlocks(String arenaName) {
		Validator.validateNotNull(arenaName, "arenaName");
		Integer destroyedBlocks = DESTROYED_BLOCKS.get(arenaName);
		return destroyedBlocks == null ? 0 : destroyedBlocks;
	}
	
	/**
	 * Gets the amount of destroyed blocks in all arenas together.
	 */
	public int getTotalDestroyedBlocks() {
		int destroyedBlocks = 0;
		for (Integer destroyedBlock : DESTROYED_BLOCKS.values()) {
			destroyedBlocks += destroyedBlock;
		}
		return destroyedBlocks;
	}
	
	/**
	 * Gets the points of the player.
	 * 
	 * @param arenaName The name of the arena.
	 */
	public int getPoints(String arenaName) {
		Validator.validateNotNull(arenaName, "arenaName");
		Integer points = POINTS.get(arenaName);
		return points == null ? 0 : points;
	}
	
	/**
	 * Gets the amount of wins earned in all arenas together.
	 */
	public int getTotalEarnedPoints() {
		int points = 0;
		for (Integer point : POINTS.values()) {
			points += point;
		}
		return points;
	}
	
	/**
	 * Gets the total amount of points of the player.
	 */
	public int getTotalPoints() {
		return totalPoints;
	}
	
	/**
	 * Writes the changes to the database. This has no effect if there aren't any changes.
	 */
	public void writeChangesToDatabase() {
		if (!notInSyncWithMainTable && NOT_IN_SYNC_WITH_DATABASE.isEmpty())
		{
			return;	// nothing to write in the database
		}
		
		final HashMap<String, Integer> WINS_CLONE = new HashMap<>(this.WINS);
		final HashMap<String, Integer> LOSSES_CLONE = new HashMap<>(this.LOSSES);
		final HashMap<String, Integer> POINTS_CLONE = new HashMap<>(this.POINTS);
		final HashMap<String, Integer> JUMPS_CLONE = new HashMap<>(this.JUMPS);
		final HashMap<String, Integer> DESTROYED_BLOCKS_CLONE = new HashMap<>(this.DESTROYED_BLOCKS);
		final ArrayList<String> NOT_IN_SYNC_WITH_DATABASE_CLONE = new ArrayList<String>(NOT_IN_SYNC_WITH_DATABASE);
		final ArrayList<String> EXISTS_IN_TABLE_CLONE = new ArrayList<String>(existsInTable);
		for (String arena : NOT_IN_SYNC_WITH_DATABASE) {
			if (!existsInTable.contains(arena)) {
				existsInTable.add(arena);	// adds all arenas where values has been changed
			}
		}
		NOT_IN_SYNC_WITH_DATABASE.clear();
		
		final boolean NOT_IN_SYNC_WITH_MAIN_TABLE_CLONE = notInSyncWithMainTable;
		final int TOTAL_POINTS_CLONE = totalPoints;
		final boolean EXISTS_IN_MAIN_TABLE_CLONE = existsInMainTable;
		notInSyncWithMainTable = false;
		existsInMainTable = true;
		
		
		
		StorageManager.getInstance().submit(new Runnable() {			
			@Override
			public void run() {			
				for (String arena : NOT_IN_SYNC_WITH_DATABASE_CLONE) {
					final String QUERY;
					final int INDEX_WINS;
					final int INDEX_LOSSES;
					final int INDEX_POINTS;
					final int INDEX_JUMPS;
					final int INDEX_DESTROYED_BLOCKS;
					final int INDEX_PLAYERUUID;
					if (!EXISTS_IN_TABLE_CLONE.contains(arena)) {
						QUERY = "INSERT INTO `stats " + arena + "` (`uuid`, `wins`, `losses`, `points`, `jumps`, `destroyedblocks`) VALUES (?, ?, ?, ?, ?, ?);";
						INDEX_WINS = 2;
						INDEX_LOSSES = 3;
						INDEX_POINTS = 4;
						INDEX_JUMPS = 5;
						INDEX_DESTROYED_BLOCKS = 6;
						INDEX_PLAYERUUID = 1;
					} else {
						QUERY = "UPDATE `stats " + arena + "` SET `wins` = ? ,`losses` = ? ,`points` = ?,`jumps` = ?,`destroyedblocks` = ? WHERE `uuid` = ?";
						INDEX_WINS = 1;
						INDEX_LOSSES = 2;
						INDEX_POINTS = 3;
						INDEX_JUMPS = 4;
						INDEX_DESTROYED_BLOCKS = 5;
						INDEX_PLAYERUUID = 6;
					}
					
					try {
						int wins = WINS_CLONE.get(arena) == null ? 0 : WINS_CLONE.get(arena);
						int losses = LOSSES_CLONE.get(arena) == null ? 0 : LOSSES_CLONE.get(arena);
						int points = POINTS_CLONE.get(arena) == null ? 0 : POINTS_CLONE.get(arena);
						int jumps = JUMPS_CLONE.get(arena) == null ? 0 : JUMPS_CLONE.get(arena);
						int destroyedBlocks = DESTROYED_BLOCKS_CLONE.get(arena) == null ? 0 : DESTROYED_BLOCKS_CLONE.get(arena);
						
						PreparedStatement stmt = StorageManager.getInstance().getSqlConnector().prepareStatement(QUERY);
						
						try {
							stmt.setInt(INDEX_WINS, wins);
							stmt.setInt(INDEX_LOSSES, losses);
							stmt.setInt(INDEX_POINTS, points);
							stmt.setInt(INDEX_JUMPS, jumps);
							stmt.setInt(INDEX_DESTROYED_BLOCKS, destroyedBlocks);
							stmt.setString(INDEX_PLAYERUUID, PLAYER.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						// execute
						if (!EXISTS_IN_TABLE_CLONE.contains(arena)) {	
							if (StorageManager.getInstance().needWriteLock()) {
								StorageManager.getInstance().getLock().lock();
							}						
							try {
								stmt.execute();
							} finally {
								if (StorageManager.getInstance().needWriteLock()) {
									StorageManager.getInstance().getLock().unlock();
								}
							}
						} else {
							if (StorageManager.getInstance().needWriteLock()) {
								StorageManager.getInstance().getLock().lock();
							}						
							try {
								stmt.executeUpdate();
							} finally {
								if (StorageManager.getInstance().needWriteLock()) {
									StorageManager.getInstance().getLock().unlock();
								}
							}
						}
					} catch (SQLException e) {
						// should not happen ...
						SpleefMain.getInstance().log(Level.SEVERE, "Failed to write playerstats into database!");
						e.printStackTrace();
						return;
					}
				}
				
				if (NOT_IN_SYNC_WITH_MAIN_TABLE_CLONE) {
					final String QUERY;
					final int INDEX_POINTS;
					final int INDEX_PLAYERUUID;		
					if (!EXISTS_IN_MAIN_TABLE_CLONE) {
						QUERY = "INSERT INTO `stats` (`uuid`, `points`) VALUES (?, ?);";
						INDEX_POINTS = 2;
						INDEX_PLAYERUUID = 1;
					} else {
						QUERY = "UPDATE `stats` SET `points` = ? WHERE `uuid` = ?";
						INDEX_POINTS = 1;
						INDEX_PLAYERUUID = 2;
					}	
					
					try {
						PreparedStatement stmt = StorageManager.getInstance().getSqlConnector().prepareStatement(QUERY);
						stmt.setInt(INDEX_POINTS, TOTAL_POINTS_CLONE);
						stmt.setString(INDEX_PLAYERUUID, PLAYER.toString());

						// execute update
						if (!EXISTS_IN_MAIN_TABLE_CLONE) {
							if (StorageManager.getInstance().needWriteLock()) {
								StorageManager.getInstance().getLock().lock();
							}						
							try {
								stmt.execute();
							} finally {
								if (StorageManager.getInstance().needWriteLock()) {
									StorageManager.getInstance().getLock().unlock();
								}
							}
						} else {
							if (StorageManager.getInstance().needWriteLock()) {
								StorageManager.getInstance().getLock().lock();
							}						
							try {
								stmt.executeUpdate();
							} finally {
								if (StorageManager.getInstance().needWriteLock()) {
									StorageManager.getInstance().getLock().unlock();
								}
							}
						}
					} catch (SQLException e) {
						// should not happen ...
						SpleefMain.getInstance().log(Level.SEVERE, "Failed to write playerstats into database!");
						e.printStackTrace();
						return;
					}
				}
			}
		});
	}
	
	/**
	 * Gets the statistics for a player with this uuid.
	 * 
	 * @param player The uuid of the player.
	 * @param callback The callback.
	 * @return The statistics for the player with the given uuid.
	 */
	public static void getPlayerStats(final UUID player, final FutureCallback<SpleefPlayerStats> callback) {
		Validator.validateNotNull(callback, "callback");
		Validator.validateNotNull(player, "player");
		if (!STATS.containsKey(player)) {
			ListenableFuture<SpleefPlayerStats> future = StorageManager.getInstance().getListeningExecutorService().submit(new Callable<SpleefPlayerStats>() {
				@Override
				public SpleefPlayerStats call() throws Exception {
					boolean existsInMainTable = false;
					int totalPoints = 0;
					final String QUERY_TOTAL_POINTS = "SELECT `points` FROM `stats` WHERE `uuid` = ?";
					int indexPlayerUUID = 1;
					try {	// load stats from database
						PreparedStatement stmt = StorageManager.getInstance().getSqlConnector().prepareStatement(QUERY_TOTAL_POINTS);
						stmt.setString(indexPlayerUUID, player.toString());	// sets the uuid
						
						stmt.execute();
						ResultSet result = stmt.getResultSet();
						while (result.next()) {
							existsInMainTable = true;
							totalPoints = result.getInt("points");
						}
					} catch (SQLException e) {
						// should not happen ...
						SpleefMain.getInstance().log(Level.SEVERE, "Failed to load playerstats from database!");
						throw e;
					}
					
					final SpleefPlayerStats stats = new SpleefPlayerStats(player, existsInMainTable, totalPoints);
					
					for (String arena : SpleefArena.getArenaNames()) {
						boolean existsInTable = false;
						int wins = 0;
						int losses = 0;
						int points = 0;
						int jumps = 0;
						int destroyedBlocks = 0;
						
						final String QUERY_LOAD_STATS = "SELECT * FROM `stats " + arena + "` WHERE `uuid` = ?";
						indexPlayerUUID = 1;
						try {	// load stats from database
							PreparedStatement stmt = StorageManager.getInstance().getSqlConnector().prepareStatement(QUERY_LOAD_STATS);
							stmt.setString(indexPlayerUUID, player.toString());	// sets the uuid
							
							stmt.execute();
							ResultSet result = stmt.getResultSet();
							while (result.next()) {
								existsInTable = true;
								wins = result.getInt("wins");
								losses = result.getInt("losses");
								points = result.getInt("points");
								jumps = result.getInt("jumps");
								destroyedBlocks = result.getInt("destroyedblocks");								
							}
							
							stats.WINS.put(arena, wins);
							stats.LOSSES.put(arena, losses);
							stats.POINTS.put(arena, points);
							stats.JUMPS.put(arena, jumps);
							stats.DESTROYED_BLOCKS.put(arena, destroyedBlocks);
							if (existsInTable) {
								stats.existsInTable.add(arena);
							}
						} catch (SQLException e) {
							// should not happen ...
							SpleefMain.getInstance().log(Level.SEVERE, "Failed to load playerstats from database!");
							throw e;
						}
					}
					return stats;					
				}				
			});
			MoreFutures.addBukkitSyncCallback(future, callback);
		} else {	// if the hashmap contains the uuid
			callback.onSuccess(STATS.get(player));
		}
	}

}
