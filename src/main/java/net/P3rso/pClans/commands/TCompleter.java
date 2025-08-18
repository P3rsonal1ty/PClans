package net.P3rso.pClans.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player)) return List.of();

        List<String> completions = new ArrayList<>();
        if(command.getName().equalsIgnoreCase("clan")){
            if(args.length == 1){
                completions.add("create");
                completions.add("remove");
                completions.add("changeName");
                completions.add("kick");
                completions.add("invite");
                completions.add("color");
                completions.add("pvp");
            }
            if(args.length == 2 && args[0].equals("kick") || args[0].equals("invite")){
                for(Player p : Bukkit.getOnlinePlayers()){
                    completions.add(p.getName());
                }
            }
        }


        return completions;
    }
}
