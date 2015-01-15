package de.oppermann.bastian.spleef.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.util.concurrent.FutureCallback;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.util.GameStatus;
import de.oppermann.bastian.spleef.util.GravityModifier;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.PlayerMemory;
import de.oppermann.bastian.spleef.util.ScoreboardConfiguration;
import de.oppermann.bastian.spleef.util.SpleefArenaConfiguration;
import de.oppermann.bastian.spleef.util.SpleefMode;
import de.oppermann.bastian.spleef.util.SpleefPlayerStats;
import de.oppermann.bastian.spleef.util.TitleManager;
import de.oppermann.bastian.spleef.util.Validator;
import de.oppermann.bastian.spleef.util.gui.GuiItem;
import de.oppermann.bastian.spleef.util.nms.WrappedEnumTitleAction;

/**
 * Class that represents a spleef arena.
 * 
 * @author Bastian Oppermann
 */
public abstract class SpleefArena implements ISpawnlocationHolder {

	private static final HashMap<String, SpleefArena> ARENAS = new HashMap<>();	// stores the arenas with the unique name as key	
	
	private final ArrayList<SpleefBlock> BLOCKS = new ArrayList<>();	// stores all the blocks of the arena that should be reseted
	private final ArrayList<SpleefSpawnLocation> SPAWNLOCATIONS = new ArrayList<>();	// stores the spawn locations
	private final ArrayList<UUID> PLAYERS = new ArrayList<>();	// the players in the arena
	private final ArrayList<SpleefSpawnLocation> FREE_SPAWN_LOCATIONS = new ArrayList<>();	// the locations that are not in use
	private final HashMap<UUID, SpleefSpawnLocation> USED_SPAWN_LOCATION = new HashMap<>();
	private final HashMap<UUID, PlayerMemory> MEMORIES = new HashMap<>();
	
	private final String NAME;	// an unique name for the arena
	private final String WORLD;	// the world of the arena
	
	private final SpleefArenaConfiguration CONFIGURATION;
	
	private final ScoreboardConfiguration SCOREBOARD_CONFIGURATION;
	
	private GameStatus status = GameStatus.WAITING_FOR_PLAYERS;
	
	private int lowestBlock = 256;	// if the player falls under the height of this block he lost the game
	
	private boolean countdownIsActive = false;
	private int countdownId = -1;
	private boolean playersAreInLobby = false;
	
	
	/**
	 * Class constructor.
	 * 
	 * @param name An unique name for the arena to identify it.
	 * @param world The name of the arena's world.
	 * @param lobby The lobby for this arena. Can be null.
	 */
	public SpleefArena(String name, String world, SpleefArenaConfiguration configuration, ScoreboardConfiguration scoreboardConfiguration) {
		// validate parameters
		Validator.validateNotNull(name, "name");
		Validator.validateNotNull(world, "world");
		Validator.validateNotNull(configuration, "configuration");
		Validator.validateNotNull(scoreboardConfiguration, "scoreboardConfiguration");
		
		// initialize the final fields
		this.NAME = name;
		this.WORLD = world;
		this.CONFIGURATION = configuration;
		this.SCOREBOARD_CONFIGURATION = scoreboardConfiguration;
		
		playersAreInLobby = CONFIGURATION.getLobby() != null;
		
		
		if (ARENAS.containsKey(NAME)) {	// check if the name is unique
			throw new IllegalArgumentException("There's already an arena with the name " + NAME);
		}
		
		ARENAS.put(name, this);
		
		if (Bukkit.getWorld(WORLD) == null) {	// check if the world exists
			throw new IllegalArgumentException("The world " + NAME + " does not exist or is not loaded yet");
		}
	}
	
	@SuppressWarnings("deprecation")	// cause mojang sucks
	private void fillArena(Material type, byte data) {
		for (SpleefBlock block : BLOCKS) {
			block.toBlock(getWorld()).setTypeIdAndData(type.getId(), data, true);
		}
	}
	
