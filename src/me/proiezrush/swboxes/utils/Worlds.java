package me.proiezrush.swboxes.utils;

import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.util.Random;

public class Worlds {

    public static World createVoidWorld(String name) {
        WorldCreator creator = new WorldCreator(name);
        creator.generator(new ChunkGenerator() {
            public byte[] generate(World world, Random random, int x, int z) {
                return new byte[32768];
            }
        });
        World world = creator.createWorld();
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setSpawnFlags(true, true);
        world.setPVP(false);
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(2147483647);
        world.setAutoSave(false);
        world.setTicksPerAnimalSpawns(1);
        world.setTicksPerMonsterSpawns(1);
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("mobGriefing", "false");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("showDeathMessages", "false");
        world.setSpawnLocation(0, 0, 0);
        setWorld(world);
        return world;
    }

    private static void setWorld(World world) {
        world.getBlockAt(0, 0, 0).setType(Material.GLASS);
        WorldBorder border = world.getWorldBorder();
        border.setCenter(0.5D, 0.5D);
        border.setSize(9.0D);
    }

    public static boolean deleteWorld(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return path.delete();
    }

}
