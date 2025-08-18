package net.P3rso.pClans.db;

import net.P3rso.pClans.clans.Clan;
import net.P3rso.pClans.methods.Methods;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static net.P3rso.pClans.PClans.LOGGER;
import static net.P3rso.pClans.db.DataBase.connection;
public class ClanOperates {
    public static final Map<UUID, Clan> playerClan = new HashMap<>();
    public static final List<Clan> clans = new ArrayList<>();

    public static void LoadClans() {
        CompletableFuture.runAsync(() -> {
            String sql = "SELECT clan_uuid, name, owner, balance, color, players, level, pvp FROM clans";
            try (Statement st = connection.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                List<Clan> loadedClans = new ArrayList<>();
                Map<UUID, Clan> playerMap = new HashMap<>();

                while (rs.next()) {
                    UUID clan_uuid = UUID.fromString(rs.getString("clan_uuid"));
                    String name = rs.getString("name");
                    UUID owner = UUID.fromString(rs.getString("owner"));
                    long balance = rs.getLong("balance");
                    int color = rs.getInt("color");
                    List<UUID> players = Methods.StringToUUIDs(rs.getString("players"));
                    int level = rs.getInt("level");
                    boolean bool = rs.getBoolean("pvp");

                    Clan clan = new Clan(clan_uuid, name, owner, balance, color, players,bool,level);
                    loadedClans.add(clan);
                    players.forEach(p -> playerMap.put(p, clan));
                }

                // потокобезопасное обновление
                synchronized (clans) {
                    clans.clear();
                    clans.addAll(loadedClans);
                }
                synchronized (playerClan) {
                    playerClan.clear();
                    playerClan.putAll(playerMap);
                }

            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE,"Ошибка загрузки кланов из БД!",e);
            }
        });
    }

    public static void SaveClans() {
            String sql = "INSERT INTO clans (clan_uuid, name, owner, balance, color, players,level,pvp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Statement st = connection.createStatement()) {
                st.executeUpdate("DELETE FROM clans");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE,"Ошибка очистки базы данных кланов!",e);
                return;
            }

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                for (Clan clan : clans) {
                    ps.setString(1, clan.getClanUUID().toString());
                    ps.setString(2, clan.getName());
                    ps.setString(3, clan.getOwner().toString());
                    ps.setLong(4, clan.getBalance());
                    ps.setInt(5, clan.getColor());
                    ps.setString(6, Methods.listToString(clan.getPlayers()));
                    ps.setInt(7,clan.getLevel());
                    ps.setBoolean(8,clan.getPvp());
                    ps.addBatch();
                }
                ps.executeBatch();
                System.out.println("Все кланы сохранены.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE,"Ошибка сохранения базы данных кланов!",e);
            }
            DataBase.disconnectFromDatabase();
    }
    public static void updateClans() {
        CompletableFuture.runAsync(()-> {
            String sql = "INSERT INTO clans (clan_uuid, name, owner, balance, color, players) VALUES (?, ?, ?, ?, ?, ?)";
            try (Statement st = connection.createStatement()) {
                st.executeUpdate("DELETE FROM clans");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Ошибка очистки базы данных кланов!", e);
                return;
            }

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                for (Clan clan : clans) {
                    ps.setString(1, clan.getClanUUID().toString());
                    ps.setString(2, clan.getName());
                    ps.setString(3, clan.getOwner().toString());
                    ps.setLong(4, clan.getBalance());
                    ps.setInt(5, clan.getColor());
                    ps.setString(6, Methods.listToString(clan.getPlayers()));
                    ps.addBatch();
                }
                ps.executeBatch();
                System.out.println("Все кланы сохранены.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Ошибка сохранения базы данных кланов!", e);
            }
        });
    }


    public static void UpdateData(JavaPlugin plugin){
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, ClanOperates::updateClans,0L,30*60*20L);

    }
}
