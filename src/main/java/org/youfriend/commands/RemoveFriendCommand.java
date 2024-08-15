package org.youfriend.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.youfriend.Friend;

import java.sql.*;

public class RemoveFriendCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("беббе");
            return true;
        }
        String friendName = strings[0];
        try{
            Connection connection = DriverManager.getConnection(Friend.url);
            String selectSql = "SELECT * FROM friends WHERE namePlayer='" + commandSender.getName()+ "' AND nameFriend='" + friendName+"'";

            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(selectSql);
            if (rs.next()) {
                String boyName = rs.getString("namePlayer");
                String youName = rs.getString("nameFriend");
                stm.execute("DELETE FROM friends WHERE namePlayer='" + commandSender.getName() + "' AND nameFriend='" + friendName + "'");
                stm.execute("DELETE FROM friends WHERE namePlayer='" + friendName + "' AND nameFriend='" + commandSender.getName() + "'");
                Player friend = Bukkit.getPlayerExact(youName);
                commandSender.sendMessage(Friend.prefix+"Ты удалил из друзей " + ChatColor.LIGHT_PURPLE + friend.getName());
                if (friend != null) {
                    friend.sendMessage(Friend.prefix + "Тебя удалил из друзей " + ChatColor.LIGHT_PURPLE  + commandSender.getName());
                }
            }
            else {
                commandSender.sendMessage(Friend.prefix + "У тебя нет такого друга...");
            }
            rs.close();
            stm.close();
            connection.close();
        }
        catch (SQLException exception)
        {
            System.out.printf(exception.toString());
        }
        return true;
    }
}
