package me.proiezrush.swboxes.boxes;

import me.proiezrush.swboxes.Main;
import me.proiezrush.swboxes.utils.InvBuilder;
import org.bukkit.entity.Player;

public class BoxMenu {

    private Main m;
    private InvBuilder invBuilder;
    private Player player;
    public BoxMenu(Main m, Player player) {
        this.m = m;
        this.player = player;
        this.invBuilder = new InvBuilder(m, m.getC().get(player, "Menu.Name"), 54);
    }

    public Player getPlayer() {
        return player;
    }

    public InvBuilder getInvBuilder() {
        return invBuilder;
    }
}
