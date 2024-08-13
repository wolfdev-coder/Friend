package org.youfriend.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.youfriend.Friend;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddFriendCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        if (strings.length > 0) {
            String friend = strings[0];
            Player player = (Player) commandSender;
            Player friendPlayer = Bukkit.getPlayerExact(friend);
            if (friendPlayer != null) {
                try (Connection connection = DriverManager.getConnection(Friend.url)) {
                    String query = "INSERT INTO users (namePlayer, nameFriend) VALUES (?, ?)";
                    PreparedStatement stmt = connection.prepareStatement(query);
                    stmt.setString(1, commandSender.getName());
                    stmt.setString(2, friend);
                    stmt.executeUpdate();
                    stmt.close();
                    connection.close();
                    friendPlayer.sendMessage("Тебя пытаются добавить в друзья - " + commandSender.getName() +", ты согласен?");
                } catch (SQLException e) {
                    commandSender.sendMessage("Ошибка при добавлении друга: " + e.getMessage());
                }
            }
            else {
                commandSender.sendMessage("Такого игрока нет в онлайне!");
            }
        }
        return true;
    }
}
