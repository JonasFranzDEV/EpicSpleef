package de.oppermann.bastian.spleef.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public enum SpleefMode {

	
	NORMAL(new ItemStack(Material.DIAMOND_SPADE, 1)),
	SUPER_SPLEEF(new ItemStack(Material.DIAMOND_SPADE, 1)),
	BOWSPLEEF(new Object() {
		public ItemStack createItemStack() {
			ItemStack bow = new ItemStack(Material.BOW);
			bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
			return bow;
		}
	}.createItemStack(), new ItemStack(Material.ARROW, 1)),
	SPLEGG(new ItemStack(Material.DIAMOND_HOE, 1)),
	SUPER_SPLEGG(new ItemStack(Material.DIAMOND_HOE, 1)),
	SPLEEF_RUN();
	
	private ItemStack[] items;
	
	private SpleefMode(ItemStack... items) {
		this.items = items;
	}

	public ItemStack[] getItems() {
		return items;
	}
	
	public String getLanguageName() {
		switch (this) {
			case BOWSPLEEF:
				return Language.VALUE_MODE_BOWSPLEEF.toString();
			case NORMAL:
				return Language.VALUE_MODE_NORMAL.toString();
			case SPLEEF_RUN:
				return Language.VALUE_MODE_SPLEEF_RUN.toString();
			case SPLEGG:
				return Language.VALUE_MODE_SPLEGG.toString();
			case SUPER_SPLEEF:
				return Language.VALUE_MODE_SUPER_SPLEEF.toString();
			case SUPER_SPLEGG:
				return Language.VALUE_MODE_SUPER_SPLEGG.toString();	
		}
		return "unknown";
	}
	
}
