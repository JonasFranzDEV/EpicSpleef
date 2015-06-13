package de.oppermann.bastian.spleef.listener;

import java.util.logging.Level;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.google.common.util.concurrent.FutureCallback;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.arena.SpleefArena;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaIsDisabledException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaIsFullException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaMisconfiguredException;
import de.oppermann.bastian.spleef.exceptions.SpleefArenaNotWaitingForPlayersException;
import de.oppermann.bastian.spleef.exceptions.TooLateToJoinException;
import de.oppermann.bastian.spleef.util.Language;
import de.oppermann.bastian.spleef.util.Particle;
import de.oppermann.bastian.spleef.util.ParticleCreatorTask;
import de.oppermann.bastian.spleef.util.PlayerManager;
import de.oppermann.bastian.spleef.util.SpleefPlayer;
import de.oppermann.bastian.spleef.util.gui.GuiInventory;

/**
 * Prevents a player from clicking items (moving them around).
 * 
 * @author Bastian Oppermann
 */
public class InventoryClickListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (PlayerManager.getArena(event.getWhoClicked().getUniqueId()) != null) {
			event.setCancelled(true);
		}
		if (GuiInventory.getLastCreatedInventoryType(event.getWhoClicked().getUniqueId()) == GuiInventory.JOIN) {
			if (compare(event.getWhoClicked().getOpenInventory().getTopInventory(), GuiInventory.getLastCreatedInventory(event.getWhoClicked().getUniqueId()))) {
				event.setCancelled(true);
				if (compare(event.getClickedInventory(), GuiInventory.getLastCreatedInventory(event.getWhoClicked().getUniqueId()))) {
					SpleefArena arena = GuiInventory.getArena(event.getSlot());
					if (arena != null) {
						Player player = (Player) event.getWhoClicked();
						try {
							arena.join(player);
						} catch (SpleefArenaNotWaitingForPlayersException e) {
							player.sendMessage(Language.CAN_NOT_JOIN_GAME_ACTIVE.toString().replace("%arena%", arena.getName()));
						} catch (SpleefArenaIsFullException e) {
							player.sendMessage(Language.CAN_NOT_JOIN_ARENA_FULL.toString().replace("%arena%", arena.getName()));
						} catch (SpleefArenaIsDisabledException e) {
							player.sendMessage(Language.CAN_NOT_JOIN_ARENA_DISABLED.toString().replace("%arena%", arena.getName()));
						} catch (SpleefArenaMisconfiguredException e) {
							player.sendMessage(Language.CAN_NOT_JOIN_ARENA_MISCONFIGURED.toString().replace("%arena%", arena.getName()));
						} catch (TooLateToJoinException e) {
							player.sendMessage(Language.CAN_NOT_JOIN_TOO_LATE.toString().replace("%arena%", arena.getName()));
						}
					}
				}
			}
		}
		if (GuiInventory.getLastCreatedInventoryType(event.getWhoClicked().getUniqueId()) == GuiInventory.STATISTICS) {
			if (compare(event.getWhoClicked().getOpenInventory().getTopInventory(), GuiInventory.getLastCreatedInventory(event.getWhoClicked().getUniqueId()))) {
				event.setCancelled(true);
			}
		}
		if (GuiInventory.getLastCreatedInventoryType(event.getWhoClicked().getUniqueId()) == GuiInventory.SHOP) {
			if (compare(event.getWhoClicked().getOpenInventory().getTopInventory(), GuiInventory.getLastCreatedInventory(event.getWhoClicked().getUniqueId()))) {
				event.setCancelled(true);
				if (event.getCurrentItem() != null) {
					final Particle PARTICLE = Particle.getParticle(event.getCurrentItem());					
					if (PARTICLE != null) {
						final Player PLAYER = (Player) event.getWhoClicked();
						SpleefPlayer.getPlayer(PLAYER.getUniqueId(), new FutureCallback<SpleefPlayer>() {							
							@Override
							public void onSuccess(SpleefPlayer spleefPlayer) {
								if (spleefPlayer.hasBought(PARTICLE)) {
									if (ParticleCreatorTask.getParticleEffect(PLAYER) == PARTICLE) {	// if the player already selected the particle remove it
										ParticleCreatorTask.removePlayer(PLAYER);
										PLAYER.closeInventory();
									} else {
										ParticleCreatorTask.addPlayer(PLAYER, PARTICLE);
										PLAYER.closeInventory();
									}
								} else {
									if (spleefPlayer.getTotalPoints() < PARTICLE.getPrice()) {
										PLAYER.sendMessage(Language.NOT_ENOUGH_POINTS_TO_BUY_PARTICLE.toString());
									} else {
										spleefPlayer.setTotalPoints(spleefPlayer.getTotalPoints() - PARTICLE.getPrice());
										spleefPlayer.addEffect(PARTICLE);
										ParticleCreatorTask.addPlayer(PLAYER, PARTICLE);
										PLAYER.closeInventory();
										PLAYER.playSound(PLAYER.getLocation(), Sound.LEVEL_UP, 1F, 1F);
										SpleefArena arena = PlayerManager.getArena(PLAYER.getUniqueId());
										if (arena != null) {	// update the scoreboard
											arena.setScoreboard(PLAYER);
										}
									}
								}
							}
							
							@Override
							public void onFailure(Throwable t) {
								SpleefMain.getInstance().log(Level.SEVERE, "Couldn't access player database... :/ (player: " + PLAYER.getName());
								t.printStackTrace();
							}
						});
						
					}
				}
			}
		}
	}
	
	private boolean compare(Inventory inv, Inventory inv2) {
		if (inv == null || inv2 == null) {
			return false;
		}
		if (!inv.getTitle().equals(inv2.getTitle())) {
			return false;
		}
		if (inv.getMaxStackSize() != inv2.getMaxStackSize()) {
			return false;
		}
		if (!inv.getViewers().equals(inv2.getViewers())) {
			return false;
		}
		return true;		
	}
	
}
