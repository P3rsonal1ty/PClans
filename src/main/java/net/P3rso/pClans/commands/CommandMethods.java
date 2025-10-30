package net.P3rso.pClans.commands;

import net.P3rso.pClans.clans.Clan;
import net.P3rso.pClans.db.ClanOperates;
import net.P3rso.pClans.vault.EcoHook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class CommandMethods {


    //обработка наличия клана у игрока вес задачи(0)
    public static boolean checkClanPlayer(Player player, boolean need){
        UUID playerUUID = player.getUniqueId();
        Clan clan = ClanOperates.playerClan.getOrDefault(playerUUID,null);
        if(clan == null && need){
            player.sendMessage("Заглушка - сначала нужно вступить в клан");
            return true;
        } else if (clan!=null && !need) {
            player.sendMessage("Заглушка - У вас уже есть клан!");
            return true;
        }
        return false;
    }

    //обработка прав игрока в клане на выполнение вес задачи(MAX)
    public static boolean checkPermission(int needWeight, Player player){
        UUID playerUUID = player.getUniqueId();
        Clan clan = ClanOperates.playerClan.getOrDefault(playerUUID,null);
        int playerWeight = clan.getPlayerWeight(playerUUID);
        if(playerWeight>needWeight) {
            player.sendMessage("Заглушка - недостаточно прав");
            return true;
        }
        return false;
    }

    //обработка входной строки вес задачи (1) //добавить сообщение для executor
    public static boolean checkString(String verifiable, String filter, Player sender){
        if(!verifiable.matches(filter)) {
            sender.sendMessage("Заглушка - сообщение см выше");
            return true;
        }
        return false;
    }

    //обработка наличия баланса игрока для выполнения команды вес задачи(3)
    public static boolean checkBalance(long price, Player sender){
        Economy econ = EcoHook.getEconomy();
        if(econ.getBalance(sender)<price) {
            sender.sendMessage("Заглушка - недостаток баланса");
            return true;
        }
        return false;
    }

    //итог - Jaro–Winkler от 90%

    //[MessageDistance][ClanName] playerName -> message
    //[ClanName] playerName [level]


    public static boolean addCommand(String[] args,int argsSize, List<String> commandTags, Runnable task){
        if(args.length!=argsSize) return false;
        for(int i = 0;i<commandTags.size();i++){
            if(!args[i].equalsIgnoreCase(commandTags.get(i))) return false;
        }
        task.run();
        return true;
    }

    public static void commandConfig(BooleanSupplier hasPermission, BooleanSupplier hasClan, BooleanSupplier hasMoney,BooleanSupplier hasStringFilter, Runnable commandAction){
        if (safeRun(hasClan)) return;
        if (safeRun(hasPermission)) return;
        if(safeRun(hasStringFilter)) return;
        if (safeRun(hasMoney)) return;
        commandAction.run();

    }

    private static boolean safeRun(BooleanSupplier action) {
        if (action == null) return false;
        return action.getAsBoolean();
    }
}
