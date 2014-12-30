package de.oppermann.bastian.spleef.util;

import org.bukkit.ChatColor;

import de.oppermann.bastian.spleef.SpleefMain;

public enum Language {
	
	// command stuff
	COMMAND_JOIN("join"),
	COMMAND_JOIN_DESCRIPTION("Attempts to join an arena with the given name"),
	
	COMMAND_STATS("stats"),
	COMMAND_STATS_DESCRIPTION("Shows your statistics"),
	
	COMMAND_CREATE("create"),
	COMMAND_CREATE_DESCRIPTION("Creates a new arena"),
	
	COMMAND_CREATELOBBY("createLobby"),
	COMMAND_CREATELOBBY_DESCRIPTION("Creates a new lobby"),
	
	COMMAND_ADD_BLOCKS("addBlocks"),
	COMMAND_ADD_BLOCKS_DESCRIPTION("Adds blocks to the arena"),
	
	COMMAND_SET_LOBBY("setLobby"),
	COMMAND_SET_LOBBY_DESCRIPTION("Sets the lobby of the arena"),
	
	COMMAND_ADD_SPAWNLOCATION("addSpawnloc"),
	COMMAND_ADD_SPAWNLOCATION_DESCRIPTION("Sets the location you are standing on as spawnlocation. This also includes yaw and pitch! Use the -lobby flag to add the spawnlocation for a lobby and not for an arena."),
	COMMAND_ADD_SPAWNLOCATION_LOBBYFLAG("-lobby"),
	
	COMMAND_SETVALUE("setValue"),
	COMMAND_SETVALUE_DESCRIPTION("Sets a flag of the arena"),

	// stuff
	NO_ARENA_WITH_NAME(ChatColor.RED + "There's no arena with the name " + ChatColor.GOLD + "%arena% " + ChatColor.RED + "!"),	
	NO_LOBBY_WITH_NAME(ChatColor.RED + "There's no lobby with the name " + ChatColor.GOLD + "%lobby% " + ChatColor.RED + "!"),	
	ALREADY_ARENA_WITH_NAME(ChatColor.RED + "There's already an arena with the name " + ChatColor.GOLD + "%arena% " + ChatColor.RED + "!"),
	ALREADY_LOBBY_WITH_NAME(ChatColor.RED + "There's already a lobby with the name " + ChatColor.GOLD + "%lobby% " + ChatColor.RED + "!"),

	JOINED_ARENA(ChatColor.YELLOW + "Joined arena %arena%!"),
	JOINED_ARENA_STATUS_WAITING_FOR_PLAYERS(ChatColor.RED + "Waiting for players... (%players%/%maxPlayers%)"),
	JOINED_ARENA_STATUS_GAME_STARTS_IN(ChatColor.GREEN + "Game starts in %seconds% seconds"),
	
	SUCCESSFULLY_CREATED_ARENA(ChatColor.GREEN + "Arena " + ChatColor.GOLD + "%arena% " + ChatColor.GREEN + " created."
			+ "%n" + ChatColor.GREEN + ChatColor.UNDERLINE +  "Things you should do now:"
			+ "%n" + ChatColor.GREEN + "1. " + ChatColor.GOLD + "/spleef addblocks" + ChatColor.GREEN + " to add blocks."
			+ "%n" + ChatColor.GREEN + "2. " + ChatColor.GOLD + "/spleef addspawnloc" + ChatColor.GREEN + " to add spawnlocations."
			+ "%n" + ChatColor.GREEN + "3. " + ChatColor.GOLD + "/spleef setLobby" + ChatColor.GREEN + " to set the lobby (additional)."
			+ "%n" + ChatColor.GREEN + "4. " + ChatColor.GOLD + "/spleef setvalue %arena% enabled true" + ChatColor.GREEN + " to enable the arena."
	),
	
	SUCCESSFULLY_CREATED_LOBBY(ChatColor.GREEN + "Lobby " + ChatColor.GOLD + "%lobby% " + ChatColor.GREEN + " created."
			+ "%n" + ChatColor.GREEN + ChatColor.UNDERLINE +  "Things you should do now:"
			+ "%n" + ChatColor.GREEN + "1. " + ChatColor.GOLD + "/spleef addspawnloc -lobby" + ChatColor.GREEN + " to add spawnlocations."
	),

