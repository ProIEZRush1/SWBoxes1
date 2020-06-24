package me.proiezrush.swboxes;

import me.proiezrush.swboxes.dependencies.PAPI;
import org.bukkit.entity.Player;

public class Admin {

    public String parsePlaceholders(Player p, String s) {
        return PAPI.parsePlaceholders(p, s);
    }
}
