package de.oppermann.bastian.spleef.util.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.oppermann.bastian.spleef.util.Language;

public enum GuiItem {

	LEAVE_ARENA(Language.LEAVE_ARENA_ITEM.toString(), Material.WATCH, (byte) 0),
	HIDE_PLAYERS(Language.HIDE_PLAYERS_ITEM.toString(), Material.SKULL_ITEM, (byte) 0),
	STATISTICS(Language.STATS_ITEM.toString(), Material.NETHER_STAR, (byte) 0),
	SHOP(Language.SHOP_ITEM.toString(), Material.CHEST, (byte) 0),
	
	STATSINV_GLOBAL(ChatColor.AQUA + "Global stats", Material.NETHER_STAR, (byte) 0);
	
	private final ItemStack ITEM;
	
	private GuiItem(String name, Material type, byte data) {
		ITEM = new ItemStack(type, 1, data);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(name);
		ITEM.setItemMeta(meta);
	}
	
	/**
	 * Gets the {@link ItemStack}.
	 */
	public ItemStack getItem() {
		return ITEM.clone();
	}
	
	/**
	 * Compares the item with an other {@link ItemStack}.
	 */
	public boolean compare(ItemStack itemStack) {
		return ITEM.equals(itemStack);
	}
	
	/**
	 * Gets the {@link GuiItem} from a given {@link ItemStack}.
	 */
	public static GuiItem getByItemStack(ItemStack itemStack) {
		for (GuiItem guiItem : values()) {
			if (guiItem.compare(itemStack)) {
				return guiItem;
			}
		}
		return null;
	}
	
}
