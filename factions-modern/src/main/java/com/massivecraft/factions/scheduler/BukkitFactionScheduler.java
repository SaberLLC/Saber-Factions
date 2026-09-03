package com.massivecraft.factions.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitFactionScheduler implements FactionScheduler {

    private final Plugin plugin;

    public BukkitFactionScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    public FactionTask runEntity(Entity e, Runnable task, Runnable retired) {
        return wrap(Bukkit.getScheduler().runTask(plugin, task));
    }

    public FactionTask runEntityLater(Entity e, long delay, Runnable task, Runnable retired) {
        return wrap(Bukkit.getScheduler().runTaskLater(plugin, task, delay));
    }

    public FactionTask runRegion(Location loc, Runnable task) {
        return wrap(Bukkit.getScheduler().runTask(plugin, task));
    }

    public FactionTask runRegionLater(Location loc, long delay, Runnable task) {
        return wrap(Bukkit.getScheduler().runTaskLater(plugin, task, delay));
    }

    public FactionTask runRegionRepeating(Location loc, long delay, long period, Runnable task) {
        return wrap(Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period));
    }

    public FactionTask runGlobal(Runnable task) {
        return wrap(Bukkit.getScheduler().runTask(plugin, task));
    }

    public FactionTask runGlobalLater(long delay, Runnable task) {
        return wrap(Bukkit.getScheduler().runTaskLater(plugin, task, delay));
    }

    public FactionTask runGlobalRepeating(long delay, long period, Runnable task) {
        return wrap(Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period));
    }

    public FactionTask runAsync(Runnable task) {
        return wrap(Bukkit.getScheduler().runTaskAsynchronously(plugin, task));
    }

    public FactionTask runAsyncLater(long delay, Runnable task) {
        return wrap(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay));
    }

    public FactionTask runAsyncRepeating(long delay, long period, Runnable task) {
        return wrap(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period));
    }

    public void cancelAll() {
        Bukkit.getScheduler().cancelTasks(plugin);
    }

    private FactionTask wrap(BukkitTask t) {
        return new FactionTask() {
            public void cancel() { t.cancel(); }
            public boolean isCancelled() { return t.isCancelled(); }
        };
    }
}
