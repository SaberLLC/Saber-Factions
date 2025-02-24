package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.beans.ConstructorProperties;
import java.util.concurrent.TimeUnit;

public class TaskRunner {

    // For synchronous tasks on the main thread
    public static final GlobalRegionScheduler REGION_SCHEDULER = Bukkit.getGlobalRegionScheduler();
    // For asynchronous tasks on a separate thread
    public static final AsyncScheduler ASYNC_SCHEDULER = Bukkit.getAsyncScheduler();

    private final JavaPlugin plugin;
    private Runnable task;

    // Instead of int taskId, store a Folia ScheduledTask
    private transient ScheduledTask scheduledTask;

    @ConstructorProperties({"plugin"})
    public TaskRunner(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static TaskRunner forPlugin(JavaPlugin plugin) {
        return new TaskRunner(plugin);
    }

    public static TaskRunner bind(Runnable task) {
        return forPlugin(FactionsPlugin.getInstance()).with(task);
    }

    public TaskRunner with(Runnable task) {
        this.task = task;
        return this;
    }

    /**
     * Cancel the running task if it's still valid and not already canceled.
     */
    public void cancelTask() {
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel();
        }
        scheduledTask = null;
    }

    /**
     * True if the task was canceled or never scheduled.
     */
    public boolean isTaskCancelled() {
        return (scheduledTask == null || scheduledTask.isCancelled());
    }

    /**
     * In Folia, there's no direct equivalent to "isQueued" or "isCurrentlyRunning".
     * We'll remove or simplify those checks. If you need them, you can store additional states.
     */
    public boolean isTaskQueued() {
        // No direct Folia equivalent; approximate by checking if it's not canceled.
        return !isTaskCancelled();
    }

    public boolean isTaskRunning() {
        // Also no direct equivalent. We'll approximate by returning !isTaskCancelled().
        return !isTaskCancelled();
    }

    /**
     * Run a one-off synchronous task immediately (0 tick delay).
     */
    public void runTaskSync() {
        cancelTask(); // optional: ensure only one task runs at a time
        scheduledTask = REGION_SCHEDULER.runDelayed(plugin, scheduledTask -> {
            if (task != null) task.run();
        }, 0L); // 0 ticks => next tick
    }

    /**
     * Run a one-off synchronous task after 'delay' ticks.
     */
    public void runTaskSyncLater(long delay) {
        cancelTask();
        scheduledTask = REGION_SCHEDULER.runDelayed(plugin, scheduledTask -> {
            if (task != null) task.run();
        }, delay);
    }

    /**
     * Run a repeating synchronous task every 'interval' ticks, starting after 'delay' ticks.
     */
    public void runTaskSyncTimer(long delay, long interval) {
        cancelTask();
        scheduledTask = REGION_SCHEDULER.runAtFixedRate(
            plugin,
            scheduledTask -> {
                if (task != null) task.run();
            },
            delay,    // initial delay in ticks
            interval  // repeat period in ticks
        );
    }

    /**
     * Run a one-off asynchronous task immediately.
     */
    public void runTaskAsync() {
        cancelTask();
        // No delay => 0 ms
        scheduledTask = ASYNC_SCHEDULER.runDelayed(plugin, scheduledTask -> {
            if (task != null) task.run();
        }, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * Run a one-off asynchronous task after 'delay' ticks.
     */
    public void runTaskAsyncLater(long delay) {
        cancelTask();
        // convert ticks -> ms
        long delayMs = delay * 50L;
        scheduledTask = ASYNC_SCHEDULER.runDelayed(plugin, scheduledTask -> {
            if (task != null) task.run();
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Run a repeating asynchronous task every 'interval' ticks, starting after 'delay' ticks.
     */
    public void runTaskAsyncTimer(long delay, long interval) {
        cancelTask();
        // convert ticks -> ms
        long delayMs = delay * 50L;
        long intervalMs = interval * 50L;
        scheduledTask = ASYNC_SCHEDULER.runAtFixedRate(
            plugin,
            scheduledTask -> {
                if (task != null) task.run();
            },
            delayMs,
            intervalMs,
            TimeUnit.MILLISECONDS
        );
    }
}
