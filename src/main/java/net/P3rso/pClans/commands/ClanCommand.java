package net.P3rso.pClans.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.clip.placeholderapi.PlaceholderAPI;
import net.P3rso.pClans.clans.Clan;
import net.P3rso.pClans.db.ClanOperates;
import net.P3rso.pClans.vault.EcoHook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.P3rso.pClans.PClans.configuration;
import static net.P3rso.pClans.commands.CommandMethods.*;
public class ClanCommand implements BasicCommand {
    private final List<String> OneArgs = List.of("create","remove","pvp","kick","invite","changeName");

    @Override
    public void execute(@NotNull CommandSourceStack source, String @NotNull [] args) {
        if(!(source.getExecutor() instanceof Player player)) return;

        //удаление клана
        if(addCommand(args,1,List.of("remove"),()-> remove(player))) return;

        //создание клана
        if(addCommand(args,2,List.of("create"),()-> create(player,args[1]))) return;

        //установка нового имени клана
        if(addCommand(args,2,List.of("changeName"),()-> changeName(player,args[1]))) return;

        //очистка цвета клана до стандартного
        if(addCommand(args,2,List.of("color","clear"),()-> clearColor(player))) return;

        //установка цвета клана
        if(addCommand(args,3,List.of("color","set"),()-> setColor(player,args[2]))) return;

        //смена пвп в клане
        if(addCommand(args,1,List.of("pvp"),()-> pvpChange(player))) return;

        //добавить денег в казну клана
        CommandMethods.addCommand(args,3,List.of("money","add"),()-> addMoney(player, args[2]));

    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack source, String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        //табКомплитер для цветов
        completions.addAll(colorCompletions(args));

        completions.addAll(OneArgsCompletions(args));

        return completions;
    }

    //Создание клана Переработан 2.0
    private static void create(Player player, String name){
        commandConfig(
                null,
                ()-> checkClanPlayer(player, false),
                ()-> checkBalance(2000,player),
                ()-> checkString(name,"[A-Za-zА-яа-я0-9_]+",player),
                ()-> {
                    Economy econ = EcoHook.getEconomy();
                    econ.withdrawPlayer(player, 2000);
                    new Clan(name,player);
                }
        );
    }

    //Удаление клана Переработан 2.0
    private static void remove(Player player){
        commandConfig(
                ()->checkPermission(0,player),
                ()->checkClanPlayer(player,true),
                null,
                null,
                ()-> ClanOperates.playerClan.get(player.getUniqueId()).remove()
        );
    }

    //добавление баланса в клан переработано 2.0
    private static void addMoney(Player player, String money){
        commandConfig(
                null,
                ()->checkClanPlayer(player,true),
                ()->checkBalance(Long.parseLong(money),player),
                ()->checkString(money,"[0-9]+",player),
                ()->{
                    Economy econ = EcoHook.getEconomy();
                    econ.withdrawPlayer(player, Long.parseLong(money));
                    ClanOperates.playerClan.get(player.getUniqueId()).addBalance(Long.parseLong(money));
                }
        );
    }

    //смена названия клана переработан 2.0
    private static void changeName(Player player, String newName){
        commandConfig(
                ()->checkPermission(0,player),
                ()->checkClanPlayer(player,true),
                ()->checkBalance(2000,player),
                ()->checkString(newName,"[A-Za-zА-яа-я0-9_]+",player),
                ()->ClanOperates.playerClan.get(player.getUniqueId()).setName(newName)
        );
    }

    //установка цвета клана переработан 2.0
    private static void setColor(Player player, String color){
        commandConfig(
                ()->checkPermission(1,player),
                ()->checkClanPlayer(player,true),
                ()->checkBalance(100,player),
                ()->checkString(color,"[0-9A-Fa-f]{6}",player),
                ()->{
                    int clanColor = (int) Long.parseLong(color,16);
                    ClanOperates.playerClan.get(player.getUniqueId()).setColor(clanColor);
                }
        );
        //String message = configuration.getString("messages.not-enough-rights");
    }
    //сброс цвета клана переработан 2.0
    private static void clearColor(Player player){
        commandConfig(
                ()->checkPermission(1,player),
                ()->checkClanPlayer(player,true),
                null,
                null,
                ()->{
                    int clanColor = (int) Long.parseLong("FFFFFF",16);
                    ClanOperates.playerClan.get(player.getUniqueId()).setColor(clanColor);
                }
        );
    }
    private static void pvpChange(Player player){
        commandConfig(
                ()->checkPermission(1,player),
                ()->checkClanPlayer(player,true),
                null,
                null,
                ()->{
                    Clan clan = ClanOperates.playerClan.get(player.getUniqueId());
                    String message = PlaceholderAPI.setPlaceholders(player, String.format("%s[%s] %s установил PvP: %s", "%clan_color%","%clan_name%", player.getName(), "%clan_pvp%"));
                    clan.clanAlert(message);
                    clan.changePvp();
                }
        );
    }


    private List<String> colorCompletions(String[] args){
        List<String> completions = new ArrayList<>();
        String zeroIndex = "color";
        if(args.length == 0){
            completions.add(zeroIndex);
        }
        if(args.length == 1){
            if (zeroIndex.startsWith(args[0]) && !args[0].equals(zeroIndex)) {
                completions.add(zeroIndex);
            }
        }
        if(args.length == 1){
            if(completions.isEmpty() && args[0].equals(zeroIndex)){
                completions.add("set");
                completions.add("clear");
            }
        }
        if(args.length == 2){
            if(args[0].equals(zeroIndex) && "set".startsWith(args[1]) && !args[1].equals("set")){
                completions.add("set");
            }
            if(args[0].equals(zeroIndex) && "clear".startsWith(args[1]) && !args[1].equals("clear")){
                completions.add("clear");
            }
        }

        return completions;
    }
    private List<String> OneArgsCompletions(String[] args){
        List<String> completions = new ArrayList<>();
        if(args.length==0){
            completions = OneArgs;
        }
        if(args.length == 1){
            for(String arg : OneArgs){
                if(arg.startsWith(args[0]) && !args[0].equals(arg)){
                    completions.add(arg);
                }
            }
        }
        return completions;
    }
}
