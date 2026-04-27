package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.beans.ConstructorProperties;

public class TaskRunner {
  @ConstructorProperties({"plugin"})
  public TaskRunner(JavaPlugin plugin) {
    this.plugin = plugin;
  }
  
  public static final BukkitScheduler scheduler = Bukkit.getScheduler();
  
  private final JavaPlugin plugin;
  
  private Runnable task;
  
  private int taskId;
  
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
    scheduler.cancelTask(this.taskId);
    this.taskId = -1;
  }
  
  public boolean isTaskCancelled() {
    return (this.taskId == -1);
  }
  
  public boolean isTaskQueued() {
    return (!isTaskCancelled() && scheduler.isQueued(this.taskId));
  }
  
  public boolean isTaskRunning() {
    return (!isTaskCancelled() && scheduler.isCurrentlyRunning(this.taskId));
  }
  
  public int runTaskSync() {
    return this.taskId = scheduler.runTask(this.plugin, this.task).getTaskId();
  }
  
  public int runTaskSyncLater(long delay) {
    return this.taskId = scheduler.runTaskLater(this.plugin, this.task, delay).getTaskId();
  }
  
  public int runTaskSyncTimer(long delay, long interval) {
    return this.taskId = scheduler.runTaskTimer(this.plugin, this.task, delay, interval).getTaskId();
  }
  
  public int runTaskAsync() {
    return this.taskId = scheduler.runTaskAsynchronously(this.plugin, this.task).getTaskId();
  }
  
  public int runTaskAsyncLater(long delay) {
    return this.taskId = scheduler.runTaskLaterAsynchronously(this.plugin, this.task, delay).getTaskId();
  }
  
  public int runTaskAsyncTimer(long delay, long interval) {
    return this.taskId = scheduler.runTaskTimerAsynchronously(this.plugin, this.task, delay, interval).getTaskId();
  }
}