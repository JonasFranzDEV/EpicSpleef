package de.oppermann.bastian.spleef;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.oppermann.bastian.spleef.arena.Lobby;
import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.arena.SpleefBlock;
import de.oppermann.bastian.spleef.arena.SpleefSpawnLocation;
import de.oppermann.bastian.spleef.arena.StandardSpleefArena;
import de.oppermann.bastian.spleef.commands.AddBlocksArgument;
import de.oppermann.bastian.spleef.commands.AddJoinSignArgument;
import de.oppermann.bastian.spleef.commands.AddSpawnlocArgument;
import de.oppermann.bastian.spleef.commands.CreateArgument;
import de.oppermann.bastian.spleef.commands.DeleteArgument;
import de.oppermann.bastian.spleef.commands.FlagsArgument;
import de.oppermann.bastian.spleef.commands.JoinArgument;
import de.oppermann.bastian.spleef.commands.LeaveArgument;
import de.oppermann.bastian.spleef.commands.ReloadArgument;
import de.oppermann.bastian.spleef.commands.SetLobbyArgument;
import de.oppermann.bastian.spleef.commands.SetValueArgument;
import de.oppermann.bastian.spleef.commands.StartArgument;
import de.oppermann.bastian.spleef.commands.StatsArgument;
import de.oppermann.bastian.spleef.commands.UpdateArgument;
import de.oppermann.bastian.spleef.listener.BlockBreakListener;
import de.oppermann.bastian.spleef.listener.EntityDamageByEntityListener;
import de.oppermann.bastian.spleef.listener.EntityDamageListener;
import de.oppermann.bastian.spleef.listener.FoodLevelChangeListener;
import de.oppermann.bastian.spleef.listener.InventoryClickListener;
import de.oppermann.bastian.spleef.listener.InventoryOpenListener;
import de.oppermann.bastian.spleef.listener.PlayerCommandPreprocessListener;
import de.oppermann.bastian.spleef.listener.PlayerDropItemListener;
import de.oppermann.bastian.spleef.listener.PlayerInteractListener;
import de.oppermann.bastian.spleef.listener.PlayerJoinListener;
import de.oppermann.bastian.spleef.listener.PlayerMoveListener;
import de.oppermann.bastian.spleef.listener.PlayerPickupItemListener;
import de.oppermann.bastian.spleef.listener.PlayerQuitListener;
import de.oppermann.bastian.spleef.listener.ProjectileHitListener;
import de.oppermann.bastian.spleef.listener.ProjectileLaunchListener;
import de.oppermann.bastian.spleef.listener.ServerListPingListener;
import de.oppermann.bastian.spleef.lobbycommands.AddLobbySpawnlocArgument;
import de.oppermann.bastian.spleef.lobbycommands.CreateLobbyArgument;
import de.oppermann.bastian.spleef.storage.ConfigAccessor;
import de.oppermann.bastian.spleef.storage.StorageManager;
import de.oppermann.bastian.spleef.util.BungeecordUtil;
import de.oppermann.bastian.spleef.util.EpicSpleefVersion;
import de.oppermann.bastian.spleef.util.GameStopReason;
import de.oppermann.bastian.spleef.util.GravityModifier;
import de.oppermann.bastian.spleef.util.Metrics;
import de.oppermann.bastian.spleef.util.ParticleCreatorTask;
import de.oppermann.bastian.spleef.util.PlayerDismountCheckTask;
import de.oppermann.bastian.spleef.util.PluginChecker;
import de.oppermann.bastian.spleef.util.ScoreboardConfiguration;
import de.oppermann.bastian.spleef.util.SimpleBlock;
import de.oppermann.bastian.spleef.util.SpectateType;
import de.oppermann.bastian.spleef.util.SpleefArenaConfiguration;
import de.oppermann.bastian.spleef.util.SpleefMode;
import de.oppermann.bastian.spleef.util.SpleefRunTask;
import de.oppermann.bastian.spleef.util.SuperModesTask;
import de.oppermann.bastian.spleef.util.UpdateChecker;
import de.oppermann.bastian.spleef.util.Validator;
import de.oppermann.bastian.spleef.util.command.SpleefCommand;

/**
 * The main class of the plugin.
 * 
 * @author Bastian Oppermann
 */
public class SpleefMain extends JavaPlugin {
	
