package de.oppermann.bastian.spleef.util.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.util.concurrent.FutureCallback;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.PlayerManager;
import de.oppermann.bastian.spleef.util.SpleefPlayerStats;
import de.oppermann.bastian.spleef.util.Validator;

public enum GuiInventory {

	JOIN(0),
	SHOP(1),
	STATISTICS(2);
	
	private static final HashMap<UUID, Inventory> LAST_CREATED = new HashMap<>();
	private static final HashMap<UUID, GuiInventory> LAST_CREATED_TYPE = new HashMap<>();
	
	private final Inventory INVENTORY;
	
	private static SpleefArena joinArenasPosition[] = null;
	
	private GuiInventory(int id) {
		switch (id) {
		default:
			INVENTORY = Bukkit.createInventory(null, 9 * 5, "ERROR");
			break;
		}
	}
	
	/**
	 * Gets the type of the last created inventory of the player.
	 */
	public static GuiInventory getLastCreatedInventoryType(UUID player) {
		Validator.validateNotNull(player, "player");
		return LAST_CREATED_TYPE.get(player);
	}
	
	/**
	 * Removes the last created inventory of the player.
	 */
	public static void onInventoryClose(UUID player) {
		Validator.validateNotNull(player, "player");
		LAST_CREATED.remove(player);
		LAST_CREATED_TYPE.remove(player);
		joinArenasPosition = null;
	}
	
	/**
	 * Gets the last created inventory of the player.
	 */
	public static Inventory getLastCreatedInventory(UUID player) {
		Validator.validateNotNull(player, "player");
		return LAST_CREATED.get(player);
	}
	
	/**
	 * Gets the position of the arena in the join inventory.
	 */
	public static SpleefArena getArena(int position) {
		if (joinArenasPosition == null) {
			return null;
		}
		if (joinArenasPosition.length <= position || position < 0) {
			return null;
		}
		return joinArenasPosition[position];
	}
	
