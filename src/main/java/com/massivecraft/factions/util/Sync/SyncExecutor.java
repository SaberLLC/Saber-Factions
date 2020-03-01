package com.massivecraft.factions.util.Sync;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;

/**
 * @author droppinganvil
 */
public class SyncExecutor {
    private static boolean started = false;
    /**
     * This queue is used to collect task that need to happen async rather than scheduling for every action.
     */
    public static Queue<SyncTask> taskQueue = new LinkedList<>();

    public static void startTask() {
        if (started) return;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(FactionsPlugin.instance, () -> {
            SyncTask syncTask = taskQueue.poll();
            if (syncTask != null) {
                try {
                    syncTask.call();
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                    FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "A task was not able to execute successfully! Please provide this stacktrace to the Saber team at Discord.Saber.pw");
                }
            }
        }, 0L, 2L);
    }
}
