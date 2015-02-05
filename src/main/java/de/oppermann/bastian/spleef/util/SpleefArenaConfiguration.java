package de.oppermann.bastian.spleef.util;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import de.oppermann.bastian.spleef.arena.Lobby;

public class SpleefArenaConfiguration {
	
	// TODO add javadocs

	private boolean disabled = false;
	
	private Lobby lobby = null;
	
	private boolean showScoreboard = true;
	
	private boolean enableSnowballs = true;
	
	private int maxSnowballs = 16;
	
	private SpleefMode mode = SpleefMode.NORMAL;
	
	private byte antiCamping = (byte) 0;	// if snowballs are allowed anti camping is not necessary.
	
	private boolean modifyGravity = false;
	
	private double gravity = 0.5D;
	
	private boolean showTitleCountdown = true;
	
	private int lobbyCountdown = 60;
	
	private int arenaCountdown = 10;
	
	private int minPlayers = 2;
	
	private int requiredPlayersToStartCountdown = 2;
	
	private boolean hurtPlayers = false;
	
	private boolean freezePlayers = true;
	
	private boolean customInventory = false;
	
	private ItemStack[] customInventoryContents = new ItemStack[9*4];
	
	{
		customInventoryContents[0] = new ItemStack(Material.DIAMOND_SPADE);	// default should be a shovel
	}
	
	private int pointsWinningReward = 50;
	
	private int moneyWinningReward = 50;
	
	private int pointsParticipationReward = 10;
	
	private int moneyParticipationReward = 10;
	
	private EntityType vehicle = null;
	
	private boolean instanstBlockDestroy = false;
	
	public SpleefArenaConfiguration() {
		
	}
	
	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public Lobby getLobby() {
		return lobby;
	}

	public void setLobby(Lobby lobby) {
		this.lobby = lobby;
	}

	public boolean isShowScoreboard() {
		return showScoreboard;
	}

	public void setShowScoreboard(boolean showScoreboard) {
		this.showScoreboard = showScoreboard;
	}

	public boolean isEnableSnowballs() {
		return enableSnowballs;
	}

	public void setEnableSnowballs(boolean enableSnowballs) {
		this.enableSnowballs = enableSnowballs;
	}

	public int getMaxSnowballs() {
		return maxSnowballs;
	}

	public void setMaxSnowballs(int maxSnowballs) {
		this.maxSnowballs = maxSnowballs;
	}

	public SpleefMode getMode() {
		return mode;
	}

	public void setMode(SpleefMode mode) {
		this.mode = mode;
	}

	public byte getAntiCamping() {
		return antiCamping;
	}

	public void setAntiCamping(byte antiCamping) {
		this.antiCamping = antiCamping;
	}

	public boolean modifyGravity() {
		return modifyGravity;
	}

	public void setModifyGravity(boolean modifyGravity) {
		this.modifyGravity = modifyGravity;
	}

	public double getGravity() {
		return gravity;
	}

	public void setGravity(double gravity) {
		this.gravity = gravity;
	}

	public boolean showTitleCountdown() {
		return showTitleCountdown;
	}

	public void setShowTitleCountdown(boolean showTitleCountdown) {
		this.showTitleCountdown = showTitleCountdown;
	}

	public int getLobbyCountdown() {
		return lobbyCountdown;
	}

	public void setLobbyCountdown(int lobbyCountdown) {
		this.lobbyCountdown = lobbyCountdown;
	}

	public int getArenaCountdown() {
		return arenaCountdown;
	}

	public void setArenaCountdown(int arenaCountdown) {
		this.arenaCountdown = arenaCountdown;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	public int getRequiredPlayersToStartCountdown() {
		return requiredPlayersToStartCountdown;
	}

	public void setRequiredPlayersToStartountdown(int requiredPlayersToStartCountdown) {
		this.requiredPlayersToStartCountdown = requiredPlayersToStartCountdown;
	}

	public boolean hurtPlayers() {
		return hurtPlayers;
	}

	public void setHurtPlayers(boolean hurtPlayers) {
		this.hurtPlayers = hurtPlayers;
	}

	public boolean freezePlayers() {
		return freezePlayers;
	}

	public void setFreezePlayers(boolean freezePlayers) {
		this.freezePlayers = freezePlayers;
	}

	public boolean hasCustomInventory() {
		return customInventory;
	}

	public void setCustomInventory(boolean customInventory) {
		this.customInventory = customInventory;
	}

	public ItemStack[] getCustomInventoryContents() {
		return customInventoryContents;
	}

	public void setCustomInventoryContents(ItemStack[] inventoryContents) {
		this.customInventoryContents = inventoryContents;
	}

	public int getPointsWinningReward() {
		return pointsWinningReward;
	}

	public void setPointsWinningReward(int pointsWinningReward) {
		this.pointsWinningReward = pointsWinningReward;
	}

	public int getMoneyWinningReward() {
		return moneyWinningReward;
	}

	public void setMoneyWinningReward(int moneyWinningReward) {
		this.moneyWinningReward = moneyWinningReward;
	}

	public int getPointsParticipationReward() {
		return pointsParticipationReward;
	}

	public void setPointsParticipationReward(int pointsParticipationReward) {
		this.pointsParticipationReward = pointsParticipationReward;
	}

	public int getMoneyParticipationReward() {
		return moneyParticipationReward;
	}

	public void setMoneyParticipationReward(int moneyParticipationReward) {
		this.moneyParticipationReward = moneyParticipationReward;
	}

	public EntityType getVehicle() {
		return vehicle;
	}

	public void setVehicle(EntityType vehicle) {
		this.vehicle = vehicle;
	}

	public boolean instanstBlockDestroy() {
		return instanstBlockDestroy;
	}

	public void setInstanstBlockDestroy(boolean instanstBlockDestroy) {
		this.instanstBlockDestroy = instanstBlockDestroy;
	}

}
