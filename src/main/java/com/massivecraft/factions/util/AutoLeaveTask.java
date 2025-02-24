package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class AutoLeaveTask implements Runnable {

    // This references your logic class that does the actual auto-leave processing.
    private static AutoLeaveProcessTask task;

    // The user-configured frequency (in minutes) for auto-leave.
    double rate;

    public AutoLeaveTask() {
        this.rate = Conf.autoLeaveRoutineRunsEveryXMinutes;
    }

    @Override
    public synchronized void run() {
        // If the existing process task is still running, don't start a new one.
        if (task != null && !task.isFinished()) {
            return;
        }

        // Create a new process task each time we run.
        task = new AutoLeaveProcessTask();

        // Folia: schedule repeated calls to 'task.run()' on the main thread,
        // with 1 tick initial delay, 1 tick period.
        // Make sure your AutoLeaveProcessTask is just a normal class with a run() method,
        // not a BukkitRunnable or anything referencing the old scheduler.
        ScheduledTask repeatingTask = FactionsPlugin.getInstance().getServer()
            .getGlobalRegionScheduler()
            .runAtFixedRate(
                FactionsPlugin.getInstance(),
                scheduledTask -> task.run(),
                1L, // initial delay in ticks
                1L  // repeating period in ticks
            );

        // If the user changed the config setting for autoLeaveRoutineRunsEveryXMinutes,
        // re-start the autoLeaveTask at the new rate.
        if (this.rate != Conf.autoLeaveRoutineRunsEveryXMinutes) {
            FactionsPlugin.getInstance().startAutoLeaveTask(true);
        }
    }
}