	/*
	 * An initialized instance of the plugin.
	 */
	private static SpleefMain instance;
	
	private ConfigAccessor config;
	private final HashMap<String, ConfigAccessor> ARENAS = new HashMap<>();
	private final HashMap<String, ConfigAccessor> LOBBIES = new HashMap<>();
	
	private ScoreboardConfiguration defaultScoreboardConfiguration;
	
	private ConfigAccessor languageConfigAccessor;
	private ConfigAccessor particleConfigAccessor;
	private ConfigAccessor bungeeConfigAccessor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	@Override
	public void onEnable() {
		long currentTimeMillisStart = System.currentTimeMillis(); // get time at  the  beginning

		instance = this; // initialize instance field

		log(Level.INFO, "Enabling EpicSpleef by BtoBastian");

		metrics(); // metrics stuff

		// update
		update(getConfig().getBoolean("auto-update.check", true), getConfig().getBoolean("auto-update.update", true), getConfig().getBoolean("auto-update.unsafeUpdates", false));

		runTasks();	// run tasks
		loadConfig(); // load the config
		loadLanguageConfig(); // load the language config
		loadParticleConfig(); // load the particle config
		//loadBungeeConfig(); // load the bungeecord config // TODO enable bungeecord support
		regListener(); // register listener
		regCommands(); // register commands
		loadLobbies(); // load the lobbies
		loadArenas(); // load the arenas
		loadStats(); // load the stats
		
		//BungeecordUtil.init(); // init bungeecord utils // TODO enable bungeecord support
		
		if (!PluginChecker.vaultIsLoaded()) {
			log(Level.INFO, "Could not find Vault. Money rewards won't work. :(");
		}

		loadClassesRequiredForDisable(); // you need to do this, or the plugin will crash on restart if someone replaced the .jar file.

		long timeTook = System.currentTimeMillis() - currentTimeMillisStart; // calculate time took  to enable the plugin
		log(Level.INFO, "Enabling took " + timeTook + " milliseconds");
		
		super.onEnable();
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 */
	@Override
	public void onDisable() {
		for (SpleefArena arena : SpleefArena.getSpleefArenas()) {
			arena.stopImmediately(GameStopReason.PLUGIN_DISABLED);
		}
		StorageManager.getInstance().getListeningExecutorService().shutdown();
		try {	
			StorageManager.getInstance().getSqlConnector().closeConnection();
		} catch (SQLException e) {
			// should not happen
			e.printStackTrace();
		}
		for (Entry<String, SpleefCommand> command : SpleefCommand.getAllCommands()) {
			command.getValue().unregister();
		}
		super.onDisable();
	}
	
	private void loadClassesRequiredForDisable() {
		GameStopReason.class.getClassLoader();
		GravityModifier.class.getClassLoader();
	}
	
	private void update(final boolean check, final boolean update, final boolean unsafeUpdates) {
		if (!check) {
			return;
		}
		new Thread(new Runnable() {			
			@Override
			public void run() {
				EpicSpleefVersion newest = UpdateChecker.getNewestVersion(unsafeUpdates);
				
				if (newest == null) {
					log(Level.INFO, "No update found...");
				} else {
					log(Level.INFO, "New update avaiable: " + newest.getVersionFileName());
					if (update) {
						UpdateChecker.downloadFile(newest, true, null);
					}
				}
			}
		}).start();
	}
	
	private void runTasks() {
		Bukkit.getScheduler().runTaskTimer(this, new PlayerDismountCheckTask(), 1, 1);
		Bukkit.getScheduler().runTaskTimer(this, new ParticleCreatorTask(), 1, 1);
		Bukkit.getScheduler().runTaskTimer(this, new SpleefRunTask(), 4, 4);
		Bukkit.getScheduler().runTaskTimer(this, new SuperModesTask(), 20, 20);
	}
	
	private void metrics() {
		try {
	        Metrics metrics = new Metrics(this);
	        metrics.start();
	    } catch (IOException e) {
	    	log(Level.INFO, "Failed to submit stats to metrics :((((((");
	    }
	}
	
	private void loadConfig() {
		config = config != null ? config : new ConfigAccessor(this, "config.yml", getDataFolder());
		config.saveDefaultConfig();
		
		defaultScoreboardConfiguration = new ScoreboardConfiguration();
		for (String line : config.getConfig().getStringList("scoreboard")) {
			defaultScoreboardConfiguration.addLine(line);
		}
	}
	
	private void loadLanguageConfig() {
		languageConfigAccessor = new ConfigAccessor(this, config.getConfig().getString("mainSettings.languagefile", "language.yml"), getDataFolder());
	}
	
	private void loadParticleConfig() {
		particleConfigAccessor = new ConfigAccessor(this, "particles.yml", getDataFolder());
		particleConfigAccessor.saveDefaultConfig();
	}
	
	private void loadBungeeConfig() {
		bungeeConfigAccessor = new ConfigAccessor(this, "bungeecord.yml", getDataFolder());
		bungeeConfigAccessor.saveDefaultConfig();
	}
	
	private void regListener() {
		Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
		Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);
		Bukkit.getPluginManager().registerEvents(new EntityDamageByEntityListener(), this);
		Bukkit.getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryOpenListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerCommandPreprocessListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDropItemListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerPickupItemListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
		Bukkit.getPluginManager().registerEvents(new ProjectileHitListener(), this);
		Bukkit.getPluginManager().registerEvents(new ProjectileLaunchListener(), this);
		// Bukkit.getPluginManager().registerEvents(new ServerListPingListener(), this);
	}
	
