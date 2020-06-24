package me.proiezrush.swboxes.player;

import org.bukkit.Location;

public class Pos {

    private Location location;
    private PosType type;
    public Pos() {
        this.location = null;
        this.type = null;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setType(PosType type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public PosType getType() {
        return type;
    }
}
