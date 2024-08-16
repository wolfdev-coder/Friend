package org.youfriend;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.youfriend.commands.*;

import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public final class Friend extends JavaPlugin implements Listener {
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
        getServer().getPluginManager().registerEvents(this, this);
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
                String query3 = "CREATE TABLE if not exists 'mail' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'nameSender' text, 'nameReader' text, 'message' text);";
                stmt.execute(query2);
                stmt.execute(query3);
                stmt.close();
                getLogger().info(prefix +Color.green+"База данных успешно создана!");
            }
            else {
                connection = DriverManager.getConnection(url);
                Statement stmt = connection.createStatement();
                String query = "CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'namePlayer' text, 'nameFriend' text);";
                stmt.execute(query);
                String query2 = "CREATE TABLE if not exists 'friends' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'namePlayer' text, 'nameFriend' text);";
                String query3 = "CREATE TABLE if not exists 'mail' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'nameSender' text, 'nameReader' text, 'message' text);";
                stmt.execute(query2);
                stmt.execute(query3);
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
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try (PreparedStatement stm = connection.prepareStatement("SELECT * FROM mail WHERE nameReader=?")) {
            stm.setString(1, event.getPlayer().getName());
            try (ResultSet result = stm.executeQuery()) {
                if (result.next()) {
                    event.getPlayer().sendMessage(prefix + "У вас есть новые сообщения от друзей\nЧтобы прочитать, введите: " + ChatColor.LIGHT_PURPLE + "/friend readmail");
                    System.out.println(prefix + "У вас есть новые сообщения от друзей\nЧтобы прочитать, введите: " + ChatColor.LIGHT_PURPLE + "/friend readmail");

                } else {
                    event.getPlayer().sendMessage(prefix + "У вас нет новых сообщений от друзей");
                    System.out.println(prefix + "У вас нет новых сообщений от друзей");
                }
            }
        } catch (SQLException e) {
            getLogger().severe("Error checking mail for player " + event.getPlayer().getName() + ": " + e.getMessage());
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the clicked inventory is the friend menu
        if (event.getView().getTitle().equals("&aДрузья")) {
            // Cancel the event to prevent item removal
            event.setCancelled(true);
        }
    }

}