	private void regCommands() {
		// arena
		SpleefCommand.createIfNotExist("spleef", "spleef").registerArgument(new AddBlocksArgument());
		SpleefCommand.createIfNotExist("spleef", "spleef").registerArgument(new AddJoinSignArgument());
		SpleefCommand.createIfNotExist("spleef", "spleef").registerArgument(new AddSpawnlocArgument());
		SpleefCommand.createIfNotExist("spleef", "spleef").registerArgument(new CreateArgument());
		SpleefCommand.createIfNotExist("spleef", "spleef").registerArgument(new DeleteArgument());
		SpleefCommand.createIfNotExist("spleef", "spleef").registerArgument(new FlagsArgument());
		SpleefCommand.createIfNotExist("spleef", "spleef").registerArgument(new JoinArgument());
		SpleefCommand.createIfNotExist("spleef", "spleef").registerArgument(new LeaveArgument());
		SpleefCommand.createIfNotExist("spleef", "spleef").registerArgument(new ReloadArgument());
		SpleefCommand.createIfNotExist("spleef", "spleef").registerArgument(new SetLobbyArgument());
		SpleefCommand.createIfNotExist("spleef", "spleef").registerArgument(new SetValueArgument());
		SpleefCommand.createIfNotExist("spleef", "spleef").registerArgument(new StartArgument());
		SpleefCommand.createIfNotExist("spleef", "spleef").registerArgument(new StatsArgument());
		SpleefCommand.createIfNotExist("spleef", "spleef").registerArgument(new UpdateArgument());
		// lobby
		SpleefCommand.createIfNotExist("spleefLobby", "spleefLobby").registerArgument(new CreateLobbyArgument());
		SpleefCommand.createIfNotExist("spleefLobby", "spleefLobby").registerArgument(new AddLobbySpawnlocArgument());
	}	
	
	private void loadLobbies() {
		File folder = new File(getDataFolder().getPath(), "lobbies");
		folder.mkdirs();
		for (File file : folder.listFiles()) {
			if (file.getName().toLowerCase().endsWith(".yml")) {
				String name = file.getName().substring(0, file.getName().length() - 4);
				log(Level.INFO, "Loading lobby " + name);
				ConfigAccessor lobbyConfig = new ConfigAccessor(this, file.getName(), folder);
				String world = lobbyConfig.getConfig().getString("world");
				if (world == null) {
					log(Level.SEVERE, "Could not load lobby " + lobbyConfig + "! World missing!");
					continue;
				}
				if (Bukkit.getWorld(world) == null) {
					log(Level.SEVERE, "Could not load lobby " + name + "! World " + world + " is not loaded!");
					continue;
				}
				
				Lobby lobby = new Lobby(name, world);
				ConfigurationSection locationSection = lobbyConfig.getConfig().getConfigurationSection("spawnlocs");
				// add spawnlocations
				int spawnlocsCounter = 0;
				if (locationSection != null) {
					for (String loc : locationSection.getKeys(false)) {
						double x = lobbyConfig.getConfig().getDouble("spawnlocs." + loc + ".x");
						double y = lobbyConfig.getConfig().getDouble("spawnlocs." + loc + ".y");
						double z = lobbyConfig.getConfig().getDouble("spawnlocs." + loc + ".z");
						float yaw = (float) lobbyConfig.getConfig().getDouble("spawnlocs." + loc + ".yaw");
						float pitch = (float) lobbyConfig.getConfig().getDouble("spawnlocs." + loc + ".pitch");
						lobby.addSpawnLocation(new SpleefSpawnLocation(x, y, z, yaw, pitch));
						spawnlocsCounter++;
					}
				}
				
				LOBBIES.put(name, lobbyConfig);
				log(Level.INFO, "Successfully loaded lobby " + name + ". (world: " + world + ", spawnLocations: " + spawnlocsCounter + ")");				
			}
		}
	}
	
