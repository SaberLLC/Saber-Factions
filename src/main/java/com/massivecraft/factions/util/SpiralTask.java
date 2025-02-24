package com.massivecraft.factions.util;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;


/*
 * reference diagram, task should move in this pattern out from chunk 0 in the center.
 * ...
 */

public abstract class SpiralTask implements Runnable {

    // general task-related reference data
    private final transient World world;
    private final transient int limit;
    private transient boolean readyToGo = false;

    // Instead of storing an int taskID, store a ScheduledTask
    private transient ScheduledTask scheduledTask;

    // values for the spiral pattern routine
    private transient int x = 0;
    private transient int z = 0;
    private transient boolean isZLeg = false;
    private transient boolean isNeg = false;
    private transient int length = -1;
    private transient int current = 0;

    @SuppressWarnings("LeakingThisInConstructor")
    public SpiralTask(FLocation fLocation, int radius) {
        // limit is determined based on spiral leg length for given radius; see insideRadius()
        this.limit = (radius - 1) * 2;

        this.world = Bukkit.getWorld(fLocation.getWorldName());
        if (this.world == null) {
            Logger.print("[SpiralTask] A valid world must be specified!", Logger.PrefixType.WARNING);
            this.stop();
            return;
        }
        this.x = fLocation.getIntX();
        this.z = fLocation.getIntZ();
        this.readyToGo = true;

        // Start a repeating synchronous task every 2 ticks on the main thread
        this.scheduledTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(
            FactionsPlugin.getInstance(),
            task -> SpiralTask.this.run(),
            2L,  // initial delay in ticks
            2L   // period in ticks
        );
    }

    private static long now() {
        return System.currentTimeMillis();
    }

    /**
     * The actual work to be done at each chunk. Return false if the entire task needs to be aborted,
     * otherwise return true to continue.
     */
    public abstract boolean work();

    /**
     * Returns an FLocation pointing at the current chunk X and Z values.
     */
    public final FLocation currentFLocation() {
        return FLocation.wrap(world.getName(), x, z);
    }

    /**
     * Returns a Location pointing at the current chunk X and Z values.
     * Note that the Location is at the corner of the chunk, not the center.
     */
    public final Location currentLocation() {
        return new Location(world, WorldUtil.chunkToBlock(x), 65.0, WorldUtil.chunkToBlock(z));
    }

    public final int getX() {
        return x;
    }

    public final int getZ() {
        return z;
    }

    /**
     * The main loop, called repeatedly on the main thread every 2 ticks.
     */
    @Override
    public final void run() {
        if (!this.valid() || !readyToGo) {
            return;
        }

        readyToGo = false;

        // Check if we're still inside the specified radius
        if (!this.insideRadius()) {
            return;
        }

        // Keep track of loop start time
        long loopStartTime = now();

        // Work until 20ms have passed, then stop to avoid choking the server
        while (now() < loopStartTime + 20) {
            // run the primary task on the current X/Z coordinates
            if (!this.work()) {
                this.finish();
                return;
            }

            // move on to next chunk in spiral
            if (!this.moveToNext()) {
                return;
            }
        }

        readyToGo = true;
    }

    /**
     * Move to the next chunk in the spiral pattern. Returns false if we're done, otherwise true.
     */
    public final boolean moveToNext() {
        if (!this.valid()) {
            return false;
        }

        // check if we need to turn down the next leg
        if (current < length) {
            current++;
            if (!this.insideRadius()) {
                return false;
            }
        } else {
            current = 0;
            isZLeg ^= true;
            // every second leg, length increases
            if (isZLeg) {
                isNeg ^= true;
                length++;
            }
        }

        // move one chunk further in the appropriate direction
        if (isZLeg) {
            z += (isNeg) ? -1 : 1;
        } else {
            x += (isNeg) ? -1 : 1;
        }

        return true;
    }

    /**
     * Check if we're still inside the radius. If not, finish.
     */
    public final boolean insideRadius() {
        boolean inside = current < limit;
        if (!inside) {
            this.finish();
        }
        return inside;
    }

    /**
     * Called when we successfully complete the spiral.
     */
    public void finish() {
        //FactionsPlugin.getInstance().log("SpiralTask successfully completed!");
        this.stop();
    }

    /**
     * Stop the task, whether we finished or got canceled.
     */
    public final void stop() {
        if (!this.valid()) {
            return;
        }
        readyToGo = false;
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel();
        }
        scheduledTask = null;
    }

    /**
     * Return whether this task is still valid/workable.
     */
    public final boolean valid() {
        return scheduledTask != null && !scheduledTask.isCancelled();
    }
}
