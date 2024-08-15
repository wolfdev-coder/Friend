package org.youfriend;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.youfriend.commands.*;

import java.awt.*;
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
    public static String prefix = ChatColor.GREEN+"Друзья "+ ChatColor.DARK_GRAY + ">> " + ChatColor.GRAY;
    @Override
    public void onEnable() {
        getLogger().info("Загрузился");
        /*getServer().getPluginCommand("friendadd").setExecutor(new AddFriendCommand());
        getServer().getPluginCommand("friendyes").setExecutor(new AcceptFriendCommand());
        getServer().getPluginCommand("friendno").setExecutor(new DenyFriendCommand());
        getServer().getPluginCommand("friendremove").setExecutor(new RemoveFriendCommand());
        getServer().getPluginCommand("friendlist").setExecutor(new ListCommand());*/
        getServer().getPluginCommand("friend").setExecutor(new AllCommandsMain());
    }
    @Override
    public void onLoad() {
        getLogger().info(prefix+Color.WHITE+"Связываюсь с базой данных");
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
                getLogger().info(prefix +Color.green+"База данных успешно создана!");
            }
            else {
                connection = DriverManager.getConnection(url);
                Statement stmt = connection.createStatement();
                String query = "CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'namePlayer' text, 'nameFriend' text);";
                stmt.execute(query);
                String query2 = "CREATE TABLE if not exists 'friends' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'namePlayer' text, 'nameFriend' text);";
                stmt.execute(query2);
                getLogger().info(prefix+Color.green+"Подключился к базе данных успешно!");
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
