package com.massivecraft.factions.data.helpers;

import com.google.common.collect.Lists;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.data.FactionData;
import com.massivecraft.factions.data.listener.FactionDataListener;
import com.massivecraft.factions.data.task.FactionDataDeploymentTask;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @Author: Driftay
 * @Date: 2/11/2022 4:41 PM
 */
public class FactionDataHelper {

    private static final String FACTION_DATA_PATH = "/faction-data/";
    private static List<FactionData> data = new ArrayList<>();


    public static void init() {
        FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new FactionDataListener(), FactionsPlugin.getInstance());
        new FactionDataDeploymentTask().runTaskTimerAsynchronously(FactionsPlugin.getInstance(), 20, 20);

        File directory = getFactionDirectory();
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public static void onDisable() {
        for (FactionData dataItem : data) {
            dataItem.removeSafely();
        }
    }

    public static File getFactionFile(Faction faction) {
        return new File(FactionsPlugin.getInstance().getDataFolder(), FACTION_DATA_PATH + faction.getId() + ".yml");
    }

    public static File getFactionDirectory() {
        return new File(FactionsPlugin.getInstance().getDataFolder() + FACTION_DATA_PATH);
    }

    public static void createConfiguration(Faction faction) throws IOException {
        File file = getFactionFile(faction);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public static void addFactionData(FactionData factionData) {
        data.add(factionData);
    }

    public static void removeFactionData(FactionData factionData) {
        data.remove(factionData);
    }

    public static void setConfigValue(Faction faction, String key, Object value) throws IOException {
        File file = getFactionFile(faction);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(key, value);
        config.save(file);
    }

    public static YamlConfiguration getConfiguration(Faction faction) {
        File file = getFactionFile(faction);
        if (!file.exists()) {
            return null;
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static List<File> getAllFactionFiles() {
        File directory = getFactionDirectory();
        File[] files = Objects.requireNonNull(directory.listFiles());
        return new ArrayList<>(Arrays.asList(files));
    }

    public static int removeDataFromFiles(String path) throws IOException {
        int count = 0;
        for (File file : Objects.requireNonNull(getFactionDirectory().listFiles())) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set(path, null);
            config.save(file);
            count++;
        }
        return count;
    }

    public static boolean doesConfigurationExist(Faction faction) {
        return getFactionFile(faction).exists();
    }

    public static FactionData findFactionData(String factionID) {
        return data.stream()
                .filter(d -> d.getFactionID().equals(factionID))
                .findFirst()
                .orElse(null);
    }

    public static FactionData findFactionData(Faction faction) {
        return findFactionData(faction.getId());
    }

    public static String getFactionIDFromFile(File file) {
        return Factions.getInstance().getFactionById(file.getName().replace(".yml", "")).getId();
    }
}