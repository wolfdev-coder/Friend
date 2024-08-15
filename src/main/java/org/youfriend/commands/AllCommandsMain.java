package org.youfriend.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.youfriend.Friend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AllCommandsMain implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("friend")) {
            String action = strings[0];
            if (action.equalsIgnoreCase("add")) {
                if (strings.length >= 1) {
                    String friend = strings[1];
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
                            friendPlayer.sendMessage(ChatColor.GREEN + Friend.prefix + "Тебя пытаются добавить в друзья - " + ChatColor.LIGHT_PURPLE + commandSender.getName() + ChatColor.GRAY + ", ты согласен?\nЕсли да, то напиши /friend accept, если нет, то /friend deny");
                            commandSender.sendMessage(Friend.prefix + "Ты отправил заявку в друзья " + ChatColor.LIGHT_PURPLE + friendPlayer.getName());
                        } catch (SQLException e) {
                            commandSender.sendMessage(Friend.prefix + "Ошибка при добавлении друга, Обратитесь к администрации");
                        }
                    } else {
                        commandSender.sendMessage("Такого игрока нет в онлайне!");
                    }
                }
            }
            else if (action.equalsIgnoreCase("accept")) {
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
            }
            else if (action.equalsIgnoreCase("deny")) {
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
            }
            else if (action.equalsIgnoreCase("remove")) {
                String friendName = strings[1];
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
            }
            else if (action.equalsIgnoreCase("list")) {
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
            }
        }
        return true;
    }
}
