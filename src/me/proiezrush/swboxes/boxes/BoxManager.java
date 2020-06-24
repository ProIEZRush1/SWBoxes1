package me.proiezrush.swboxes.boxes;

import me.proiezrush.swboxes.Cuboid;
import me.proiezrush.swboxes.Main;
import me.proiezrush.swboxes.player.BoxPlayer;
import me.proiezrush.swboxes.utils.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BoxManager {

    public static void create(Main m, Player p, String name) {
        BoxPlayer boxPlayer = m.getBoxPlayer(p.getName());
        boxPlayer.setLastLoc(p.getLocation());
        World world = Worlds.createVoidWorld(name);
        world.getBlockAt(0,0,0).setType(Material.GLASS);
        Location l = new Location(world, 0.5D,1D,0.5D);
        p.teleport(l);
    }

    public static void save(Main m, Player p, String name) {
        BoxPlayer boxPlayer = m.getBoxPlayer(p.getName());
        Box box = new Box(name);
        box.setDisplay("&" + getRandomNumberInRange(0, 9) + "Edit box name in boxes.yml");
        box.setPermission("swboxes.box." + name);
        int icon = getRandomNumberInRange(0, 500);
        if (Material.getMaterial(icon) == null) {
            icon = getRandomNumberInRange(0, 500);
        }
        box.setIcon(icon);
        box.setIconData(0);
        Cuboid cuboid = new Cuboid(boxPlayer.getPos1().getLocation(), boxPlayer.getPos2().getLocation());
        List<Block> blocks = cuboid.getBlocks();
        List<String> data = new ArrayList<>();
        for (int i=0;i<blocks.size();i++) {
            data.add(blocks.get(i).getX() + "," + blocks.get(i).getY() + "," + blocks.get(i).getZ() + "," + blocks.get(i).getType() + "," + blocks.get(i).getData());
        }
        box.setData(data);
        saveBox(m, box);
        m.addBox(box);
        if (boxPlayer.getLastLoc() != null) {
            p.teleport(boxPlayer.getLastLoc());
        }
        Worlds.deleteWorld(Bukkit.getWorld(name).getWorldFolder());
    }

    public static void delete(Main m, String name) {
        for (BoxPlayer p : m.getBoxPlayers().values()) {
            if (p.getCurrentBox().getName().equalsIgnoreCase(name)) {
                p.setCurrentBox(m.getDefaultBox());
            }
        }
        m.removeBox(name);
        m.getBoxes().set("Boxes." + name, null);
        m.getBoxes().save();
    }

    public static void edit(Main m, Player p, String name) {
        World w = Worlds.createVoidWorld(name);
        for (String s : m.getBoxes().getList("Boxes." + name + ".data")) {
            String[] values = s.split(",");
            int x = Integer.parseInt(values[0]);
            int y = Integer.parseInt(values[1]);
            int z = Integer.parseInt(values[2]);
            String id = values[3];
            int data = Integer.parseInt(values[4]);
            w.getBlockAt(x, y, z).setType(Material.valueOf(id));
            w.getBlockAt(x, y, z).setData((byte) data);
        }
        p.teleport(new Location(w, 0,1,0));
    }

    public static void saveBox(Main m, Box box) {
        m.getBoxes().set("Boxes." + box.getName() + ".display", box.getDisplay());
        m.getBoxes().set("Boxes." + box.getName() + ".permission", box.getPermission());
        m.getBoxes().set("Boxes." + box.getName() + ".icon", box.getIcon());
        m.getBoxes().set("Boxes." + box.getName() + ".iconData", box.getIconData());
        m.getBoxes().set("Boxes." + box.getName() + ".price", box.getPrice());
        m.getBoxes().set("Boxes." + box.getName() + ".data", box.getData());
        m.getBoxes().save();
    }

    public static void setBox(BoxPlayer player, Location loc) { //On join
        Box box = player.getCurrentBox();
        player.getPlayer().teleport(player.getCurrentLoc());
        for (String s : box.getData()) {
            String[] values = s.split(",");
            int x = Integer.parseInt(values[0]);
            int y = Integer.parseInt(values[1]);
            int z = Integer.parseInt(values[2]);
            String id = values[3];
            int data = Integer.parseInt(values[4]);
            Location l = new Location(Bukkit.getWorld(box.getName()), loc.getX() - x, loc.getY() + y - 1.0D, loc.getZ() - z).clone();
            l.setWorld(loc.getWorld());
            l.getBlock().setType(Material.valueOf(id));
            l.getBlock().setData((byte) data);
        }
    }

    public static void removeBox(BoxPlayer player, Location loc) { //On start
        Box box = player.getCurrentBox();
        for (String s : box.getData()) {
            String[] values = s.split(",");
            int x = Integer.parseInt(values[0]);
            int y = Integer.parseInt(values[1]);
            int z = Integer.parseInt(values[2]);
            Location l = new Location(Bukkit.getWorld(box.getName()), loc.getX() - x, loc.getY() + y - 1.0D, loc.getZ() - z).clone();
            l.setWorld(loc.getWorld());
            l.getBlock().setType(Material.AIR);
            l.getBlock().setData((byte) 0);
        }
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

}
