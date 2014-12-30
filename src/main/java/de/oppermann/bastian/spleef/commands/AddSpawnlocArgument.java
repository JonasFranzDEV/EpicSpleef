package de.oppermann.bastian.spleef.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.arena.SpleefSpawnLocation;
import de.oppermann.bastian.spleef.storage.ConfigAccessor;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.command.AbstractArgument;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandHelp;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandResult;

public class AddSpawnlocArgument extends AbstractArgument {

	/**
	 * Class constructor.
	 */
	public AddSpawnlocArgument() {
		super(new String[]{Language.COMMAND_ADD_SPAWNLOCATION.toString()}, -1, "spleef.addspawnloc", null, Language.COMMAND_ADD_SPAWNLOCATION_DESCRIPTION.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#executeForPlayer(org.bukkit.entity.Player, org.bukkit.command.Command, java.lang.String[])
	 */
	@Override
	public CommandResult executeForPlayer(Player player, Command cmd, String[] args) {		
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
			
			ArrayList<SpleefSpawnLocation> spawnLocs = arena.getSpawnLocations();
			
			Location location = player.getLocation();
			SpleefSpawnLocation spawnLocation = new SpleefSpawnLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

			ConfigAccessor accessor = SpleefMain.getInstance().getArenaAccessor(arena.getName());
			
			accessor.getConfig().set("spawnlocs." + (spawnLocs.size() + 1) + ".x", spawnLocation.getX());
			accessor.getConfig().set("spawnlocs." + (spawnLocs.size() + 1) + ".y", spawnLocation.getY());
			accessor.getConfig().set("spawnlocs." + (spawnLocs.size() + 1) + ".z", spawnLocation.getZ());
			accessor.getConfig().set("spawnlocs." + (spawnLocs.size() + 1) + ".yaw", spawnLocation.getYaw());
			accessor.getConfig().set("spawnlocs." + (spawnLocs.size() + 1) + ".pitch", spawnLocation.getPitch());			
			
			accessor.saveConfig();
			
			arena.addSpawnLocation(spawnLocation);
			
			player.sendMessage(Language.SUCCESSFULLY_ADDED_SPAWNLOCATION.toString());
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
			list.add(Language.COMMAND_ADD_SPAWNLOCATION.toString());
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
		return new CommandHelp("/%cmd% " + Language.COMMAND_ADD_SPAWNLOCATION + " " + Language.ARGUMENT_ARENA, getDescription());
	}

}