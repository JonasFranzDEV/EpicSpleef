package de.oppermann.bastian.spleef.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.storage.ConfigAccessor;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.SimpleBlock;
import de.oppermann.bastian.spleef.util.command.AbstractArgument;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandHelp;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandResult;

public class AddJoinSignArgument extends AbstractArgument {

	/**
	 * Class constructor.
	 */
	public AddJoinSignArgument() {
		super(new String[]{Language.COMMAND_ADD_JOIN_SIGN.toString()}, 2, "spleef.addjoinsign", null, Language.COMMAND_ADD_JOIN_SIGN_DESCRIPTION.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#executeForPlayer(org.bukkit.entity.Player, org.bukkit.command.Command, java.lang.String[])
	 */
	@SuppressWarnings("deprecation")	// who cares?
	@Override
	public CommandResult executeForPlayer(Player player, Command cmd, String[] args) {
		if (args.length > 2 || args.length < 1) {	// only 1 or 2 arguments are allowed
			return CommandResult.ERROR;
		}
		
		if (!player.hasPermission(getPermission())) {
			return CommandResult.NO_PERMISSION;
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
			
			Block target = null;
			try {
				target = player.getTargetBlock(null, 20);
			} catch (IllegalStateException e) {
				// :(
			}
			
			if (!(target.getState() instanceof Sign)) {
				player.sendMessage(Language.MUST_LOOK_AT_SIGN.toString());
				return CommandResult.SUCCESS;
			}	
			
			for (SpleefArena arenas : SpleefArena.getSpleefArenas()) {
				if (arenas.isJoinSign(target)) {
					player.sendMessage(Language.ALREADY_JOIN_SIGN.toString());
					return CommandResult.SUCCESS;
				}
			}
			
			ArrayList<SimpleBlock> signs = arena.getJoinSigns();
			
			SimpleBlock block = new SimpleBlock(target.getWorld().getName(), target.getX(), target.getY(), target.getZ());

			ConfigAccessor accessor = SpleefMain.getInstance().getArenaAccessor(arena.getName());

			accessor.getConfig().set("joinsigns." + (signs.size() + 1) + ".world", block.getWorldName());
			accessor.getConfig().set("joinsigns." + (signs.size() + 1) + ".x", block.getX());
			accessor.getConfig().set("joinsigns." + (signs.size() + 1) + ".y", block.getY());
			accessor.getConfig().set("joinsigns." + (signs.size() + 1) + ".z", block.getZ());	
			
			accessor.saveConfig();
			
			arena.addJoinSign(target);
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
			list.add(Language.COMMAND_ADD_JOIN_SIGN.toString());
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
		return new CommandHelp("/%cmd% " + Language.COMMAND_ADD_JOIN_SIGN + " " + Language.ARGUMENT_ARENA_OPTIONAL, getDescription());
	}

}
