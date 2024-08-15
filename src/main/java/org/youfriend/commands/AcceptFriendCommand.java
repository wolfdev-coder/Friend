package org.youfriend.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.youfriend.Friend;

import java.sql.*;

public class AcceptFriendCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Еррор, ток игроком можно");
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
                    String sql = "INSERT INTO friends (namePlayer, nameFriend) VALUES ('" + boyName + "', '" + youName + "')";
                    String sql1 = "INSERT INTO friends (namePlayer, nameFriend) VALUES ('" + youName + "', '" + boyName + "')";
                    stm.execute(sql);
                    stm.execute(sql1);
                    commandSender.sendMessage(Friend.prefix+"Теперь вы друзья с " + boyName);
                    String del = "DELETE FROM users WHERE namePlayer='" + commandSender.getName() + "' AND nameFriend='" + youName + "'";
                    stm.executeUpdate(del);
                    Player friend = Bukkit.getPlayerExact(boyName);
                    friend.sendMessage(Friend.prefix + "вы друзья с " + ChatColor.LIGHT_PURPLE + commandSender.getName());
                }
            }
            rs.close();
            stm.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
