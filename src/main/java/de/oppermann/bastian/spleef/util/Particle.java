package de.oppermann.bastian.spleef.util;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.oppermann.bastian.spleef.SpleefMain;

public enum Particle {
	
	ENCHANTMENT(0, Material.BOOK, (byte) 0, Language.PARTICLE_ENCHANTMENT.toString(), Language.PARTICLE_ENCHANTMENT_DESCRIPTION.toString(), "enchantment"),
	CRAPPING(1, Material.SOUL_SAND, (byte) 0, Language.PARTICLE_CRAPPING.toString(), Language.PARTICLE_CRAPPING_DESCRIPTION.toString(), "crapping"),
	@SuppressWarnings("deprecation")	// who cares?
	HEART(2, Material.WOOL, DyeColor.RED.getWoolData(), Language.PARTICLE_HEART.toString(), Language.PARTICLE_HEART_DESCRIPTION.toString(), "hearts"),
	RAINCLOUD(3, Material.INK_SACK, (byte) 4, Language.PARTICLE_RAINCLOUD.toString(), Language.PARTICLE_RAINCLOUD_DESCRIPTION.toString(), "raincloud"),
	@SuppressWarnings("deprecation")	// who cares?
	CLOUD_TAIL(4, Material.WOOL, DyeColor.WHITE.getWoolData(), Language.PARTICLE_CLOUD_TAIL.toString(), Language.PARTICLE_CLOUD_TAIL_DESCRIPTION.toString(), "cloudtail"),
	FLAMES(5, Material.BLAZE_POWDER, (byte) 0, Language.PARTICLE_FLAMES.toString(), Language.PARTICLE_FLAMES_DESCRIPTION.toString(), "flames"),
	SPELL(6, Material.ENCHANTED_BOOK, (byte) 0, Language.PARTICLE_SPELL.toString(), Language.PARTICLE_SPELL_DESCRIPTION.toString(), "spell"),
	GREEN_PATH(7, Material.EMERALD, (byte) 0, Language.PARTICLE_GREEN_PATH.toString(), Language.PARTICLE_GREEN_PATH_DESCRIPTION.toString(), "greenPath");
	
	private int id;
	private Material type;
	byte data;
	String displayName;
	String description;
	int price;
	String particleConfigName;
	
	private Particle(int id, Material type, byte data, String displayName, String description, String particleConfigName) {
		this.id = id;
		this.type = type;
		this.data = data;
		this.displayName = displayName;
		this.description = description;
		this.price = SpleefMain.getInstance().getParticleConfigAccessor().getConfig().getInt("particles." + particleConfigName + ".price", 1500);
		this.particleConfigName = particleConfigName;
	}

	public int getId() {
		return id;
	}
	
	public int getPrice() {
		return price;
	}
	
	public boolean isEnabled() {
		return SpleefMain.getInstance().getParticleConfigAccessor().getConfig().getBoolean("particles." + particleConfigName + ".enabled", true);
	}
	
	public ItemStack getItem(boolean bought) {
		return createItemStack(
				bought ? type : Material.INK_SACK, 
				bought ? data : (byte) 8,
				(bought ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD.toString() + displayName,
				ChatColor.BLUE + description,
				bought ? null : this != HEART ?Language.PARTICLEPRICE.toString().replace("%price%", String.valueOf(price)) : Language.PARTICLEPRICE_HEART.toString().replace("%price%", String.valueOf(price)));
	}
	
	public static Particle getParticle(ItemStack itemStack) {
		for (Particle particle : values()) {
			if (itemStack.isSimilar(particle.getItem(true)) || itemStack.isSimilar(particle.getItem(false))) {
				return particle;
			}
		}
		return null;
	}
	
	private static ItemStack createItemStack(Material type, byte data, String displayName, String... lore) {
		ItemStack itemStack = new ItemStack(type, 1, data);
		ItemMeta meta = itemStack.getItemMeta();
		
		meta.setDisplayName(displayName);
		itemStack.setItemMeta(meta);
		
		ArrayList<String> loreList = new ArrayList<>();
		for (String str : lore) {
			if (str != null) {
				loreList.add(str);
			}
		}				
		meta.setLore(loreList);
		
		itemStack.setItemMeta(meta);
		return itemStack;
	}

}
