package de.oppermann.bastian.spleef.api;

import org.bukkit.scoreboard.Scoreboard;

public interface ISpleefArenaConfiguration {
	
	/**
	 * Sets if the arena should be disabled or not.
	 * 
	 * @param disabled Whether the arena should be disabled or not.
	 */
	public void setDisabled(boolean disabled);
	
	/**
	 * Checks if the arena is disabled.
	 * 
	 * @return Whether the arena is disabled or not.
	 */
	public boolean isDisabled();
	
	/**
	 * Sets the lobby of the arena.
	 * 
	 * @param lobby The lobby to set. Could be <code>null</code> to remove the lobby.
	 */
	public void setLobby(ILobby lobby);
	
	/**
	 * Gets the lobby of the arena.
	 * 
	 * @return The lobby of the arena. Could be <code>null</code>.
	 */
	public ILobby getLobby();
	
	/**
	 * Sets if the {@link Scoreboard} of the arena should be shown.
	 * 
	 * @param showScoreboard Whether the scoreboard should be shown or not.
	 */
	public void setShowScoreboard(boolean showScoreboard);
	
	/**
	 * Checks if the {@link Scoreboard} is shown.
	 * 
	 * @return Whether if the scoreboard is shown or not.
	 */
	public boolean isShowScoreboard();
	
	/**
	 * Sets if snowballs should be enabled or not.
	 * 
	 * @param enableSnowballs Whether snownballs should be enabled or not.
	 */
	public void setEnableSnowballs(boolean enableSnowballs);
	
	public boolean isEnableSnowballs();
	
}
