package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.scheduler.FactionTask;

public class AutoLeaveTask implements Runnable {

    private static AutoLeaveProcessTask processTask;
    double rate;

    public AutoLeaveTask() {
        this.rate = Conf.autoLeaveRoutineRunsEveryXMinutes;
    }

    public synchronized void run() {
        if (processTask != null && !processTask.isFinished()) {
            return;
        }

        processTask = new AutoLeaveProcessTask();
        FactionTask task = FactionsPlugin.getScheduler().runGlobalRepeating(1L, 1L, processTask);
        processTask.setTask(task);

        // maybe setting has been changed? if so, restart this task at new rate
        if (this.rate != Conf.autoLeaveRoutineRunsEveryXMinutes) {
            FactionsPlugin.getInstance().startAutoLeaveTask(true);
        }
    }
}
