package de.oppermann.bastian.spleef.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.Language;
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
			
			player.sendMessage(Language.UNKNOWN_FLAG.toString().replace("%flag%", args[2]));
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
			list.add(Language.COMMAND_SETVALUE.toString());
		}
		if (args.length == 2) {
			list.addAll(Sets.newHashSet(SpleefArena.getArenaNames()));
		}
		if (args.length == 3) {
			list.add(Language.FLAG_ENABLED.toString());
			list.add(Language.FLAG_SNOWBALLS_ENABLED.toString());
			list.add(Language.FLAG_MAX_SNOWBALLS.toString());
		}
		if (args.length == 4) {
			if (args[2].equalsIgnoreCase(Language.FLAG_ENABLED.toString()) || args[3].equalsIgnoreCase(Language.FLAG_SNOWBALLS_ENABLED.toString())) {
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
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#getCommandHelp()
	 */
	@Override
	public CommandHelp getCommandHelp() {
		return new CommandHelp("/%cmd% " + Language.COMMAND_SETVALUE + " " + Language.ARGUMENT_ARENA + " " + Language.ARGUMENT_FLAG + " " + Language.ARGUMENT_VALUE, getDescription());
	}

}
