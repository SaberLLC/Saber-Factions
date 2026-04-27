package com.massivecraft.factions.cmd.audit;

/**
 * @author Saser
 */

import com.google.common.reflect.TypeToken;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.JSONUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FLogManager {

    private Map<String, FactionLogs> factionLogMap = new ConcurrentHashMap<>();
    private File logFile;
    private Type logToken = (new TypeToken<ConcurrentHashMap<String, FactionLogs>>() {}).getType();
    private Map<UUID, LogTimer> logTimers = new ConcurrentHashMap<>();
    private boolean saving = false;

    public FLogManager() {}

    public void log(Faction faction, FLogType type, String... arguments) {
        FactionLogs logs = factionLogMap.computeIfAbsent(faction.getId(), n -> new FactionLogs());
        logs.log(type, arguments);
    }

    public void loadLogs(FactionsPlugin plugin) {
        try {
            setupLogFile(plugin);
            loadFactionLogData();

            factionLogMap.forEach(this::handleFactionLog);
        } catch (Exception e) {
            e.printStackTrace();
        }

        scheduleLogMaintenance();
    }

    private void setupLogFile(FactionsPlugin plugin) throws IOException {
        logFile = new File(plugin.getDataFolder() + File.separator + "data", "factionLogs.json");
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
    }

    private void loadFactionLogData() throws Exception {
        factionLogMap = (Map<String, FactionLogs>) JSONUtils.fromJson(logFile, logToken);
        if (factionLogMap == null) {
            factionLogMap = new ConcurrentHashMap<>();
        }
    }

    private void handleFactionLog(String factionId, FactionLogs factionLogs) {
        Faction faction = Factions.getInstance().getFactionById(factionId);

        if (faction != null && faction.isNormal()) {
            factionLogs.checkExpired();
            if (factionLogs.isEmpty()) {
                factionLogMap.remove(factionId);
            }
        } else {
            Bukkit.getLogger().info("Removing dead faction logs for " + factionId + "!");
            factionLogMap.remove(factionId);
        }
    }

    private void scheduleLogMaintenance() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(FactionsPlugin.instance, () -> {
            if (saving) {
                Bukkit.getLogger().info("Ignoring saveLogs scheduler due to saving == true!");
                return;
            }

            logTimers.entrySet().removeIf(entry -> {
                LogTimer logTimer = entry.getValue();

                if (logTimer == null || logTimer.getFactionId() == null || Factions.getInstance().getFactionById(logTimer.getFactionId()) == null) {
                    Bukkit.getLogger().info("Null faction for logs " + logTimer.getFactionId());
                    return true;
                }

                return logTimer.isEmpty();
            });
        }, 20L, 400L);
    }

    public void pushPendingLogs(LogTimer.TimerType type) {
        for (LogTimer logTimer : getLogTimers().values()) {
            Faction faction = Factions.getInstance().getFactionById(logTimer.getFactionId());
            if (faction == null) continue;

            if (type != null) {
                Map<LogTimer.TimerSubType, LogTimer.Timer> timers = logTimer.get(type);
                if (timers != null) {
                    logTimer.pushLogs(faction, type);
                }
            } else {
                logTimer.keySet().forEach(timerType -> logTimer.pushLogs(faction, timerType));
                logTimer.clear();
            }
        }

        if (type == null) {
            getLogTimers().clear();
        }
    }

    public void saveLogs() {
        if (saving) {
            Bukkit.getLogger().info("Ignoring saveLogs due to saving==true!");
            return;
        }

        saving = true;

        try {
            pushPendingLogs(null);
            JSONUtils.saveJSONToFile(logFile, factionLogMap, logToken);
        } catch (Exception e) {
            Bukkit.getLogger().info("Error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            saving = false;
        }
    }

    public Map<String, FactionLogs> getFactionLogMap() {
        return factionLogMap;
    }

    public Map<UUID, LogTimer> getLogTimers() {
        return logTimers;
    }
}