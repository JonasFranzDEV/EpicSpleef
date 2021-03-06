package de.oppermann.bastian.spleef.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.arena.SpleefBlock;
import de.oppermann.bastian.spleef.hooks.WorldEditHook;
import de.oppermann.bastian.spleef.storage.ConfigAccessor;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.PluginChecker;
import de.oppermann.bastian.spleef.util.algorithm.FloodfillAlgorithm;
import de.oppermann.bastian.spleef.util.command.AbstractArgument;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandHelp;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandResult;

public class AddBlocksArgument extends AbstractArgument {

	/**
	 * Class constructor.
	 */
	public AddBlocksArgument() {
		super(new String[]{Language.COMMAND_ADD_BLOCKS.toString()}, -1, "spleef.addblocks", null, Language.COMMAND_ADD_BLOCKS_DESCRIPTION.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#executeForPlayer(org.bukkit.entity.Player, org.bukkit.command.Command, java.lang.String[])
	 */
	@SuppressWarnings("deprecation")
	@Override
	public CommandResult executeForPlayer(Player player, Command cmd, String[] args) {		
		if (args.length == 3 || args.length == 4) {
			
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
			
			boolean rememberMaterialFlag = false;
			if (args.length == 4) {
				if (args[3].equals(Language.COMMAND_ADD_BLOCKS_REMEMBER_MATERIAL_FLAG.toString())) {
					rememberMaterialFlag = true;
				} else {
					player.sendMessage(Language.COMMAND_ADD_BLOCKS_UNKNOWN_FLAG.toString());
				}
			}
			
			if (args[2].equalsIgnoreCase("worldedit")) {
				if (!PluginChecker.worldeditIsLoaded()) {
					player.sendMessage(Language.WORLDEDIT_NOT_LOADED.toString());
					return CommandResult.SUCCESS;
				}
				
				if (WorldEditHook.getWorldEditPlugin().getSelection(player) == null) {
					player.sendMessage(Language.WORLDEDIT_NO_SELECTION.toString());
					return CommandResult.SUCCESS;
				}
				
				Location min = WorldEditHook.getWorldEditPlugin().getSelection(player).getMinimumPoint();
				Location max = WorldEditHook.getWorldEditPlugin().getSelection(player).getMaximumPoint();
				
				if (min.getWorld() != arena.getWorld() || max.getWorld() != arena.getWorld()) {
					player.sendMessage(Language.WORLDEDIT_SELECTION_IN_OTHER_WORLD.toString());
					return CommandResult.SUCCESS;
				}
				
				ConfigAccessor accessor = SpleefMain.getInstance().getArenaAccessor(arena.getName());
				ArrayList<SpleefBlock> blocks = arena.getBlocks();
				int counter = blocks.size();
				for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
					for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
						for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
							SpleefBlock block;
							if (rememberMaterialFlag) {
								block = new SpleefBlock(x, y, z, min.getWorld().getBlockAt(x, y, z).getType(), min.getWorld().getBlockAt(x, y, z).getData());
							} else {
								block = new SpleefBlock(x, y, z, Material.SNOW_BLOCK, (byte) 0);
							}
							
							if (blocks.contains(block)) {
								continue;
							}
							
							counter++;
							
							accessor.getConfig().set("blocks." + counter + ".x", x);
							accessor.getConfig().set("blocks." + counter + ".y", y);
							accessor.getConfig().set("blocks." + counter + ".z", z);
							accessor.getConfig().set("blocks." + counter + ".type", block.getType().name());
							accessor.getConfig().set("blocks." + counter + ".data", block.getData());
							
							if (rememberMaterialFlag) {
								arena.addSpleefBlock(new SpleefBlock(x, y, z, min.getWorld().getBlockAt(x, y, z).getType(), min.getWorld().getBlockAt(x, y, z).getData()));
							} else {
								arena.addSpleefBlock(new SpleefBlock(x, y, z, Material.SNOW_BLOCK, (byte) 0));
							}
						}
					}
				}
				
				accessor.saveConfig();
				player.sendMessage(Language.SUCCESSFULLY_ADDED_BLOCKS.toString().replace("%amount%", String.valueOf((counter - blocks.size()))));
				return CommandResult.SUCCESS;
			}
			
			// TODO check if player is in arena world for lookingAt and standingOn
			if (player.getWorld() != arena.getWorld()) {
				player.sendMessage(Language.MUST_BE_IN_WORLD_OF_ARENA.toString().replace("%arena%", arena.getName()));
			}
			
			// looking at
			if (args[2].equalsIgnoreCase("lookingAt")) {
				try {
					Block block = player.getTargetBlock((HashSet<Byte>) null, 400);
					if (block == null) {
						player.sendMessage(Language.SUCCESSFULLY_ADDED_BLOCKS.toString().replace("%amount%", "0"));
					} else if (block.getType() == Material.AIR) {
						player.sendMessage(Language.SUCCESSFULLY_ADDED_BLOCKS.toString().replace("%amount%", "0"));
					} else {
						ArrayList<SpleefBlock> blocks = FloodfillAlgorithm.fill4(block.getWorld(), block.getX(), block.getY(), block.getZ(), arena, rememberMaterialFlag);
						ConfigAccessor accessor = SpleefMain.getInstance().getArenaAccessor(arena.getName());
						ArrayList<SpleefBlock> arenaBlocks = arena.getBlocks();
						for (int i = 0; i < arenaBlocks.size(); i++) {
							accessor.getConfig().set("blocks." + (i + 1) + ".x", arenaBlocks.get(i).getX());
							accessor.getConfig().set("blocks." + (i + 1) + ".y", arenaBlocks.get(i).getY());
							accessor.getConfig().set("blocks." + (i + 1) + ".z", arenaBlocks.get(i).getZ());
							accessor.getConfig().set("blocks." + (i + 1) + ".type",arenaBlocks.get(i).getType().name());
							accessor.getConfig().set("blocks." + (i + 1) + ".data", arenaBlocks.get(i).getData());
						}
						accessor.saveConfig();
						player.sendMessage(Language.SUCCESSFULLY_ADDED_BLOCKS.toString().replace("%amount%", String.valueOf(blocks.size())));
					}
				} catch (IllegalStateException e) {
					player.sendMessage(Language.SUCCESSFULLY_ADDED_BLOCKS.toString().replace("%amount%", "0"));
				}	
				return CommandResult.SUCCESS;
			}
			
			// standing on
			if (args[2].equalsIgnoreCase("standingOn")) {
				Block block = player.getLocation().getBlock().getRelative(0, -1, 0);
				if (block.getType() != Material.AIR) {
					ArrayList<SpleefBlock> blocks = FloodfillAlgorithm.fill4(block.getWorld(), block.getX(), block.getY(), block.getZ(), arena, rememberMaterialFlag);
					ConfigAccessor accessor = SpleefMain.getInstance().getArenaAccessor(arena.getName());
					ArrayList<SpleefBlock> arenaBlocks = arena.getBlocks();
					for (int i = 0; i < arenaBlocks.size(); i++) {
						accessor.getConfig().set("blocks." + (i + 1) + ".x", arenaBlocks.get(i).getX());
						accessor.getConfig().set("blocks." + (i + 1) + ".y", arenaBlocks.get(i).getY());
						accessor.getConfig().set("blocks." + (i + 1) + ".z", arenaBlocks.get(i).getZ());
						accessor.getConfig().set("blocks." + (i + 1) + ".type",arenaBlocks.get(i).getType().name());
						accessor.getConfig().set("blocks." + (i + 1) + ".data", arenaBlocks.get(i).getData());
					}
					accessor.saveConfig();
					player.sendMessage(Language.SUCCESSFULLY_ADDED_BLOCKS.toString().replace("%amount%", String.valueOf(blocks.size())));
				} else {
					player.sendMessage(Language.SUCCESSFULLY_ADDED_BLOCKS.toString().replace("%amount%", "0"));
				}
				return CommandResult.SUCCESS;
			}
			return CommandResult.ERROR;
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
			list.add(Language.COMMAND_ADD_BLOCKS.toString());
		}
		if (args.length == 2) {
			list.addAll(Sets.newHashSet(SpleefArena.getArenaNames()));
		}
		if (args.length == 3) {
			list.add("worldedit");
			list.add("lookingAt");
			list.add("standingOn");
		}
		if (args.length == 4) {
			list.add(Language.COMMAND_ADD_BLOCKS_REMEMBER_MATERIAL_FLAG.toString());
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#getCommandHelp()
	 */
	@Override
	public CommandHelp getCommandHelp() {
		return new CommandHelp("/%cmd% " + Language.COMMAND_ADD_BLOCKS + " " + Language.ARGUMENT_ARENA + " <worldedit/lookingAt/standingOn> [" + Language.COMMAND_ADD_BLOCKS_REMEMBER_MATERIAL_FLAG.toString() + "]", getDescription());
	}

}
