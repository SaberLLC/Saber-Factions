package com.massivecraft.factions.util;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;


/*
 * reference diagram, task should move in this pattern out from chunk 0 in the center.
 *  8 [>][>][>][>][>] etc.
 * [^][6][>][>][>][>][>][6]
 * [^][^][4][>][>][>][4][v]
 * [^][^][^][2][>][2][v][v]
 * [^][^][^][^][0][v][v][v]
 * [^][^][^][1][1][v][v][v]
 * [^][^][3][<][<][3][v][v]
 * [^][5][<][<][<][<][5][v]
 * [7][<][<][<][<][<][<][7]
 */

public abstract class SpiralTask implements Runnable {

    private final World world;
    private final int limit;
    private boolean readyToGo = false;
    private int taskID = -1;
    private int x = 0;
    private int z = 0;
    private int length = -1;
    private int current = 0;

    public SpiralTask(FLocation fLocation, int radius) {
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

        this.setTaskID(Bukkit.getScheduler().runTaskTimer(FactionsPlugin.getInstance(), this, 2, 2).getTaskId());
    }

    private static long now() {
        return System.nanoTime();
    }

    public abstract boolean work();

    public final FLocation currentFLocation() {
        return FLocation.wrap(world.getName(), x, z);
    }

    public final Location currentLocation() {
        return new Location(world, WorldUtil.chunkToBlock(x), 65.0, WorldUtil.chunkToBlock(z));
    }

    public final int getX() {
        return x;
    }

    public final int getZ() {
        return z;
    }

    public final void setTaskID(int ID) {
        if (ID == -1) {
            this.stop();
        }
        taskID = ID;
    }

    public final void run() {
        if (!this.valid() || !readyToGo) {
            return;
        }

        readyToGo = false;

        if (!this.insideRadius()) {
            return;
        }

        long loopStartTime = now();

        while (now() < loopStartTime + 20) {
            if (!this.work()) {
                this.finish();
                return;
            }

            if (!this.moveToNext()) {
                return;
            }
        }

        readyToGo = true;
    }

    public final boolean moveToNext() {
        if (!this.valid()) {
            return false;
        }

        if (current < length) {
            current++;
            if (!this.insideRadius()) {
                return false;
            }
        } else {
            current = 0;
            length++;
        }

        boolean isZLeg = length % 2 == 0;
        boolean isNeg = length / 2 % 2 != 0;

        z += (isZLeg ? (isNeg ? -1 : 1) : 0);
        x += (isZLeg ? 0 : (isNeg ? -1 : 1));

        return true;
    }

    public final boolean insideRadius() {
        boolean inside = current < limit;
        if (!inside) {
            this.finish();
        }
        return inside;
    }

    public void finish() {
        this.stop();
    }

    public final void stop() {
        if (!this.valid()) {
            return;
        }

        readyToGo = false;
        Bukkit.getScheduler().cancelTask(taskID);
        taskID = -1;
    }

    public final boolean valid() {
        return taskID != -1;
    }
}
