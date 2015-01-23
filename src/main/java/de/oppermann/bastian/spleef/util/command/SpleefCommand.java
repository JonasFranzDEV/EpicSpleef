package de.oppermann.bastian.spleef.util.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.util.ChatUtil;
import de.oppermann.bastian.spleef.util.CraftBukkitUtil;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.Validator;

/**
 * Does not require the plugin.yml so it's cool. :)
 * 
 * @author Bastian Oppermann
 */
public class SpleefCommand extends Command {

	private static final HashMap<String, SpleefCommand> COMMANDS = new HashMap<>();	// the commands
	
	private static final String VERSION;	// the craftbukkit version
	static {
		String path = Bukkit.getServer().getClass().getPackage().getName();
		VERSION = path.substring(path.lastIndexOf(".") + 1, path.length());
	}

	protected final JavaPlugin PLUGIN;
	protected final String COMMAND;
	protected final String PERMISSION;
	protected final List<AbstractArgument> ARGUMENTS = new ArrayList<AbstractArgument>();

	/*
	 * @see #createIfNotExist(String, String)
	 */
	private SpleefCommand(JavaPlugin plugin, String command, String description, String permission, String... aliases) {
		super(command);
		Validator.validateNotNull(plugin, "plugin");
		Validator.validateNotNull(description, "description");
		Validator.validateNotNull(permission, "permission");
		this.PLUGIN = plugin;
		this.COMMAND = command;
		this.PERMISSION = permission;

		super.setDescription(description);
		List<String> aliasList = new ArrayList<String>();
		for (String alias : aliases) {
			aliasList.add(alias);
		}
		super.setAliases(aliasList);
		this.register();
		
		COMMANDS.put(command.toLowerCase(), this);
	}

	private void register() {
		try {
			Field f = Class.forName("org.bukkit.craftbukkit." + SpleefCommand.VERSION + ".CraftServer").getDeclaredField("commandMap");
			f.setAccessible(true);

			CommandMap map = (CommandMap) f.get(Bukkit.getServer());
			map.register(this.PLUGIN.getName(), this);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}	

	/*
	 * (non-Javadoc)
	 * @see org.bukkit.command.Command#tabComplete(org.bukkit.command.CommandSender, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> tabComplete(CommandSender sender, String command, String[] args) throws IllegalArgumentException {
		return onTabComplete(sender, this, command, args);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bukkit.command.Command#execute(org.bukkit.command.CommandSender, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean execute(CommandSender cs, String label, String[] args) {
		return onCommand(cs, this, label, args);
	}

	/**
	 * Adds an argument to the command.
	 */
	public void registerArgument(AbstractArgument arg) {
		Validator.validateNotNull(arg, "arg");
		ARGUMENTS.add(arg);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// show help topic
		if (args.length == 1 && args[0].equalsIgnoreCase("?")) {	// TODO customizable command (the ?)
			ChatUtil.sendLine(sender, ChatColor.GREEN, Language.HELP_HEADLINE.toString().replace("%cmd%", COMMAND));
			for (AbstractArgument arg : ARGUMENTS) {
				if (!(sender instanceof Player) || ((Player) sender).hasPermission(arg.getPermission())) {
					arg.getCommandHelp().send(sender, COMMAND);
				}
			}
			ChatUtil.sendLine(sender, ChatColor.GREEN);
			return true;	// always return true cause false is ugly
		} else {	// if it's not the help

			CommandResult result;

			if (sender instanceof Player) {	// if the sender is a player
				Player player = (Player) sender;
				result = onPlayerCommand(player, cmd, args);
			} else {
				result = onServerCommand(sender, cmd, args);
			}

			if (result != null && result.getText() != null) {
				sender.sendMessage(result.getText().replace("%cmd%", cmd.getName()).replace("%player%", sender.getName()));
			}
			return true;	// always return true cause false is ugly
		}
	}

	private List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {	// tab complete only for players (poor console :()
			return null;
		}
		List<String> list = new ArrayList<String>();
		// add help topic
		if (args.length == 1) {
			if ("?".startsWith(args[0])) {	// TODO customizable command (the ?)
				list.add("?");
			}
		}

		Player player = (Player) sender;

		list = onTabComplete(player, cmd, args, list);

		if (list == null) {	// no list -> no tabcomplete
			return null;
		}

		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String str = it.next();
			if (!str.toLowerCase().startsWith(args[args.length-1].toLowerCase())) {
				it.remove();
			}
		}
		
