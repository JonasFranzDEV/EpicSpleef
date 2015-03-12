package de.oppermann.bastian.spleef.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public enum SpleefMode {

	
	NORMAL(new ItemStack(Material.DIAMOND_SPADE, 1)),
	BOWSPLEEF(new Object() {
		public ItemStack createItemStack() {
			ItemStack bow = new ItemStack(Material.BOW);
			bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
			return bow;
		}
	}.createItemStack(), new ItemStack(Material.ARROW, 1)),
	SPLEGG(new ItemStack(Material.DIAMOND_HOE, 1)),
	SPLEEF_RUN();
	
	private ItemStack[] items;
	
	private SpleefMode(ItemStack... items) {
		this.items = items;
	}

	public ItemStack[] getItems() {
		return items;
	}
	
}
