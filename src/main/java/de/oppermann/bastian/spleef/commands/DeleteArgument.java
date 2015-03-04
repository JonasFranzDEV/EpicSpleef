package de.oppermann.bastian.spleef.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.GameStopReason;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.command.AbstractArgument;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandHelp;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandResult;

public class DeleteArgument extends AbstractArgument {

	/**
	 * Class constructor.
	 */
	public DeleteArgument() {
		super(new String[]{Language.COMMAND_DELETE.toString()}, -1, "spleef.delete", null, Language.COMMAND_DELETE_DESCRIPTION.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#executeForPlayer(org.bukkit.entity.Player, org.bukkit.command.Command, java.lang.String[])
	 */
	@Override
	public CommandResult executeForPlayer(Player player, Command cmd, String[] args) {
		if (args.length == 2 || args.length == 3 || args.length == 4) {	
			
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
			
			if (args.length <= 2 || !args[1].equals(args[2])) {
				player.sendMessage(Language.ARENAS_MUST_BE_THE_SAME.toString().replace("%arena%", args[1]));
				return CommandResult.SUCCESS;
			}
			
			if (arena == null) {
				player.sendMessage(Language.NO_ARENA_WITH_NAME.toString().replace("%arena%", args[1]));
				return CommandResult.SUCCESS;
			}
			
			boolean deleteStats = false;
			
			if (args.length == 4) {
				if (args[3].equalsIgnoreCase(Language.VALUE_TRUE.toString())) {
					deleteStats = true;
				}
			}			
			
			arena.stopImmediately(GameStopReason.EDIT_ARENA);
			arena.delete(deleteStats);
			player.sendMessage(Language.SUCCESSFULLY_DELETED_ARENA.toString().replace("%arena%", args[1]));
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
			list.add(Language.COMMAND_DELETE.toString());
		}
		if (args.length == 2) {
			list.addAll(Sets.newHashSet(SpleefArena.getArenaNames()));
		}
		if (args.length == 3) {
			list.add(args[1]);
		}
		if (args.length == 4) {
			list.add(Language.VALUE_TRUE.toString());
			list.add(Language.VALUE_FALSE.toString());			
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#getCommandHelp()
	 */
	@Override
	public CommandHelp getCommandHelp() {
		return new CommandHelp("/%cmd% " + Language.COMMAND_DELETE + " " + Language.ARGUMENT_ARENA + " " + Language.ARGUMENT_ARENA + " " + Language.ARGUMENT_DELETE_STATS_OPTIONAL, getDescription());
	}

}
