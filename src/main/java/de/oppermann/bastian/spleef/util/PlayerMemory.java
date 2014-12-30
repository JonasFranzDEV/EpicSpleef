package de.oppermann.bastian.spleef.util;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Class that helps to save and reset the state of a player.
 */
public class PlayerMemory {

	private final Location LOCATION;
	
	private final double MAX_HEALTH;
	private final double HEALTH;	
	private final int LEVEL;
	private final float EXP;
	private final int FOOD_LEVEL;
	
	private GameMode GAME_MODE;
	
	private boolean CAN_PICKUP_ITEMS;	

	private final ItemStack[] INVENTORY_CONTENTS;
	private final ItemStack[] INVENTORY_ARMORCONTENTS;
	
	private final String DISPLAY_NAME;
	
	private final Scoreboard SCOREBOARD;
	
	// stuff that may be not wanted to reset
	boolean resetLocation = true;
	boolean resetInventory = true;
	boolean resetLevel = true;
	
	/**
	 * Class constructor.
	 * 
	 * @param player The player.
	 */
	public PlayerMemory(Player player) {
		LOCATION = player.getLocation();
		
		MAX_HEALTH = player.getMaxHealth();		
		HEALTH = player.getHealth();
		LEVEL = player.getLevel();
		EXP = player.getExp();
		FOOD_LEVEL = player.getFoodLevel();
		
		GAME_MODE = player.getGameMode();
		
		CAN_PICKUP_ITEMS = player.getCanPickupItems();

		INVENTORY_CONTENTS = player.getInventory().getContents();
		INVENTORY_ARMORCONTENTS = player.getInventory().getArmorContents();
		
		DISPLAY_NAME = player.getDisplayName();
		
		SCOREBOARD = player.getScoreboard();
	}
	
	/**
	 * Restores the original state of the player.
	 * 
	 * @param player The player.
	 */
	public void restore(Player player) {
		if (resetLocation) {
			player.teleport(LOCATION);
		}
		player.setMaxHealth(MAX_HEALTH);
		player.setHealth(HEALTH);
		if (resetLevel) {
			player.setLevel(LEVEL);
			player.setExp(EXP);
		}
		player.setFoodLevel(FOOD_LEVEL);
		player.setGameMode(GAME_MODE);
		player.setCanPickupItems(CAN_PICKUP_ITEMS);
		if (resetInventory) {
			player.getInventory().setContents(INVENTORY_CONTENTS);
			player.getInventory().setArmorContents(INVENTORY_ARMORCONTENTS);
		}
		player.setDisplayName(DISPLAY_NAME);
		player.setFallDistance(0F);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.setScoreboard(SCOREBOARD == null ? Bukkit.getScoreboardManager().getMainScoreboard() : SCOREBOARD);
	}
	
	/**
	 * Sets if the location should be restored.
	 */
	public void setResetLocation(boolean resetLocation) {
		this.resetLocation = resetLocation;
	}
	
	/**
	 * Sets if the inventory should be restored.
	 */
	public void setResetInventory(boolean resetInventory) {
		this.resetInventory = resetInventory;
	}
	
	/**
	 * Sets if the level should be restored.
	 */
	public void setResetLevel(boolean resetLevel) {
		this.resetLevel = resetLevel;
	}
	
}
