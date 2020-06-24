package me.proiezrush.swboxes.utils;

import me.proiezrush.swboxes.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ItemBuilder {

    private final ItemStack item;

    public ItemBuilder(Main m, String name, String material, int data, boolean hasLore, List<String> lore) {
        this.item = new ItemStack(Material.valueOf(material), 1, (short) data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(m.getAdm().parsePlaceholders(null, name));
        if (hasLore) {
            List<String> l = new ArrayList<>();
            for (String s : lore) {
                l.add(m.getAdm().parsePlaceholders(null, s));
            }
            meta.setLore(l);
        }
        this.item.setItemMeta(meta);
    }

    public ItemBuilder(Main m, String name, int material, int data, boolean hasLore, List<String> lore) {
        this.item = new ItemStack(Material.getMaterial(material), 1, (short) data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(m.getAdm().parsePlaceholders(null, name));
        if (hasLore) {
            List<String> l = new ArrayList<>();
            for (String s : lore) {
                l.add(m.getAdm().parsePlaceholders(null, s));
            }
            meta.setLore(l);
        }
        this.item.setItemMeta(meta);
    }

    public ItemBuilder(Main m, String name, int material, int data, boolean hasLore, List<String> lore, int price) {
        this.item = new ItemStack(Material.getMaterial(material), 1, (short) data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(m.getAdm().parsePlaceholders(null, name));
        if (hasLore) {
            List<String> l = new ArrayList<>();
            for (String s : lore) {
                l.add(m.getAdm().parsePlaceholders(null, s.replace("%price%", String.valueOf(price))));
            }
            meta.setLore(l);
        }
        this.item.setItemMeta(meta);
    }


    public ItemStack build() {
        return item;
    }

}
