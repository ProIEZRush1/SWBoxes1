package me.proiezrush.swboxes.boxes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Box {

    private String name;
    private String display;
    private String permission;
    private int icon;
    private int iconData;
    private List<String> data;
    private int price;

    public Box(String name) {
        this.name = name;
        this.display = null;
        this.permission = null;
        this.icon = getRandomNumberInRange(0, 500);
        this.iconData = 0;
        this.data = new ArrayList<>();
        this.price = 500;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setIconData(int iconData) {
        this.iconData = iconData;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void addData(String data) {
        this.data.add(data);
    }

    public void removeData(String data) {
        this.data.remove(data);
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    public String getPermission() {
        return permission;
    }

    public int getIcon() {
        return icon;
    }

    public int getIconData() {
        return iconData;
    }

    public List<String> getData() {
        return data;
    }

    public int getPrice() {
        return price;
    }

    private int getRandomNumberInRange(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }


}
