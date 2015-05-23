package de.oppermann.bastian.spleef.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Sets;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.SpectateType;
import de.oppermann.bastian.spleef.util.SpleefMode;
import de.oppermann.bastian.spleef.util.command.AbstractArgument;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandHelp;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandResult;

public class SetValueArgument extends AbstractArgument {

	/**
	 * Class constructor.
	 */
	public SetValueArgument() {
		super(new String[]{Language.COMMAND_SETVALUE.toString()}, -1, "spleef.setvalue", null, Language.COMMAND_SETVALUE_DESCRIPTION.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#executeForPlayer(org.bukkit.entity.Player, org.bukkit.command.Command, java.lang.String[])
	 */
	@Override
	public CommandResult executeForPlayer(Player player, Command cmd, String[] args) {
		if (args.length == 4) {
			
			if (!player.hasPermission(getPermission())) {
				return CommandResult.NO_PERMISSION;
			}
			
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
			
			if (args[2].equalsIgnoreCase(Language.FLAG_ENABLED.toString())) {
				boolean enabled = args[3].equalsIgnoreCase(Language.VALUE_TRUE.toString());
				String strValue = enabled ? Language.VALUE_TRUE.toString() : Language.VALUE_FALSE.toString();
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_ENABLED.toString()).replace("%value%", strValue));
				
				arena.getConfiguration().setDisabled(!enabled);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("enabled", enabled);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_SNOWBALLS_ENABLED.toString())) {
				boolean enabled = args[3].equalsIgnoreCase(Language.VALUE_TRUE.toString());
				String strValue = enabled ? Language.VALUE_TRUE.toString() : Language.VALUE_FALSE.toString();
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_ENABLED.toString()).replace("%value%", strValue));
				