		Collections.sort(list);	// sort the list
		return list;
	}

	private List<String> onTabComplete(Player player, Command cmd, String[] args, List<String> list) {		
		for (AbstractArgument arg : ARGUMENTS) {	// iterate throw all arguments
			if (player.hasPermission(arg.getPermission())) {	// if the player does not have permissions for the argument
				if (args.length <= arg.getNeededArguments() || arg.getNeededArguments() < 0) {
					if (args.length <= arg.getFixedArguments().length) {
						if (arg.getFixedArguments()[args.length - 1].toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
							list.add(arg.getFixedArguments()[args.length - 1]);
						}
					} else {
						if (arg.getFixedArguments().length != 0 && arg.getFixedArguments()[arg.getFixedArguments().length - 1].toLowerCase().startsWith(args[arg.getFixedArguments().length - 1].toLowerCase())) {
							list.addAll(arg.onTabComplete(player, args));
						} else if (arg.getFixedArguments().length == 0) {
							list.addAll(arg.onTabComplete(player, args));
						}
					}
				}
			}
		}
		return list;
	}

	private CommandResult onPlayerCommand(Player player, Command cmd, String[] args) {
		for (AbstractArgument arg : ARGUMENTS) {
			if (arg.checkArgs(args)) {
				return arg.executeForPlayer(player, cmd, args);
			}
		}
		return CommandResult.ERROR;
	}

	private CommandResult onServerCommand(CommandSender sender, Command cmd, String[] args) {
		for (AbstractArgument arg : ARGUMENTS) {
			if (arg.checkArgs(args)) {
				return arg.executeForServer(sender, cmd, args);
			}
		}
		return CommandResult.ERROR;
	}
	
	/**
	 * Gets the permission of this command.
	 * 
	 * @return The permission.
	 */
	@Override
	public String getPermission() {
		return PERMISSION;
	}
	
	/**
	 * Unregisters the command.
	 */
	public void unregister() {	
		try {
			Field fMap = Command.class.getDeclaredField("commandMap");
			fMap.setAccessible(true);
			CommandMap map = (CommandMap) fMap.get(this);
			this.unregister(map);
			
			Field fKnownCommands = map.getClass().getDeclaredField("knownCommands");
			fKnownCommands.setAccessible(true);
			@SuppressWarnings("unchecked")
			HashMap<String, Command> knownCommands = (HashMap<String, Command>) fKnownCommands.get(map);
			for (Entry<String, Command> entry : knownCommands.entrySet()) {
				if (entry.getValue() == this) {
					knownCommands.remove(entry.getKey());
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	/*========== static stuff ==========*/
	
	/**
	 * Checks if cmdName is a {@link SpleefCommand}.
	 */
	public static boolean isSpleefCommand(String cmdName) {
		Validator.validateNotNull(cmdName, "cmdName");
		return COMMANDS.containsKey(cmdName.toLowerCase());
	}
	
	/**
	 * Gets a {@link SpleefCommand} by its name.
	 * 
	 * @param cmdName The name of the command without slash.
	 * @param cmdPermission The permission to use this command.
	 */
	public static SpleefCommand getSpleefCommand(String cmdName) {
		Validator.validateNotNull(cmdName, "cmdName");
		if (COMMANDS.containsKey(cmdName.toLowerCase())) {
			return COMMANDS.get(cmdName.toLowerCase());
		} else {
			return null;
		}
	}
	
	private static SpleefCommand createSpleefCommand(String cmdName, String cmdPermission) {
		Validator.validateNotNull(cmdName, "cmdName");
		Validator.validateNotNull(cmdPermission, "cmdPermission");
		if (COMMANDS.containsKey(cmdName.toLowerCase())) {
			return COMMANDS.get(cmdName.toLowerCase());
		} else {
			return new SpleefCommand(SpleefMain.getInstance(), cmdName, "A spleef command", cmdPermission, new String[0]);
		}
	}
	
	/** 
	 * Creates a new {@link SpleefCommand} if there is no {@link SpleefCommand} for this command.
	 * 
	 * @param cmdName The name of the command without slash.
	 * @param cmdPermission The permission to use this command.
	 */
	public static SpleefCommand createIfNotExist(String cmdName, String cmdPermission) {
		Validator.validateNotNull(cmdName, "cmdName");
		Validator.validateNotNull(cmdPermission, "cmdPermission");
		SpleefCommand command = SpleefCommand.getSpleefCommand(cmdName);
		if (command == null) {
			command = SpleefCommand.createSpleefCommand(cmdName, cmdPermission);
		}
		return command;
	}
	
	/**
	 * Gets all commands.
	 */
	public static Set<Entry<String, SpleefCommand>> getAllCommands() {
		return COMMANDS.entrySet();
	}

	public static class CommandResult {
		
		/**
		 * Return this if nothing more should happen.
		 */
		public final static CommandResult SUCCESS = new CommandResult(null);
		/**
		 * Return this if the player does not have the required permission.
		 */
		public final static CommandResult NO_PERMISSION = new CommandResult(Language.NO_PERMISSION.toString());
		/**
		 * If you use variable arguments instead of fixed return this if the player/server use them the wrong way.
		 */
		public final static CommandResult ERROR = new CommandResult(Language.WRONG_USAGE.toString());
		/**
		 * If you do not want that the server can use an arguent return this in the
		 *  {@link AbstractArgument#executeForServer(CommandSender, Command, String[])} method.
		 */
		public final static CommandResult ONLY_PLAYER = new CommandResult(Language.ONLY_PLAYER.toString());
		
		private final String TEXT;

		/**
		 * Class constructor.
		 * 
		 * @param text The text the player should receive.<p>
		 * 		<code>null</code> if the player should receive nothing.<p>
		 * 		%cmd% will be replaced with the command without slash.
		 */
		public CommandResult(String text) {
			this.TEXT = text;
		}

		/**
		 * Gets the text.
		 * 
		 * @return The text.
		 */
		public String getText() {
			return TEXT;
		}

	}

	/**
	 * Helps to keep an uniform style.
	 * <p>
	 * This is what is shown in the help-page (/command ?).
	 */
	public static class CommandHelp {

		private final String CMD;
		private final String DESCRIPTION;
		private final String FULL_TEXT;

		/**
		 * Class constructor.
		 * 
		 * @param cmd The command/argument name <u>with</u> a slash ( / ).
		 * @param description The description of the command/argument.
		 */
		public CommandHelp(String cmd, String description) {
			Validator.validateNotNull(cmd, "cmd");
			Validator.validateNotNull(description, "description");
			this.CMD = cmd;
			this.DESCRIPTION = description;
			this.FULL_TEXT = ChatColor.GOLD + CMD + ChatColor.GRAY + " - " + DESCRIPTION;	// TODO customizable colors
		}

		/**
		 * Send's the helptext to the player.
		 * 
		 * @param sender The reciever of the helptext.
		 * @param command The command.
		 */
		public void send(CommandSender sender, String command) {
			Validator.validateNotNull(sender, "sender");
			Validator.validateNotNull(command, "command");
			if (sender instanceof Player) {
				String shortDescription = getText().replace("%cmd%", command);
				int length = ChatUtil.getStringWidth(shortDescription);
				while (length > 1255) {
					shortDescription = shortDescription.substring(0, shortDescription.length() - 2);	// TODO use StringBuilder for better performance
					length = ChatUtil.getStringWidth(shortDescription);
					if (length <= 1255) {
						shortDescription += "...";
					}
				}
				
				StringBuilder strBuilder = new StringBuilder();
				// TODO customizable colors
				strBuilder.append(ChatColor.GOLD.toString()).append(CMD.replace("%cmd%", command)).append("\n").append(ChatColor.RED.toString());
				int partLength = 0;
				boolean firstIteration = true;
				for (String part : DESCRIPTION.split(" ")) {
					if (partLength > 1000) {	// TODO customizable length
						strBuilder.append("\n").append(ChatColor.RED.toString()).append(part);	// TODO customizable colors
						partLength = 0;
					} else {
						if (firstIteration) {
							strBuilder.append(part);
							firstIteration = false;
						} else {
							strBuilder.append(" ").append(part);
						}
					}
					partLength += ChatUtil.getStringWidth(part);
				}
				
				String suggestCommand = command;
					
				final String JSON = 
					"{\"text\":\"\",\"extra\":[{\"text\":\""
					+ shortDescription +
					"\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/"
					+ suggestCommand +
					"\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\""
					+ strBuilder.toString() +
					"\"}]}}}]}";
				
				if (!(sender instanceof Player) || !CraftBukkitUtil.sendJSONText((Player) sender, JSON)) {
					sender.sendMessage(shortDescription);
				}
			} else {			
				sender.sendMessage(getText().replace("%cmd%", command));				
			}	// end if
		}

		/**
		 * Gets the complete text which the player will see.
		 * 
		 * @return The complete text which the player will see.
		 */
		public String getText() {
			return FULL_TEXT;
		}

	}
	
}
