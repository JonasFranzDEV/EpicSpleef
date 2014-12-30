package de.oppermann.bastian.spleef.lobbycommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.oppermann.bastian.spleef.arena.Lobby;
import de.oppermann.bastian.spleef.storage.StorageManager;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.command.AbstractArgument;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandHelp;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandResult;

public class CreateLobbyArgument extends AbstractArgument {

	/**
	 * Class constructor.
	 */
	public CreateLobbyArgument() {
		super(new String[]{Language.COMMAND_CREATELOBBY.toString()}, 2, "spleef.createLobby", null, Language.COMMAND_CREATELOBBY_DESCRIPTION.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#executeForPlayer(org.bukkit.entity.Player, org.bukkit.command.Command, java.lang.String[])
	 */
	@Override
	public CommandResult executeForPlayer(Player player, Command cmd, String[] args) {
		if (args.length == 2) {			
			Lobby lobby = null;
			for (Lobby sLobby : Lobby.getLobbies()) {
				if (sLobby.getName().equals(args[1])) {
					lobby = sLobby;
					break;
				}
			}
			
			if (lobby != null) {
				player.sendMessage(Language.ALREADY_LOBBY_WITH_NAME.toString().replace("%lobby%", args[1]));
				return CommandResult.SUCCESS;
			}
			
			lobby = new Lobby(args[1], player.getWorld().getName());
			
			StorageManager.getInstance().createConfigForLobby(lobby);
			player.sendMessage(Language.SUCCESSFULLY_CREATED_LOBBY.toString().replace("%lobby%", args[1]));
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
			list.add(Language.COMMAND_CREATELOBBY.toString());
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#getCommandHelp()
	 */
	@Override
	public CommandHelp getCommandHelp() {
		return new CommandHelp("/%cmd% " + Language.COMMAND_CREATELOBBY + " " + Language.ARGUMENT_ARENA, getDescription());
	}

}
