package de.oppermann.bastian.spleef.storage;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.arena.Lobby;
import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.arena.SpleefBlock;
import de.oppermann.bastian.spleef.arena.SpleefSpawnLocation;
import de.oppermann.bastian.spleef.util.Validator;

/**
 * Class that handles the connection to the database.
 * 
 * @author Bastian Oppermann
 */
public class StorageManager {
	
	//private ExecutorService threadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	private ExecutorService threadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,  60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	private ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(threadPool);

	private static StorageManager instance;	// singleton instance of the class
	
	private static boolean sqlLite = true;
	
	private SQLConnector connector;
	
	private ReentrantLock lock = new ReentrantLock();
	
	private StorageManager() {
		instance = this;
		threadPool.submit(new Runnable() {			
			@Override
			public void run() {
				try {
					if (sqlLite) {
						connector = new SQLiteConnector(SpleefMain.getInstance(), "database.db");
					} else {
						// TODO add mySQL support
					}			
				} catch (SQLException e) {
					// would be very sad, if this happens :(
					SpleefMain.getInstance().log(Level.SEVERE, "Falied to connect to database. Stopping plugin.");
					e.printStackTrace();
					Bukkit.getPluginManager().disablePlugin(SpleefMain.getInstance());
					return;
				}
				
				try {
					connector.getStatement().execute(
						"CREATE TABLE IF NOT EXISTS `stats` (" +
					    "`uuid` varchar(16) NOT NULL," +
						"`points` int(11) NOT NULL," +
						"PRIMARY KEY (uuid) )"
					);
					// TODO for future database changes: add missing columns
				} catch (SQLException e) {
					// would be very sad, if this happens :(
					SpleefMain.getInstance().log(Level.SEVERE, "Falied to create table in database. Stopping plugin.");
					e.printStackTrace();
					Bukkit.getPluginManager().disablePlugin(SpleefMain.getInstance());
					return;
				}
			}
		});
	}
	
	/**
	 * Creates a new table if it doesn't exits.
	 */
	public void createTableForArena(final String arenaName) {
		Validator.validateNotNull(arenaName, "arenaName");
		if (arenaName.contains("`")) {
			throw new IllegalArgumentException("arenaName contains an invalid character. ( ` )");
		}
		
		submit(new Runnable() {
			@Override
			public void run() {
				try {
					connector.getStatement().execute(
						"CREATE TABLE IF NOT EXISTS `stats " + arenaName + "` (" +
						"`uuid` varchar(16) NOT NULL," +		// the uuid of the player
						"`wins` int(11) NOT NULL," +			// the wins of the player in this arena
						"`losses` int(11) NOT NULL," +			// the losses of the player in this arena
						"`points` int(11) NOT NULL," +			// the earned points of the player in this arena (!= the total amount of points)
						"`jumps` int(11) NOT NULL," +			// the jums of the player
						"`destroyedblocks` int(11) NOT NULL," +	// the amount of destroyed blocks
						"PRIMARY KEY (uuid) )"
					);
					// TODO for future database changes: add missing columns
					// TODO option to enable loading the data to memory on startup.
				} catch (SQLException e) {
					// would be very sad, if this happens :(
					SpleefMain.getInstance().log(Level.SEVERE, "Falied to create table \"" + arenaName + "\" in database. Stopping plugin.");
					e.printStackTrace();
					Bukkit.getPluginManager().disablePlugin(SpleefMain.getInstance());
					return;
				}
			}
		});
	}
	
