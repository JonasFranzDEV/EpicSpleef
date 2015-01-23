package de.oppermann.bastian.spleef.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.util.CraftBukkitUtil;
import de.oppermann.bastian.spleef.util.EpicSpleefVersion;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.UpdateChecker;
import de.oppermann.bastian.spleef.util.command.AbstractArgument;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandHelp;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandResult;

public class UpdateArgument extends AbstractArgument {

	private static EpicSpleefVersion[] lastQuery = new EpicSpleefVersion[0];
	static {
		new BukkitRunnable() {			
			@Override
			public void run() {
				EpicSpleefVersion[] versions = UpdateChecker.queryVersions();
				synchronized (lastQuery) {
					lastQuery = versions;					
				}
			}
		}.runTaskAsynchronously(SpleefMain.getInstance());
	}
	
	/**
	 * Class constructor.
	 */
	public UpdateArgument() {
		super(new String[]{Language.COMMAND_UPDATE.toString()}, -1, "spleef.update", null, Language.COMMAND_UPDATE_DESCRIPTION.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see de.oppermann.bastian.spleef.util.command.AbstractArgument#executeForPlayer(org.bukkit.entity.Player, org.bukkit.command.Command, java.lang.String[])
	 */
	@Override
	public CommandResult executeForPlayer(Player player, Command cmd, String[] args) {		
		if (!player.hasPermission(getPermission())) {
			return CommandResult.NO_PERMISSION;
		}
		
		final Player PLAYER = player;
		final String[] ARGS = args;
		if (args.length == 2) {
			// list all versions
			if (args[1].equalsIgnoreCase(Language.COMMAND_UPDATE_ARGUMENT_LISTALLVERSIONS.toString())) {
				Bukkit.getScheduler().runTaskAsynchronously(SpleefMain.getInstance(), new Runnable() {					
					@Override
					public void run() {
						EpicSpleefVersion[] versions = UpdateChecker.queryVersions();
						synchronized (lastQuery) {
							lastQuery = versions;					
						}
						listVersions(PLAYER, versions);
					}
				});
				return CommandResult.SUCCESS;
			}
			if (args[1].equalsIgnoreCase(Language.COMMAND_UPDATE_ARGUMENT_LISTUPDATES.toString())) {
				Bukkit.getScheduler().runTaskAsynchronously(SpleefMain.getInstance(), new Runnable() {					
					@Override
					public void run() {
						EpicSpleefVersion[] versions = UpdateChecker.getUpdates();
						listVersions(PLAYER, versions);
					}
				});
				return CommandResult.SUCCESS;
			}	
		}
		if (args.length == 2 || args.length == 3) {
			if (args[1].equalsIgnoreCase(Language.COMMAND_UPDATE_ARGUMENT_INSTALL.toString())) {
				download(true, ARGS, PLAYER);
				return CommandResult.SUCCESS;
			}
			if (args[1].equalsIgnoreCase(Language.COMMAND_UPDATE_ARGUMENT_DOWNLOAD.toString())) {
				download(false, ARGS, PLAYER);
				return CommandResult.SUCCESS;
			}
		}
		return CommandResult.ERROR;	// should never happen
	}
	
	private void listVersions(Player player, EpicSpleefVersion[] versions) {
		if (versions.length == 0) {
			player.sendMessage(Language.UPDATE_NO_UPDATES.toString());
			return;
		}
		player.sendMessage(Language.UPDATE_VERSIONLIST_HEAD.toString());
		for (EpicSpleefVersion version : versions) {
			String text = Language.UPDATE_VERSIONLIST_VALUE.toString().replace("%pluginVersion%", version.getVersionName().replace(".jar", "")).replace("%gameVersion%", version.getVersionGameVersion());
			text = text.replace("\"", "\\\"");
			String simpleVersion = version.getVersionName().replace("EpicSpleef-", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace(".jar", "");
			String suggestCommand = "/spleef " + Language.COMMAND_UPDATE + " " + Language.COMMAND_UPDATE_ARGUMENT_INSTALL + " " + simpleVersion;	// TODO custom /spleef command
			suggestCommand = suggestCommand.replace("\"", "\\\"");
			String popup = Language.UPDATE_VERSIONLIST_POPUP.toString().replace("\"", "\\\"").replace("%pluginVersion%", version.getVersionName().replace(".jar", "")).replace("%gameVersion%", version.getVersionGameVersion());
			final String JSON = 
				"{\"text\":\"\",\"extra\":[{\"text\":\""
				+ text +
				"\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\""
				+ suggestCommand +
				"\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\""
				+ popup +
				"\"}]}}}]}";
			if (!CraftBukkitUtil.sendJSONText(player, JSON)) {
				player.sendMessage(Language.UPDATE_VERSIONLIST_VALUE.toString().replace("%pluginVersion%", version.getVersionName().replace(".jar", "")).replace("%gameVersion%", version.getVersionGameVersion()));
			}
		}
		player.sendMessage(Language.UPDATE_VERSIONLIST_BOTTOM.toString());
	}
	
	private void download(boolean install, String[] args, Player player) {
		final Player PLAYER = player;
		final String[] ARGS = args;
		final boolean INSTALL = install;
		new BukkitRunnable() {				
			@Override
			public void run() {						
				EpicSpleefVersion toInstall = null;
				if (ARGS.length == 2) {
					toInstall = UpdateChecker.getNewestVersion(SpleefMain.getInstance().getConfig().getBoolean("auto-update.unsafeUpdates", false));
					if (toInstall == null) {
						if (SpleefMain.getInstance().getConfig().getBoolean("auto-update.unsafeUpdates", false)) {
							PLAYER.sendMessage(Language.UPDATE_NO_NEW_VERSION.toString());
						} else {
							EpicSpleefVersion unsafeUpdate = UpdateChecker.getNewestVersion(true);
							if (unsafeUpdate == null) {
								PLAYER.sendMessage(Language.UPDATE_NO_NEW_VERSION.toString());
							} else {
								String simpleVersion = unsafeUpdate.getVersionName().replace("EpicSpleef-", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace(".jar", "");
								String suggestCommand = "/spleef " + Language.COMMAND_UPDATE + " " + Language.COMMAND_UPDATE_ARGUMENT_INSTALL + " " + simpleVersion;	// TODO custom /spleef command
								suggestCommand = suggestCommand.replace("\"", "\\\"");
								PLAYER.sendMessage(Language.UPDATE_NO_NEW_VERSION_BUT_UNSAFE.toString().replace("%updateCommand%", suggestCommand).replace("%gameVersion%", unsafeUpdate.getVersionGameVersion()));
							}
						}
						return;
					}							
				}
				if (ARGS.length == 3) {
					EpicSpleefVersion[] versions = UpdateChecker.queryVersions();
					synchronized (lastQuery) {
						lastQuery = versions;					
					}
					for (EpicSpleefVersion version : versions) {
						String simpleVersion = version.getVersionName().replace("EpicSpleef-", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace(".jar", "");
						if (simpleVersion.equals(ARGS[2])) {
							toInstall = version;
							continue;
						}
					}
					if (toInstall == null) {
						PLAYER.sendMessage(Language.UPDATE_UNKNOWN_VERSION.toString());
						return;
					}
				}
				// download version and maybe install
				UpdateChecker.downloadFile(toInstall, INSTALL, PLAYER);
			}
		}.runTaskAsynchronously(SpleefMain.getInstance());
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
			list.add(Language.COMMAND_UPDATE.toString());
		}
		if (args.length == 2) {
			list.add(Language.COMMAND_UPDATE_ARGUMENT_DOWNLOAD.toString());
			list.add(Language.COMMAND_UPDATE_ARGUMENT_LISTALLVERSIONS.toString());
			list.add(Language.COMMAND_UPDATE_ARGUMENT_LISTUPDATES.toString());
			list.add(Language.COMMAND_UPDATE_ARGUMENT_INSTALL.toString());
		}
		if (args.length == 3) {
			if (args[1].equalsIgnoreCase(Language.COMMAND_UPDATE_ARGUMENT_INSTALL.toString())) {
				synchronized (lastQuery) {
					if (lastQuery != null) {
						for (EpicSpleefVersion version : lastQuery) {
							String simpleVersion = version.getVersionName().replace("EpicSpleef-", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace(".jar", "");						
							list.add(simpleVersion);
						}
					}
				}
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
		return new CommandHelp("/%cmd% " + Language.COMMAND_UPDATE + " <" + Language.COMMAND_UPDATE_ARGUMENT_INSTALL + "/" + Language.COMMAND_UPDATE_ARGUMENT_LISTUPDATES + "/" + Language.COMMAND_UPDATE_ARGUMENT_LISTALLVERSIONS + "/" + Language.COMMAND_UPDATE_ARGUMENT_DOWNLOAD + ">", getDescription());
	}

}
