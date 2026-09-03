package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.scheduler.FactionTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.beans.ConstructorProperties;

public class TaskRunner {
  @ConstructorProperties({"plugin"})
  public TaskRunner(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  private final JavaPlugin plugin;

  private Runnable task;

  private volatile FactionTask currentTask;

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

  public void cancelTask() {
    FactionTask t = this.currentTask;
    if (t != null) t.cancel();
    this.currentTask = null;
  }

  public boolean isTaskCancelled() {
    FactionTask t = this.currentTask;
    return t == null || t.isCancelled();
  }

  public void runTaskSync() {
    this.currentTask = FactionsPlugin.getScheduler().runGlobal(this.task);
  }

  public void runTaskSyncLater(long delay) {
    this.currentTask = FactionsPlugin.getScheduler().runGlobalLater(delay, this.task);
  }

  public void runTaskSyncTimer(long delay, long interval) {
    this.currentTask = FactionsPlugin.getScheduler().runGlobalRepeating(delay, interval, this.task);
  }

  public void runTaskAsync() {
    this.currentTask = FactionsPlugin.getScheduler().runAsync(this.task);
  }

  public void runTaskAsyncLater(long delay) {
    this.currentTask = FactionsPlugin.getScheduler().runAsyncLater(delay, this.task);
  }

  public void runTaskAsyncTimer(long delay, long interval) {
    this.currentTask = FactionsPlugin.getScheduler().runAsyncRepeating(delay, interval, this.task);
  }
}
