package me.proiezrush.swboxes.player;

import me.proiezrush.swboxes.Main;
import me.proiezrush.swboxes.boxes.Box;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BoxPlayer {

    private Player player;
    private Location lastLoc;
    private Pos pos1;
    private Pos pos2;
    private List<Box> boxes;
    private boolean isInEditorMode;
    private Box currentBox;
    private Location currentLoc;
    public BoxPlayer(Main m, Player player) {
        this.player = player;
        this.lastLoc = null;
        this.pos1 = new Pos();
        this.pos2 = new Pos();
        this.boxes = new ArrayList<>();
        this.isInEditorMode = false;
        this.currentBox = m.getDefaultBox();
        this.currentLoc = null;
    }

    public void setLastLoc(Location lastLoc) {
        this.lastLoc = lastLoc;
    }

    public void addBox(Box box) {
        boxes.add(box);
    }

    public void removeBox(Box box) {
        boxes.remove(box);
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }

    public void setInEditorMode(boolean inEditorMode) {
        isInEditorMode = inEditorMode;
    }

    public void setCurrentBox(Box currentBox) {
        this.currentBox = currentBox;
    }

    public void setCurrentLoc(Location currentLoc) {
        this.currentLoc = currentLoc;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLastLoc() {
        return lastLoc;
    }

    public Pos getPos1() {
        return pos1;
    }

    public Pos getPos2() {
        return pos2;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public boolean isInEditorMode() {
        return isInEditorMode;
    }

    public Box getCurrentBox() {
        return currentBox;
    }

    public Location getCurrentLoc() {
        return currentLoc;
    }

    public boolean hasBox(Box box) {
        if (boxes != null) {
            return boxes.contains(box);
        }
        return false;
    }
}
