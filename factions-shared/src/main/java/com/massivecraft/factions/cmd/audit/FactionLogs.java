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

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd hh:mmaa");

    private final Map<FLogType, LinkedList<FactionLog>> mostRecentLogs = new ConcurrentHashMap<>();

    public FactionLogs() {
    }

    public void log(FLogType type, String... arguments) {
        if (type.getRequiredArgs() > arguments.length) {
            logWarning("Invalid argument count met. Required: " + type.getRequiredArgs());
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
        return mostRecentLogs.isEmpty();
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

    private static void logWarning(String message) {
        Bukkit.getLogger().warning(message);
        new Exception().printStackTrace();
    }

    public static class FactionLog {
        private final long timestamp;
        private final List<String> arguments;

        public FactionLog(long timestamp, List<String> arguments) {
            this.timestamp = timestamp;
            this.arguments = (arguments != null) ? new ArrayList<>(arguments) : new ArrayList<>();
        }

        public boolean isExpired(long duration) {
            return System.currentTimeMillis() - timestamp >= duration;
        }

        public String getLogLine(FLogType type, boolean includeTimestamp) {
            String[] args = arguments.toArray(new String[0]);
            String timeFormat = includeTimestamp ? FORMAT.format(new Date(timestamp)) : "";
            return String.format(ChatColor.translateAlternateColorCodes('&', type.getMsg()), (Object[]) args)
                    + (includeTimestamp ? ChatColor.GRAY + " - " + timeFormat : "");
        }
    }
}