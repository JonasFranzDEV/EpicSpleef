package de.oppermann.bastian.spleef.util;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import de.oppermann.bastian.spleef.SpleefMain;

public enum BlockRemoveAnimation {

	BREAK(0),
	FLY_UP(1),
	FLY_UP_WITH_EXPLOSION(2);
	
	private final int ID;
	
	private BlockRemoveAnimation(int id) {
		this.ID = id;
	}
	
	public void animate(Block block, Material oldType, byte oldData) {
		switch (this) {
			case BREAK:
				animateBreak(block, oldType, oldData);
				break;
			case FLY_UP:
				animateFlyUp(block, oldType, oldData, false);
				break;
			case FLY_UP_WITH_EXPLOSION:
				animateFlyUp(block, oldType, oldData, true);
				break;
			default:
				break;
		}
		
	}
	
	public int getId() {
		return this.ID;
	}

	@SuppressWarnings("deprecation") // cause mojang sucks ...
	private void animateBreak(Block block, Material oldType, byte oldData) {
		ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(oldType, oldData), 0F, 0F, 0F, 1, 32, block.getLocation(), 32);		
		block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, oldType.getId());
	}
	
	@SuppressWarnings("deprecation") // cause mojang sucks ...
	private void animateFlyUp(Block hittenBlock, Material oldType, byte oldData, boolean explosion) {
		final boolean EXPLOSION = explosion;
		final Entity FALLING_BLOCK = hittenBlock.getWorld().spawnFallingBlock(hittenBlock.getLocation().add(0, 0.01, 0), oldType, oldData);
		FALLING_BLOCK.setVelocity(new Vector(0, 0.5, 0)); // up to the sky!
		
		Bukkit.getScheduler().runTaskLater(SpleefMain.getInstance(), new Runnable() {			
			@Override
			public void run() {
				if (EXPLOSION) {
					FALLING_BLOCK.getWorld().createExplosion(FALLING_BLOCK.getLocation(), 0f); // create explosion
				} else {
					for (int i = 0; i < 100; i++) {
						FALLING_BLOCK.getWorld().playEffect(FALLING_BLOCK.getLocation().add(Math.random(), Math.random(), Math.random()), Effect.CRIT, 0);
					}
					FALLING_BLOCK.getWorld().playSound(FALLING_BLOCK.getLocation(), Sound.FIZZ, 1, 1);
				}
				FALLING_BLOCK.remove(); // remove the falling block
			}
		}, 5);
	}
	
}
