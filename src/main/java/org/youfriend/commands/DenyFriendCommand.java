package org.youfriend.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.youfriend.Friend;

import java.sql.*;

public class DenyFriendCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("беббе");
            return true;
        }
        try {
            Connection connection = DriverManager.getConnection(Friend.url);
            String selectSql = "SELECT * FROM users WHERE nameFriend='" + commandSender.getName()+ "'";
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(selectSql);
            if (rs.next()) {
                String boyName = rs.getString("namePlayer");
                String youName = rs.getString("nameFriend");
                if (commandSender.getName().equalsIgnoreCase(youName)) {
                    stm.execute("DELETE FROM users WHERE nameFriend='" + commandSender.getName() + "' AND namePlayer='" + boyName + "'");
                    commandSender.sendMessage(Friend.prefix+"Ты отказался от дружбы с " + boyName);
                    Player playerNo = Bukkit.getPlayerExact(boyName);
                    if (playerNo != null) {
                        playerNo.sendMessage(Friend.prefix+ commandSender.getName() + " Отказался от дружбы с вами :(");
                    }
                }
            }
            else {
                commandSender.sendMessage(Friend.prefix + "У тебя нету запросов в друзья... Ты кто?");
            }
            stm.close();
            rs.close();
            connection.close();
        } catch (SQLException exception) {
            commandSender.sendMessage(exception.toString());
        }

        return true;

    }
}
