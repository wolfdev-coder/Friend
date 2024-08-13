package org.youfriend.commands;

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
                    commandSender.sendMessage("Нашел в базе данных и вам действителньо предложили дружить! Принимаю запрос..");
                    String sql = "INSERT INTO friends (namePlayer, nameFriend) VALUES ('" + boyName + "', '" + youName + "')";
                    stm.execute(sql);
                    commandSender.sendMessage("Теперь вы друзья с " + boyName);
                    String del = "DELETE FROM users WHERE namePlayer='" + boyName + "'";
                    stm.executeUpdate(del);
                }
            }
            else {
                commandSender.sendMessage("кажется у вас нету запросов");
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
