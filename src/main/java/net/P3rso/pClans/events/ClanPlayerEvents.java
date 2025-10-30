package net.P3rso.pClans.events;

import net.P3rso.pClans.clans.Clan;
import net.P3rso.pClans.db.ClanOperates;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class ClanPlayerEvents implements Listener {
    @EventHandler
    public static void pvpManager(EntityDamageByEntityEvent e){
        Entity player = e.getDamager();
        Entity player1 = e.getEntity();
        if(player instanceof Player damager && player1 instanceof Player damaged){
            Clan clanDamager = ClanOperates.playerClan.getOrDefault(damager.getUniqueId(),null);
            if(clanDamager==null) return;
            if(clanDamager!=ClanOperates.playerClan.getOrDefault(damaged.getUniqueId(),null)) return;
            if(!clanDamager.getPvp()) e.setCancelled(true);
        }
    }

    @EventHandler
    public static void check(BlockBreakEvent event){
        Block block = event.getBlock();
        BlockState state = block.getState();


    }
}
