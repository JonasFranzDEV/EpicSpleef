package de.oppermann.bastian.spleef.util;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.ParticleEffect.BlockData;

/**
 * (Threadsafe class)
 */
public class ParticleCreatorTask implements Runnable {

	private static final HashMap<UUID, Particle> PLAYERS = new HashMap<>();
	
	@Override
	public void run() {
		synchronized (PLAYERS) {
			for (UUID playerUUID : PLAYERS.keySet()) {
				SpleefArena arena = PlayerManager.getArena(playerUUID);
				if (arena == null) {
					continue;
				}
				Player player = Bukkit.getPlayer(playerUUID);
				if (player != null) {
					Particle particle = PLAYERS.get(playerUUID);
					if (particle == Particle.CRAPPING) {
						ParticleEffect.BLOCK_DUST.display(new BlockData(Material.SOUL_SAND, (byte) 0), 0F, 0F, 0F, 0F, 15, player.getLocation().add(player.getLocation().getDirection().getX() * -0.5, 0.8F, player.getLocation().getDirection().getZ() * -0.5), 15);
					}
					if (particle == Particle.ENCHANTMENT) {
						if (arena.getStatus() == GameStatus.ACTIVE) {
							ParticleEffect.ENCHANTMENT_TABLE.display(0.5F, 0.5F, 0.5F, 1F, 7, player.getLocation().add(0, 0.8, 0), 15);							
						} else {
							ParticleEffect.ENCHANTMENT_TABLE.display(0.5F, 0.5F, 0.5F, 1F, 15, player.getLocation().add(0, 0.8, 0), 15);							
						}
					}
					if (particle == Particle.HEART) {
						if (arena.getStatus() == GameStatus.ACTIVE) {
							if (System.currentTimeMillis() % 3 == 0) {
								ParticleEffect.HEART.display(0.5F, 0.5F, 0.5F, 1F, 1, player.getLocation().add(0, 0.8, 0), 15);
							}
						} else {
							if (System.currentTimeMillis() % 2 == 0) {
								ParticleEffect.HEART.display(0.5F, 0.5F, 0.5F, 1F, 1, player.getLocation().add(0, 0.8, 0), 15);
							}
						}						
					}
					if (particle == Particle.RAINCLOUD) {
						ParticleEffect.WATER_DROP.display(0F, 0F, 0F, 0F, 4, player.getLocation().add((Math.random() -0.5) * 1.5, 3.5, (Math.random() - 0.5) * 1.5), 15);
						ParticleEffect.CLOUD.display(0F, 0F, 0F, 0.01F, 5, player.getLocation().add((Math.random() - 0.5) * 1.5, 3.5, (Math.random() - 0.5) * 1.5), 15);	
					}
					if (particle == Particle.CLOUD_TAIL) {
						if (arena.getStatus() == GameStatus.ACTIVE) {
							ParticleEffect.CLOUD.display(0F, 0.05F, 0F, 0.02F, 2, player.getLocation().add(player.getLocation().getDirection().getX() * -0.5, 0.8F, player.getLocation().getDirection().getZ() * -0.5), 15);
						} else {
							ParticleEffect.CLOUD.display(0F, 0.05F, 0F, 0.02F, 5, player.getLocation().add(player.getLocation().getDirection().getX() * -0.5, 0.8F, player.getLocation().getDirection().getZ() * -0.5), 15);
						}
					}
					if (particle == Particle.FLAMES) {
						if (arena.getStatus() == GameStatus.ACTIVE) {
							ParticleEffect.FLAME.display(0.1F, 0.1F, 0.1F, 0.05F, 5, player.getLocation().add(0, 0.75, 0), 15);
						} else {
							ParticleEffect.FLAME.display(0.1F, 0.1F, 0.1F, 0.05F, 10, player.getLocation().add(0, 0.75, 0), 15);
						}						
					}
					if (particle == Particle.SPELL) {
						if (arena.getStatus() == GameStatus.ACTIVE) {
							ParticleEffect.SPELL_WITCH.display(0.3F, 0F, 0.3F, 0.02F, 10, player.getLocation().add(0, 0.05, 0), 15);
						} else {
							ParticleEffect.SPELL_WITCH.display(0.3F, 0F, 0.3F, 0.02F, 25, player.getLocation().add(0, 0.05, 0), 15);
						}						
					}
					if (particle == Particle.GREEN_PATH) {
						if (arena.getStatus() == GameStatus.ACTIVE) {
							ParticleEffect.VILLAGER_HAPPY.display(0.07F, 0F, 0.07F, 0.02F, 6, player.getLocation().add(0, 0.05, 0), 15);							
						} else {
							ParticleEffect.VILLAGER_HAPPY.display(0.15F, 0F, 0.15F, 0.02F, 15, player.getLocation().add(0, 0.05, 0), 15);							
						}
					}
				}
			}
		}
	}
	
	public static Particle getParticleEffect(Player player) {
		synchronized (PLAYERS) {
			return PLAYERS.get(player.getUniqueId());
		}
	}
	
	public static void addPlayer(Player player, Particle effect) {
		synchronized (PLAYERS) {
			PLAYERS.put(player.getUniqueId(), effect);
		}
	}
	
	public static void removePlayer(Player player) {
		synchronized (PLAYERS) {
			PLAYERS.remove(player.getUniqueId());
		}
	}

}