	WORLDEDIT_NOT_LOADED(ChatColor.RED + "Worldedit is missing."),
	WORLDEDIT_NO_SELECTION(ChatColor.RED + "No worldedit area is selected."),
	WORLDEDIT_SELECTION_IN_OTHER_WORLD(ChatColor.RED + "The selected points must be in the world of the arena."),

	SUCCESSFULLY_ADDED_BLOCKS(ChatColor.GREEN + "Added %amount% blocks."),
	
	SUCCESSFULLY_ADDED_SPAWNLOCATION(ChatColor.GREEN + "Added spawnlocation."),
	
	SUCCESSFULLY_SET_LOBBY(ChatColor.GREEN + "Set lobby."),	

	SUCCESSFULLY_SET_VALUE(ChatColor.GREEN + "Set " + ChatColor.GOLD + "%flag%" + ChatColor.GREEN + " to " + ChatColor.GOLD + "%value%" + ChatColor.GREEN + "."),
	UNKNOWN_FLAG(ChatColor.RED + "There's no flag called " + ChatColor.GOLD + "%flag" + ChatColor.RED + "."),
	INVALID_VALUE(ChatColor.RED + "Invalid value."),
	
	VALUE_MUST_BE_A_NUMBER(ChatColor.RED + "The value must be a number."),
	
	// flags
	FLAG_ENABLED("enabled"),
	FLAG_SNOWBALLS_ENABLED("snowballsEnabled"),
	FLAG_MAX_SNOWBALLS("maxSnowballs"),
	
	// valued
	VALUE_TRUE("true"),
	VALUE_FALSE("false"),
	
	// items
	HIDE_PLAYERS_ITEM(ChatColor.LIGHT_PURPLE + "Hide players"),
	STATS_ITEM(ChatColor.AQUA + "Statistics"),
	SHOP_ITEM(ChatColor.GOLD + "Shop"),
	LEAVE_ARENA_ITEM(ChatColor.RED + "Leave arena"),
	
	//game status
	STATUS_WAITING_FOR_PLAYERS(ChatColor.GREEN + "Waiting for players"),
	STATUS_ACTIVE(ChatColor.RED + "Game running"),
	STATUS_DISABLED(ChatColor.RED + "Disabled"),
	
	// single words
	ARGUMENT_ARENA("<arena>"),
	ARGUMENT_LOBBY("<lobby>"),
	ARGUMENT_FLAG("<flag>"),
	ARGUMENT_VALUE("<value>"),
	
	HELP_HEADLINE(ChatColor.BLUE + " Help for " + ChatColor.RED + "/%cmd% "),
	WRONG_USAGE(ChatColor.RED + "Wrong usage! Please type " + ChatColor.GOLD + "/%cmd% ? " + ChatColor.RED + "for more information!"),
	ONLY_PLAYER("This command is only for players!"),
	NO_PERMISSION(ChatColor.RED + "You are not allowed to use this command!");
	
	private String defaultText;
	private String path;
	
	private String text;
	
	private Language(String defaultText) {
		construct(defaultText, name().toLowerCase());
	}
	
	private Language(String defaultText, String path) {
		construct(defaultText, path);
	}
	
	private void construct(String defaultText, String path) {
		Validator.validateNotNull(defaultText, "defaultText");
		Validator.validateNotNull(path, "path");
		
		if (SpleefMain.getInstance() == null || SpleefMain.getInstance().getLanguageConfigAccessor() == null) {
			throw new IllegalStateException("The language file is not ready!");
		}
		
		this.defaultText = defaultText.replaceAll("§((?i)[0-9a-fk-or])", "&$1").replace("\n", "%n");	// replace the ChatColors with &-colorcodes
		this.path = path;
		
		SpleefMain.getInstance().getLanguageConfigAccessor().getConfig().addDefault(path, this.defaultText);
		SpleefMain.getInstance().getLanguageConfigAccessor().getConfig().options().copyDefaults(true);
		SpleefMain.getInstance().getLanguageConfigAccessor().saveConfig();
		this.text = SpleefMain.getInstance().getLanguageConfigAccessor().getConfig().getString(path, this.defaultText);
		this.text = this.text.replaceAll("&((?i)[0-9a-fk-or])", "§$1").replace("%n", "\n");	// replace the &-colorcodes with the ChatColor
	}
	
	/**
	 * Gets the default text.
	 */
	public String getDefaultText() {
		return defaultText;
	}
	
	/**
	 * Gets the path.
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Gets the text.
	 */
	public String getText() {
		return text;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getText();
	}

}
