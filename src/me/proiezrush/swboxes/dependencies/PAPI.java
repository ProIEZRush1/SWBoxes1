package me.proiezrush.swboxes.dependencies;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PAPI {

    public static String parsePlaceholders(Player p, String s) {
        if (Bukkit.getPluginManager().getPlugin("PlacehodlerAPI") != null && p != null) {
            return ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(p, s));
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
