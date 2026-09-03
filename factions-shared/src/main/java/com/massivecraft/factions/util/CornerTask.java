package com.massivecraft.factions.util;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.scheduler.FactionTask;
import com.massivecraft.factions.zcore.util.TL;

import java.util.List;

public class CornerTask implements Runnable {
    private final FPlayer fPlayer;
    private final List<FLocation> surrounding;
    private int amount;
    private volatile FactionTask task;
    private volatile boolean cancelled = false;

    public CornerTask(FPlayer fPlayer, List<FLocation> surrounding) {
        this.amount = 0;
        this.fPlayer = fPlayer;
        this.surrounding = surrounding;
    }

    public void setTask(FactionTask task) {
        this.task = task;
    }

    private void cancel() {
        this.cancelled = true;
        if (this.task != null && !this.task.isCancelled()) {
            this.task.cancel();
        }
        this.task = null;
    }

    public void run() {
        if (this.cancelled) return;
        if (this.fPlayer.isOffline()) {
            cancel();
            return;
        }

        while (!this.surrounding.isEmpty()) {
            FLocation fLocation = this.surrounding.remove(0);
            if (this.fPlayer.attemptClaim(this.fPlayer.getFaction(), fLocation, true)) {
                ++amount;
            } else {
                this.fPlayer.sendMessage(TL.COMMAND_CORNER_FAIL_WITH_FEEDBACK.toString() + amount);
                cancel();
                return;
            }
        }

        this.fPlayer.sendMessage(TL.COMMAND_CORNER_CLAIMED.format(this.amount));
        cancel();
    }
}
