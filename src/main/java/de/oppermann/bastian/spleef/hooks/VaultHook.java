package de.oppermann.bastian.spleef.hooks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultHook {

	private static Permission permission = null;
	private static Economy economy = null;
	private static Chat chat = null;
	
	public static Permission getPermission() {
		if (permission == null) {
			setupPermissions();
		}
		return permission;
	}
	
	public static Economy getEconomy() {
		if (economy == null) {
			setupEconomy();
		}
		return economy;
	}
	
	public static Chat getChat() {
		if (chat == null) {
			setupChat();
		}
		return chat;
	}

	private static boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	private static boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServicesManager().getRegistration(Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}

		return (chat != null);
	}

	private static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

}
