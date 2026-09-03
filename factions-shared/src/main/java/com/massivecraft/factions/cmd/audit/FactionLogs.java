package com.massivecraft.factions.cmd.audit;

import com.massivecraft.factions.FactionsPlugin;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FactionLogs {

    private Map<FLogType, ArrayDeque<FactionLog>> logsByType = new HashMap<>();

    public FactionLogs() {
    }

    public synchronized void log(FLogType type, String... arguments) {
        log(type, System.currentTimeMillis(), arguments);
    }

    public synchronized void log(FLogType type, long timestamp, String... arguments) {
        ArrayDeque<FactionLog> logs = this.logsByType.computeIfAbsent(type, ignored -> new ArrayDeque<>());
        logs.addLast(new FactionLog(timestamp, arguments));

        int maxEntries = resolveMaxEntries(type);
        while (logs.size() > maxEntries) {
            logs.pollFirst();
        }
    }

    public synchronized List<FactionLog> getSnapshot(FLogType type) {
        ArrayDeque<FactionLog> logs = this.logsByType.get(type);
        if (logs == null || logs.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(logs);
    }

    public synchronized Map<FLogType, List<FactionLog>> getAllSnapshots() {
        Map<FLogType, List<FactionLog>> snapshot = new HashMap<>();
        for (Map.Entry<FLogType, ArrayDeque<FactionLog>> entry : this.logsByType.entrySet()) {
            snapshot.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return snapshot;
    }

    public synchronized void pruneExpired() {
        Iterator<Map.Entry<FLogType, ArrayDeque<FactionLog>>> iterator = this.logsByType.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<FLogType, ArrayDeque<FactionLog>> entry = iterator.next();
            long retentionMillis = resolveRetentionMillis(entry.getKey());
            ArrayDeque<FactionLog> queue = entry.getValue();

            if (queue == null || queue.isEmpty()) {
                iterator.remove();
                continue;
            }

            queue.removeIf(log -> log == null || log.isExpired(retentionMillis));
            if (queue.isEmpty()) {
                iterator.remove();
            }
        }
    }

    public synchronized boolean isEmpty() {
        return this.logsByType.isEmpty();
    }

    public synchronized int getCount(FLogType type) {
        ArrayDeque<FactionLog> logs = this.logsByType.get(type);
        return logs == null ? 0 : logs.size();
    }

    public synchronized void normalize() {
        if (this.logsByType == null) {
            this.logsByType = new HashMap<>();
            return;
        }

        this.logsByType.entrySet().removeIf(entry -> entry.getKey() == null || entry.getValue() == null);
        for (Map.Entry<FLogType, ArrayDeque<FactionLog>> entry : this.logsByType.entrySet()) {
            ArrayDeque<FactionLog> queue = entry.getValue();
            queue.removeIf(log -> log == null);

            int maxEntries = resolveMaxEntries(entry.getKey());
            while (queue.size() > maxEntries) {
                queue.pollFirst();
            }
        }
    }

    private int resolveMaxEntries(FLogType type) {
        if (type == null) {
            return 1;
        }

        FactionsPlugin plugin = FactionsPlugin.getInstance();
        FLogManager manager = plugin == null ? null : plugin.getFlogManager();
        return manager == null ? type.getDefaultMaxEntries() : manager.getTypeMaxEntries(type);
    }

    private long resolveRetentionMillis(FLogType type) {
        if (type == null) {
            return 24L * 60L * 60L * 1000L;
        }

        FactionsPlugin plugin = FactionsPlugin.getInstance();
        FLogManager manager = plugin == null ? null : plugin.getFlogManager();
        return manager == null
                ? type.getDefaultRetentionDays() * 24L * 60L * 60L * 1000L
                : manager.getTypeRetentionMillis(type);
    }

    public static class FactionLog {
        private long timestamp;
        private List<String> arguments;

        public FactionLog() {
            this(0L, new String[0]);
        }

        public FactionLog(long timestamp, String... arguments) {
            this.timestamp = timestamp;
            this.arguments = new ArrayList<>();
            if (arguments != null) {
                Collections.addAll(this.arguments, arguments);
            }
        }

        public boolean isExpired(long duration) {
            return System.currentTimeMillis() - this.timestamp >= duration;
        }

        public long getTimestamp() {
            return this.timestamp;
        }

        public List<String> getArguments() {
            return this.arguments == null ? Collections.emptyList() : this.arguments;
        }
    }
}
