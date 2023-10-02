package com.massivecraft.factions.cmd.audit;

/**
 * @author Saser
 */

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class FactionLogs {

    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd hh:mmaa");

    private final Map<FLogType, LinkedList<FactionLog>> mostRecentLogs = new ConcurrentHashMap<>();

    public FactionLogs() {
    }

    public void log(FLogType type, String... arguments) {
        if (type.getRequiredArgs() > arguments.length) {
            Bukkit.getLogger().warning("Invalid argument count met. Required: " + type.getRequiredArgs());
            new Exception().printStackTrace();
        } else {
            LinkedList<FactionLog> logs = mostRecentLogs.computeIfAbsent(type, k -> new LinkedList<>());
            logs.add(new FactionLog(System.currentTimeMillis(), Arrays.asList(arguments)));
            int maxLog = type == FLogType.F_TNT ? 200 : 60;
            if (logs.size() > maxLog) {
                logs.pop();
            }
        }
    }

    public boolean isEmpty() {
        return this.mostRecentLogs.isEmpty();
    }

    public void checkExpired() {
        long duration = TimeUnit.DAYS.toMillis(7L);
        List<FLogType> toRemove = new LinkedList<>();
        mostRecentLogs.forEach((logType, logs) -> {
            if (logs == null || (logType != FLogType.F_TNT && logs.isEmpty())) {
                toRemove.add(logType);
                return;
            }

            logs.removeIf(log -> log == null || log.isExpired(duration));
        });
        toRemove.forEach(mostRecentLogs::remove);
    }

    public Map<FLogType, LinkedList<FactionLog>> getMostRecentLogs() {
        return mostRecentLogs;
    }

    public static class FactionLog {
        private final long t;
        private final List<String> a;

        public FactionLog(long t, List<String> a) {
            this.t = t;
            this.a = a != null ? new ArrayList<>(a) : new ArrayList<>();
        }

        public boolean isExpired(long duration) {
            return System.currentTimeMillis() - t >= duration;
        }

        public String getLogLine(FLogType type, boolean timestamp) {
            String[] args = a.toArray(new String[0]);
            String timeFormat = "";
            if (timestamp) {
                timeFormat = FORMAT.format(new Date(t));
            }
            return String.format(ChatColor.translateAlternateColorCodes('&', type.getMsg()), (Object[]) args) + (timestamp ? ChatColor.GRAY + " - " + timeFormat : "");
        }
    }
}