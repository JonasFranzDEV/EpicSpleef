package de.oppermann.bastian.spleef.util.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.Particle;

public enum ShopItem {

	ENCHANTMENT(Particle.ENCHANTMENT, Language.LEAVE_ARENA_ITEM.toString(), Material.WATCH, (byte) 0),
	CRAPPING(Particle.CRAPPING, Language.HIDE_PLAYERS_ITEM.toString(), Material.SKULL_ITEM, (byte) 0);
	
	private final Particle PARTICLE;
	private final ItemStack ITEM;
	
	private ShopItem(Particle particle, String name, Material type, byte data, String... lore) {
		PARTICLE = particle;
		ITEM = new ItemStack(type, 1, data);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(name);
		ITEM.setItemMeta(meta);
	}
	
	public Particle getParticle() {
		return PARTICLE;
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
	 * Gets the {@link ShopItem} from a given {@link ItemStack}.
	 */
	public static ShopItem getByItemStack(ItemStack itemStack) {
		for (ShopItem shopItem : values()) {
			if (shopItem.compare(itemStack)) {
				return shopItem;
			}
		}
		return null;
	}
	
}