	private void teleportToLobby(Player player) {
		player.teleport(CONFIGURATION.getLobby().getRandomSpawnLocation().toLocation(CONFIGURATION.getLobby().getWorld()));
		
		if (player.getGameMode() != GameMode.ADVENTURE) {
			player.setGameMode(GameMode.ADVENTURE);
		}
		
		player.setFoodLevel(20);
		
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getInventory().setItem(2, GuiItem.HIDE_PLAYERS.getItem());
		player.getInventory().setItem(4, GuiItem.STATISTICS.getItem());
		player.getInventory().setItem(6, GuiItem.SHOP.getItem());
		player.getInventory().setItem(8, GuiItem.LEAVE_ARENA.getItem());			
		player.getInventory().setHeldItemSlot(0);
	}
	
	private void teleportToArena(Player player) {
		SpleefSpawnLocation spawnLoc = FREE_SPAWN_LOCATIONS.get((int) (Math.random() * FREE_SPAWN_LOCATIONS.size()));	// gets a random free spawn location
		FREE_SPAWN_LOCATIONS.remove(spawnLoc);
		USED_SPAWN_LOCATION.put(player.getUniqueId(), spawnLoc);
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
		System.out.println(player);
		System.out.println(spawnLoc);
		System.out.println(getWorld());
		System.out.println(spawnLoc.toLocation(getWorld()));
		player.teleport(spawnLoc.toLocation(getWorld()));
		
		if (player.getGameMode() != GameMode.SURVIVAL) {
			player.setGameMode(GameMode.SURVIVAL);
		}
		
		if (CONFIGURATION.modifyGravity()) {
			GravityModifier.modifyGravity(player.getUniqueId(), CONFIGURATION.getGravity());
		}
		
		if (CONFIGURATION.hasCustomInventory()) {
			player.getInventory().setContents(CONFIGURATION.getCustomInventoryContents());
		} else {
			if (CONFIGURATION.getMode() == SpleefMode.BOWSPLEEF) {
				ItemStack bow = new ItemStack(Material.BOW);
				bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
				player.getInventory().setItem(0, bow);
				player.getInventory().setItem(8, new ItemStack(Material.ARROW, 1));	
				
			} else if (CONFIGURATION.getMode() == SpleefMode.SPLEGG) {
				// TODO At the moment it's just a useless hoe and no awesome weapon ... :D
				player.getInventory().setItem(0, new ItemStack(Material.DIAMOND_HOE, 1));
				
			} else if (CONFIGURATION.getMode() == SpleefMode.PIGSPLEEF) {
				// TODO not implementet yet
				
			} else {
				player.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SPADE, 1));
				
			}
		}
	}
	
	private void startCountdown(boolean inLobby, int time) {
		if (countdownIsActive) {
			throw new IllegalStateException("There is already a countdown running");
		}
		final boolean IN_LOBBY = inLobby;
		this.countdownIsActive = true;
		final int[] COUNTER = new int[] { time };
		final int[] ID = new int[1];
		ID[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(SpleefMain.getInstance(), new Runnable() {
			public void run() {
				for (UUID playerUUID : PLAYERS) {
					Player player = Bukkit.getPlayer(playerUUID);
					player.setLevel(COUNTER[0]);
				}
				
				if (COUNTER[0] == 0) {
					countdownIsActive = false;					
					if (IN_LOBBY) {
						fillArena(Material.SNOW_BLOCK, (byte) 0); 
						for (UUID uuidPlayer : PLAYERS) {
							Player player = Bukkit.getPlayer(uuidPlayer);
							teleportToArena(player);
						}
						startCountdown(false, getConfiguration().getArenaCountdown());
					} else {
						status = GameStatus.ACTIVE;						
					}
					
					Bukkit.getScheduler().cancelTask(ID[0]);
					return;
				}
				
				if (COUNTER[0] <= 5 || (COUNTER[0] <= 30 && COUNTER[0] % 5 == 0) || (COUNTER[0] <= 100 && COUNTER[0] % 20 == 0) || COUNTER[0] % 100 == 0) {
					broadcastMessage(Language.JOINED_ARENA_STATUS_GAME_STARTS_IN.toString().replace("%seconds%", String.valueOf(COUNTER[0])));
					if (getConfiguration().showTitleCountdown()) {
						broadcastTitle("", WrappedEnumTitleAction.TITLE);
						broadcastTitle(Language.JOINED_ARENA_STATUS_GAME_STARTS_IN.toString().replace("%seconds%", String.valueOf(COUNTER[0])), WrappedEnumTitleAction.SUBTITLE);
					}
					for (UUID playerUUID : PLAYERS) {
						Player player = Bukkit.getPlayer(playerUUID);
						player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
					}
				}
				
				COUNTER[0]--;
			}
		}, 0L, 20L);
		countdownId = ID[0];
	}
	
	private void broadcastMessage(String message) {
		for (UUID uuidPlayer : PLAYERS) {
			Player player = Bukkit.getPlayer(uuidPlayer);
			player.sendMessage(message);
		}
	}
	
	private void broadcastTitle(String message, WrappedEnumTitleAction type) {
		for (UUID uuidPlayer : PLAYERS) {
			Player player = Bukkit.getPlayer(uuidPlayer);
			TitleManager.sendTitle(player, message, type);
		}
	}
	
	private void setScoreboard(Player player) {
		SCOREBOARD_CONFIGURATION.setScores(player, this);
	}
	
	/**
	 * Tries to add player to the arena. May throw an exception if this is not possible.
	 * 
	 * @param player The player to add.
	 */
	public void join(Player player) {
		Validator.validateNotNull(player, "player");
		
		if (PLAYERS.contains(player.getUniqueId())) {	// a player can not join a arena twice. (YOU DONT SAY?!?)
			throw new IllegalArgumentException("The player " + player.getName() + " has already joined the arena");
		}
		
		if (status != GameStatus.WAITING_FOR_PLAYERS) {	// the player cannot join at the moment
			throw new IllegalStateException("The arena is not waiting for players to join");
		}
		
		if (SPAWNLOCATIONS.size() <= PLAYERS.size()) {
			throw new IllegalStateException("The arena is full");
		}
		
		if (getConfiguration().isDisabled()) {
			throw new IllegalStateException("The arena is disabled");
		}
		
		MEMORIES.put(player.getUniqueId(), new PlayerMemory(player));
		
		if (playersAreInLobby) {
			teleportToLobby(player);									
		} else {
			// TODO option to use the locations in a given order
			teleportToArena(player);
		}
		
		player.setLevel(0);
		player.setExp(0f);

		
		if (PLAYERS.size() >= (CONFIGURATION.getRequiredPlayersToStartCountdown() - 1) && !countdownIsActive()) {
			startCountdown(CONFIGURATION.getLobby() != null, CONFIGURATION.getLobby() != null ? getConfiguration().getLobbyCountdown() : getConfiguration().getArenaCountdown());
		} else {
			TitleManager.sendTitle(player, Language.JOINED_ARENA.toString()
					.replace("%arena%", getName()), WrappedEnumTitleAction.TITLE);
			TitleManager.sendTitle(player, Language.JOINED_ARENA_STATUS_WAITING_FOR_PLAYERS.toString()
					.replace("%players%", String.valueOf(PLAYERS.size() + 1))
					.replace("%maxPlayers%", String.valueOf(SPAWNLOCATIONS.size())), WrappedEnumTitleAction.SUBTITLE);
		}		
		
		 
		// TODO Lots of other stuff
		
		PLAYERS.add(player.getUniqueId());	// add the player
		setScoreboard(player);	// must be after adding player to PLAYERS cause this is checked in the method
	}
	
	/**
	 * Removes a player from the arena.
	 */
	public void removePlayer(Player player) {
		Validator.validateNotNull(player, "player");
		
		if (!PLAYERS.contains(player.getUniqueId())) {
			throw new IllegalArgumentException("The player " + player.getName() + " is not in the arena");
		}
		
		SpleefSpawnLocation spawnLoc = USED_SPAWN_LOCATION.get(player.getUniqueId());
		USED_SPAWN_LOCATION.remove(player.getUniqueId());
		FREE_SPAWN_LOCATIONS.add(spawnLoc);
		PLAYERS.remove(player.getUniqueId());
		PlayerMemory memory = MEMORIES.get(player.getUniqueId());
		MEMORIES.remove(player.getUniqueId());
		memory.restore(player);
		GravityModifier.resetGravity(player.getUniqueId());
		
		if (countdownIsActive() && playersAreInLobby()) {
			if (PLAYERS.size() < CONFIGURATION.getMinPlayers()) {
				Bukkit.getScheduler().cancelTask(countdownId);
				countdownId = -1;
				countdownIsActive = false;
				
				for (UUID playerUUID : PLAYERS) {
					Player p = Bukkit.getPlayer(playerUUID);
					p.setLevel(0);
				}
			}
		}
		
		if (countdownIsActive() && !playersAreInLobby() && getConfiguration().getLobby() != null) {
			if (PLAYERS.size() < CONFIGURATION.getMinPlayers()) {
				for (UUID uuidPlayer : PLAYERS) {
					Player other = Bukkit.getPlayer(uuidPlayer);
					teleportToLobby(other);
					
					FREE_SPAWN_LOCATIONS.clear();
					FREE_SPAWN_LOCATIONS.addAll(SPAWNLOCATIONS);
					USED_SPAWN_LOCATION.clear();
					
					Bukkit.getScheduler().cancelTask(countdownId);
					countdownId = 0;
					countdownIsActive = false;
					
					other.sendMessage(ChatColor.RED + "You get teleported back to lobby cause there aren't enough players!");	// TODO
					other.setLevel(0);
					GravityModifier.resetGravity(uuidPlayer);
				}
			}
		} else {
			if (countdownIsActive() && PLAYERS.size() < CONFIGURATION.getMinPlayers() && getConfiguration().getLobby() == null) {				
				Bukkit.getScheduler().cancelTask(countdownId);
				countdownId = 0;
				countdownIsActive = false;
			}
		}
		
		if (getStatus() == GameStatus.ACTIVE) {
			
			// add loss to the playerstats
			SpleefPlayerStats.getPlayerStats(player.getUniqueId(), new FutureCallback<SpleefPlayerStats>() {
				@Override
				public void onFailure(Throwable e) {
					e.printStackTrace();
				}

				@Override
				public void onSuccess(SpleefPlayerStats stats) {
					stats.addLosses(getName(), 1);
				}
			});
			
			if (PLAYERS.size() == 1) {
				UUID winnerUUID = PLAYERS.get(0);
				Player winner = Bukkit.getPlayer(winnerUUID);
				
				PlayerMemory winnerMemory = MEMORIES.get(winnerUUID);
				winnerMemory.restore(winner);
				
				player.chat("Well played...");
				winner.chat("Thx!");
				
				// add win to the playerstats
				SpleefPlayerStats.getPlayerStats(winnerUUID, new FutureCallback<SpleefPlayerStats>() {
					@Override
					public void onFailure(Throwable e) {
						e.printStackTrace();
					}

					@Override
					public void onSuccess(SpleefPlayerStats stats) {
						stats.addWins(getName(), 1);
						// TODO add points
					}
				});
				
				// reset arena
				PLAYERS.clear();
				FREE_SPAWN_LOCATIONS.clear();
				FREE_SPAWN_LOCATIONS.addAll(SPAWNLOCATIONS);
				USED_SPAWN_LOCATION.clear();
				MEMORIES.clear();
				fillArena(Material.SNOW_BLOCK, (byte) 0);
				status = GameStatus.WAITING_FOR_PLAYERS;
				playersAreInLobby = CONFIGURATION.getLobby() != null;
				GravityModifier.resetGravity(winner.getUniqueId());
			}
		}
	}
	
	/**
	 * Called if the player falls under the lowest block.
	 */
	public void onLose(Player player) {
		// TODO spectator mode
		removePlayer(player);
	}
	
	/**
	 * Checks whether a block belongs to the arena or not.
	 */
	public boolean isArenaBlock(Block block) {
		for (SpleefBlock blocks : BLOCKS) {
			if (block.getWorld().getName().equals(WORLD)) {
				if (blocks.getX() == block.getX()) {
					if (blocks.getY() == block.getY()) {
						if (blocks.getZ() == block.getZ()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Adds a {@link SpleefBlock}.
	 * 
	 * @param block The block to add.
	 */
	public void addSpleefBlock(SpleefBlock block) {
		Validator.validateNotNull(block, "block");	// validate the parameter
		if (!BLOCKS.contains(block)) {	// would be pointless to add a block that is already added
			BLOCKS.add(block);
			if (block.getY() < lowestBlock) {
				lowestBlock = block.getY();
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.arena.ISpawnlocationHolder#addSpawnLocation(de.oppermann.bastian.spleef.arena.SpleefSpawnLocation)
	 */
	@Override
	public void addSpawnLocation(SpleefSpawnLocation spawnLocation) {
		Validator.validateNotNull(spawnLocation, "spawnLocation");	// validate the parameter
		SPAWNLOCATIONS.add(spawnLocation);
		FREE_SPAWN_LOCATIONS.add(spawnLocation);
	}
	
	/* Getter */
	
	/**
	 * Gets the y-coordinate of the lowest block.
	 */
	public int getLowestBlock() {
		return this.lowestBlock;
	}
	
	/**
	 * Gets the unique name for the arena.
	 */
	public String getName() {
		return this.NAME;
	}
	
	/**
	 * Gets the name of the arena's world.
	 */
	public String getWorldName() {
		return this.WORLD;
	}
	
	/**
	 * Gets the world of the arena.
	 */
	public World getWorld() {
		return Bukkit.getWorld(WORLD);
	}	
	
	/**
	 * Gets the configuration of the arena.
	 */
	public SpleefArenaConfiguration getConfiguration() {
		return this.CONFIGURATION;
	}
	
	/**
	 * Gets the players in the arena.
	 */
	public ArrayList<UUID> getPlayers() {
		return new ArrayList<UUID>(PLAYERS);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.arena.ISpawnlocationHolder#getSpawnLocations()
	 */
	@Override
	public ArrayList<SpleefSpawnLocation> getSpawnLocations() {
		ArrayList<SpleefSpawnLocation> locations = new ArrayList<>();
		for (SpleefSpawnLocation spawnLocation : SPAWNLOCATIONS) {
			locations.add(new SpleefSpawnLocation(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), spawnLocation.getYaw(), spawnLocation.getPitch()));
		}
		return locations;
	}
	
	/**
	 * Gets the blocks of arena.
	 */
	public ArrayList<SpleefBlock> getBlocks() {
		ArrayList<SpleefBlock> blocks = new ArrayList<>();
		for (SpleefBlock block : BLOCKS) {
			blocks.add(new SpleefBlock(block.getX(), block.getY(), block.getZ()));
		}
		return blocks;
	}
	
	/**
	 * Gets the status of the game/arena.
	 */
	public GameStatus getStatus() {
		return status;
	}
	
	/**
	 * Checks if the countdown is active. (in lobby OR in arena => check #stillInLobby() )
	 */
	public boolean countdownIsActive() {
		return countdownIsActive;
	}
	
	/**
	 * Checks if the players are still in the lobby.
	 */
	public boolean playersAreInLobby() {
		return playersAreInLobby;
	}

	/**
	 * Gets the names of all arenas.
	 */
	public static String[] getArenaNames() {
		return ARENAS.keySet().toArray(new String[ARENAS.keySet().size()]);
	}
	
	/**
	 * Gets all arenas.
	 */
	public static SpleefArena[] getSpleefArenas() {
		return ARENAS.values().toArray(new SpleefArena[ARENAS.values().size()]);
	}
	
}
