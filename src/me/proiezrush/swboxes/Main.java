package me.proiezrush.swboxes;

import me.proiezrush.swboxes.boxes.Box;
import me.proiezrush.swboxes.boxes.BoxMenu;
import me.proiezrush.swboxes.mysql.MySQL;
import me.proiezrush.swboxes.player.BoxPlayer;
import me.proiezrush.swboxes.utils.ItemBuilder;
import me.proiezrush.swboxes.utils.Settings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class Main extends JavaPlugin {

    private Settings config, boxes;
    private Admin adm;
    private HashMap<Player, BoxPlayer> players;
    private HashMap<String, Box> box;
    private HashMap<Player, BoxMenu> menus;
    private MySQL mySQL;
    public String PREFIX;

    @Override
    public void onEnable() {
        File f = new File("plugins/SWBoxes");
        if (!f.exists()) {
            f.mkdir();
        }
        this.config = new Settings(this, "config", false, true);
        this.boxes = new Settings(this, "boxes", false, true);
        this.adm = new Admin();
        this.players = new HashMap<>();
        this.box = new HashMap<>();
        this.menus = new HashMap<>();
        this.PREFIX = config.get(null, "PREFIX").replaceAll("<l>", "¡").replaceAll("&", "§").replaceAll("-,-", "ñ");
        try {
            this.mySQL = new MySQL(this, config.getConfig().getString("MySQL.host"), config.getConfig().getString("MySQL.database"),
                    config.getConfig().getString("MySQL.username"), config.getConfig().getString("MySQL.password"), config.getInt("MySQL.port"));
        } catch (SQLException | ClassNotFoundException throwables) {
            Bukkit.getConsoleSender().sendMessage(adm.parsePlaceholders(null, "&cMySQL not connected, disabling..."));
            Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("SWBoxes"));
            return;
        }

        this.getCommand("swboxes").setExecutor(new BoxCommand(this));
        Bukkit.getPluginManager().registerEvents(new Listeners(this), this);
        loadBoxes();

        Bukkit.getConsoleSender().sendMessage(adm.parsePlaceholders(null, PREFIX + "&aPlugin activado"));
        Bukkit.getConsoleSender().sendMessage(adm.parsePlaceholders(null, PREFIX + "&aHecho por &e" + getDescription().getAuthors()));
        Bukkit.getConsoleSender().sendMessage(adm.parsePlaceholders(null, PREFIX + "&aVersion &e" + getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        if (mySQL != null) {
            mySQL.close();
        }
        Bukkit.getConsoleSender().sendMessage(adm.parsePlaceholders(null, PREFIX + "&cPlugin desactivado"));
        Bukkit.getConsoleSender().sendMessage(adm.parsePlaceholders(null, PREFIX + "&cHecho por &e" + getDescription().getAuthors()));
        Bukkit.getConsoleSender().sendMessage(adm.parsePlaceholders(null, PREFIX + "&cVersion &e" + getDescription().getVersion()));
    }

    public void addBoxPlayer(BoxPlayer player) {
        players.putIfAbsent(player.getPlayer(), player);
    }

    public void removeBoxPlayer(Player player) {
        players.remove(player);
    }

    public BoxPlayer getBoxPlayer(String name) {
        return players.get(Bukkit.getPlayer(name));
    }

    public HashMap<Player, BoxPlayer> getBoxPlayers() {
        return players;
    }

    public void addBox(Box box) {
        this.box.putIfAbsent(box.getName(), box);
    }

    public void removeBox(String name) {
        this.box.remove(name);
    }

    public Box getBox(String name) {
        return this.box.get(name);
    }

    public HashMap<String, Box> getBoxs() {
        return this.box;
    }

    public void addBoxMenu(BoxMenu menu) {
        menus.putIfAbsent(menu.getPlayer(), menu);
    }

    public void removeBoxMenu(Player player) {
        menus.remove(player);
    }

    public BoxMenu getBoxMenu(String name) {
        return menus.get(Bukkit.getPlayer(name));
    }

    public HashMap<Player, BoxMenu> getMenus() {
        return menus;
    }

    public Settings getC() {
        return config;
    }

    public Settings getBoxes() {
        return boxes;
    }

    public Admin getAdm() {
        return adm;
    }

    public ItemStack getWand() {
        return new ItemBuilder(this, "&6&lSWBOXES WAND", "IRON_AXE", 0, false, null).build();
    }

    public MySQL getMySQL() {
        return mySQL;
    }
    
    public BoxMenu createBoxMenu(BoxPlayer boxPlayer) {
        List<String> lockedLore = this.getC().getList("Cages.LockedLore");
        List<String> unlockedLore = this.getC().getList("Cages.UnlockedLore");
        BoxMenu menu = new BoxMenu(this, boxPlayer.getPlayer());
        int i = 10;
        for (Box box : this.getBoxs().values()) {
            if (i == 17 || i == 18 || i == 26 || i == 27 || i == 35 || i > 35) {
                i++;
                continue;
            }
            if (boxPlayer.hasBox(box)) {
                menu.getInvBuilder().addItem(i, new ItemBuilder(this, box.getDisplay(), box.getIcon(), box.getIconData(), true, unlockedLore).build());
            }
            else menu.getInvBuilder().addItem(i, new ItemBuilder(this, box.getDisplay(), box.getIcon(), box.getIconData(), true, lockedLore, box.getPrice()).build());
            i++;
        }
        menu.getInvBuilder().addItem(49, getCloseItem(boxPlayer));
        menu.getInvBuilder().addItem(50, getCurrentCageItem(boxPlayer));
        return menu;
    }

    public ItemStack getCloseItem(BoxPlayer boxPlayer) {
        return new ItemBuilder(this, config.get(boxPlayer.getPlayer(), "Menu.CloseItem.Name"),
                config.get(boxPlayer.getPlayer(), "Menu.CloseItem.Material"), config.getInt("Menu.CloseItem.Data"), true, config.getList("Menu.CloseItem.Lore")).build();
    }

    public ItemStack getCurrentCageItem(BoxPlayer boxPlayer) {
        return new ItemBuilder(this, boxPlayer.getCurrentBox().getDisplay(), boxPlayer.getCurrentBox().getIcon(),
                boxPlayer.getCurrentBox().getIconData(), true, config.getList("Menu.CurrentBox.Lore")).build();
    }

    public Box getDefaultBox() {
        return getBox(boxes.getConfig().getString("DefaultBox"));
    }

    private void loadBoxes() {
        ConfigurationSection a = boxes.getConfig().getConfigurationSection("Boxes");
        for (String k : a.getKeys(false)) {
            String display = a.getString(k + ".display");
            String permission = a.getString(k + ".permission");
            int icon = a.getInt(k + ".icon");
            int iconData = a.getInt(k + ".iconData");
            int price = a.getInt(k + ".price");
            List<String> data = a.getStringList(k + ".data");
            Box box = new Box(k);
            box.setDisplay(display);
            box.setPermission(permission);
            box.setIcon(icon);
            box.setIconData(iconData);
            box.setPrice(price);
            box.setData(data);
            this.addBox(box);
        }
    }
}
