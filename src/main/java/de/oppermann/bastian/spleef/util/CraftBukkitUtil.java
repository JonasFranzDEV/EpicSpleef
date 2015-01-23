package de.oppermann.bastian.spleef.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.oppermann.bastian.spleef.SpleefMain;

public class CraftBukkitUtil {
	
	private static boolean nmsFailed = false;
	
	private static Class<?> CLASS_CRAFT_PLAYER;				// should be final but java sucks
	private static Class<?> CLASS_PACKET_PLAY_OUT_CHAT;		// should be final but java sucks
	private static Class<?> CLASS_CHAT_SERIALIZER;			// should be final but java sucks
	private static Class<?> CLASS_I_CHAT_BASE_COMPONENT;	// should be final but java sucks
	private static Class<?> CLASS_PACKET;					// should be final but java sucks
	
	private static String VERSION;	// the craftbukkit version (should also be final but ...)

	static {
		String path = Bukkit.getServer().getClass().getPackage().getName();
		VERSION = path.substring(path.lastIndexOf(".") + 1, path.length());
		
		try {
			CLASS_CRAFT_PLAYER = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftPlayer");
			CLASS_PACKET_PLAY_OUT_CHAT = Class.forName("net.minecraft.server." + VERSION + ".PacketPlayOutChat");
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
	 * Sends a raw json string to the player.
	 * 
	 * @param player The player.
	 * @param json The json string.
	 * @return Whether it succeed or failed.
	 */
	public static boolean sendJSONText(Player player, String json) {			
		if (!nmsFailed) {
			try {
				Object entityPlayer = CLASS_CRAFT_PLAYER.getMethod("getHandle").invoke(player);
				Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);					
				Method sendPacketMethod = playerConnection.getClass().getMethod("sendPacket", CLASS_PACKET);
				Object iChatBaseComponent = CLASS_CHAT_SERIALIZER.getMethod("a", String.class).invoke(playerConnection, json);
				Object packetPlayOutChat = CLASS_PACKET_PLAY_OUT_CHAT.getConstructor(CLASS_I_CHAT_BASE_COMPONENT).newInstance(iChatBaseComponent);
				
				// send packet to the player
				sendPacketMethod.invoke(playerConnection, packetPlayOutChat);
				return true;
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException | IllegalArgumentException | NoSuchFieldException | InstantiationException e) {
				return false;
			}
		}
		return false;
	}

}
