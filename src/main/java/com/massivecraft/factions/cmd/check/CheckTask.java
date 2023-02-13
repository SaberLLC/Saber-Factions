package com.massivecraft.factions.cmd.check;

import com.google.common.collect.Lists;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CheckTask implements Runnable {

    /**
     * @author Driftay
     */

    private final Map<Integer, List<String>> wallChecks = new ConcurrentHashMap<>();
    private final Map<Integer, List<String>> bufferChecks = new ConcurrentHashMap<>();
    private static final int[] INTERVALS_MINUTES = {3, 5, 10, 15, 30};
    private static final Calendar CALENDAR = Calendar.getInstance();

    private static CheckTask instance;

    private CheckTask() {
        for (int interval : INTERVALS_MINUTES) {
            wallChecks.put(interval, new ArrayList<>());
            bufferChecks.put(interval, new ArrayList<>());
        }
    }

    public static CheckTask getInstance() {
        if (instance == null) {
            instance = new CheckTask();
        }
        return instance;
    }

    public boolean addWallCheck(String factionId, int minute) {
        return wallChecks.get(minute).add(factionId);
    }

    public boolean removeWallCheck(String factionId, int minute) {
        return wallChecks.get(minute).remove(factionId);
    }

    public boolean addBufferCheck(String factionId, int minute) {
        return bufferChecks.get(minute).add(factionId);
    }

    public boolean removeBufferCheck(String factionId, int minute) {
        return bufferChecks.get(minute).remove(factionId);
    }

    public void cleanupTask() {
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (!faction.isNormal() || faction.getChecks() == null) {
                continue;
            }
            List<Long> timestamps = new ArrayList<>(faction.getChecks().keySet());
            int count = 0;
            for (long timestamp : Lists.reverse(timestamps)) {
                if (count >= 54) {
                    faction.getChecks().remove(timestamp);
                }
                ++count;
            }
        }
    }


    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        int minute = CALENDAR.get(Calendar.MINUTE);

        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (!faction.isNormal()) {
                continue;
            }

            if (faction.getWallCheckMinutes() % 60 == minute % 60) {
                List<String> checks = this.wallChecks.get(faction.getWallCheckMinutes());
                if (this.wallChecks.containsKey(faction.getWallCheckMinutes()) && checks.contains(faction.getId())) {
                    continue;
                }
                List<String> found = this.wallChecks.computeIfAbsent(faction.getWallCheckMinutes(), integer -> new ArrayList<>());
                found.add(faction.getId());

                faction.msg(TL.CHECK_WALLS_CHECK);
                Bukkit.getScheduler().runTask(
                        FactionsPlugin.getInstance(),
                        () -> faction.getChecks().put(currentTime, "J")
                );
            }

            if (faction.getBufferCheckMinutes() % 60 == minute % 60) {
                List<String> checks = this.bufferChecks.get(faction.getBufferCheckMinutes());
                if (this.bufferChecks.containsKey(faction.getBufferCheckMinutes()) && checks.contains(faction.getId())) {
                    continue;
                }
                List<String> found = this.bufferChecks.computeIfAbsent(faction.getBufferCheckMinutes(), integer -> new ArrayList<>());
                found.add(faction.getId());

                faction.msg(TL.CHECK_BUFFERS_CHECK);
                Bukkit.getScheduler().runTask(
                        FactionsPlugin.getInstance(),
                        () -> faction.getChecks().put(currentTime, "H")
                );
            }
        }
    }
}