	private void loadArenas() {
		File folder = new File(getDataFolder().getPath(), "arenas");
		folder.mkdirs();
		for (File file : folder.listFiles()) {
			if (file.getName().toLowerCase().endsWith(".yml")) {
				String name = file.getName().substring(0, file.getName().length() - 4);
				log(Level.INFO, "Loading arena " + name);
				ConfigAccessor arenaConfig = new ConfigAccessor(this, file.getName(), folder);
				String world = arenaConfig.getConfig().getString("world");
				if (world == null) {
					log(Level.SEVERE, "Could not load arena " + name + "! World missing!");
					continue;
				}
				if (Bukkit.getWorld(world) == null) {
					log(Level.SEVERE, "Could not load arena " + name + "! World " + world + " is not loaded!");
					continue;
				}
				
				String strLobby = arenaConfig.getConfig().getString("lobby");
				Lobby lobby = strLobby == null ? null : Lobby.getLobbyByName(strLobby);	// the lobby can be null, but not the name
				
				SpleefArenaConfiguration configuration = new SpleefArenaConfiguration();
				
				configuration.setLobby(lobby);
				
				configuration.setDisabled(!arenaConfig.getConfig().getBoolean("enabled", true));
				try {
					configuration.setMode(SpleefMode.valueOf(arenaConfig.getConfig().getString("mode", "normal").toUpperCase()));
				} catch (IllegalArgumentException e) {
					log(Level.SEVERE, "Unknown mode: " + arenaConfig.getConfig().getString("mode", "normal").toUpperCase() + "; Using mode NORMAL.");
				}
				configuration.setModifyGravity(arenaConfig.getConfig().getBoolean("modifygravity.enable", false));
				configuration.setGravity(arenaConfig.getConfig().getDouble("modifygravity.gravity", 0.5D));
				configuration.setEnableSnowballs(arenaConfig.getConfig().getBoolean("snowballs.enable", true));
				configuration.setMaxSnowballs(arenaConfig.getConfig().getInt("snowballs.maxSnowballs", 16));
				configuration.setMinPlayers(arenaConfig.getConfig().getInt("minPlayers", 2));
				configuration.setRequiredPlayersToStartountdown(arenaConfig.getConfig().getInt("requiredPlayersToStartCountdown", 2));
				configuration.setFreezePlayers(arenaConfig.getConfig().getBoolean("freezePlayers", true));
				configuration.setCustomInventory(arenaConfig.getConfig().getBoolean("customInventory.enabled", false));
				configuration.setPointsWinningReward(arenaConfig.getConfig().getInt("reward.points.winning"));
				configuration.setPointsParticipationReward(arenaConfig.getConfig().getInt("reward.points.participation"));
				configuration.setMoneyWinningReward(arenaConfig.getConfig().getInt("reward.money.winning"));
				configuration.setMoneyParticipationReward(arenaConfig.getConfig().getInt("reward.money.participation"));
				configuration.setArenaCountdown(arenaConfig.getConfig().getInt("arenaCountdown", 10));
				configuration.setLobbyCountdown(arenaConfig.getConfig().getInt("lobbyCountdown", 60));
				try {
					configuration.setVehicle(arenaConfig.getConfig().getString("vehicle") == null ? null : EntityType.valueOf(arenaConfig.getConfig().getString("vehicle").toUpperCase()));
				} catch (IllegalArgumentException e) {
					// unknown vehicle or "none"
				}
				configuration.setInstanstBlockDestroy(arenaConfig.getConfig().getBoolean("instantBlockDestroy", false));
				try {
					configuration.setSpectateType(SpectateType.valueOf(arenaConfig.getConfig().getString("spectateType", "normal").toUpperCase()));
				} catch (IllegalArgumentException e) {
					log(Level.SEVERE, "Unknown spectateType: " + arenaConfig.getConfig().getString("mode", "normal").toUpperCase() + "; Using type NORMAL.");
				}
				
				Location spectateLocation = new Location(Bukkit.getWorld(world), 0, 0, 0, 0, 0);
				spectateLocation.setX(arenaConfig.getConfig().getInt("spectateLocation.x", 0));
				spectateLocation.setY(arenaConfig.getConfig().getInt("spectateLocation.y", 0));
				spectateLocation.setZ(arenaConfig.getConfig().getInt("spectateLocation.z", 0));
				spectateLocation.setYaw((float) arenaConfig.getConfig().getDouble("spectateLocation.yaw", 0));
				spectateLocation.setPitch((float) arenaConfig.getConfig().getDouble("spectateLocation.pitch", 0));
				
				configuration.setSpectateLocation(spectateLocation);
				
				ItemStack[] customInventoryContents = new ItemStack[9*4];
				for (int i = 0; i < customInventoryContents.length; i++) {
					customInventoryContents[i] = arenaConfig.getConfig().getItemStack("customInventory.items." + i);	// could be null but never mind :)
				}
				configuration.setCustomInventoryContents(customInventoryContents);
				
				// create the arena
				SpleefArena arena = new StandardSpleefArena(name, world, configuration, defaultScoreboardConfiguration);
				
				ConfigurationSection blocksSection = arenaConfig.getConfig().getConfigurationSection("blocks");
				// add blocks
				int blocksCounter = 0;
				if (blocksSection != null) {
					for (String block : blocksSection.getKeys(false)) {
						int x = arenaConfig.getConfig().getInt("blocks." + block + ".x");
						int y = arenaConfig.getConfig().getInt("blocks." + block + ".y");
						int z = arenaConfig.getConfig().getInt("blocks." + block + ".z");
						String strType = arenaConfig.getConfig().getString("blocks." + block + ".type", "SNOW_BLOCK");
						byte data = (byte) arenaConfig.getConfig().getInt("blocks." + block + ".data", 0);
						Material type = Material.SNOW_BLOCK;
						try {
							type = Material.valueOf(strType);
						} catch (IllegalArgumentException e) {
							
						}
						arena.addSpleefBlock(new SpleefBlock(x, y, z, type, data));
						blocksCounter++;
					}
				}
				
				ConfigurationSection locationSection = arenaConfig.getConfig().getConfigurationSection("spawnlocs");
				// add spawnlocations
				int spawnlocsCounter = 0;
				if (locationSection != null) {
					for (String loc : locationSection.getKeys(false)) {
						double x = arenaConfig.getConfig().getDouble("spawnlocs." + loc + ".x");
						double y = arenaConfig.getConfig().getDouble("spawnlocs." + loc + ".y");
						double z = arenaConfig.getConfig().getDouble("spawnlocs." + loc + ".z");
						float yaw = (float) arenaConfig.getConfig().getDouble("spawnlocs." + loc + ".yaw");
						float pitch = (float) arenaConfig.getConfig().getDouble("spawnlocs." + loc + ".pitch");
						arena.addSpawnLocation(new SpleefSpawnLocation(x, y, z, yaw, pitch));
						spawnlocsCounter++;
					}
				}
				
				ConfigurationSection signsSection = arenaConfig.getConfig().getConfigurationSection("joinsigns");
				// add blocks
				if (signsSection != null) {
					for (String sign : signsSection.getKeys(false)) {
						String signWorld = arenaConfig.getConfig().getString("joinsigns." + sign + ".world", arena.getWorldName());
						int x = arenaConfig.getConfig().getInt("joinsigns." + sign + ".x");
						int y = arenaConfig.getConfig().getInt("joinsigns." + sign + ".y");
						int z = arenaConfig.getConfig().getInt("joinsigns." + sign + ".z");
						SimpleBlock simpleSignBlock = new SimpleBlock(signWorld, x, y, z);
						if (simpleSignBlock.getWorld() == null) {
							log(Level.SEVERE, "Could not load joinsign in world " + signWorld + " at " + x + ", " + y + ", " + z + " cause the world does not exist!");
							continue;
						}
						if (!(simpleSignBlock.toBlock().getState() instanceof Sign)) {
							log(Level.SEVERE, "Could not load joinsign in world " + signWorld + " at " + x + ", " + y + ", " + z + " cause there is no sign!");
							continue;
						}
						arena.addJoinSign(simpleSignBlock.toBlock());
					}
				}
				
				ARENAS.put(name, arenaConfig);
				log(Level.INFO, "Successfully loaded arena " + name + ". (world: " + world + ", blocks: " + blocksCounter + ", maxPlayers: " + spawnlocsCounter + (lobby == null ? "" : ", lobby:" + lobby.getName()) + ")");	
			}
		}
	}
	
