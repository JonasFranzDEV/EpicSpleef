package de.oppermann.bastian.spleef.util;

public enum SpectateType {
	
	NONE(),
	NORMAL(),
	NORMAL_FLYING(),
	GAMEMODE_3();
	
	public String getLanguageName() {
		switch (this) {
		case GAMEMODE_3:
			return Language.SPECTATE_TYPE_GAMEMODE_3.toString();
		case NONE:
			return Language.SPECTATE_TYPE_NONE.toString();
		case NORMAL:
			return Language.SPECTATE_TYPE_NORMAL.toString();
		case NORMAL_FLYING:
			return Language.SPECTATE_TYPE_NORMAL_FLYING.toString();		
		}
		return "unknown";
	}

}
