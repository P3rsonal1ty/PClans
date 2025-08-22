package net.P3rso.pClans.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.clip.placeholderapi.PlaceholderAPI;
import net.P3rso.pClans.clans.Clan;
import net.P3rso.pClans.db.ClanOperates;
import net.P3rso.pClans.vault.EcoHook;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static net.P3rso.pClans.PClans.configuration;
public class ClanCommand implements BasicCommand {
    private final List<String> OneArgs = List.of("create","remove","pvp","kick","invite");

    @Override
    public void execute(@NotNull CommandSourceStack source, String @NotNull [] args) {
        if(!(source.getExecutor() instanceof Player player)) return;

        //удаление клана
        if(args.length==1 && args[0].equals("remove")){
            remove(player);
            return;
        }

        //создание клана
        if(args.length==2 && args[0].equals("create")){
            String name = args[1];
            create(player,name);
            return;
        }

        //установка нового имени клана
        if(args.length==2 && args[0].equals("changeName")){
            changeName(player,args[1]);
            return;
        }

        //очистка цвета клана до стандартного
        if(args.length==2 && args[0].equals("color")){
            if(args[1].equals("clear")){
                clearColor(player);
                return;
            }

        }

        //установка цвета клана
        if(args.length==3 && args[0].equals("color")){
            if(args[1].equals("set")){
                setColor(player,args[2]);
                return;
            }
        }

        if(args.length==1 && args[0].equals("pvp")){
             pvpChange(player);
             return;
        }

        if(args.length==3 && args[0].equals("money") && args[1].equals("set")){
            addMoney(player, args[2]);
        }

    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack source, String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        //табКомплитер для цветов
        completions.addAll(colorCompletions(args));
        completions.addAll(OneArgsCompletions(args));

        return completions;
    }

    private static void create(Player player, String name){
        UUID uuid = player.getUniqueId();
        Clan clan = ClanOperates.playerClan.getOrDefault(uuid,null);
        

        if(!checkName(name)){
            System.out.println("Неподходящее название клана");
            return;
        }
        if(clan!=null){
            player.sendMessage("У вас уже есть клан!");
            return;
        }
        new Clan(name,player);
    }
    private static void remove(Player player){
        UUID uuid = player.getUniqueId();
        Clan clan = ClanOperates.playerClan.getOrDefault(uuid,null);
        if(clan==null){
            String message = configuration.getString("messages.no-clan");
            if(message!=null) player.sendMessage(message);
            return;
        }
        if(!clan.getOwner().equals(uuid)){
            String message = configuration.getString("messages.not-enough-rights");
            if(message!=null) player.sendMessage(message);
            return;
        }
        clan.remove();
    }
    private static void addMoney(Player player, String arg){
        UUID uuid = player.getUniqueId();
        Clan clan = ClanOperates.playerClan.getOrDefault(uuid,null);
        if(clan == null){
            String message = configuration.getString("messages.no-clan");
            if(message!=null) player.sendMessage(message);
            return;
        }
        if(!arg.matches("[0-9]+")){
            player.sendMessage("Неправильный формат ввода");
            return;
        }
        long balance = Long.parseLong(arg);
        Economy econ = EcoHook.getEconomy();
        if(econ.getBalance(player)>=balance) {
            econ.withdrawPlayer(player, balance);
            clan.addBalance(balance);
            player.sendMessage("Успешное пополнение баланса клана!");
            return;
        }
        player.sendMessage("Недостаточно денег на балансе!");
    }


    private static void changeName(Player player, String newName){
        UUID uuid = player.getUniqueId();
        Clan clan = ClanOperates.playerClan.getOrDefault(uuid,null);
        if(clan == null){
            String message = configuration.getString("messages.no-clan");
            if(message!=null) player.sendMessage(message);
            return;
        }
        if(!checkName(newName)){
            System.out.println("Неподходящее название клана");
            return;
        }
        if(!clan.getOwner().equals(uuid)){
            String message = configuration.getString("messages.not-enough-rights");
            if(message!=null) player.sendMessage(message);
            return;
        }
        clan.setName(newName);
    }

    private static void setColor(Player player, String color){
        UUID uuid = player.getUniqueId();
        Clan clan = ClanOperates.playerClan.getOrDefault(uuid,null);
        if(clan==null){
            String message = configuration.getString("messages.no-clan");
            if(message!=null) player.sendMessage(message);
            return;
        }
        if(!clan.getOwner().equals(uuid)){
            String message = configuration.getString("messages.not-enough-rights");
            if(message!=null) player.sendMessage(message);
            return;
        }
        if(!color.matches("[0-9A-Fa-f]{6}")){
            player.sendMessage("Недопустимый цвет для клана!");
            return;
        }
        int clanColor = (int) Long.parseLong(color,16);
        clan.setColor(clanColor);
    }
    private static void clearColor(Player player){
        UUID uuid = player.getUniqueId();
        Clan clan = ClanOperates.playerClan.getOrDefault(uuid,null);
        if(clan==null){
            String message = configuration.getString("messages.no-clan");
            if(message!=null) player.sendMessage(message);
            return;
        }
        if(!clan.getOwner().equals(uuid)){
            player.sendMessage("Недостаточно прав");
            return;
        }
        clan.setColor(0xFFFFFF);
    }
    private static void pvpChange(Player player){
        UUID uuid = player.getUniqueId();
        Clan clan = ClanOperates.playerClan.getOrDefault(uuid,null);
        if(clan == null){
            String message = configuration.getString("messages.no-clan");
            if(message!=null) player.sendMessage(message);
            return;
        }
        if(!clan.getOwner().equals(uuid)){
            player.sendMessage("Недостаточно прав!");
            return;
        }
        clan.changePvp();

        String message = PlaceholderAPI.setPlaceholders(player, String.format("%s[%s] %s установил PvP: %s", "%clan_color%","%clan_name%", player.getName(), "%clan_pvp%"));
        for(UUID uuidClan : clan.getPlayers()) {
            Player clanPlayer = Bukkit.getPlayer(uuidClan);
            if(clanPlayer!=null){
                clanPlayer.sendMessage(MiniMessage.miniMessage().deserialize(message));
            }
        }
    }

    private static boolean checkName(String name){
        return name.matches("[A-Za-zА-яа-я0-9_]+");
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
