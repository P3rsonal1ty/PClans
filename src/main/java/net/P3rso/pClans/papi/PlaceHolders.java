package net.P3rso.pClans.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.P3rso.pClans.clans.Clan;
import net.P3rso.pClans.db.ClanOperates;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlaceHolders extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "clan";
    }

    @Override
    public @NotNull String getAuthor() {
        return "P3rsonal1ty";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier){
        UUID uuid = player.getUniqueId();
        Clan profile = ClanOperates.playerClan.getOrDefault(uuid,null);
        if(profile==null) return "";
        return switch (identifier) {
            case ("name") -> profile.getName();
            case ("owner") -> profile.getOwnerName();
            case ("balance") -> profile.getBalance() + "ꐠꑆ";
            case ("size") -> String.valueOf(profile.getPlayers().size());
            case ("color") -> profile.getColorString();
            case ("colorName") -> profile.getColorString() + profile.getName();
            case ("pvp") -> getPvpStatus(profile.getPvp());

            default -> null;
        };
    }


    private static String getPvpStatus(boolean bool){
        if(bool) return "вкл";
        else return "выкл";
    }

}
