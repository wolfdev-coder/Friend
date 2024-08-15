package org.youfriend.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.youfriend.Friend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ListCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        try {
            Connection connection = DriverManager.getConnection(Friend.url);
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM friends WHERE namePlayer=?");
            stm.setString(1, commandSender.getName());
            try(ResultSet rs = stm.executeQuery()) {
                List<String> listFriends = new ArrayList<>();
                while (rs.next()) {
                    String friendName = rs.getString("nameFriend");
                    listFriends.add(rs.getString("nameFriend"));
                }
                String list = String.join(", ", listFriends);
                if (list != null) {
                    commandSender.sendMessage(Friend.prefix + "Вот ваш список друзей:\n" + list);
                }
                else {
                    commandSender.sendMessage(Friend.prefix + "У вас нет друзей(");

                }
            }
                


        }
        catch (SQLException e) {
        }
        return true;
    }
}
