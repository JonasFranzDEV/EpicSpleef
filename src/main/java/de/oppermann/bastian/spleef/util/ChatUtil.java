package de.oppermann.bastian.spleef.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Very powerful class with lots of useful methods.
 * <p>
 * This class helps to guarantee a standard chat system.
 * Avoid using {@link Player#sendMessage(String)} to send a player a message.
 */
public class ChatUtil {

	private static String charWidthIndexIndex = " !\"#$%" +
			"&'()*+" +
			",-./" +
			"0123456789" +
			":;<=>?@" +
			"ABCDEF"+
			"GHIJKL" +
			"MNOPQR" +
			"STUVWX" + 
			"YZ" +
			"[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~⌂ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»ß";
	private static int[] charWidths = { 16, 6, 18, 15, 18, 18, 
		18, 18, 20, 20, 20, 24, 
		8, 24, 8, 24, 
		24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 
		8, 8, 20, 24, 20, 24, 28, 
		24, 24, 24, 24, 24, 24, 
		24,	24, 16, 24, 24, 24, 
		24, 24, 24, 24, 24, 24, 
		24, 24, 24, 24, 24, 24, 
		24, 24, 
		18, 24, 16, 24, 24, 12, 24, 24, 24, 24, 24, 
		20, 24, 24, 8, 24, 20, 12, 24, 24, 24, 24, 24, 24, 24, 16, 24, 24, 24, 24,
		24, 24, 20, 8, 20, 28, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 16, 24, 12, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 16, 24, 24, 12, 24, 24, 24, 24, 24, 24, 24, 28,
		24, 24, 24, 8, 24, 24, 32, 36, 36, 24, 24, 24, 32, 32, 24, 32, 32, 32, 32, 32, 24, 24, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 24, 36,
		36, 36, 20, 36, 36, 32, 28, 28, 32, 28, 32, 32, 32, 28, 32, 32, 28, 36, 36, 24, 28, 28, 28, 28, 28, 36, 24, 28, 32, 28, 24, 24, 36, 28, 24, 28, 4 , 24};

	// no instance
	private ChatUtil() { }
	
	/**
	 * Sends a simple line to a player.
	 * 
	 * @param sender The player.
	 * @param color The color of the line.
	 */
	public static void sendLine(CommandSender sender, ChatColor color) {
		Validator.validateNotNull(color, "color");
		sender.sendMessage(color + "-----------------------------------------------------");	
	}
	
	/**
	 * Sends a line with text between it to a player
	 * 
	 * @param sender The player.
	 * @param color The color of the line.
	 * @param text The text between the line.
	 */
	public static void sendLine(CommandSender sender, ChatColor color, String text) {
		Validator.validateNotNull(color, "color");
		Validator.validateNotNull(text, "text");
		double length = getStringWidth(text);
		double maxHyphen = ((1280 / 2) - (length / 2)) / 24;
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < (int) maxHyphen; i++) {
			strBuilder.append("-");
		}
		boolean additional = (maxHyphen - (((int) maxHyphen))) >= 0.5;
		String msg = color + strBuilder.toString() + text + color + strBuilder.toString() + (additional ? "-" : "");
		sender.sendMessage(msg);
	}
	
	/**
	 * Gets the width of a string.
	 * 
	 * @param str The string.
	 * @return The width of the string.
	 */
	public static int getStringWidth(String str) {
		Validator.validateNotNull(str, "str");
		str = str.replaceAll("&((?i)[0-9a-fk-or])", "");
		str = str.replaceAll("§((?i)[0-9a-fk-or])", "");
		int i = 0;
		if (str != null)
			for (int j = 0; j < str.length(); j++)
				i += getCharWidth(str.charAt(j));
		return i;
	}

	/**
	 * Gets the width of a character.
	 * 
	 * @param c The character.
	 * @return The width of the character.
	 */
	public static int getCharWidth(char c) {
		int k = charWidthIndexIndex.indexOf(c);
		if (c != '\247' && k >= 0)
			return charWidths[k];
		return 0;
	}
	
	/**
	 * Builds a string from a array with a given startIndex.
	 * 
	 * @param array The array.
	 * @param startIndex The startIndex.
	 * @return A string which was formed from the array.
	 */
	public static String buildString(String[] array, int startIndex) {
		StringBuilder strBuilder = new StringBuilder();
		if (array.length > startIndex) {
			strBuilder.append(array[startIndex]);
			for (int i = startIndex + 1; i < array.length; i++) {
				strBuilder.append(" ").append(array[i]);
			}
		}
		return strBuilder.toString();
	}
	
}
