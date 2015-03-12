package de.oppermann.bastian.spleef.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.util.concurrent.FutureCallback;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaIsDisabledException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaIsFullException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaMisconfiguredException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaNotWaitingForPlayersException;
import de.oppermann.bastian.spleef.hooks.VaultHook;
import de.oppermann.bastian.spleef.storage.StorageManager;
import de.oppermann.bastian.spleef.util.GameStatus;
import de.oppermann.bastian.spleef.util.GameStopReason;
import de.oppermann.bastian.spleef.util.GravityModifier;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.PetMaker;
import de.oppermann.bastian.spleef.util.PlayerDismountCheckTask;
import de.oppermann.bastian.spleef.util.PlayerMemory;
import de.oppermann.bastian.spleef.util.PluginChecker;
import de.oppermann.bastian.spleef.util.ScoreboardConfiguration;
import de.oppermann.bastian.spleef.util.SimpleBlock;
import de.oppermann.bastian.spleef.util.SpectateType;
import de.oppermann.bastian.spleef.util.SpleefArenaConfiguration;
import de.oppermann.bastian.spleef.util.SpleefPlayer;
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
	private final ArrayList<UUID> SPECTATORS = new ArrayList<>();	// the spectators in the arena
	private final ArrayList<SpleefSpawnLocation> FREE_SPAWN_LOCATIONS = new ArrayList<>();	// the locations that are not in use
	private final HashMap<UUID, SpleefSpawnLocation> USED_SPAWN_LOCATION = new HashMap<>();
	private final HashMap<UUID, PlayerMemory> MEMORIES = new HashMap<>();
	private final ArrayList<SimpleBlock> JOIN_SIGNS = new ArrayList<>();	// the join signs
	
	private final String NAME;	// an unique name for the arena
	private final String WORLD;	// the world of the arena
	
	private final SpleefArenaConfiguration CONFIGURATION;
	
	private final ScoreboardConfiguration SCOREBOARD_CONFIGURATION;
	
	private GameStatus status = GameStatus.WAITING_FOR_PLAYERS;
	
	private int lowestBlock = 256;	// if the player falls under the height of this block he lost the game
	
	private boolean countdownIsActive = false;
	private int countdownId = -1;
	private boolean playersAreInLobby = false;
	
	private final int[] COUNTDOWN_COUNTER = new int[] { 0 };
	
	
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
	
	@SuppressWarnings("deprecation")	// cause mojang/bukkit/everyone sucks
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
		player.updateInventory();
	}
	
	private void teleportToArena(Player player) {
		SpleefSpawnLocation spawnLoc = FREE_SPAWN_LOCATIONS.get((int) (Math.random() * FREE_SPAWN_LOCATIONS.size()));	// gets a random free spawn location
		FREE_SPAWN_LOCATIONS.remove(spawnLoc);
		USED_SPAWN_LOCATION.put(player.getUniqueId(), spawnLoc);
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
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
			for (ItemStack is : CONFIGURATION.getMode().getItems()) {
				player.getInventory().addItem(is);
			}
			if (CONFIGURATION.getVehicle() != null) {
				if (CONFIGURATION.getVehicle().isSpawnable()) {
					final Player PLAYER = player;
					Bukkit.getScheduler().runTaskLater(SpleefMain.getInstance(), new Runnable() {					
						@Override
						public void run() {
							LivingEntity entity = (LivingEntity) PLAYER.getWorld().spawnEntity(PLAYER.getLocation(), CONFIGURATION.getVehicle());
							entity.setPassenger(PLAYER);
							PlayerDismountCheckTask.addDisallowedPlayer(PLAYER);
							PetMaker.setGuide(entity, PLAYER);
						}
					}, 5);
				}
			}
		}
		player.updateInventory();
	}
	
	private void startCountdown(boolean inLobby, int time) {
		if (countdownIsActive) {
			throw new IllegalStateException("There is already a countdown running");
		}
		final boolean IN_LOBBY = inLobby;
		this.countdownIsActive = true;
		COUNTDOWN_COUNTER[0] = time;
		final int[] ID = new int[1];
		ID[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(SpleefMain.getInstance(), new Runnable() {
			public void run() {
				for (UUID playerUUID : PLAYERS) {
					Player player = Bukkit.getPlayer(playerUUID);
					player.setLevel(COUNTDOWN_COUNTER[0]);
				}
				
				if (COUNTDOWN_COUNTER[0] == 0) {
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
					updateSigns(-1);
					return;
				}
				
				if (COUNTDOWN_COUNTER[0] <= 5 || (COUNTDOWN_COUNTER[0] <= 30 && COUNTDOWN_COUNTER[0] % 5 == 0) || (COUNTDOWN_COUNTER[0] <= 100 && COUNTDOWN_COUNTER[0] % 20 == 0) || COUNTDOWN_COUNTER[0] % 100 == 0) {
					broadcastMessage(Language.JOINED_ARENA_STATUS_GAME_STARTS_IN.toString().replace("%seconds%", String.valueOf(COUNTDOWN_COUNTER[0])));
					if (getConfiguration().showTitleCountdown()) {
						broadcastTitle("", WrappedEnumTitleAction.TITLE);
						broadcastTitle(Language.JOINED_ARENA_STATUS_GAME_STARTS_IN.toString().replace("%seconds%", String.valueOf(COUNTDOWN_COUNTER[0])), WrappedEnumTitleAction.SUBTITLE);
					}
					for (UUID playerUUID : PLAYERS) {
						Player player = Bukkit.getPlayer(playerUUID);
						player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
					}
				}
				
				updateSigns(COUNTDOWN_COUNTER[0]);
				COUNTDOWN_COUNTER[0]--;
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
	
	/**
	 * (Re-)sets the scoreboard.
	 */
	public void setScoreboard(Player player) {
		SCOREBOARD_CONFIGURATION.setScores(player, this);
	}
	
	/**
	 * Sets the countdown.
	 */
	public void setCountdown(int countdown, boolean startIfPossible) {
		COUNTDOWN_COUNTER[0] = countdown;
		if (!countdownIsActive()) {
			if (PLAYERS.size() >= 2 && getStatus() != GameStatus.ACTIVE) {
				startCountdown(playersAreInLobby, countdown);
			}
		}
	}
	
	/**
	 * Tries to add player to the arena. May throw an exception if this is not possible.
	 * 
	 * @param player The player to add.
	 */
	public void join(Player player) throws SpleefArenaNotWaitingForPlayersException, SpleefArenaIsFullException, SpleefArenaIsDisabledException, SpleefArenaMisconfiguredException {
		Validator.validateNotNull(player, "player");
		
		if (PLAYERS.contains(player.getUniqueId())) {	// a player can not join a arena twice. (YOU DONT SAY?!?)
			throw new IllegalArgumentException("The player " + player.getName() + " has already joined the arena");
		}
		
		if (status != GameStatus.WAITING_FOR_PLAYERS) {	// the player cannot join at the moment
			throw new SpleefArenaNotWaitingForPlayersException("The arena is not waiting for players to join");
		}
		
		if (SPAWNLOCATIONS.size() <= PLAYERS.size()) {
			throw new SpleefArenaIsFullException("The arena is full");
		}
		
		if (getConfiguration().isDisabled()) {
			throw new SpleefArenaIsDisabledException("The arena is disabled");
		}
		
		if (BLOCKS.isEmpty()) {
			throw new SpleefArenaMisconfiguredException("The arena has no blocks");
		}
		
		if (SPAWNLOCATIONS.size() < 2) {
			throw new SpleefArenaMisconfiguredException("The arena has less than 2 spawnlocations");
		}
		
		if (getConfiguration().getRequiredPlayersToStartCountdown() < 2) {
			throw new SpleefArenaMisconfiguredException("The required players to start can't be less than 2");
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
		
		updateSigns(-1);
	}
	
	/**
	 * Stops the game immediately.
	 */
	public void stopImmediately(GameStopReason reason) {
		Validator.validateNotNull(reason, "reason");
		for (UUID uuidPlayer : PLAYERS) {
			Player player = Bukkit.getPlayer(uuidPlayer);
			GravityModifier.resetGravity(uuidPlayer);
			PlayerMemory memory = MEMORIES.get(uuidPlayer);
			memory.restore(player);
			switch (reason) {
				case EDIT_ARENA:
					player.sendMessage(Language.STOP_REASON_EDIT_ARENA.toString());
					break;
				case PLUGIN_DISABLED:
					player.sendMessage(Language.STOP_REASON_PLUGIN_DISABLED.toString());
					break;	
				default:
					// should never happen
					player.sendMessage(ChatColor.RED + "Something strange happend! :(");
					break;
				}
		}
		for (UUID uuidPlayer : SPECTATORS) {
			Player player = Bukkit.getPlayer(uuidPlayer);
			GravityModifier.resetGravity(uuidPlayer);
			PlayerMemory memory = MEMORIES.get(uuidPlayer);
			memory.restore(player);
			switch (reason) {
				case EDIT_ARENA:
					player.sendMessage(Language.STOP_REASON_EDIT_ARENA.toString());
					break;
				case PLUGIN_DISABLED:
					player.sendMessage(Language.STOP_REASON_PLUGIN_DISABLED.toString());
					break;	
				default:
					// should never happen
					player.sendMessage(ChatColor.RED + "Something strange happend! :(");
					break;
			}
		}
		PLAYERS.clear();
		SPECTATORS.clear();
		FREE_SPAWN_LOCATIONS.clear();
		FREE_SPAWN_LOCATIONS.addAll(SPAWNLOCATIONS);
		USED_SPAWN_LOCATION.clear();
		MEMORIES.clear();
		fillArena(Material.SNOW_BLOCK, (byte) 0);
		status = GameStatus.WAITING_FOR_PLAYERS;
		playersAreInLobby = CONFIGURATION.getLobby() != null;
		updateSigns(-1);
	}
	
	/**
	 * Removes a player from the arena.
	 */
	public void removePlayer(Player player) {
		removePlayer(player, false);
	}
	
	/**
	 * Removes a player from the arena.
	 */
	public void removePlayer(Player player, boolean spectate) {
		Validator.validateNotNull(player, "player");

		boolean isSpectator = SPECTATORS.contains(player.getUniqueId());
		if (isSpectator) {
			spectate = false;	// a spectator can't get a spectator...
		}
		if (PLAYERS.size() == 2) {
			spectate = false;
		}
		
		if (CONFIGURATION.getSpectateLocation() == null && spectate) {
			switch (CONFIGURATION.getSpectateType()) {
			case NORMAL:
				spectate = false;
				break;
			case NORMAL_FLYING:
				spectate = false;
				break;
			case GAMEMODE_3:
				// you don't hinder anyone in gamemode 3
				break;
			case NONE:
				spectate = false;
				break;
			}
		}
		
		if (!PLAYERS.contains(player.getUniqueId()) && !isSpectator) {
			throw new IllegalArgumentException("The player " + player.getName() + " is not in the arena");
		}		
		
		PlayerDismountCheckTask.removePlayer(player);
		
		if (player.getVehicle() != null) {
			player.getVehicle().remove();
		}
		
		if (!isSpectator) {
			SpleefSpawnLocation spawnLoc = USED_SPAWN_LOCATION.get(player.getUniqueId());
			USED_SPAWN_LOCATION.remove(player.getUniqueId());
			FREE_SPAWN_LOCATIONS.add(spawnLoc);
			PLAYERS.remove(player.getUniqueId());
		} else {
			SPECTATORS.remove(player.getUniqueId());
		}
		
		if (!spectate) {
			PlayerMemory memory = MEMORIES.get(player.getUniqueId());
			MEMORIES.remove(player.getUniqueId());			
			memory.restore(player);			
		} else {
			if (CONFIGURATION.getSpectateLocation() != null) {
				player.teleport(CONFIGURATION.getSpectateLocation());
			}
			SPECTATORS.add(player.getUniqueId());
			if (CONFIGURATION.getSpectateType() == SpectateType.GAMEMODE_3) {
				try {
					player.setGameMode(GameMode.SPECTATOR);
				} catch (Exception e) {
					SpleefMain.getInstance().log(Level.SEVERE, "The spectate type of arena " + getName() + " is set to gamemode 3 but the server doesn't support gamemode 3! (older version than MC 1.8)");
					// older versions of minecraft don't have a spectator mode :(
				}
			} else {
				player.setGameMode(GameMode.ADVENTURE);
			}
			if (CONFIGURATION.getSpectateType() == SpectateType.NORMAL_FLYING) {
				player.setAllowFlight(true);
			}
			player.getInventory().clear();
			player.getInventory().setArmorContents(new ItemStack[4]);
		}
		
		GravityModifier.resetGravity(player.getUniqueId());
		
		if (countdownIsActive() && playersAreInLobby() && !isSpectator) {
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
		
		if (countdownIsActive() && !playersAreInLobby() && getConfiguration().getLobby() != null && !isSpectator) {
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
			if (countdownIsActive() && PLAYERS.size() < CONFIGURATION.getMinPlayers() && getConfiguration().getLobby() == null && !isSpectator) {				
				Bukkit.getScheduler().cancelTask(countdownId);
				countdownId = 0;
				countdownIsActive = false;
			}
		}
		
		if (!isSpectator) {
			updateSigns(-1);
		}
		
		if (getStatus() == GameStatus.ACTIVE && !isSpectator) {
			
			// add loss to the playerstats
			SpleefPlayer.getPlayer(player.getUniqueId(), new FutureCallback<SpleefPlayer>() {
				@Override
				public void onFailure(Throwable e) {
					e.printStackTrace();
				}

				@Override
				public void onSuccess(SpleefPlayer stats) {
					stats.addLosses(getName(), 1);
					stats.addPoints(getName(), CONFIGURATION.getPointsParticipationReward());
				}
			});
			
			if (PluginChecker.vaultIsLoaded()) {
				Economy eco = VaultHook.getEconomy();
				if (eco != null) {
					eco.depositPlayer(player, CONFIGURATION.getMoneyParticipationReward());					
				}
			}
			
			for (UUID uuidPlayer : PLAYERS) {
				Bukkit.getPlayer(uuidPlayer).sendMessage(Language.PLAYER_ELIMINATED.toString().replace("%player%", player.getName()).replace("%prefix%", Language.PREFIX.toString()));
			}
			
			for (UUID uuidPlayer : SPECTATORS) {
				Bukkit.getPlayer(uuidPlayer).sendMessage(Language.PLAYER_ELIMINATED.toString().replace("%player%", player.getName()).replace("%prefix%", Language.PREFIX.toString()));
			}
			
			if (PLAYERS.size() == 1) {
				UUID winnerUUID = PLAYERS.get(0);
				Player winner = Bukkit.getPlayer(winnerUUID);
				
				PlayerDismountCheckTask.removePlayer(winner);
				
				if (winner.getVehicle() != null) {
					winner.getVehicle().remove();
				}
				
				for (UUID uuidPlayer : SPECTATORS) {
					if (Bukkit.getPlayer(uuidPlayer).getVehicle() != null) {
						Bukkit.getPlayer(uuidPlayer).getVehicle().remove();
					}
				}
				
				PlayerMemory winnerMemory = MEMORIES.get(winnerUUID);
				winnerMemory.restore(winner);
				
				for (UUID uuidPlayer : SPECTATORS) {
					PlayerMemory spectatorMemory = MEMORIES.get(uuidPlayer);
					spectatorMemory.restore(Bukkit.getPlayer(uuidPlayer));
				}
				
				player.sendMessage(Language.PLAYER_WON_GAME.toString().replace("%player%", winner.getName()));
				for (UUID uuidPlayer : SPECTATORS) {
					Bukkit.getPlayer(uuidPlayer).sendMessage(Language.PLAYER_WON_GAME.toString().replace("%player%", winner.getName()));
				}
				winner.sendMessage(Language.PLAYER_WHO_WON.toString().replace("%player%", winner.getName()));
				
				// add win to the playerstats
				SpleefPlayer.getPlayer(winnerUUID, new FutureCallback<SpleefPlayer>() {
					@Override
					public void onFailure(Throwable e) {
						e.printStackTrace();
					}

					@Override
					public void onSuccess(SpleefPlayer stats) {
						stats.addWins(getName(), 1);
						stats.addPoints(getName(), CONFIGURATION.getPointsWinningReward());
						// TODO add points
					}
				});
				
				if (PluginChecker.vaultIsLoaded()) {
					Economy eco = VaultHook.getEconomy();
					if (eco != null) {
						eco.depositPlayer(winner, CONFIGURATION.getMoneyWinningReward());
					}
				}				
				
				// reset arena
				PLAYERS.clear();
				SPECTATORS.clear();;
				FREE_SPAWN_LOCATIONS.clear();
				FREE_SPAWN_LOCATIONS.addAll(SPAWNLOCATIONS);
				USED_SPAWN_LOCATION.clear();
				MEMORIES.clear();
				fillArena(Material.SNOW_BLOCK, (byte) 0);
				status = GameStatus.WAITING_FOR_PLAYERS;
				playersAreInLobby = CONFIGURATION.getLobby() != null;
				GravityModifier.resetGravity(winner.getUniqueId());
				
				updateSigns(-1);
			}
		}
	}
	
	/**
	 * Called if the player falls under the lowest block.
	 */
	public void onLose(Player player) {
		if (PLAYERS.contains(player.getUniqueId())) {
			removePlayer(player, CONFIGURATION.getSpectateType() != SpectateType.NONE);
		} else {
			// is spectator -> Spectators can't lose...
		}
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
	
	/**
	 * Adds a join sign.
	 */
	public boolean addJoinSign(Block block) {
		if (!(block.getState() instanceof Sign)) {
			return false;
		}
		
		for (SimpleBlock simpleBlock : JOIN_SIGNS) {
			if (simpleBlock.equalsRealBlock(block)) {
				return false;
			}
		}
		
		JOIN_SIGNS.add(new SimpleBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ()));
		updateSigns(-1);	// could be -1, no one cares
		return true;
	}
	
	/**
	 * Checks if a block is a join sign.
	 */
	public boolean isJoinSign(Block block) {
		for (SimpleBlock signBlock : JOIN_SIGNS) {
			if (signBlock.toBlock() != null && signBlock.toBlock().equals(block)) {
				return true;
			}
		}
		return false;
	}
	
	private void updateSigns(int countdown) {
		for (SimpleBlock simpleBlock : JOIN_SIGNS) {
			if (!(simpleBlock.toBlock().getState() instanceof Sign)) {
				continue;
			}
			Sign sign = (Sign) simpleBlock.toBlock().getState();
			String[] lines = new String[4];
			String type = "none";
			
			if (getStatus() == GameStatus.ACTIVE) {				
				type = "inProgress";				
			}
			if (getStatus() == GameStatus.WAITING_FOR_PLAYERS) {
				if (PLAYERS.size() >= SPAWNLOCATIONS.size()) {
					type = "full";
				} else {
					type = "waitingForPlayers";
				}
			}
			if (getStatus() == GameStatus.DISABLED) {
				type = "disabled";
			}
			
			
			lines[0] = SpleefMain.getInstance().getConfig().getString("joinsigns." + type + ".lines.1");
			lines[1] = SpleefMain.getInstance().getConfig().getString("joinsigns." + type + ".lines.2");
			lines[2] = SpleefMain.getInstance().getConfig().getString("joinsigns." + type + ".lines.3");
			lines[3] = SpleefMain.getInstance().getConfig().getString("joinsigns." + type + ".lines.4");	
			for (int i = 0; i < lines.length; i++) {
				if (lines[i] == null) {
					continue;
				}
				String line = lines[i];
				line = ChatColor.translateAlternateColorCodes('&', line);
				line = line.replace("%arena%", this.getName());
				line = line.replace("%players%", Integer.toString(PLAYERS.size()));
				line = line.replace("%maxPlayers%", Integer.toString(SPAWNLOCATIONS.size()));
				line = line.replace("%countdown%", Integer.toString(countdown));
				sign.setLine(i, line.length() <= 16 ? line : line.substring(0, 16));
			}	
			sign.update();
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
	
	/**
	 * Gets the spectators in the arena.
	 */
	public ArrayList<UUID> getSpectators() {
		return new ArrayList<UUID>(SPECTATORS);
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
	 * Gets the join signs of arena.
	 */
	public ArrayList<SimpleBlock> getJoinSigns() {
		ArrayList<SimpleBlock> blocks = new ArrayList<>();
		for (SimpleBlock block : JOIN_SIGNS) {
			blocks.add(new SimpleBlock(block.getWorldName(), block.getX(), block.getY(), block.getZ()));
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
	 * Deletes the arena.
	 * 
	 * @param deleteStats Wheter the stats should be deleted or not.
	 */
	public void delete(boolean deleteStats) {	
		stopImmediately(GameStopReason.EDIT_ARENA);
		StorageManager.getInstance().deleteConfig(this);
		if (deleteStats) {
			StorageManager.getInstance().deleteTable(getName());
		}
		ARENAS.remove(this);
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
