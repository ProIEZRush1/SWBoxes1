package me.proiezrush.swboxes;

import ak.CookLoco.SkyWars.SkyWars;
import ak.CookLoco.SkyWars.api.SkyWarsAPI;
import ak.CookLoco.SkyWars.arena.Arena;
import ak.CookLoco.SkyWars.arena.ArenaBox;
import ak.CookLoco.SkyWars.events.ArenaJoinEvent;
import ak.CookLoco.SkyWars.events.ArenaLeaveEvent;
import ak.CookLoco.SkyWars.events.ArenaTickEvent;
import ak.CookLoco.SkyWars.player.SkyPlayer;
import ak.CookLoco.SkyWars.utils.MSG;
import me.proiezrush.swboxes.boxes.Box;
import me.proiezrush.swboxes.boxes.BoxManager;
import me.proiezrush.swboxes.player.BoxPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class Listeners implements Listener {

    private Main m;
    public Listeners(Main m) {
        this.m = m;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        BoxPlayer boxPlayer = new BoxPlayer(m, p);
        m.addBoxPlayer(boxPlayer);
        //Data
        try {
            boxPlayer.setBoxes(m.getMySQL().getPlayerBoxes(p));
            boxPlayer.setCurrentBox(m.getMySQL().getCurrentBox(m.getBoxPlayer(p.getName())));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (!boxPlayer.hasBox(m.getDefaultBox())) {
            boxPlayer.addBox(m.getDefaultBox());
        }
        if (boxPlayer.getCurrentBox() == null) {
            boxPlayer.setCurrentBox(m.getDefaultBox());
        }
        /*try {
            m.getMySQL().saveBoxes(boxPlayer);
            m.getMySQL().setCurrentBox(boxPlayer, boxPlayer.getCurrentBox());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

         */
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        BoxPlayer boxPlayer = m.getBoxPlayer(p.getName());
        /*try {
            m.getMySQL().saveBoxes(boxPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

         */
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        BoxPlayer boxPlayer = m.getBoxPlayer(p.getName());
        Block b = e.getClickedBlock();
        ItemStack item = e.getItem();
        if (item != null) {
            if (item.equals(m.getWand())) {
                if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) { //POS1
                    boxPlayer.getPos1().setLocation(b.getLocation());
                    p.sendMessage(m.getC().get(p, "pos1-set"));
                }
                else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { //POS2
                    boxPlayer.getPos2().setLocation(b.getLocation());
                    p.sendMessage(m.getC().get(p, "pos2-set"));
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        HumanEntity hE = e.getWhoClicked();
        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            if (inv != null) {
                if (inv.getName().equals(m.getC().get(null, "Menu.Name"))) {
                    if (item != null && item.hasItemMeta()) {
                        ConfigurationSection a = m.getBoxes().getConfig().getConfigurationSection("Boxes");
                        for (String k : a.getKeys(false)) {
                            String display = a.getString(k + ".display");
                            if (item.getItemMeta().getDisplayName().equals(m.getAdm().parsePlaceholders(null, display))) {
                                BoxPlayer boxPlayer = m.getBoxPlayer(p.getName());
                                Box box = m.getBox(k);
                                if (!boxPlayer.hasBox(box)) {
                                    //Buy it
                                    if (SkyWarsAPI.getSkyPlayer(p).getCoins() >= box.getPrice()) {
                                        SkyWarsAPI.getSkyPlayer(p).setCoins(SkyWarsAPI.getSkyPlayer(p).getCoins() - box.getPrice());
                                        if ((SkyWarsAPI.getSkyPlayer(p).isInArena())) {
                                            BoxManager.removeBox(boxPlayer, boxPlayer.getCurrentLoc());
                                        }
                                        boxPlayer.addBox(box);
                                        boxPlayer.setCurrentBox(box);
                                        if ((SkyWarsAPI.getSkyPlayer(p).isInArena())) {
                                            BoxManager.setBox(boxPlayer, boxPlayer.getCurrentLoc());
                                        }
                                        p.sendMessage(m.getC().get(p, "box-bought"));
                                        p.closeInventory();
                                        e.setCancelled(true);
                                        try {
                                            m.getMySQL().saveBoxes(boxPlayer);
                                            m.getMySQL().setCurrentBox(boxPlayer, box);
                                        } catch (SQLException throwables) {
                                            throwables.printStackTrace();
                                        }
                                    }
                                    else {
                                        p.sendMessage(m.getC().get(p, "not-enough-coins"));
                                        p.closeInventory();
                                        e.setCancelled(true);
                                    }
                                }
                                else {
                                    if ((SkyWarsAPI.getSkyPlayer(p).isInArena())) {
                                        BoxManager.removeBox(boxPlayer, boxPlayer.getCurrentLoc());
                                    }
                                    boxPlayer.setCurrentBox(box);
                                    if ((SkyWarsAPI.getSkyPlayer(p).isInArena())) {
                                        BoxManager.setBox(boxPlayer, boxPlayer.getCurrentLoc());
                                    }
                                    p.sendMessage(m.getC().get(p, "box-selected"));
                                    p.closeInventory();
                                    e.setCancelled(true);
                                    try {
                                        m.getMySQL().setCurrentBox(boxPlayer, box);
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInvOpen(InventoryOpenEvent e) {
        HumanEntity hE = e.getPlayer();
        Inventory inv = e.getInventory();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            if (inv != null) {
                if (inv.getName().equals(m.getAdm().parsePlaceholders(null, SkyWars.getMessage(MSG.SETTINGS_MENU_BOXES_TITLE)))) {
                    e.setCancelled(true);
                    p.closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onInvClick1(InventoryClickEvent e) {
        HumanEntity hE = e.getWhoClicked();
        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            if (inv != null) {
                if (inv.getName().equals(m.getC().get(null, "Menu.Name"))) {
                    if (item != null && item.hasItemMeta()) {
                        BoxPlayer boxPlayer = m.getBoxPlayer(p.getName());
                        if (item.equals(m.getCloseItem(boxPlayer))) {
                            p.closeInventory();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInvClick2(InventoryClickEvent e) {
        HumanEntity hE = e.getWhoClicked();
        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            if (inv != null) {
                if (inv.getName().equalsIgnoreCase(m.getAdm().parsePlaceholders(null, SkyWars.getMessage(MSG.SETTINGS_MENU_TITLE)))
                        || inv.getName().equalsIgnoreCase(m.getAdm().parsePlaceholders(null, SkyWars.getMessage(MSG.SETTINGS_MENU_TITLE)) + ChatColor.GRAY)) {
                    if (item.getType().equals(Material.valueOf(m.getC().getConfig().getString("Item.Material"))) && item.getDurability() == m.getC().getInt("Item.Data")) {
                        p.closeInventory();
                        Bukkit.getScheduler().scheduleSyncDelayedTask(m, () -> {
                            m.removeBoxMenu(p);
                            m.addBoxMenu(m.createBoxMenu(m.getBoxPlayer(p.getName())));
                            p.openInventory(m.getBoxMenu(p.getName()).getInvBuilder().getInventory());
                        }, 1L);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArenaJoin(ArenaJoinEvent e) {
        SkyPlayer skyPlayer = e.getPlayer();
        Player p = skyPlayer.getPlayer();
        if (!skyPlayer.isSpectating()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(m, () -> {
                ArenaBox arenaBox = new ArenaBox(p.getLocation());
                arenaBox.removeAll();
                arenaBox.setBox(0,0);
            }, 1L);
            Bukkit.getScheduler().scheduleSyncDelayedTask(m, () -> {
                m.getBoxPlayer(p.getName()).setCurrentLoc(p.getLocation());
                BoxManager.setBox(m.getBoxPlayer(p.getName()), m.getBoxPlayer(p.getName()).getCurrentLoc());
            }, 2L);
        }
    }

    @EventHandler
    public void onStart(ArenaTickEvent e) {
        Arena arena = e.getArena();
        if (arena.getStartCountdown() == 0) {
            for (SkyPlayer player : arena.getPlayers()) {
                BoxManager.removeBox(m.getBoxPlayer(player.getPlayer().getName()), m.getBoxPlayer(player.getPlayer().getName()).getCurrentLoc());
            }
        }
    }

    @EventHandler
    public void onQuit(ArenaLeaveEvent e) {
        Arena arena = e.getGame();
        if (arena.getStartCountdown() == 0) {
            for (SkyPlayer player : arena.getPlayers()) {
                BoxManager.removeBox(m.getBoxPlayer(player.getPlayer().getName()), m.getBoxPlayer(e.getPlayer().getPlayer().getName()).getCurrentLoc());
            }
        }
    }
}
