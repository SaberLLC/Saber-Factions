package com.massivecraft.factions.cmd.audit;

/**
 * @author Saser
 */

import com.google.common.collect.Lists;
import com.massivecraft.factions.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class FactionLogs {
    public static transient SimpleDateFormat format = new SimpleDateFormat("MM/dd hh:mmaa"); //MM/dd hh:mmaa
    private Map<FLogType, LinkedList<FactionLog>> mostRecentLogs = new ConcurrentHashMap<>();

    public FactionLogs() {
    }

    public void log(FLogType type, String... arguments) {
        if (type.getRequiredArgs() > arguments.length) {
            Bukkit.getLogger().info("INVALID ARGUMENT COUNT MET: " + type.getRequiredArgs() + " REQUIRED: ");
            Thread.dumpStack();
        } else {
            LinkedList<FactionLog> logs = mostRecentLogs.computeIfAbsent(type, (lists) -> new LinkedList<>());
            logs.add(new FactionLog(System.currentTimeMillis(), Lists.newArrayList(arguments)));
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
        List<FLogType> toRemove = Lists.newArrayList();
        mostRecentLogs.forEach((logType, logs) -> {
            if (logs == null) {
                toRemove.add(logType);
            } else if (logType != FLogType.F_TNT) {
                Iterator<FactionLog> iter = logs.iterator();
                while (iter.hasNext()) {
                    try {
                        FactionLog log = iter.next();
                        if (log == null || log.isExpired(duration)) {
                            iter.remove();
                        }
                    } catch (Exception e) {
                        Bukkit.getLogger().info("ERROR TRYING TO GET next FACTION LOG: " + e.getMessage());
                        try {
                            iter.remove();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                if (logs.isEmpty())
                    toRemove.add(logType);
            }
        });
        toRemove.forEach((rem) -> mostRecentLogs.remove(rem));
    }

    public Map<FLogType, LinkedList<FactionLog>> getMostRecentLogs() {
        return mostRecentLogs;
    }

    public static class FactionLog {
        private long t;
        private List<String> a;

        public FactionLog(long t, List<String> a) {
            this.t = t;
            this.a = a;
        }

        public boolean isExpired(long duration) {
            return System.currentTimeMillis() - t >= duration;
        }

        public String getLogLine(FLogType type, boolean timestamp) {
            String[] args = a.toArray(new String[0]);
            String timeFormat = "";
            if (timestamp) {
                timeFormat = FactionLogs.format.format(t);
                if (timeFormat.startsWith("0")) {
                    timeFormat = timeFormat.substring(1);
                }
            }
            return String.format(CC.translate(type.getMsg()), args) + (timestamp ? ChatColor.GRAY + " - " + timeFormat : "");
        }
    }
}
