package de.oppermann.bastian.spleef.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaIsDisabledException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaIsFullException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaMisconfiguredException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaNotWaitingForPlayersException;
import de.oppermann.bastian.spleef.exceptions.TooLateToJoinException;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.command.AbstractArgument;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandHelp;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandResult;
import de.oppermann.bastian.spleef.util.gui.GuiInventory;

public class JoinArgument extends AbstractArgument {

	/**
	 * Class constructor.
	 */
	public JoinArgument() {
		super(new String[]{Language.COMMAND_JOIN.toString()}, -1, "spleef.join", null, Language.COMMAND_JOIN_DESCRIPTION.toString());
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
			player.openInventory(GuiInventory.JOIN.createInventory(player));
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
			
			try {
				arena.join(player);
			} catch (SpleefArenaNotWaitingForPlayersException e) {
				player.sendMessage(Language.CAN_NOT_JOIN_GAME_ACTIVE.toString().replace("%arena%", arena.getName()));
			} catch (SpleefArenaIsFullException e) {
				player.sendMessage(Language.CAN_NOT_JOIN_ARENA_FULL.toString().replace("%arena%", arena.getName()));
			} catch (SpleefArenaIsDisabledException e) {
				player.sendMessage(Language.CAN_NOT_JOIN_ARENA_DISABLED.toString().replace("%arena%", arena.getName()));
			} catch (SpleefArenaMisconfiguredException e) {
				player.sendMessage(Language.CAN_NOT_JOIN_ARENA_MISCONFIGURED.toString().replace("%arena%", arena.getName()));
			} catch (TooLateToJoinException e) {
				player.sendMessage(Language.CAN_NOT_JOIN_TOO_LATE.toString().replace("%arena%", arena.getName()));
			}
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
			list.add(Language.COMMAND_JOIN.toString());
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
		return new CommandHelp("/%cmd% " + Language.COMMAND_JOIN + " " + Language.ARGUMENT_ARENA_OPTIONAL, getDescription());
	}

}
