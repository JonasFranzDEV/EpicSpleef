package de.oppermann.bastian.spleef.util.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.oppermann.bastian.spleef.util.Validator;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandHelp;
import de.oppermann.bastian.spleef.util.command.SpleefCommand.CommandResult;

public abstract class AbstractArgument {

	private final String[] FIXED_ARGUMENTS;
	private final int NEEDED_ARGUMENTS;
	private final String PERMISSION, PERMISSION_OTHER, DESCRIPTION;

	/**
	 * Class constructor.
	 * 
	 * @param fixedArguments There are variable arguments and fixed.<p>
	 * 						For example <code>/economy set player money</code>.<p>
	 * 						<code>/economy</code> is the command, <code>set</code> is a fixed argument,
	 * 						<code>player</code> and <code>money</code> are variable.
	 * @param neededArguments The amount of needed arguments. Use <code>-1</code> for unlimited.
	 * @param permission The required permission for this command.
	 * @param permissionOther Used to do stuff for other players (e.g. <code>/spleef join arena1 player</code>
	 * @param description The description shown in the help-page (/command ?).
	 */
	public AbstractArgument(String[] fixedArguments, int neededArguments, String permission, String permissionOther, String description) {
		Validator.validateNotNull(fixedArguments, "fixedArguments");
		Validator.validateNotNull(description, "description");
		this.FIXED_ARGUMENTS = fixedArguments;
		this.NEEDED_ARGUMENTS = neededArguments;
		this.PERMISSION = permission;
		this.PERMISSION_OTHER = permissionOther;
		this.DESCRIPTION = description;
	}

	/**
	 * Gets the fixed arguments.
	 * 
	 * @return The fixed arguments.
	 */
	public String[] getFixedArguments() {
		return FIXED_ARGUMENTS;
	}

	/**
	 * Gets the required amount of arguments.
	 * 
	 * @return The required amount of arguments.
	 */
	public int getNeededArguments() {
		return NEEDED_ARGUMENTS;
	}
	
	/**
	 * Gets the required permission to use this argument.
	 * 
	 * @return The required permission.
	 */
	public String getPermission() {
		return PERMISSION;
	}

	/**
	 * Gets the required permission to use this argument to effect other players.
	 * 
	 * @return The required permission to effect other players.
	 */
	public String getPermissionOther() {
		return PERMISSION_OTHER;
	}

	/**
	 * Gets the description shown in the help-page (/command ?)
	 * 
	 * @return The description shown in the help-page (/command ?).
	 */
	public String getDescription() {
		return DESCRIPTION;
	}

	/**
	 * Helps for tabcomplete.
	 * <p>
	 * Often you have to autocomplete the name of online players.
	 * This method gives you a list of all online players which starts with the given start letter(s).
	 * 
	 * @param startLetter First letter(s) with which the name starts.
	 * @param without Disregarded a player, null if with everybody.
	 * @return A list of all online players which starts with the given start letter(s).
	 */
	public List<String> getPlayers(String startLetter, Player without) {
		Validator.validateNotNull(startLetter, "startLetter");
		List<String> list = new ArrayList<String>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p != without) {
				if (p.getName().toLowerCase().startsWith(startLetter.toLowerCase())) {
					list.add(p.getName());
				}
			}
		}
		return list;
	}

	/**
	 * Checks if the argument should be called.
	 * <p>
	 * This method is intern required and can be ignored.
	 * 
	 * @param args The string array with all the arguments.
	 * @return <code>true</code> if should be called, else false.
	 */
	public boolean checkArgs(String[] args) {
		Validator.validateNotNull(args, "args");
		if (NEEDED_ARGUMENTS >= 0 && args.length != NEEDED_ARGUMENTS) {
			return false;
		}
		if (NEEDED_ARGUMENTS <= 0 && FIXED_ARGUMENTS.length > args.length) {
			return false;
		}
		for (int i = 0; i < FIXED_ARGUMENTS.length; i++) {
			if (!args[i].equalsIgnoreCase(FIXED_ARGUMENTS[i])) {
				return false;
			}
		}
		return true;
	}

	public abstract CommandResult executeForPlayer(Player p, Command cmd, String[] args);

	public abstract CommandResult executeForServer(CommandSender sender, Command cmd, String[] args);

	public abstract List<String> onTabComplete(Player sender, String[] args);

	public abstract CommandHelp getCommandHelp();
	
}