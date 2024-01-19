package com.massivecraft.factions.data;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.data.helpers.FactionDataHelper;
import com.massivecraft.factions.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Driftay
 * @Date: 2/11/2022 4:37 PM
 */
public class FactionData {

    private static final String FACTION_DATA_PATH = "/faction-data/";

    private final String factionID;
    private final String factionTag;
    private Map<String, Object> map;
    private boolean saving;

    public FactionData(Faction faction) {
        this.factionTag = faction.getTag();
        this.factionID = faction.getId();
        this.map = new HashMap<>();
        this.saving = false;
        this.load();
    }

    /**
     * Generates the path for the faction file.
     *
     * @return The path as a string.
     */
    private String getFactionFilePath() {
        return FactionsPlugin.getInstance().getDataFolder() + FACTION_DATA_PATH + factionID + ".yml";
    }

    public void addStoredValue(String key, Object value) {
        if (key != null && value != null) {
            if (!this.map.containsKey(key)) {
                this.map.put(key, value);
            }
        } else {
            // Handle null key or value, you may choose to log a warning or throw an exception
            System.out.println("Warning: Attempted to add null key or value.");
        }
    }

    public boolean isSaving() {
        return this.saving;
    }

    public YamlConfiguration getConfiguration() {
        return YamlConfiguration.loadConfiguration(new File(getFactionFilePath()));
    }

    public Object getStoredValue(String key) {
        return this.map.get(key);
    }

    public boolean hasStoredValue(String key) {
        return this.map.containsKey(key);
    }

    public boolean hasConfigValue(String key) {
        return this.getConfiguration().contains(key);
    }

    public boolean hasValue(String key) {
        boolean value = hasConfigValue(key) || hasStoredValue(key);
        if (!value) {
            setDefaultPath(key, null);
        }
        return value;
    }

    public void setDefaultPath(String path, Object value) {
        this.map.put(path, value);
    }

    public Map<String, Object> getStoredValues() {
        return this.map;
    }

    public void setStoredValues(Map<String, Object> map) {
        this.map = map;
    }

    public Object getValue(String key, Object defaultValue) {
        return this.map.computeIfAbsent(key, k -> {
            Object value = getConfiguration().get(k);
            return (value != null) ? value : defaultValue;
        });
    }

    public void deleteFactionData(Faction faction) {
        File file = new File(getFactionFilePath());

        if (file.delete()) {
            Logger.print("Deleting faction-data for faction " + faction.getTag(), Logger.PrefixType.DEFAULT);
        } else {
            Logger.print("Failed to delete faction-data for faction " + faction.getTag(), Logger.PrefixType.WARNING);
        }
    }

    public void save() {
        if (this.isSaving()) {
            return;
        }
        this.saving = true;
        File file = new File(getFactionFilePath());
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        Bukkit.getLogger().info("[FactionData] Saving " + this.factionTag + "'s Data to the disk");

        map.forEach(configuration::set);

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.saving = false;
        }
    }

    public void load() {
        File file = new File(getFactionFilePath());
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        // Load values from the configuration into the map
        for (String key : configuration.getKeys(false)) {
            map.put(key, configuration.get(key));
        }
    }

    public String getFactionID() {
        return factionID;
    }

    public String getFactionTag() {
        return factionTag;
    }

    public void removeSafely() {
        this.save();
    }

    @Override
    public String toString() {
        return "FactionData{" +
                "factionID='" + factionID + '\'' +
                ", factionTag='" + factionTag + '\'' +
                ", map=" + map +
                ", saving=" + saving +
                '}';
    }
}