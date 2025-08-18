package net.P3rso.pClans.events;

import net.P3rso.pClans.dialogs.ColorDialog;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;



public class ClanTestEvents implements Listener {

    @EventHandler
    public static void join(PlayerJoinEvent e){
        Player player = e.getPlayer();
        ColorDialog.openColorDialog(player,0,0,0);
    }
}