	/**
	 * {@link #loadArenas()} must be called first!
	 */
	private void loadStats() {
		setupDatabase();
	}
	
	private void setupDatabase() {		
		for (String arena : SpleefArena.getArenaNames()) {
			StorageManager.getInstance().createTableForArena(arena);
		}
		if (SpleefArena.getArenaNames().length <= 0) {
			StorageManager.getInstance();
		}
	}
	
	/**
	 * Gets the accessor for the arena.
	 */
	public ConfigAccessor getArenaAccessor(String arena) {
		// validate parameters
		Validator.validateNotNull(arena, "arena");
		return ARENAS.get(arena);
	}
	
	/**
	 * Gets the accessor for the lobby.
	 */
	public ConfigAccessor getLobbyAccessor(String lobby) {
		// validate parameters
		Validator.validateNotNull(lobby, "lobby");
		return LOBBIES.get(lobby);
	}
	
	/**
	 * Adds an accessor.
	 */
	public void addArenaConfiguration(String arena, ConfigAccessor accessor) {
		// validate parameters
		Validator.validateNotNull(arena, "arena");
		Validator.validateNotNull(accessor, "accessor");
		ARENAS.put(arena, accessor);
	}
	
	/**
	 * Adds an accessor.
	 */
	public void addLobbyConfiguration(String lobby, ConfigAccessor accessor) {
		// validate parameters
		Validator.validateNotNull(lobby, "lobby");
		Validator.validateNotNull(accessor, "accessor");
		LOBBIES.put(lobby, accessor);
	}
	
