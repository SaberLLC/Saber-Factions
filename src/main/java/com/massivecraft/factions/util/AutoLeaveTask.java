package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;

public class AutoLeaveTask implements Runnable {

    private static AutoLeaveProcessTask task;
    double rate;

    public AutoLeaveTask() {
        this.rate = Conf.autoLeaveRoutineRunsEveryXMinutes;
    }

    public synchronized void run() {
        if (task != null && !task.isFinished()) {
            return;
        }

        task = new AutoLeaveProcessTask();
        task.runTaskTimer(FactionsPlugin.getInstance(), 1, 1);

        // maybe setting has been changed? if so, restart this task at new rate
        if (this.rate != Conf.autoLeaveRoutineRunsEveryXMinutes) {
            FactionsPlugin.getInstance().startAutoLeaveTask(true);
        }
    }
}