				arena.getConfiguration().setEnableSnowballs(enabled);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("snowballs.enabled", enabled);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_MAX_SNOWBALLS.toString())) {
				int maxSnowballs;
				try {
					maxSnowballs = Integer.valueOf(args[3]);
				} catch (NumberFormatException e) {
					player.sendMessage(Language.VALUE_MUST_BE_A_NUMBER.toString());
					return CommandResult.SUCCESS;
				}				
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_ENABLED.toString()).replace("%value%", String.valueOf(maxSnowballs)));
				
				arena.getConfiguration().setMaxSnowballs(maxSnowballs);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("snowballs.maxSnowballs", maxSnowballs);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_MODE.toString())) {
				if (args[3].equalsIgnoreCase(Language.VALUE_MODE_NORMAL.toString())) {
					arena.getConfiguration().setMode(SpleefMode.NORMAL);
					player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_MODE.toString()).replace("%value%", Language.VALUE_MODE_NORMAL.toString()));
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("mode", arena.getConfiguration().getMode().name().toLowerCase());
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
					return CommandResult.SUCCESS;
				}
				if (args[3].equalsIgnoreCase(Language.VALUE_MODE_BOWSPLEEF.toString())) {
					arena.getConfiguration().setMode(SpleefMode.BOWSPLEEF);
					player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_MODE.toString()).replace("%value%", Language.VALUE_MODE_BOWSPLEEF.toString()));
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("mode", arena.getConfiguration().getMode().name().toLowerCase());
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
					return CommandResult.SUCCESS;
				}
				if (args[3].equalsIgnoreCase(Language.VALUE_MODE_SPLEGG.toString())) {
					arena.getConfiguration().setMode(SpleefMode.SPLEGG);
					player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_MODE.toString()).replace("%value%", Language.VALUE_MODE_SPLEGG.toString()));
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("mode", arena.getConfiguration().getMode().name().toLowerCase());
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
					return CommandResult.SUCCESS;
				}
				if (args[3].equalsIgnoreCase(Language.VALUE_MODE_SPLEEF_RUN.toString())) {
					arena.getConfiguration().setMode(SpleefMode.SPLEEF_RUN);
					player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_MODE.toString()).replace("%value%", Language.VALUE_MODE_SPLEEF_RUN.toString()));
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("mode", arena.getConfiguration().getMode().name().toLowerCase());
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
					return CommandResult.SUCCESS;
				}
				if (args[3].equalsIgnoreCase(Language.VALUE_MODE_SUPER_SPLEEF.toString())) {
					arena.getConfiguration().setMode(SpleefMode.SUPER_SPLEEF);
					player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_MODE.toString()).replace("%value%", Language.VALUE_MODE_SUPER_SPLEEF.toString()));
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("mode", arena.getConfiguration().getMode().name().toLowerCase());
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
					return CommandResult.SUCCESS;
				}
				if (args[3].equalsIgnoreCase(Language.VALUE_MODE_SUPER_SPLEGG.toString())) {
					arena.getConfiguration().setMode(SpleefMode.SUPER_SPLEGG);
					player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_MODE.toString()).replace("%value%", Language.VALUE_MODE_SUPER_SPLEGG.toString()));
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("mode", arena.getConfiguration().getMode().name().toLowerCase());
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
					return CommandResult.SUCCESS;
				}
				player.sendMessage(Language.UNKNOWN_MODE.toString());
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_CUSTOMINVENTORY_ENABLED.toString())) {
				boolean enabled = args[3].equalsIgnoreCase(Language.VALUE_TRUE.toString());
				String strValue = enabled ? Language.VALUE_TRUE.toString() : Language.VALUE_FALSE.toString();
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_CUSTOMINVENTORY_ENABLED.toString()).replace("%value%", strValue));
				
				arena.getConfiguration().setCustomInventory(enabled);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("customInventory.enabled", enabled);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_VEHICLE.toString())) {
				if (args[3].equalsIgnoreCase("none")) {
					arena.getConfiguration().setVehicle(null);
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("vehicle", "none");
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
					player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_VEHICLE.toString()).replace("%value%", "none"));			
					return CommandResult.SUCCESS;
				}
				EntityType type;
				try {
					type = EntityType.valueOf(args[3].toUpperCase());
				} catch (IllegalArgumentException e) {
					player.sendMessage(Language.UNKNOWN_ENTITY.toString());
					return CommandResult.SUCCESS;
				}
				if (!type.isSpawnable()) {
					player.sendMessage(Language.UNKNOWN_ENTITY.toString());
					return CommandResult.SUCCESS;
				}
				/* TODO
				if (!type.getEntityClass().isAssignableFrom(LivingEntity.class)) {
					player.sendMessage(Language.UNKNOWN_ENTITY.toString());
					return CommandResult.SUCCESS;
				}
				*/
				arena.getConfiguration().setVehicle(type);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("vehicle", type.name().toLowerCase());
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_VEHICLE.toString()).replace("%value%", type.name().toLowerCase()));			
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_INSTANT_BLOCK_DESTORY.toString())) {
				boolean enabled = args[3].equalsIgnoreCase(Language.VALUE_TRUE.toString());
				String strValue = enabled ? Language.VALUE_TRUE.toString() : Language.VALUE_FALSE.toString();
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_INSTANT_BLOCK_DESTORY.toString()).replace("%value%", strValue));
				
				arena.getConfiguration().setInstanstBlockDestroy(enabled);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("instantBlockDestroy", enabled);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_REWARD_MONEY_PARTICIPATION.toString())) {
				int reward;
				try {
					reward = Integer.valueOf(args[3]);
				} catch (NumberFormatException e) {
					player.sendMessage(Language.VALUE_MUST_BE_A_NUMBER.toString());
					return CommandResult.SUCCESS;
				}				
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_REWARD_MONEY_PARTICIPATION.toString()).replace("%value%", String.valueOf(reward)));
				
				arena.getConfiguration().setMoneyParticipationReward(reward);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("reward.money.participation", reward);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_REWARD_MONEY_WINNING.toString())) {
				int reward;
				try {
					reward = Integer.valueOf(args[3]);
				} catch (NumberFormatException e) {
					player.sendMessage(Language.VALUE_MUST_BE_A_NUMBER.toString());
					return CommandResult.SUCCESS;
				}				
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_REWARD_MONEY_WINNING.toString()).replace("%value%", String.valueOf(reward)));
				
				arena.getConfiguration().setMoneyWinningReward(reward);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("reward.money.winning", reward);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_REWARD_POINTS_PARTICIPATION.toString())) {
				int reward;
				try {
					reward = Integer.valueOf(args[3]);
				} catch (NumberFormatException e) {
					player.sendMessage(Language.VALUE_MUST_BE_A_NUMBER.toString());
					return CommandResult.SUCCESS;
				}				
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_REWARD_POINTS_PARTICIPATION.toString()).replace("%value%", String.valueOf(reward)));
				
				arena.getConfiguration().setPointsParticipationReward(reward);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("reward.points.participation", reward);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_REWARD_POINTS_WINNING.toString())) {
				int reward;
				try {
					reward = Integer.valueOf(args[3]);
				} catch (NumberFormatException e) {
					player.sendMessage(Language.VALUE_MUST_BE_A_NUMBER.toString());
					return CommandResult.SUCCESS;
				}				
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_REWARD_POINTS_WINNING.toString()).replace("%value%", String.valueOf(reward)));
				
				arena.getConfiguration().setPointsWinningReward(reward);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("reward.points.winning", reward);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_FREEZE_PLAYERS.toString())) {
				boolean enabled = args[3].equalsIgnoreCase(Language.VALUE_TRUE.toString());
				String strValue = enabled ? Language.VALUE_TRUE.toString() : Language.VALUE_FALSE.toString();
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_FREEZE_PLAYERS.toString()).replace("%value%", strValue));
				
				arena.getConfiguration().setFreezePlayers(enabled);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("freezePlayers", enabled);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_MODIFY_GRAVITY.toString())) {
				boolean enabled = args[3].equalsIgnoreCase(Language.VALUE_TRUE.toString());
				String strValue = enabled ? Language.VALUE_TRUE.toString() : Language.VALUE_FALSE.toString();
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_MODIFY_GRAVITY.toString()).replace("%value%", strValue));
				
				arena.getConfiguration().setModifyGravity(enabled);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("modifygravity.enable", enabled);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_GRAVITY.toString())) {
				double gravity;
				try {
					gravity = Double.valueOf(args[3]);
				} catch (NumberFormatException e) {
					player.sendMessage(Language.VALUE_MUST_BE_A_NUMBER_BETWEEN_0_AND_1.toString());
					return CommandResult.SUCCESS;
				}
				if (gravity <= 0 || gravity >= 1) {
					player.sendMessage(Language.VALUE_MUST_BE_A_NUMBER_BETWEEN_0_AND_1.toString());
					return CommandResult.SUCCESS;
				}
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_GRAVITY.toString()).replace("%value%", String.valueOf(gravity)));
				
				arena.getConfiguration().setGravity(gravity);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("modifygravity.gravity", gravity);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_MIN_PLAYERS.toString())) {
				int minPlayers;
				try {
					minPlayers = Integer.valueOf(args[3]);
				} catch (NumberFormatException e) {
					player.sendMessage(Language.VALUE_MUST_BE_A_NUMBER.toString());
					return CommandResult.SUCCESS;
				}
				
				minPlayers = minPlayers < 2 ? 2 : minPlayers;	// must be at least 2
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_MIN_PLAYERS.toString()).replace("%value%", String.valueOf(minPlayers)));
				
				arena.getConfiguration().setMinPlayers(minPlayers);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("minPlayers", minPlayers);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_REQUIRED_PLAYERS_TO_START_COUNTDOWN.toString())) {
				int requiredPlayersToStartCountdown;
				try {
					requiredPlayersToStartCountdown = Integer.valueOf(args[3]);
				} catch (NumberFormatException e) {
					player.sendMessage(Language.VALUE_MUST_BE_A_NUMBER.toString());
					return CommandResult.SUCCESS;
				}
				
				requiredPlayersToStartCountdown = requiredPlayersToStartCountdown < 2 ? 2 : requiredPlayersToStartCountdown;	// must be at least 2
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_REQUIRED_PLAYERS_TO_START_COUNTDOWN.toString()).replace("%value%", String.valueOf(requiredPlayersToStartCountdown)));
				
				arena.getConfiguration().setMinPlayers(requiredPlayersToStartCountdown);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("requiredPlayersToStartCountdown", requiredPlayersToStartCountdown);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_SPECTATE_TYPE.toString())) {
				
				SpectateType spectateType = SpectateType.NONE;
				String spectateTypeName = Language.SPECTATE_TYPE_NONE.toString();
				if (args[3].equalsIgnoreCase(Language.SPECTATE_TYPE_NORMAL.toString())) {
					spectateType = SpectateType.NORMAL;
					spectateTypeName = Language.SPECTATE_TYPE_NORMAL.toString();
				}
				if (args[3].equalsIgnoreCase(Language.SPECTATE_TYPE_NORMAL_FLYING.toString())) {
					spectateType = SpectateType.NORMAL_FLYING;
					spectateTypeName = Language.SPECTATE_TYPE_NORMAL_FLYING.toString();
				}
				if (args[3].equalsIgnoreCase(Language.SPECTATE_TYPE_GAMEMODE_3.toString())) {
					spectateType = SpectateType.GAMEMODE_3;
					spectateTypeName = Language.SPECTATE_TYPE_GAMEMODE_3.toString();
				}
				player.sendMessage(Language.SUCCESSFULLY_SET_VALUE.toString().replace("%flag%", Language.FLAG_SPECTATE_TYPE.toString()).replace("%value%", spectateTypeName));
				
				arena.getConfiguration().setSpectateType(spectateType);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("spectateType", spectateType.name().toLowerCase());
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				return CommandResult.SUCCESS;
			}
			
			player.sendMessage(Language.UNKNOWN_FLAG.toString().replace("%flag%", args[2]));
			return CommandResult.SUCCESS;
		}
		
		// flags without arguments
		if (args.length == 3) {
			if (!player.hasPermission(getPermission())) {
				return CommandResult.NO_PERMISSION;
			}
			
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
			
			
			if (args[2].equalsIgnoreCase(Language.FLAG_CUSTOMINVENTORY.toString())) {				
				ItemStack[] contents = player.getInventory().getContents();
				for (int i = 0; i < contents.length; i++) {					
					SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("customInventory.items." + i, contents[i]);					
				}
				
				arena.getConfiguration().setCustomInventoryContents(contents);
				arena.getConfiguration().setCustomInventory(true);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("customInventory.enabled", true);
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				player.sendMessage(Language.SUCCESSFULLY_SET_CUSTOMINVENTORY.toString());
				return CommandResult.SUCCESS;
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_SPECTATE_LOCATION.toString())) {
				arena.getConfiguration().setSpectateLocation(player.getLocation());
				
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("spectateLocation.x", player.getLocation().getX());
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("spectateLocation.y", player.getLocation().getY());
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("spectateLocation.z", player.getLocation().getZ());
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("spectateLocation.yaw", player.getLocation().getYaw());
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).getConfig().set("spectateLocation.pitch", player.getLocation().getPitch());
				SpleefMain.getInstance().getArenaAccessor(arena.getName()).saveConfig();
				player.sendMessage(Language.SUCCESSFULLY_SET_SPECTATE_LOCATION.toString());
				return CommandResult.SUCCESS;
			}
		}
		return CommandResult.ERROR;
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
			list.add(Language.COMMAND_SETVALUE.toString());
		}
		if (args.length == 2) {
			list.addAll(Sets.newHashSet(SpleefArena.getArenaNames()));
		}
		if (args.length == 3) {
			list.add(Language.FLAG_ENABLED.toString());
			list.add(Language.FLAG_SNOWBALLS_ENABLED.toString());
			list.add(Language.FLAG_MAX_SNOWBALLS.toString());
			list.add(Language.FLAG_MODE.toString());
			list.add(Language.FLAG_CUSTOMINVENTORY.toString());
			list.add(Language.FLAG_CUSTOMINVENTORY_ENABLED.toString());
			list.add(Language.FLAG_VEHICLE.toString());
			list.add(Language.FLAG_INSTANT_BLOCK_DESTORY.toString());
			list.add(Language.FLAG_REWARD_MONEY_PARTICIPATION.toString());
			list.add(Language.FLAG_REWARD_MONEY_WINNING.toString());
			list.add(Language.FLAG_REWARD_POINTS_PARTICIPATION.toString());
			list.add(Language.FLAG_REWARD_POINTS_WINNING.toString());
			list.add(Language.FLAG_FREEZE_PLAYERS.toString());
			list.add(Language.FLAG_MODIFY_GRAVITY.toString());
			list.add(Language.FLAG_GRAVITY.toString());
			list.add(Language.FLAG_MIN_PLAYERS.toString());
			list.add(Language.FLAG_REQUIRED_PLAYERS_TO_START_COUNTDOWN.toString());
			list.add(Language.FLAG_SPECTATE_TYPE.toString());
			list.add(Language.FLAG_SPECTATE_LOCATION.toString());
		}
		if (args.length == 4) {
			if (args[2].equalsIgnoreCase(Language.FLAG_ENABLED.toString())
				|| args[2].equalsIgnoreCase(Language.FLAG_SNOWBALLS_ENABLED.toString())
				|| args[2].equalsIgnoreCase(Language.FLAG_CUSTOMINVENTORY_ENABLED.toString())
				|| args[2].equalsIgnoreCase(Language.FLAG_INSTANT_BLOCK_DESTORY.toString())
				|| args[2].equalsIgnoreCase(Language.FLAG_FREEZE_PLAYERS.toString())
				|| args[2].equalsIgnoreCase(Language.FLAG_MODIFY_GRAVITY.toString()))
			{
				list.add(Language.VALUE_TRUE.toString());
				list.add(Language.VALUE_FALSE.toString());
			}
			if (args[2].equalsIgnoreCase(Language.FLAG_MAX_SNOWBALLS.toString())) {
				list.add("0");
				list.add("1");
				list.add("5");
				list.add("16");
				list.add("32");
				list.add("64");
			}
			if (args[2].equalsIgnoreCase(Language.FLAG_MODE.toString())) {
				list.add(Language.VALUE_MODE_NORMAL.toString());
				list.add(Language.VALUE_MODE_BOWSPLEEF.toString());
				list.add(Language.VALUE_MODE_SPLEGG.toString());
				list.add(Language.VALUE_MODE_SPLEEF_RUN.toString());
				//list.add(Language.VALUE_MODE_SUPER_SPLEEF.toString()); // TODO enable super gamemodes
				//list.add(Language.VALUE_MODE_SUPER_SPLEGG.toString()); // TODO enable super gamemodes
			}
			if (args[2].equalsIgnoreCase(Language.FLAG_VEHICLE.toString())) {
				try {
					list.add(EntityType.RABBIT.name().toLowerCase());
				} catch (Exception e) {
					// older versions of minecraft don't have rabbits :(
					// A life without rabbits is not livable!
				}
				list.add(EntityType.VILLAGER.name().toLowerCase());
				list.add(EntityType.PIG.name().toLowerCase());
				list.add(EntityType.OCELOT.name().toLowerCase());
				list.add(EntityType.SHEEP.name().toLowerCase());
				list.add("none");				
			}
			if (args[2].equalsIgnoreCase(Language.FLAG_REWARD_MONEY_PARTICIPATION.toString())
				|| args[2].equalsIgnoreCase(Language.FLAG_REWARD_MONEY_WINNING.toString())
				|| args[2].equalsIgnoreCase(Language.FLAG_REWARD_POINTS_PARTICIPATION.toString())
				|| args[2].equalsIgnoreCase(Language.FLAG_REWARD_POINTS_WINNING.toString()))
			{
				list.add("0");
				list.add("10");
				list.add("25");
				list.add("50");
				list.add("100");
				list.add("150");
				list.add("200");
				list.add("500");				
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_GRAVITY.toString())) {
				list.add("0.25");
				list.add("0.5");
				list.add("0.75");
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_MIN_PLAYERS.toString())
				|| args[2].equalsIgnoreCase(Language.FLAG_REQUIRED_PLAYERS_TO_START_COUNTDOWN.toString()))
			{
				list.add("2");
				list.add("4");
				list.add("8");
				list.add("12");
				list.add("16");
			}
			
			if (args[2].equalsIgnoreCase(Language.FLAG_SPECTATE_TYPE.toString())) {
				list.add(Language.SPECTATE_TYPE_NONE.toString());
				list.add(Language.SPECTATE_TYPE_NORMAL.toString());
				list.add(Language.SPECTATE_TYPE_NORMAL_FLYING.toString());
				list.add(Language.SPECTATE_TYPE_GAMEMODE_3.toString());
			}
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#getCommandHelp()
	 */
	@Override
	public CommandHelp getCommandHelp() {
		return new CommandHelp("/%cmd% " + Language.COMMAND_SETVALUE + " " + Language.ARGUMENT_ARENA + " " + Language.ARGUMENT_FLAG + " " + Language.ARGUMENT_VALUE_OPTIONAL, getDescription());
	}

}
