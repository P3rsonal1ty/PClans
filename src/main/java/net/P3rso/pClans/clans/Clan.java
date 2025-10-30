package net.P3rso.pClans.clans;

import net.P3rso.pClans.db.ClanOperates;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class Clan {
    private String name;
    private final List<UUID> players;
    private int color;
    private UUID owner;
    private final UUID clanUUID;
    private final List<UUID> invites;
    private long balance;
    private boolean pvp;
    private int level;
    private Map<UUID, Integer> weightPlayer = new HashMap<>();

    public Clan(UUID clanUUID, String name,UUID owner, long balance, int color, List<UUID> players, boolean pvp, int level){
        this.clanUUID = clanUUID;
        this.name = name;
        this.level = level;
        this.pvp = pvp;
        this.owner = owner;
        this.balance = balance;
        this.color = color;
        this.players = players;
        this.invites = new ArrayList<>();
        ClanOperates.clans.add(this);
        ClanOperates.playerClan.put(owner,this);
    }
    public Clan(String name, Player player){
        this.clanUUID = UUID.randomUUID();
        this.name = name;
        this.level = 0;
        this.pvp = false;
        this.owner = player.getUniqueId();
        this.balance = 0;
        this.color = 0xFFFFFF;
        this.players = new ArrayList<>();
        this.invites = new ArrayList<>();
        this.players.add(owner);
        ClanOperates.playerClan.put(owner,this);
        ClanOperates.clans.add(this);
    }

    public void changePvp(){
        this.pvp = !this.pvp;
    }
    public boolean getPvp(){
        return this.pvp;
    }
    public int getLevel(){
        return this.level;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public String getColorString(){
        return "#"+Integer.toHexString(this.color);
    }
    public void setColor(int color) {
        this.color = color;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void removeBalance(long balance){
        if(this.balance>=balance) this.balance-=balance;
    }
    public void addBalance(long balance){
        this.balance += balance;
    }

    public UUID getClanUUID(){
        return this.clanUUID;
    }
    public UUID getOwner(){
        return this.owner;
    }
    public String getOwnerName(){
        OfflinePlayer player = Bukkit.getOfflinePlayer(this.owner);
        return player.getName();
    }
    public void setOwner(UUID owner){
        this.owner = owner;
    }
    public void addPlayer(UUID uuid){
        players.add(uuid);
    }
    public void removePlayer(UUID uuid){
        players.remove(uuid);
    }
    public List<UUID> getPlayers(){
        return this.players;
    }
    public List<UUID> getInvites(){
        return this.invites;
    }

    public void setWeightPlayer(UUID playerUUID, int weight){
        weightPlayer.put(playerUUID, weight);
    }
    public int getPlayerWeight(UUID playerUUID){
        if(playerUUID.equals(owner)) return 0;
        return weightPlayer.getOrDefault(playerUUID,5);
    }
    public void remove(){
        ClanOperates.clans.remove(this);
        for(UUID uuid : players){
            ClanOperates.playerClan.put(uuid,null);
        }
    }

    public void clanAlert(String Message){
        players.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if(player!=null) player.sendMessage(MiniMessage.miniMessage().deserialize(Message));
        });

    }

}
