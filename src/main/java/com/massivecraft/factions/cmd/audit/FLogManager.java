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
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FLogManager {
    private Map<String, FactionLogs> factionLogMap = new ConcurrentHashMap<>();
    private File logFile;
    private Type logToken = (new TypeToken<ConcurrentHashMap<String, FactionLogs>>() {
    }).getType();
    private Map<UUID, LogTimer> logTimers = new ConcurrentHashMap<>();
    private boolean saving = false;

    public FLogManager() {
    }

    public void log(Faction faction, FLogType type, String... arguments) {
        FactionLogs logs = factionLogMap.computeIfAbsent(faction.getId(), (n) -> new FactionLogs());
        logs.log(type, arguments);
    }

    public void loadLogs(FactionsPlugin plugin) {
        try {
            logFile = new File(plugin.getDataFolder() + File.separator + "data", "factionLogs.json");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            factionLogMap = (Map<String, FactionLogs>) JSONUtils.fromJson(logFile, logToken);
            if (factionLogMap == null) {
                factionLogMap = new ConcurrentHashMap<>();
            }

            factionLogMap.forEach((factionId, factionLogs) -> {

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
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(FactionsPlugin.instance, () -> {
            if (saving) {
                Bukkit.getLogger().info("Ignoring saveLogs scheduler due to saving == true!");
            } else {
                logTimers.forEach((uuid, logTimer) -> {
                    if (logTimer != null && logTimer.getFactionId() != null) {
                        Faction faction = Factions.getInstance().getFactionById(logTimer.getFactionId());
                        if (faction == null) {
                            logTimers.remove(uuid);
                            Bukkit.getLogger().info("Null faction for logs " + logTimer.getFactionId());
                        } else {
                            if (logTimer.isEmpty()) {
                                logTimers.remove(uuid);
                            }
                        }
                    } else {
                        logTimers.remove(uuid);
                    }
                });
            }
        }, 20L, 400L);
    }

    public void pushPendingLogs(LogTimer.TimerType type) {
        Faction faction = null;

        for (Map.Entry<UUID, LogTimer> uuidLogTimerEntry : getLogTimers().entrySet()) {
            LogTimer logTimer = uuidLogTimerEntry.getValue();
            if (faction == null) {
                faction = Factions.getInstance().getFactionById(logTimer.getFactionId());
            }

            if (type != null) {
                Map<LogTimer.TimerSubType, LogTimer.Timer> timers = logTimer.get(type);
                if (timers != null && faction != null) {
                    logTimer.pushLogs(faction, type);
                }
            } else if (faction != null) {
                Faction finalFaction = faction;
                logTimer.keySet().forEach((timerType) -> logTimer.pushLogs(finalFaction, timerType));
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
        } else {
            saving = true;

            try {
                pushPendingLogs(null);
            } catch (Exception e) {
                Bukkit.getLogger().info("error pushing pending logs: " + e.getMessage());
                e.printStackTrace();
            }

            try {
                JSONUtils.saveJSONToFile(logFile, factionLogMap, logToken);
            } catch (Exception e1) {
                Bukkit.getLogger().info("ERROR SAVING JSON LOGS: " + e1.getMessage());
                e1.printStackTrace();
            }

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
