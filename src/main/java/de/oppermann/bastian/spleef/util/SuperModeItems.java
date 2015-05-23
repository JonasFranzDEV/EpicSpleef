package de.oppermann.bastian.spleef.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum SuperModeItems {

	SPEED_BOOST(Material.LEATHER_BOOTS, (byte) 0),
	JUMP_BOOST(Material.FEATHER, (byte) 0),
	INVISIBILITY(Material.EYE_OF_ENDER, (byte) 0),
	GRENADE(Material.EGG, (byte) 0);
	
	private final Material TYPE;
	private final byte DATA;
	
	private SuperModeItems(Material type, byte data) {
		this.TYPE = type;
		this.DATA = data;
	}

	public ItemStack getItemStack() {
		ItemStack is = new ItemStack(TYPE, 1, DATA);
		return is;
	}
	
	
}
