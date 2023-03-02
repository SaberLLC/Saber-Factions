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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Author: Driftay
 * @Date: 2/11/2022 4:41 PM
 */
public class FactionDataHelper {

    private static List<FactionData> data;

    public static void init() {
        FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new FactionDataListener(), FactionsPlugin.getInstance());
        new FactionDataDeploymentTask().runTaskTimerAsynchronously(FactionsPlugin.getInstance(), 20, 20);
        data = new ArrayList<>();
        File file = new File(FactionsPlugin.getInstance().getDataFolder() + "/faction-data");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static void onDisable() {
        for (int i = FactionDataHelper.data.size() - 1; i >= 0; i--) {
            FactionData data = FactionDataHelper.data.get(i);
            data.removeSafely();
        }
    }

    public FactionDataHelper(FactionData data) {
        FactionDataHelper.data.add(data);
    }


    public static void createConfiguration(Faction faction) {
        File file = new File(FactionsPlugin.getInstance().getDataFolder() + "/faction-data/"
                + faction.getId() + ".yml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setConfigValue(Faction faction, String key, Object value) {
        File file = new File(FactionsPlugin.getInstance().getDataFolder() + "/faction-data/"
                + faction.getId() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(key, value);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static YamlConfiguration getConfiguration(Faction faction) {
        File file = new File(FactionsPlugin.getInstance().getDataFolder() + "/faction-data/"
                + faction.getId() + ".yml");
        if (!file.exists()) {
            return null;
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static List<File> getAllFactionFiles() {
        List<File> files = Lists.newArrayList();
        Collections.addAll(files, Objects.requireNonNull(new File(FactionsPlugin.getInstance().getDataFolder() + "/faction-data/")
                .listFiles()));
        return files;
    }

    public static int removeDataFromFiles(String path) {
        int count = 0;
        for (File file : new File(FactionsPlugin.getInstance().getDataFolder() + "/faction-data/")
                .listFiles()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set(path, null);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            count++;
        }
        return count;
    }

    public static File getFile(Faction faction) {
        File file = new File(FactionsPlugin.getInstance().getDataFolder() + "/faction-data/"
                + faction.getId() + ".yml");
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    public static boolean doesConfigurationExist(Faction faction) {
        File file = new File(FactionsPlugin.getInstance().getDataFolder() + "/faction-data/"
                + faction.getId() + ".yml");
        return file.exists();
    }

    public static List<FactionData> getData() {
        return data;
    }

    public static FactionData findFactionData(String factionID) {
        for (FactionData data : FactionDataHelper.data) {
            if (data.getFactionID().equals(factionID)) {
                return data;
            }
        }
        return null;
    }

    public static FactionData findFactionData(Faction faction) {
        return findFactionData(faction.getId());
    }

    public static String getFactionIDFromFile(File file) {
        return Factions.getInstance().getFactionById(file.getName().substring(0, file.getName().indexOf("."))).getId();
    }
}
