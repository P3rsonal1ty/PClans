package net.P3rso.pClans.vault;

import net.P3rso.pClans.clans.Clan;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import static net.P3rso.pClans.PClans.LOGGER;
public class EcoHook {
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    public static void loadEconomy(JavaPlugin plugin){
        if (!setupEconomy(plugin)) {
            LOGGER.severe(String.format("[%s] - Выключение плагина, плагин экономики не найден!", plugin.getDescription().getName()));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public static boolean setupEconomy(JavaPlugin plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static boolean setupChat(JavaPlugin plugin) {
        RegisteredServiceProvider<Chat> rsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    public static boolean setupPermissions(JavaPlugin plugin) {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }
}
