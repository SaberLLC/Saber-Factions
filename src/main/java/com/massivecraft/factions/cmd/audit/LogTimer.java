package com.massivecraft.factions.cmd.audit;

/**
 * @author Saser
 */

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import org.apache.commons.lang.StringUtils;
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
                                    //Bukkit.getLogger().info("Unable to find EntityType for " + data.getData() + " for " + subTimer + " for fac " + factionId + "!");
                                } else {
                                    entityCounts.computeIfAbsent(types, (e) -> new AtomicInteger(0)).addAndGet(ints.get());
                                }
                            });
                            entityCounts.forEach((entityType, count) -> FactionsPlugin.instance.getFlogManager().log(faction, FLogType.SPAWNER_EDIT, username, subTimer == TimerSubType.SPAWNER_BREAK ? "broke" : "placed", count.get() + "x", StringUtils.capitaliseAllWords(entityType.name().toLowerCase().replace("_", " "))));
                        }
                    });
                }
            }
        });
        remove(type);
    }

    public String getFactionId() {
        return this.factionId;
    }

    public String getUsername() {
        return this.username;
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

    public class Timer {
        private long startTime;
        private long count;
        private Object extraData;

        public Timer(long startTime, long count, Object extraData) {
            this.startTime = startTime;
            this.count = count;
            this.extraData = extraData;
        }

        LogTimer.Timer increment(long amount) {
            this.count += amount;
            return this;
        }

        public boolean isReadyToLog(long expiration) {
            return System.currentTimeMillis() - this.startTime >= expiration;
        }

        public long getStartTime() {
            return this.startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getCount() {
            return this.count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public Object getExtraData() {
            return this.extraData;
        }

        public void setExtraData(Object extraData) {
            this.extraData = extraData;
        }
    }
}
