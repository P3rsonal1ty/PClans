package net.P3rso.pClans.db;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static net.P3rso.pClans.PClans.LOGGER;
import static net.P3rso.pClans.PClans.configuration;
public class DataBase {
    protected static Connection connection;
    public static void createTable() {
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS clans (clan_uuid VARCHAR(36) PRIMARY KEY, name VARCHAR(15), owner VARCHAR(36), balance INT, color INT, players TEXT, level INT, pvp TINYINT(1))");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Ошибка создания MySQL таблицы clans!", e);
        }
    }

    public static void connectToDatabase() {
        try {
            String host = configuration.getString("mysql.host");
            int port = configuration.getInt("mysql.port");
            String database = configuration.getString("mysql.database");
            String username = configuration.getString("mysql.username");
            String password = configuration.getString("mysql.password");

            String url = String.format(
                    "jdbc:mysql://%s:%d/%s?useSSL=false&autoReconnect=true&characterEncoding=UTF-8",
                    host, port, database
            );
            connection = DriverManager.getConnection(url, username, password);
            LOGGER.info("MySQL Подключено!");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Ошибка подключения к MySQL!", e);
        }
    }
    public static void disconnectFromDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.info("Отключение от MySQL успешно!");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Ошибка отключения от MySQL!", e);
        }
    }
}
