package de.oppermann.bastian.spleef.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.arena.Lobby;
import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.storage.ConfigAccessor;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.command.AbstractArgument;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandHelp;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandResult;

public class SetLobbyArgument extends AbstractArgument {

	/**
	 * Class constructor.
	 */
	public SetLobbyArgument() {
		super(new String[]{Language.COMMAND_SET_LOBBY.toString()}, -1, "spleef.setlobby", null, Language.COMMAND_SET_LOBBY_DESCRIPTION.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#executeForPlayer(org.bukkit.entity.Player, org.bukkit.command.Command, java.lang.String[])
	 */
	@Override
	public CommandResult executeForPlayer(Player player, Command cmd, String[] args) {		
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
			
			Lobby lobby = null;
			for (Lobby sLobby : Lobby.getLobbies()) {
				if (sLobby.getName().equals(args[2])) {
					lobby = sLobby;
					break;
				}
			}
			
			if (lobby == null) {
				player.sendMessage(Language.NO_LOBBY_WITH_NAME.toString().replace("%lobby%", args[2]));
				return CommandResult.SUCCESS;
			}

			ConfigAccessor accessor = SpleefMain.getInstance().getArenaAccessor(arena.getName());			
			accessor.getConfig().set("lobby", lobby.getName());					
			accessor.saveConfig();
			
			arena.getConfiguration().setLobby(lobby);
			
			player.sendMessage(Language.SUCCESSFULLY_SET_LOBBY.toString());
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
			list.add(Language.COMMAND_SET_LOBBY.toString());
		}
		if (args.length == 2) {
			list.addAll(Sets.newHashSet(SpleefArena.getArenaNames()));
		}
		if (args.length == 3) {
			list.addAll(Sets.newHashSet(Lobby.getLobbyNames()));
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#getCommandHelp()
	 */
	@Override
	public CommandHelp getCommandHelp() {
		return new CommandHelp("/%cmd% " + Language.COMMAND_SET_LOBBY + " " + Language.ARGUMENT_ARENA + " " + Language.ARGUMENT_LOBBY, getDescription());
	}

}
