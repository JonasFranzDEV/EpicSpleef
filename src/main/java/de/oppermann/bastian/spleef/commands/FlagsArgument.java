package de.oppermann.bastian.spleef.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.SpleefArenaConfiguration;
import de.oppermann.bastian.spleef.util.command.AbstractArgument;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandHelp;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandResult;

public class FlagsArgument extends AbstractArgument {

	private static final SpleefArenaConfiguration DEFAULT_CONFIGURATION = new SpleefArenaConfiguration();
	
	/**
	 * Class constructor.
	 */
	public FlagsArgument() {
		super(new String[]{Language.COMMAND_FLAGS.toString()}, -1, "spleef.flags", null, Language.COMMAND_FLAGS_DESCRIPTION.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#executeForPlayer(org.bukkit.entity.Player, org.bukkit.command.Command, java.lang.String[])
	 */
	@Override
	public CommandResult executeForPlayer(Player player, Command cmd, String[] args) {
		if (args.length > 2 || args.length < 1) {	// only 1 or 2 arguments are allowed
			return CommandResult.ERROR;
		}
		
		if (!player.hasPermission(getPermission())) {
			return CommandResult.NO_PERMISSION;
		}
		
		if (args.length == 1) {
			player.sendMessage(Language.FLAGS_LIST_HEAD.toString());
			sendValue(player, Language.FLAG_CUSTOMINVENTORY, "/");
			sendValue(player, Language.FLAG_CUSTOMINVENTORY_ENABLED, String.valueOf(DEFAULT_CONFIGURATION.hasCustomInventory()));
			sendValue(player, Language.FLAG_ENABLED, String.valueOf(!DEFAULT_CONFIGURATION.isDisabled()));
			sendValue(player, Language.FLAG_FREEZE_PLAYERS, String.valueOf(DEFAULT_CONFIGURATION.freezePlayers()));
			sendValue(player, Language.FLAG_MODIFY_GRAVITY, String.valueOf(DEFAULT_CONFIGURATION.modifyGravity()));
			sendValue(player, Language.FLAG_GRAVITY, String.valueOf(DEFAULT_CONFIGURATION.getGravity()));
			sendValue(player, Language.FLAG_INSTANT_BLOCK_DESTORY, String.valueOf(DEFAULT_CONFIGURATION.instanstBlockDestroy()));
			sendValue(player, Language.FLAG_SNOWBALLS_ENABLED, String.valueOf(DEFAULT_CONFIGURATION.isEnableSnowballs()));
			sendValue(player, Language.FLAG_MAX_SNOWBALLS, String.valueOf(DEFAULT_CONFIGURATION.getMaxSnowballs()));
			sendValue(player, Language.FLAG_MIN_PLAYERS, String.valueOf(DEFAULT_CONFIGURATION.getMinPlayers()));
			sendValue(player, Language.FLAG_MODE, DEFAULT_CONFIGURATION.getMode().getLanguageName());
			sendValue(player, Language.FLAG_REQUIRED_PLAYERS_TO_START_COUNTDOWN, String.valueOf(DEFAULT_CONFIGURATION.getRequiredPlayersToStartCountdown()));
			sendValue(player, Language.FLAG_REWARD_MONEY_PARTICIPATION, String.valueOf(DEFAULT_CONFIGURATION.getMoneyParticipationReward()));
			sendValue(player, Language.FLAG_REWARD_MONEY_WINNING, String.valueOf(DEFAULT_CONFIGURATION.getMoneyWinningReward()));
			sendValue(player, Language.FLAG_REWARD_POINTS_PARTICIPATION, String.valueOf(DEFAULT_CONFIGURATION.getPointsParticipationReward()));
			sendValue(player, Language.FLAG_REWARD_POINTS_WINNING, String.valueOf(DEFAULT_CONFIGURATION.getPointsWinningReward()));
			sendValue(player, Language.FLAG_SPECTATE_LOCATION, locToString(DEFAULT_CONFIGURATION.getSpectateLocation()));
			sendValue(player, Language.FLAG_SPECTATE_TYPE, DEFAULT_CONFIGURATION.getSpectateType().getLanguageName());
			sendValue(player, Language.FLAG_VEHICLE, DEFAULT_CONFIGURATION.getVehicle() == null ? "/" : DEFAULT_CONFIGURATION.getVehicle().name().toLowerCase());
			player.sendMessage(Language.FLAGS_LIST_BOTTOM.toString());
			return CommandResult.SUCCESS;
		}
		if (args.length == 2) {
			SpleefArena arena = null;
			for (SpleefArena sArena : SpleefArena.getSpleefArenas()) {
				if (sArena.getName().equals(args[1])) {
					arena = sArena;
					break;
				}
			}
			
			if (arena == null) {
				player.sendMessage(Language.NO_ARENA_WITH_NAME.toString().replace("%arena%", args[1]));
				return CommandResult.SUCCESS;
			}
			
			SpleefArenaConfiguration configuration = arena.getConfiguration();
			
			player.sendMessage(Language.FLAGS_LIST_HEAD.toString());
			sendValue(player, Language.FLAG_CUSTOMINVENTORY, "/", "/");
			sendValue(player, Language.FLAG_CUSTOMINVENTORY_ENABLED, String.valueOf(DEFAULT_CONFIGURATION.hasCustomInventory()), String.valueOf(configuration.hasCustomInventory()));
			sendValue(player, Language.FLAG_ENABLED, String.valueOf(!DEFAULT_CONFIGURATION.isDisabled()), String.valueOf(!configuration.isDisabled()));
			sendValue(player, Language.FLAG_FREEZE_PLAYERS, String.valueOf(DEFAULT_CONFIGURATION.freezePlayers()), String.valueOf(configuration.freezePlayers()));
			sendValue(player, Language.FLAG_MODIFY_GRAVITY, String.valueOf(DEFAULT_CONFIGURATION.modifyGravity()), String.valueOf(configuration.modifyGravity()));
			sendValue(player, Language.FLAG_GRAVITY, String.valueOf(DEFAULT_CONFIGURATION.getGravity()), String.valueOf(configuration.getGravity()));
			sendValue(player, Language.FLAG_INSTANT_BLOCK_DESTORY, String.valueOf(DEFAULT_CONFIGURATION.instanstBlockDestroy()), String.valueOf(configuration.instanstBlockDestroy()));
			sendValue(player, Language.FLAG_SNOWBALLS_ENABLED, String.valueOf(DEFAULT_CONFIGURATION.isEnableSnowballs()), String.valueOf(configuration.isEnableSnowballs()));
			sendValue(player, Language.FLAG_MAX_SNOWBALLS, String.valueOf(DEFAULT_CONFIGURATION.getMaxSnowballs()), String.valueOf(configuration.getMaxSnowballs()));
			sendValue(player, Language.FLAG_MIN_PLAYERS, String.valueOf(DEFAULT_CONFIGURATION.getMinPlayers()), String.valueOf(configuration.getMinPlayers()));
			sendValue(player, Language.FLAG_MODE, DEFAULT_CONFIGURATION.getMode().getLanguageName(), configuration.getMode().getLanguageName());
			sendValue(player, Language.FLAG_REQUIRED_PLAYERS_TO_START_COUNTDOWN, String.valueOf(DEFAULT_CONFIGURATION.getRequiredPlayersToStartCountdown()), String.valueOf(configuration.getRequiredPlayersToStartCountdown()));
			sendValue(player, Language.FLAG_REWARD_MONEY_PARTICIPATION, String.valueOf(DEFAULT_CONFIGURATION.getMoneyParticipationReward()), String.valueOf(configuration.getMoneyParticipationReward()));
			sendValue(player, Language.FLAG_REWARD_MONEY_WINNING, String.valueOf(DEFAULT_CONFIGURATION.getMoneyWinningReward()), String.valueOf(configuration.getMoneyWinningReward()));
			sendValue(player, Language.FLAG_REWARD_POINTS_PARTICIPATION, String.valueOf(DEFAULT_CONFIGURATION.getPointsParticipationReward()), String.valueOf(configuration.getPointsParticipationReward()));
			sendValue(player, Language.FLAG_REWARD_POINTS_WINNING, String.valueOf(DEFAULT_CONFIGURATION.getPointsWinningReward()), String.valueOf(configuration.getPointsWinningReward()));
			sendValue(player, Language.FLAG_SPECTATE_LOCATION, locToString(DEFAULT_CONFIGURATION.getSpectateLocation()), locToString(configuration.getSpectateLocation()));
			sendValue(player, Language.FLAG_SPECTATE_TYPE, DEFAULT_CONFIGURATION.getSpectateType().getLanguageName(), configuration.getSpectateType().getLanguageName());
			sendValue(player, Language.FLAG_VEHICLE, DEFAULT_CONFIGURATION.getVehicle() == null ? "/" : DEFAULT_CONFIGURATION.getVehicle().name().toLowerCase(), configuration.getVehicle() == null ? "/" : configuration.getVehicle().name().toLowerCase());
			player.sendMessage(Language.FLAGS_LIST_BOTTOM.toString());
			return CommandResult.SUCCESS;
		}
		
		
		return CommandResult.ERROR;	// should never happen
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#executeForServer(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String[])
	 */
	@Override
	public CommandResult executeForServer(CommandSender sender, Command cmd, String[] args) {
		return CommandResult.ONLY_PLAYER; // the console can't join an arena
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#onTabComplete(org.bukkit.entity.Player, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(Player sender, String[] args) {	
		ArrayList<String> list = new ArrayList<>();
		if (args.length == 1) {
			list.add(Language.COMMAND_FLAGS.toString());
		}
		if (args.length == 2) {
			list.addAll(Sets.newHashSet(SpleefArena.getArenaNames()));
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#getCommandHelp()
	 */
	@Override
	public CommandHelp getCommandHelp() {
		return new CommandHelp("/%cmd% " + Language.COMMAND_FLAGS + " " + Language.ARGUMENT_ARENA_OPTIONAL, getDescription());
	}
	
	private String locToString(Location location) {
		if (location == null) {
			return "/";
		}
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		String world = location.getWorld().getName();
		return "(" + world + ", x: " + x + ", y: " + y + ", z: " + z + ")";		
	}
	
	private void sendValue(Player player, Language flag, String defaultValue) {
		sendValue(player, flag, defaultValue, null);
	}
	
	private void sendValue(Player player, Language flag, String defaultValue, String value) {
		String message;
		if (value == null) {
			message = Language.FLAGS_LIST_WITHOUT_VALUE.toString();
		} else {
			message = Language.FLAGS_LIST_WITH_VALUE.toString();
			message = message.replace("%value%", value);
		}
		message = message.replace("%flag%", flag.toString());
		message = message.replace("%defaultValue%", defaultValue);
		player.sendMessage(message);
	}

}
