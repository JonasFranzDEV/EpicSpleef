package de.oppermann.bastian.spleef.listener;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import com.google.common.util.concurrent.FutureCallback;

import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.util.ParticleEffect;
import de.oppermann.bastian.spleef.util.PlayerManager;
import de.oppermann.bastian.spleef.util.SpleefPlayer;

public class ProjectileHitListener implements Listener {
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {		
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
		
		if (projectile.hasMetadata("EpicSpleef:Grenade")) {
			for (Block block : getSphere(getHittenBlock(projectile).getLocation(), 3)) {
				remove(arena, block, shooter);
			}
			event.getEntity().remove();
			return;
		}
		
		Block hittenBlock = getHittenBlock(projectile);
		remove(arena, hittenBlock, shooter);
		event.getEntity().remove();	// remove the entity	
	}
	
	private void remove(SpleefArena arena, Block block, Player shooter) {
		if (!arena.isArenaBlock(block)) {
			return;
		}
			
		
		Material oldType = block.getType();
		@SuppressWarnings("deprecation")	// cause mojang sucks ...
		byte oldData = block.getData();		
		
		block.setType(Material.AIR);	// sets the block to air
		
		
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
		
		// animateFlyingUp(hittenBlock, oldType, oldData);		// TODO option in config
		animateBreak(block, oldType, oldData);
	}
	
	@SuppressWarnings("deprecation")
	private void animateBreak(Block hittenBlock, Material oldType, byte oldData) {
		ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(oldType, oldData), 0F, 0F, 0F, 1, 32, hittenBlock.getLocation(), 32);		
		hittenBlock.getWorld().playEffect(hittenBlock.getLocation(), Effect.STEP_SOUND, oldType.getId());
	}
	
	/*
	private void animateFlyingUp(Block hittenBlock, Material oldType, byte oldData) {
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
	*/

	private static ArrayList<Block> getSphere(Location center, int radius) {
		Vector vec = new BlockVector(center.getBlockX(), center.getY(), center.getZ());
		int x2 = center.getBlockX();
		int y2 = center.getBlockY();
		int z2 = center.getBlockZ();
		// world
		World world = center.getWorld();
	        
		ArrayList<Block> blocks = new ArrayList<>();
		
		// iterate through all blocks
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					if (y+y2 < 0 || y+y2 > 256)
						continue;
					
					Vector position = vec.clone().add(new Vector(x, y, z));
					
					// check if is cricle
					if (vec.distanceSquared(position) <= (radius + 0.5) * (radius + 0.5)) {
						Block block = world.getBlockAt(x+x2, y+y2, z+z2);
						blocks.add(block);
					}
				}
			}
		}
	        
		return blocks;
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