	/**
	 * Removes an accessor.
	 */
	public void removeArenaConfiguration(String arena) {
		// validate parameters
		Validator.validateNotNull(arena, "arena");
		ARENAS.remove(arena);
	}
	
	/**
	 * Removes an accessor.
	 */
	public void removeLobbyConfiguration(String lobby) {
		// validate parameters
		Validator.validateNotNull(lobby, "lobby");
		LOBBIES.remove(lobby);
	}
	
	/**
	 * Gets the {@link ConfigAccessor} for the language file.
	 */
	public ConfigAccessor getLanguageConfigAccessor() {
		return languageConfigAccessor;
	}	
	
	/**
	 * Gets the {@link ConfigAccessor} for the particles.
	 */
	public ConfigAccessor getParticleConfigAccessor() {
		return particleConfigAccessor;
	}
	
	/**
	 * Gets the {@link ConfigAccessor} for bungeecord.
	 */
	public ConfigAccessor getBungeeConfigAccessor() {
		return bungeeConfigAccessor;
	}
	
	/**
	 * Gets the default {@link ScoreboardConfiguration} for the arenas.
	 */
	public ScoreboardConfiguration getDefaultScoreboardConfiguration() {
		return defaultScoreboardConfiguration;
	}
	
	/**
	 * Gets the main {@link ConfigAccessor}.
	 */
	public ConfigAccessor getConfigAccessor() {
		return config;
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#getConfig()
	 */
	@Override
	public FileConfiguration getConfig() {
		config = config != null ? config : new ConfigAccessor(this, "config.yml", getDataFolder());
		return config.getConfig();
	}

	/**
	 * Logs a text.
	 */
	public void log(Level level, String msg) {
		// validate parameters
		Validator.validateNotNull(level, "level");
		Validator.validateNotNull(msg, "msg");
		
		getLogger().log(level, msg);
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#getFile()
	 */
	@Override
	public File getFile() {	// make method public
		return super.getFile();
	}

	/**
	 * Returns an initialized instance of the plugin.
	 */
	public static SpleefMain getInstance() {
		return instance;
	}
	
}
