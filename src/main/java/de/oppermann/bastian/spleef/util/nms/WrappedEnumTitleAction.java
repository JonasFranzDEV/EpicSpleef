package de.oppermann.bastian.spleef.util.nms;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.util.UpdateChecker;

public enum WrappedEnumTitleAction {
	
	CLEAR(),
	RESET(),
	SUBTITLE(),
	TIMES(),
	TITLE();
	
	private static boolean nmsFailed = false;
	
	private static Class<?> CLASS_ENUM_TITLE_ACTION;	
	private static Class<?> CLASS_PACKET_PLAY_OUT_TITLE;	// should be final but java sucks
	
	private static String VERSION;	// the craftbukkit version (should also be final but ...)

	static {
		String path = Bukkit.getServer().getClass().getPackage().getName();
		VERSION = path.substring(path.lastIndexOf(".") + 1, path.length());
		
		try {
			CLASS_PACKET_PLAY_OUT_TITLE = Class.forName("net.minecraft.server." + VERSION + ".PacketPlayOutTitle");
			if (!UpdateChecker.compareMinecraftVersionServerIsHigherOrEqual("1.8.3")) {
				CLASS_ENUM_TITLE_ACTION = Class.forName("net.minecraft.server." + VERSION + ".EnumTitleAction");
			} else {
				for (Class<?> clazz : CLASS_PACKET_PLAY_OUT_TITLE.getDeclaredClasses()) {
					if (clazz.getSimpleName().equals("EnumTitleAction")) {
						CLASS_ENUM_TITLE_ACTION = clazz;
						break;
					}
				}
			}
			if (CLASS_ENUM_TITLE_ACTION == null) {
				SpleefMain.getInstance().log(Level.SEVERE, "Could not access NMS classes. Please use a plugin version which is compatible with your server version for full functionality. (can't find classes EnumTitleAction)");
				nmsFailed = true;
			}
		} catch (ClassNotFoundException e) {
			// incompatible version
			SpleefMain.getInstance().log(Level.SEVERE, "Could not access NMS classes. Please use a plugin version which is compatible with your server version for full functionality.");
			e.printStackTrace();
			nmsFailed = true;
		}

	}
	
	/**
	 * Gets the original enum.
	 */
	public Object getWrappedEnum() {
		if (nmsFailed) {
			return null;
		}
		for (Object e : CLASS_ENUM_TITLE_ACTION.getEnumConstants()) {
			if (e.toString().equals(name())) {
				return e;
			}
		}
		return null;
	}
	
}