	/**
	 * Creates an inventory for the player.
	 */
	public Inventory createInventory(Player player) {		
		Validator.validateNotNull(player, "player");
		
		LAST_CREATED_TYPE.put(player.getUniqueId(), this);
		
		if (this == SHOP) {
			Inventory inventory = Bukkit.createInventory(null, 9 * 5, Language.SHOP_ITEM.toString());
			
			inventory.setItem(10, createItemStack(
					Material.BOOK, 
					ChatColor.WHITE + "Enchantment particles",	// TODO customizable text
					ChatColor.RED + "Costs: " + ChatColor.GOLD + "100 points",	// TODO own price and customizable text
					ChatColor.BLUE + "Looks very cool!"));	// TODO customizable text
			
			inventory.setItem(12, createItemStack(
					Material.SOUL_SAND,
					ChatColor.WHITE + "Crapping",	// TODO customizable text
					ChatColor.RED + "Costs: " + ChatColor.GOLD + "500 points",	// TODO own price and customizable text
					ChatColor.BLUE + "That's ugly :/"));	// TODO customizable text
			
			LAST_CREATED.put(player.getUniqueId(), inventory);
			return inventory;
		}
		
		if (this == JOIN) {
			Inventory inventory = Bukkit.createInventory(null, 9 * 5, ChatColor.DARK_GREEN + "Select arena");	// TODO customizable text
			int i = 0;
			for (SpleefArena arena : SpleefArena.getSpleefArenas()) {
				ItemStack itemStack = new ItemStack(Material.DIAMOND_SPADE, arena.getPlayers().size() > 64 ? 64 : arena.getPlayers().size());
				ItemMeta meta = itemStack.getItemMeta();
				
				meta.setDisplayName(ChatColor.GOLD + arena.getName());	// TODO customizable color
				
				ArrayList<String> lore = new ArrayList<>();
				lore.add(ChatColor.ITALIC + ChatColor.GRAY.toString() + "Click to join arena " + arena.getName());	// TODO customizable text
				lore.add(arena.getPlayers().size() + " / " + arena.getSpawnLocations().size() + " players");	// TODO customizable text
				lore.add(arena.getStatus().toString());
				meta.setLore(lore);
				
				itemStack.setItemMeta(meta);
				inventory.setItem(i++, itemStack);
			}
			joinArenasPosition = SpleefArena.getSpleefArenas();
			
			LAST_CREATED.put(player.getUniqueId(), inventory);
			return inventory;
		}	
		
		if (this == STATISTICS) {
			final Inventory INVENTORY = Bukkit.createInventory(null, 9 * 5, Language.STATS_ITEM.toString());
			final UUID PLAYER = player.getUniqueId();
			
			SpleefPlayerStats.getPlayerStats(player.getUniqueId(), new FutureCallback<SpleefPlayerStats>() {
					
				@Override
				public void onSuccess(SpleefPlayerStats stats) {
					
					ItemStack globalStatsItemStack = GuiItem.STATSINV_GLOBAL.getItem();
					ItemMeta globalStatsMeta = globalStatsItemStack.getItemMeta();
					ArrayList<String> globalStatsLore = new ArrayList<>();
					globalStatsLore.add(ChatColor.GREEN + "Wins: " + ChatColor.RED + stats.getTotalWins());	// TODO customizable text
					globalStatsLore.add(ChatColor.GREEN + "Losses: " + ChatColor.RED + stats.getTotalLosses());	// TODO customizable text
					globalStatsLore.add(ChatColor.GREEN + "Jumps: " + ChatColor.RED + stats.getTotalJumps());	// TODO customizable text
					globalStatsLore.add(ChatColor.GREEN + "Destroyed blocks: " + ChatColor.RED + stats.getTotalDestroyedBlocks());	// TODO customizable text						
					globalStatsLore.add(ChatColor.GREEN + "Earned points: " + ChatColor.RED + stats.getTotalEarnedPoints());	// TODO customizable text						
					globalStatsMeta.setLore(globalStatsLore);
					
					globalStatsItemStack.setItemMeta(globalStatsMeta);
					INVENTORY.setItem(4, globalStatsItemStack);
					
					SpleefArena playerArena = PlayerManager.getArena(PLAYER);
					int i = 18;
					for (SpleefArena arena : SpleefArena.getSpleefArenas()) {
						Material type = playerArena == arena ? Material.GOLD_SPADE : Material.DIAMOND_SPADE;	// if the player is in a arena
						ItemStack itemStack = new ItemStack(type, 1);
						ItemMeta meta = itemStack.getItemMeta();
						
						meta.setDisplayName(ChatColor.GOLD + arena.getName());	// TODO customizable color
						itemStack.setItemMeta(meta);
						
						ArrayList<String> lore = new ArrayList<>();
						lore.add(ChatColor.GREEN + "Wins: " + ChatColor.RED + stats.getWins(arena.getName()));	// TODO customizable text
						lore.add(ChatColor.GREEN + "Losses: " + ChatColor.RED + stats.getLosses(arena.getName()));	// TODO customizable text
						lore.add(ChatColor.GREEN + "Jumps: " + ChatColor.RED + stats.getJumps(arena.getName()));	// TODO customizable text
						lore.add(ChatColor.GREEN + "Destroyed blocks: " + ChatColor.RED + stats.getDestroyedBlocks(arena.getName()));	// TODO customizable text						
						lore.add(ChatColor.GREEN + "Earned points: " + ChatColor.RED + stats.getPoints(arena.getName()));	// TODO customizable text						
						meta.setLore(lore);
						
						itemStack.setItemMeta(meta);
						INVENTORY.setItem(i++, itemStack);
					}
				}
				
				@Override
				public void onFailure(Throwable e) {
					e.printStackTrace();
				}
			});
				
				
			LAST_CREATED.put(player.getUniqueId(), INVENTORY);
			return INVENTORY;			
		}
		
		LAST_CREATED.put(player.getUniqueId(), INVENTORY);
		return INVENTORY;
	}
	
	private ItemStack createItemStack(Material type, String displayName, String... lore) {
		ItemStack itemStack = new ItemStack(type, 1);
		ItemMeta meta = itemStack.getItemMeta();
		
		meta.setDisplayName(displayName);
		itemStack.setItemMeta(meta);
		
		ArrayList<String> loreList = new ArrayList<>();
		for (String str : lore) {
			loreList.add(str);
		}				
		meta.setLore(loreList);
		
		itemStack.setItemMeta(meta);
		return itemStack;
	}
 
}
