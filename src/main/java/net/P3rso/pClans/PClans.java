package net.P3rso.pClans;

import net.P3rso.pClans.commands.ClanCommand;
import net.P3rso.pClans.db.ClanOperates;
import net.P3rso.pClans.db.DataBase;
import net.P3rso.pClans.events.ClanTestEvents;
import net.P3rso.pClans.papi.PlaceHolders;
import net.P3rso.pClans.vault.EcoHook;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PClans extends JavaPlugin {
    public static Logger LOGGER;
    public static FileConfiguration configuration;


    @Override
    public void onEnable() {
        LOGGER = getLogger();
        configuration = getConfig();

        //подключение экономики
        EcoHook.loadEconomy(this);

        //проверка на PAPI
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            LOGGER.warning("Could not find PlaceholderAPI! This plugin is required."); //
            Bukkit.getPluginManager().disablePlugin(this);
        }
        else{
            new PlaceHolders().register();
        }

        //подключение к дб
        DataBase.connectToDatabase();
        DataBase.createTable();

        //загрузка кланов
        ClanOperates.LoadClans();

        //загрузка ивентЛистенеров
        Bukkit.getPluginManager().registerEvents(new ClanTestEvents(),this);

        //загрузка команд
        this.registerCommand("clan",new ClanCommand());

        //принудительное сохранение данных каждые n минут
        ClanOperates.UpdateData(this);

    }

    @Override
    public void onDisable() {

        //сохранение кланов
        ClanOperates.SaveClans();


    }
}
