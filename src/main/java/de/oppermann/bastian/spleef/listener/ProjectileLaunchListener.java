package de.oppermann.bastian.spleef.listener;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.GameStatus;
import de.oppermann.bastian.spleef.util.PlayerManager;

public class ProjectileLaunchListener implements Listener {
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Player shooter = null;
		
		Projectile projectile = event.getEntity();
		
		if (projectile.getShooter() instanceof Player) {
			shooter = (Player) projectile.getShooter();
		}	
		
		if (shooter == null) {
			return;
		}
		
		SpleefArena arena = PlayerManager.getArena(shooter.getUniqueId());
		
		if (arena == null) {
			return;
		}
		
		if (arena.getStatus() != GameStatus.ACTIVE || arena.countdownIsActive()) {
			event.setCancelled(true);
			return;
		}
		
		// splegg weapon
		if (shooter.getItemInHand() != null && shooter.getItemInHand().getType() == Material.DIAMOND_HOE) {
			return;
		}
		
		if (projectile.getType() == EntityType.EGG) {
			projectile.setMetadata("EpicSpleef:Grenade", new FixedMetadataValue(SpleefMain.getInstance(), 1));
		}
	}

}
