package me.proiezrush.swboxes.mysql;

import me.proiezrush.swboxes.Main;
import me.proiezrush.swboxes.boxes.Box;
import me.proiezrush.swboxes.player.BoxPlayer;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQL {

    private Main m;
    private Connection connection;
    private String host, db, user, password;
    private int port;
    public MySQL(Main m, String host, String db, String user, String password, int port) throws SQLException, ClassNotFoundException {
        this.m = m;
        this.host = host;
        this.db = db;
        this.user = user;
        this.password = password;
        this.port = port;
        openConnection();
        setup();
    }

    private void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.db, this.user, this.password);
        }
    }

    private void setup() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `player_Data` (UUID VARCHAR(36),PURCHASED VARCHAR(200),CURRENTBOX VARCHAR(15))");
        ps.executeUpdate();
    }

    public void saveBoxes(BoxPlayer player) throws SQLException {
        if (player.getBoxes() != null) {
            delete(player.getPlayer());
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `player_Data` (UUID,PURCHASED,CURRENTBOX) VALUES (?,?,?)");
            ps.setString(1, player.getPlayer().getUniqueId().toString());
            StringBuilder a = new StringBuilder();
            for (Box box : player.getBoxes()) {
                if (box != null) {
                    a.append(" ").append(box.getName());
                }
            }
            ps.setString(2, a.toString());
            ps.setString(3, player.getCurrentBox().getName());
            ps.executeUpdate();
        }
    }

    public List<Box> getPlayerBoxes(Player player) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT PURCHASED FROM `player_Data` WHERE UUID = ?");
        ps.setString(1, player.getUniqueId().toString());
        ResultSet res = ps.executeQuery();
        String boxes = null;
        if (res.next()) {
            boxes = res.getString("PURCHASED");
        }
        if (boxes != null) {
            List<Box> b = new ArrayList<>();
            String[] values = boxes.split(" ");
            for (int i=0;i<values.length;i++) {
                b.add(m.getBox(values[i]));
            }
            return b;
        }
        return new ArrayList<>();
    }

    public void setCurrentBox(BoxPlayer player, Box box) throws SQLException {
        if (player.getCurrentBox() != null) {
            delete(player.getPlayer());
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `player_Data` (UUID,PURCHASED,CURRENTBOX) VALUES (?,?,?)");
            ps.setString(1, player.getPlayer().getUniqueId().toString());
            StringBuilder a = new StringBuilder();
            for (Box boxs : player.getBoxes()) {
                if (boxs != null) {
                    a.append(" ").append(boxs.getName());
                }
            }
            ps.setString(2, a.toString());
            ps.setString(3, player.getCurrentBox().getName());
            ps.executeUpdate();
        }
    }

    public Box getCurrentBox(BoxPlayer player) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT CURRENTBOX FROM `player_Data` WHERE UUID = ?");
        ps.setString(1, player.getPlayer().getUniqueId().toString());
        ResultSet res = ps.executeQuery();
        String name = null;
        if (res.next()) {
            name = res.getString("CURRENTBOX");
        }
        return m.getBox(name);
    }

    public void delete(Player p) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM `player_Data` WHERE UUID = ?");
        ps.setString(1, p.getUniqueId().toString());
        ps.executeUpdate();
    }

    public void close() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return (connection != null);
    }
}
