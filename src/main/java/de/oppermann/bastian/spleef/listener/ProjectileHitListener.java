package de.oppermann.bastian.spleef.listener;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.google.common.util.concurrent.FutureCallback;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.PlayerManager;
import de.oppermann.bastian.spleef.util.SpleefPlayer;

public class ProjectileHitListener implements Listener {
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {		
		Player shooter = null;
		
		if (event.getEntity().getShooter() instanceof Player) {
			shooter = (Player) event.getEntity().getShooter();
		}	
		
		if (shooter == null) {
			return;
		}
		
		SpleefArena arena = PlayerManager.getArena(shooter.getUniqueId());
		
		if (arena == null) {
			return;
		}
		
		Block hittenBlock = getHittenBlock(event.getEntity());
		
		if (!arena.isArenaBlock(hittenBlock)) {
			return;
		}
		
		event.getEntity().remove();	// remove the entity		
		
		Material oldType = hittenBlock.getType();
		@SuppressWarnings("deprecation")	// cause mojang sucks ...
		byte oldData = hittenBlock.getData();		
		
		hittenBlock.setType(Material.AIR);	// sets the block to air
		
		
		final SpleefArena ARENA = arena;
		SpleefPlayer.getPlayer(shooter.getUniqueId(), new FutureCallback<SpleefPlayer>() {
			@Override
			public void onFailure(Throwable e) {
				e.printStackTrace();
			}
	
			@Override
			public void onSuccess(SpleefPlayer stats) {
				stats.addDestroyedBlocks(ARENA.getName(), 1);
			}
		});
		
		
		@SuppressWarnings("deprecation")	// cause mojang sucks ...
		final Entity fallingBlock = hittenBlock.getWorld().spawnFallingBlock(hittenBlock.getLocation().add(0, 0.01, 0), oldType, oldData);
		fallingBlock.setVelocity(new Vector(0, 0.5, 0));	// let the block fly up
		
		Bukkit.getScheduler().runTaskLater(SpleefMain.getInstance(), new Runnable() {			
			@Override
			public void run() {
				boolean createExplosion = false;	// TODO add config option
				if (createExplosion) {
					fallingBlock.getWorld().createExplosion(fallingBlock.getLocation(), 0f);	// create explosion
				} else {
					for (int i = 0; i < 100; i++) {
						fallingBlock.getWorld().playEffect(fallingBlock.getLocation().add(Math.random(), Math.random(), Math.random()), Effect.CRIT, 0);
					}
					fallingBlock.getWorld().playSound(fallingBlock.getLocation(), Sound.FIZZ, 1, 1);
				}
				fallingBlock.remove();	// remove the falling block
			}
		}, 5);
	}
	
	private Block getHittenBlock(Entity entity) {
		World world = entity.getWorld();
	    BlockIterator iterator = new BlockIterator(world, entity.getLocation().toVector(), entity.getVelocity().normalize(), 0, 4);
	    Block block = null;

	    while (iterator.hasNext()) {
	        block = iterator.next();
	        if (block.getType().isSolid()) {	// if it is solid it's the hitten block
	            break;
	        }
	    }
	    return block;
	}

}
