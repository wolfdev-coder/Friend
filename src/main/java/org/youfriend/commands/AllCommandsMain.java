package org.youfriend.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.ChatPaginator;
import org.joml.FrustumIntersection;
import org.youfriend.Friend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

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
            else if (action.equalsIgnoreCase("sendmail")) {
                try {
                    String playerReader = strings[1];
                    StringBuilder messages = new StringBuilder();
                    for (int i = 2; i < strings.length; i++) {
                        messages.append(strings[i]).append(" ");
                    }
                    String message = messages.toString().trim();
                    if (playerReader != null && message != null) {
                        Connection connection = DriverManager.getConnection(Friend.url);
                        PreparedStatement stm = connection.prepareStatement("SELECT * FROM friends WHERE namePlayer=? AND nameFriend=?");
                        stm.setString(1, commandSender.getName());
                        stm.setString(2, playerReader);
                        ResultSet rs = stm.executeQuery();
                        if (rs.next()) {
                            PreparedStatement stm2 = connection.prepareStatement("INSERT INTO mail (nameSender, nameReader, message) VALUES (?,?,?)");
                            stm2.setString(1, commandSender.getName());
                            stm2.setString(2, playerReader);
                            stm2.setString(3, message);
                            stm2.executeUpdate();
                            stm2.close();
                            commandSender.sendMessage(Friend.prefix+"Вы отправили сообщение другу " + ChatColor.LIGHT_PURPLE + playerReader + ChatColor.GRAY+"\nВаше сообщение: " + ChatColor.LIGHT_PURPLE + message);
                            Player friend = Bukkit.getPlayerExact(playerReader);
                            if (friend !=null) {
                                friend.sendMessage(Friend.prefix+"Вы получили письмо от своего друга " + ChatColor.LIGHT_PURPLE + commandSender.getName() + ChatColor.GRAY + "\nЧтобы его прочитать введите " + ChatColor.LIGHT_PURPLE + "/friend readmail " + commandSender.getName());
                            }
                        }
                        stm.close();
                        rs.close();
                        connection.close();
                    }
                }
                catch (SQLException exception) {}
            }
            else if (action.equalsIgnoreCase("readmail")) {
                String response = Friend.prefix + "У вас нет сообщений..";
                if (strings.length == 2) {
                    String writer = strings[1];
                    try (Connection connection = DriverManager.getConnection(Friend.url);
                         PreparedStatement stm = connection.prepareStatement(getQuery(writer))) {
                        if (writer != null) {
                            stm.setString(1, writer);
                            stm.setString(2, commandSender.getName());
                        }
                        try (ResultSet rs = stm.executeQuery()) {
                            int countMessage = 1;
                            while (rs.next()) {
                                String sender = rs.getString("nameSender");
                                String message = rs.getString("message");
                                commandSender.sendMessage(Friend.prefix + countMessage + ") Сообщение от " + ChatColor.LIGHT_PURPLE+ sender + ChatColor.GRAY+" >> " + message);
                                countMessage++;
                                deleteMessage(connection, sender, commandSender.getName(), message);
                                response = Friend.prefix + "Вы прочитали все сообщения!";
                            }
                        }
                        commandSender.sendMessage(response);
                    } catch (SQLException e) {
                        System.out.printf("Error reading mail", e);
                    }
                }
                else if (strings.length < 2) {
                    try (Connection connection = DriverManager.getConnection(Friend.url)) {
                        try (PreparedStatement stm = connection.prepareStatement("SELECT * FROM mail WHERE nameReader=?")) {
                            stm.setString(1,commandSender.getName());
                            try (ResultSet rs = stm.executeQuery()) {
                                int countMessage = 1;
                                while (rs.next()) {
                                    String sender = rs.getString("nameSender");
                                    String message = rs.getString("message");
                                    commandSender.sendMessage(Friend.prefix + countMessage + ") Сообщение от " + ChatColor.LIGHT_PURPLE+ sender + ChatColor.GRAY+" >> " + message);
                                    countMessage++;
                                    deleteMessage(connection, sender, commandSender.getName(), message);
                                    response = Friend.prefix + "Вы прочитали все сообщения!";
                                }
                                commandSender.sendMessage(response);
                            }
                        }
                    } catch (SQLException e) {
                        System.out.printf("Error reading mail", e);
                    }
                }
            }
            else if (action == null || action.equalsIgnoreCase("menu")) {
                commandSender.sendMessage(Friend.prefix+"Открываю меню");
                Player player = (Player) commandSender;
                createPlayerHeadMenu(player);
            }

        }
        return true;
    }
    private String getQuery(String writer) {
        if (writer != null) {
            return "SELECT * FROM mail WHERE nameSender=? AND nameReader=?";
        } else {
            return "SELECT * FROM mail WHERE nameReader=?";
        }
    }

    private void deleteMessage(Connection connection, String sender, String reader, String message) throws SQLException {
        try (PreparedStatement deleteStm = connection.prepareStatement("DELETE FROM mail WHERE nameSender=? AND nameReader=? AND message=?")) {
            deleteStm.setString(1, sender);
            deleteStm.setString(2, reader);
            deleteStm.setString(3, message);
            deleteStm.executeUpdate();
        }
    }

    public void createPlayerHeadMenu(Player viewer) {
        Inventory menu = Bukkit.createInventory(null, 9, "&aДрузья");
        updateMenu(viewer, menu);
        viewer.openInventory(menu);

        // Schedule the task to run every 5 seconds
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            updateMenu(viewer, menu);
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void updateMenu(Player viewer, Inventory menu) {
        try (Connection connection = DriverManager.getConnection(Friend.url);
             PreparedStatement stm = connection.prepareStatement("SELECT * FROM friends WHERE namePlayer = ?")) {
            stm.setString(1, viewer.getName());
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    ItemStack[] contents = new ItemStack[9];
                    int i = 0;
                    do {
                        String friend = rs.getString("nameFriend");
                        Player friendPlayer = Bukkit.getPlayerExact(friend);
                        ItemStack headItem = createHeadItem(friend, friendPlayer);
                        contents[i] = headItem;
                        i++;
                    } while (rs.next());
                    menu.setContents(contents);
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error updating player head menu", e);
        }
    }

    private ItemStack createHeadItem(String friendName, Player friendPlayer) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + friendName);
        List<String> lore;
        if (friendPlayer != null) {
            lore = getCoordinatesLore(friendPlayer);
        } else {
            lore = getDefaultLore();
        }
        meta.setLore(lore);
        head.setItemMeta(meta);
        return head;

    }

    private List<String> getCoordinatesLore(Player player) {
        return List.of(
                ChatColor.WHITE+"Координаты:",
                String.format(ChatColor.GRAY+"X: %.2f", player.getLocation().getX()),
                String.format(ChatColor.GRAY+"Y: %.2f", player.getLocation().getY()),
                String.format(ChatColor.GRAY+"Z: %.2f", player.getLocation().getZ())
        );
    }

    private List<String> getDefaultLore() {
        return List.of(
                ChatColor.WHITE+"Координаты:",
                ChatColor.GRAY+"X: 0",
                ChatColor.GRAY+"Y: 0",
                ChatColor.GRAY+"Z: 0"
        );
    }

}


