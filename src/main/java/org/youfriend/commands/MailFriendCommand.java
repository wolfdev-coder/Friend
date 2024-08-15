package org.youfriend.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.youfriend.Friend;

import java.sql.*;

public class MailFriendCommand  implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
       /* if (!(commandSender instanceof Player)) {
            return true;
        }
        try {
            String playerReader = strings[0];
            String message = strings[1];
            if (playerReader != null) {
                Connection connection = DriverManager.getConnection(Friend.url);
                PreparedStatement stm = connection.prepareStatement();
            }
        }
        catch (SQLException exception) {}*/
        return true;
    }
}