	/**
	 * Creates a new {@link ConfigAccessor} for the arena if it doesn't already exits.
	 */
	public ConfigAccessor createConfigForArena(SpleefArena arena) {		
		ConfigAccessor accessor = SpleefMain.getInstance().getArenaAccessor(arena.getName());
		
		if (accessor == null) {
			File folder = new File(SpleefMain.getInstance().getDataFolder().getPath(), "arenas");
			accessor = new ConfigAccessor(SpleefMain.getInstance(), arena.getName() + ".yml", folder);
			
			accessor.getConfig().set("enabled", !arena.getConfiguration().isDisabled());
			accessor.getConfig().set("mode", arena.getConfiguration().getMode().name().toLowerCase());
			accessor.getConfig().set("world", arena.getWorldName());
			accessor.getConfig().set("lobby", arena.getConfiguration().getLobby() == null ? null : arena.getConfiguration().getLobby().getName());
			accessor.getConfig().set("modifygravity.enable", arena.getConfiguration().modifyGravity());
			accessor.getConfig().set("modifygravity.gravity", arena.getConfiguration().getGravity());
			accessor.getConfig().set("snowballs.enable", arena.getConfiguration().isEnableSnowballs());
			accessor.getConfig().set("snowballs.maxSnowballs", arena.getConfiguration().getMaxSnowballs());
			accessor.getConfig().set("minPlayers", arena.getConfiguration().getMinPlayers());
			accessor.getConfig().set("requiredPlayersToStartCountdown", arena.getConfiguration().getRequiredPlayersToStartCountdown());
			accessor.getConfig().set("freezePlayers", arena.getConfiguration().freezePlayers());
			
			int i = 1;
			for (SpleefSpawnLocation spawnLocation : arena.getSpawnLocations()) {
				accessor.getConfig().set("spawnlocs." + i + ".x", spawnLocation.getX());
				accessor.getConfig().set("spawnlocs." + i + ".y", spawnLocation.getY());
				accessor.getConfig().set("spawnlocs." + i + ".z", spawnLocation.getZ());
				accessor.getConfig().set("spawnlocs." + i + ".yaw", spawnLocation.getYaw());
				accessor.getConfig().set("spawnlocs." + i + ".pitch", spawnLocation.getPitch());
				i++;
			}
			
			i = 1;	// reset i
			for (SpleefBlock block : arena.getBlocks()) {
				accessor.getConfig().set("blocks." + i + ".x", block.getX());
				accessor.getConfig().set("blocks." + i + ".y", block.getY());
				accessor.getConfig().set("blocks." + i + ".z", block.getZ());
				i++;
			}
			
			accessor.saveConfig();
			SpleefMain.getInstance().addArenaConfiguration(arena.getName(), accessor);
		}
		
		return accessor;		
	}
	
	/**
	 * Creates a new {@link ConfigAccessor} for the lobby if it doesn't already exits.
	 */
	public ConfigAccessor createConfigForLobby(Lobby lobby) {		
		ConfigAccessor accessor = SpleefMain.getInstance().getLobbyAccessor(lobby.getName());
		
		if (accessor == null) {
			File folder = new File(SpleefMain.getInstance().getDataFolder().getPath(), "lobbies");
			accessor = new ConfigAccessor(SpleefMain.getInstance(), lobby.getName() + ".yml", folder);
			
			accessor.getConfig().set("world", lobby.getWorldName());
		
			int i = 1;
			for (SpleefSpawnLocation spawnLocation : lobby.getSpawnLocations()) {
				accessor.getConfig().set("spawnlocs." + i + ".x", spawnLocation.getX());
				accessor.getConfig().set("spawnlocs." + i + ".y", spawnLocation.getY());
				accessor.getConfig().set("spawnlocs." + i + ".z", spawnLocation.getZ());
				accessor.getConfig().set("spawnlocs." + i + ".yaw", spawnLocation.getYaw());
				accessor.getConfig().set("spawnlocs." + i + ".pitch", spawnLocation.getPitch());
				i++;
			}
			
			accessor.saveConfig();
			SpleefMain.getInstance().addLobbyConfiguration(lobby.getName(), accessor);
		}
		
		return accessor;		
	}
	
	/**
	 * Gets the threadpool.
	 */
	public ExecutorService getThreadpool() {
		return this.threadPool;
	}
	
	/**
	 * Gets the lock.
	 */
	public ReentrantLock getLock() {
		return lock;
	}
	
	/**
	 * Gets if the database supports asynchronous writing or not.
	 */
	public boolean needWriteLock() {
		return sqlLite;
	}
	
	/**
	 * Gets the {@link ListeningExecutorService}.
	 */
	public ListeningExecutorService getListeningExecutorService() {
		return this.listeningExecutorService;
	}
	
	/**
	 * Adds a runnable that should be performed in the storage thread.
	 * 
	 * @param runnable The runnable that should be performed.
	 */
	public void submit(Runnable runnable) {
		Validator.validateNotNull(runnable, "runnable");
		threadPool.submit(runnable);
	}
	
	/**
	 * Gets the {@link SQLConnector}.
	 */
	public SQLConnector getSqlConnector() {
		return connector;
	}
	
	/**
	 * Gets a singleton instance of the class.
	 * 
	 * @return A singleton instance of the class.
	 */
	public synchronized static StorageManager getInstance() {
		if (instance == null) {
			instance = new StorageManager();
		}
		return instance;
	}

}
