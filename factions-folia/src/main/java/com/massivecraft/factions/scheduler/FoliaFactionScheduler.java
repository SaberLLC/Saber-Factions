package com.massivecraft.factions.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FoliaFactionScheduler implements FactionScheduler {

    private final Plugin plugin;
    // Registry required: Folia has no plugin-scoped global cancel API.
    // Pruned on each track() to prevent unbounded growth.
    private final List<FactionTask> registry = Collections.synchronizedList(new ArrayList<>());

    public FoliaFactionScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    public FactionTask runEntity(Entity e, Runnable task, Runnable retired) {
        return track(wrap(e.getScheduler().run(plugin, st -> task.run(), retired)));
    }

    public FactionTask runEntityLater(Entity e, long delay, Runnable task, Runnable retired) {
        return track(wrap(e.getScheduler().runDelayed(plugin, st -> task.run(), retired, Math.max(1L, delay))));
    }

    public FactionTask runRegion(Location loc, Runnable task) {
        return track(wrap(Bukkit.getRegionScheduler().run(plugin, loc, st -> task.run())));
    }

    public FactionTask runRegionLater(Location loc, long delay, Runnable task) {
        return track(wrap(Bukkit.getRegionScheduler().runDelayed(plugin, loc, st -> task.run(), Math.max(1L, delay))));
    }

    public FactionTask runRegionRepeating(Location loc, long delay, long period, Runnable task) {
        return track(wrap(Bukkit.getRegionScheduler().runAtFixedRate(plugin, loc, st -> task.run(), Math.max(1L, delay), period)));
    }

    public FactionTask runGlobal(Runnable task) {
        return track(wrap(Bukkit.getGlobalRegionScheduler().run(plugin, st -> task.run())));
    }

    public FactionTask runGlobalLater(long delay, Runnable task) {
        return track(wrap(Bukkit.getGlobalRegionScheduler().runDelayed(plugin, st -> task.run(), Math.max(1L, delay))));
    }

    public FactionTask runGlobalRepeating(long delay, long period, Runnable task) {
        return track(wrap(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, st -> task.run(), Math.max(1L, delay), period)));
    }

    public FactionTask runAsync(Runnable task) {
        return track(wrap(Bukkit.getAsyncScheduler().runNow(plugin, st -> task.run())));
    }

    public FactionTask runAsyncLater(long delay, Runnable task) {
        // AsyncScheduler uses real time. 1 tick = 50ms at 20 TPS.
        return track(wrap(Bukkit.getAsyncScheduler().runDelayed(
                plugin, st -> task.run(), delay * 50L, TimeUnit.MILLISECONDS)));
    }

    public FactionTask runAsyncRepeating(long delay, long period, Runnable task) {
        return track(wrap(Bukkit.getAsyncScheduler().runAtFixedRate(
                plugin, st -> task.run(), delay * 50L, period * 50L, TimeUnit.MILLISECONDS)));
    }

    public void cancelAll() {
        synchronized (registry) {
            for (FactionTask t : registry) {
                if (!t.isCancelled()) t.cancel();
            }
            registry.clear();
        }
    }

    private FactionTask wrap(ScheduledTask st) {
        return new FactionTask() {
            public void cancel() { st.cancel(); }
            public boolean isCancelled() {
                ScheduledTask.ExecutionState state = st.getExecutionState();
                return state == ScheduledTask.ExecutionState.CANCELLED
                    || state == ScheduledTask.ExecutionState.CANCELLED_RUNNING;
            }
        };
    }

    private FactionTask track(FactionTask t) {
        registry.removeIf(FactionTask::isCancelled);
        registry.add(t);
        return t;
    }
}
