package com.massivecraft.factions.data;

import com.massivecraft.factions.Faction;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FactionData {
    private final String factionId;
    private final String tag;
    private final Faction faction;
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    public FactionData(Faction faction) {
        this.faction = faction;
        this.factionId = faction.getId();
        this.tag = faction.getTag();
    }

    public static FactionData fromConfig(Faction faction, YamlConfiguration config) {
        FactionData data = new FactionData(faction);
        for (String key : config.getKeys(false)) {
            data.values.put(key, config.get(key));
        }
        return data;
    }

    public YamlConfiguration toConfig() {
        YamlConfiguration config = new YamlConfiguration();
        values.forEach(config::set);
        return config;
    }

    public Object get(String key) { return values.get(key); }
    public void set(String key, Object value) { values.put(key, value); }
    public String getFactionId() { return factionId; }
    public String getTag() { return tag; }
    public Faction getFaction() { return faction; }
}