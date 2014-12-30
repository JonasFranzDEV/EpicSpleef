package de.oppermann.bastian.spleef.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.oppermann.bastian.spleef.SpleefMain;
import de.oppermann.bastian.spleef.util.nms.WrappedEnumTitleAction;

public class TitleManager {
	
	private static boolean nmsFailed = false;

	private static Class<?> CLASS_ENUM_TITLE_ACTION;		// should be final but java sucks
	private static Class<?> CLASS_CRAFT_PLAYER;				// should be final but java sucks
	private static Class<?> CLASS_PACKET_PLAY_OUT_TITLE;	// should be final but java sucks
	private static Class<?> CLASS_CHAT_SERIALIZER;			// should be final but java sucks
	private static Class<?> CLASS_I_CHAT_BASE_COMPONENT;	// should be final but java sucks
	private static Class<?> CLASS_PACKET;					// should be final but java sucks
	
	private static String VERSION;	// the craftbukkit version (should also be final but ...)

	static {
		String path = Bukkit.getServer().getClass().getPackage().getName();
		VERSION = path.substring(path.lastIndexOf(".") + 1, path.length());
		
		try {
			CLASS_ENUM_TITLE_ACTION = Class.forName("net.minecraft.server." + VERSION + ".EnumTitleAction");
			CLASS_CRAFT_PLAYER = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftPlayer");
			CLASS_PACKET_PLAY_OUT_TITLE = Class.forName("net.minecraft.server." + VERSION + ".PacketPlayOutTitle");
			CLASS_CHAT_SERIALIZER = Class.forName("net.minecraft.server." + VERSION + ".ChatSerializer");
			CLASS_I_CHAT_BASE_COMPONENT = Class.forName("net.minecraft.server." + VERSION + ".IChatBaseComponent");
			CLASS_PACKET = Class.forName("net.minecraft.server." + VERSION + ".Packet");
		} catch (ClassNotFoundException e) {
			// incompatible version
			SpleefMain.getInstance().log(Level.SEVERE, "Could not access NMS classes. Please use a plugin version which is compatible with your server version for full functionality.");
			nmsFailed = true;
		}

	}
	
	/**
	 * Shows the player a title.
	 * 
	 * @param text The text of the title.
	 * @param type The type of title.
	 * @return Whether the sending failed or succeed.
	 */
	public static boolean sendTitle(Player player, String text, WrappedEnumTitleAction type) {
		if (nmsFailed) {
			return false;
		}
		try {
			Object iChatBaseComponent = CLASS_CHAT_SERIALIZER.getMethod("a", String.class).invoke(null, "{text:\"" + text +  "\"}");
			Object packetPlayOutTitle = CLASS_PACKET_PLAY_OUT_TITLE.getConstructor(CLASS_ENUM_TITLE_ACTION, CLASS_I_CHAT_BASE_COMPONENT)
					.newInstance(type.getWrappedEnum(), iChatBaseComponent);
			Object entityPlayer = CLASS_CRAFT_PLAYER.getMethod("getHandle").invoke(player);
			Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);					
			Method sendPacketMethod = playerConnection.getClass().getMethod("sendPacket", CLASS_PACKET);
			
			sendPacketMethod.invoke(playerConnection, packetPlayOutTitle);
			return true;
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NoSuchFieldException e) {
			// incompatible version
			SpleefMain.getInstance().log(Level.SEVERE, "Could not access NMS classes. Please use a plugin version which is compatible with your server version for full functionality.");
			nmsFailed = true;
			e.printStackTrace();
			return false;
		}
	}
	
}
