package de.oppermann.bastian.spleef.util;

public enum GameStatus {

	WAITING_FOR_PLAYERS(Language.STATUS_WAITING_FOR_PLAYERS.toString()),
	ACTIVE(Language.STATUS_ACTIVE.toString()),
	COUNTDOWN_IN_ARENA_AFTER_LOBBY(Language.STATUS_ACTIVE.toString()),
	DISABLED(Language.STATUS_DISABLED.toString());
	
	private final String TEXT;
	
	private GameStatus(String text) {
		this.TEXT = text;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return TEXT;
	}
	
}
