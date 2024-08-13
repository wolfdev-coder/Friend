package org.youfriend;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.youfriend.commands.AcceptFriendCommand;
import org.youfriend.commands.AddFriendCommand;
import org.youfriend.commands.DenyFriendCommand;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public final class Friend extends JavaPlugin {
    public static Connection connection;
    public static String url = "jdbc:sqlite:plugins/Friend/database.db";
    public static String prefix = "&eДрузья &7>> &f";
    @Override
    public void onEnable() {
        getLogger().info("Загрузился");
        getServer().getPluginCommand("friendadd").setExecutor(new AddFriendCommand());
        getServer().getPluginCommand("friendadd").setAliases(Arrays.asList("friend add"));
        getServer().getPluginCommand("friendyes").setExecutor(new AcceptFriendCommand());
        getServer().getPluginCommand("friendyes").setAliases(Arrays.asList("friend yes"));
        getServer().getPluginCommand("friendno").setExecutor(new DenyFriendCommand());
        getServer().getPluginCommand("friendno").setAliases(Arrays.asList("friend no"));
    }
    @Override
    public void onLoad() {
        getServer().getLogger().info("Связываюсь с базой данных");
        try {
            File file = new File("plugins/Friend");
            if (!file.exists()) {
                file.mkdirs();
                connection = DriverManager.getConnection(url);
                Statement stmt = connection.createStatement();
                String query = "CREATE TABLE if not exists 'users' ('idPlayer' INTEGER PRIMARY KEY AUTOINCREMENT, 'namePlayer' text, 'nameFriend' text);";
                stmt.execute(query);
                String query2 = "CREATE TABLE if not exists 'friends' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'namePlayer' text, 'nameFriend' text);";
                stmt.execute(query2);
                stmt.close();
                connection.close();
                getLogger().info("База данных успешно создана!" + ChatColor.DARK_AQUA);
            }
            else {
                connection = DriverManager.getConnection(url);
                Statement stmt = connection.createStatement();
                String query = "CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'namePlayer' text, 'nameFriend' text);";
                stmt.execute(query);
                String query2 = "CREATE TABLE if not exists 'friends' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'namePlayer' text, 'nameFriend' text);";
                stmt.execute(query2);
                getLogger().info("Подключился к базе данных успешно!" + ChatColor.GREEN);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
