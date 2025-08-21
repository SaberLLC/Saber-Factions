package com.massivecraft.factions.data.helpers;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.data.FactionData;
import com.massivecraft.factions.util.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FactionDataHelper {
    private final Map<String, WeakReference<FactionData>> cache = new ConcurrentHashMap<>();
    private final ExecutorService ioPool = Executors.newCachedThreadPool();
    private final File dataDir;

    public FactionDataHelper(File pluginDataFolder) {
        this.dataDir = new File(pluginDataFolder, "faction-data");
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                throw new IllegalStateException("Failed to create faction-data directory: " + dataDir.getAbsolutePath());
            }
        }
    }

    public FactionData loadFactionDataSync(Faction faction) {
        try {
            File file = getFactionFile(faction.getId());
            FactionData data;
            if (!file.exists()) {
                data = new FactionData(faction);
            } else {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                data = FactionData.fromConfig(faction, config);
            }
            cache.put(faction.getId(), new WeakReference<>(data));
            return data;
        } catch (Exception e) {
            Logger.print("[FactionDataHelper] Error loading faction-data for " + faction.getId() + ": " + e.getMessage(), Logger.PrefixType.FAILED);
            return new FactionData(faction);
        }
    }

    public CompletableFuture<FactionData> loadFactionData(Faction faction) {
        WeakReference<FactionData> ref = cache.get(faction.getId());
        FactionData cached = (ref != null) ? ref.get() : null;
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                File file = getFactionFile(faction.getId());
                if (!file.exists()) return new FactionData(faction);
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                return FactionData.fromConfig(faction, config);
            } catch (Exception e) {
                Logger.print("[FactionDataHelper] Error loading faction-data for " + faction.getId() + ": " + e.getMessage(), Logger.PrefixType.FAILED);
                return new FactionData(faction);
            }
        }, ioPool).thenApply(data -> {
            cache.put(faction.getId(), new WeakReference<>(data));
            return data;
        });
    }

    public FactionData getOrLoadFactionData(Faction faction) {
        WeakReference<FactionData> ref = cache.get(faction.getId());
        FactionData cached = (ref != null) ? ref.get() : null;

        if (cached != null) {
            return cached;
        }

        return loadFactionDataSync(faction);
    }

    public void setFactionData(Faction faction, String key, Object value) {
        FactionData data = getOrLoadFactionData(faction);
        data.set(key, value);
        saveFactionData(data);
    }

    public Object getFactionData(Faction faction, String key) {
        FactionData data = getOrLoadFactionData(faction);
        return data.get(key);
    }

    public Object getFactionData(Faction faction, String key, Object defaultValue) {
        Object value = getFactionData(faction, key);
        return value != null ? value : defaultValue;
    }

    public void saveFactionData(FactionData data) {
        ioPool.submit(() -> {
            try {
                File file = getFactionFile(data.getFaction().getId());
                YamlConfiguration config = data.toConfig();
                config.save(file);
            } catch (Exception e) {
                Logger.print("[FactionDataHelper] Error saving faction-data for " + data.getFaction().getId() + ": " + e.getMessage(), Logger.PrefixType.FAILED);
            }
        });
    }

    public void saveAllCachedData() {
        cache.forEach((factionId, ref) -> {
            FactionData data = ref.get();
            if (data != null) {
                saveFactionData(data);
            }
        });
    }

    public void deleteFactionData(Faction faction) {
        ioPool.submit(() -> {
            File file = getFactionFile(faction.getId());
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    Logger.print("[FactionDataHelper] Warning: Failed to delete faction-data file: " + file.getAbsolutePath(), Logger.PrefixType.WARNING);
                }
            }
            cache.remove(faction.getId());
        });
    }

    public Map<String, WeakReference<FactionData>> getCache() {
        return cache;
    }

    public FactionData getCached(Faction faction) {
        WeakReference<FactionData> ref = cache.get(faction.getId());
        return ref == null ? null : ref.get();
    }

    private File getFactionFile(String id) {
        return new File(dataDir, id + ".yml");
    }

    public void shutdown() {
        saveAllCachedData();
        ioPool.shutdown();
    }
}