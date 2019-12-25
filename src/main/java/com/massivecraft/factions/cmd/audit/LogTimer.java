package com.massivecraft.factions.cmd.audit;

/**
 * @author Saser
 */
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LogTimer extends ConcurrentHashMap<LogTimer.TimerType, Map<LogTimer.TimerSubType, LogTimer.Timer>> {
    private String factionId;
    private String username;

    public LogTimer(String username, String factionId) {
        this.username = username;
        this.factionId = factionId;
    }

    public Map<LogTimer.TimerSubType, LogTimer.Timer> getCurrentTimersOrCreate(LogTimer.TimerType type) {
        return this.computeIfAbsent(type, (m) -> new ConcurrentHashMap<>());
    }

    public LogTimer.Timer attemptLog(LogTimer.TimerType type, LogTimer.TimerSubType subType, long increment) {
        return this.getCurrentTimersOrCreate(type).computeIfAbsent(subType, (e) -> new Timer(System.currentTimeMillis(), 0L, null)).increment(increment);
    }

    public void pushLogs(Faction faction, LogTimer.TimerType type) {
        StringBuilder soldString = new StringBuilder();
        forEach((timerType, map) -> {
            if (timerType == type) {
                if (timerType == LogTimer.TimerType.SPAWNER_EDIT) {
                    map.forEach((subTimer, timer) -> {
                        Map<EntityType, AtomicInteger> entityCounts = new HashMap<>();
                        Map<MaterialData, AtomicInteger> currentCounts = (Map) timer.getExtraData();
                        if (currentCounts != null) {
                            currentCounts.forEach((data, ints) -> {
                                EntityType types = EntityType.fromId(data.getData());
                                if (types == null) {
                                    Bukkit.getLogger().info("Unable to find EntityType for " + data.getData() + " for " + subTimer + " for fac " + this.factionId + "!");
                                } else {
                                    entityCounts.computeIfAbsent(types, (e) -> new AtomicInteger(0)).addAndGet(ints.get());
                                }
                            });
                            entityCounts.forEach((entityType, count) -> FactionsPlugin.instance.getFlogManager().log(faction, FLogType.SPAWNER_EDIT, this.username, subTimer == TimerSubType.SPAWNER_BREAK ? "broke" : "placed", count.get() + "x", StringUtils.capitaliseAllWords(entityType.name().toLowerCase().replace("_", " "))));
                        }
                    });
                }
            }
        });
        this.remove(type);
    }

    public String getFactionId() {
        return this.factionId;
    }

    public String getUsername() {
        return this.username;
    }

    public class Timer {
        private long startTime;
        private long count;
        private Object extraData;

        LogTimer.Timer increment(long amount) {
            this.count += amount;
            return this;
        }

        public boolean isReadyToLog(long expiration) {
            return System.currentTimeMillis() - this.startTime >= expiration;
        }

        public Timer(long startTime, long count, Object extraData) {
            this.startTime = startTime;
            this.count = count;
            this.extraData = extraData;
        }

        public long getStartTime() {
            return this.startTime;
        }

        public long getCount() {
            return this.count;
        }

        public Object getExtraData() {
            return this.extraData;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public void setExtraData(Object extraData) {
            this.extraData = extraData;
        }
    }

    public enum TimerSubType {
        SPAWNER_BREAK,
        SPAWNER_PLACE;
        TimerSubType() {
        }
    }

    public enum TimerType {
        SPAWNER_EDIT;
        TimerType() {
        }
    }
}
