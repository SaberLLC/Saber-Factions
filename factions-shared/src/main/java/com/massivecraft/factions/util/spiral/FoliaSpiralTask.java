package com.massivecraft.factions.util.spiral;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.scheduler.FactionTask;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Folia-compatible spiral processor. Each chunk is processed on its owning
 * region thread via a recursive chain of region-scheduled one-shot tasks.
 * On Bukkit use SpiralTask (runGlobalRepeating) instead.
 */
public abstract class FoliaSpiralTask {

    private final Queue<FLocation> spiralQueue;
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final AtomicReference<FactionTask> currentTask = new AtomicReference<>();

    public FoliaSpiralTask(Queue<FLocation> spiralQueue) {
        this.spiralQueue = spiralQueue;
    }

    public void start() {
        scheduleNext();
    }

    private void scheduleNext() {
        if (stopped.get() || spiralQueue.isEmpty()) {
            if (!stopped.get()) finish();
            return;
        }
        FLocation next = spiralQueue.poll();
        if (next == null) {
            finish();
            return;
        }
        FactionTask t = FactionsPlugin.getScheduler().runRegion(next.asBukkitLocation(), () -> {
            if (!stopped.get()) {
                work(next);
                scheduleNext();
            }
        });
        currentTask.set(t);
        // If stop() raced before we stored the task, cancel it now
        if (stopped.get() && !t.isCancelled()) {
            t.cancel();
        }
    }

    protected abstract void work(FLocation location);

    protected void finish() {}

    public void stop() {
        stopped.set(true);
        FactionTask t = currentTask.getAndSet(null);
        if (t != null && !t.isCancelled()) t.cancel();
    }
}
