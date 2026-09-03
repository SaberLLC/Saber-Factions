package com.massivecraft.factions.util.spiral;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.scheduler.FactionTask;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.util.spiral.coord.ChunkCoord;
import com.massivecraft.factions.util.spiral.generator.SpiralGenerator;
import org.bukkit.Bukkit;

import java.util.Queue;

public abstract class SpiralTask implements Runnable {

    private final String worldName;
    private final Queue<ChunkCoord> spiralQueue;
    private final TaskProgressTracker progressTracker;
    private final AdaptiveBatchExecutor batchExecutor = new AdaptiveBatchExecutor();

    private volatile boolean active = false;
    private volatile FactionTask task;

    public SpiralTask(FLocation center, int radius, SpiralGenerator generator) {
        this.worldName = center.getWorldName();
        if (Bukkit.getWorld(this.worldName) == null) {
            Logger.print("[SpiralTask] Invalid world: " + center.getWorldName(), Logger.PrefixType.WARNING);
            this.spiralQueue = null;
            this.progressTracker = new TaskProgressTracker(0);
            return;
        }

        this.spiralQueue = generator.generate(center.getIntX(), center.getIntZ(), radius);
        this.progressTracker = new TaskProgressTracker(spiralQueue.size());

        if (spiralQueue.isEmpty()) {
            Logger.print("[SpiralTask] No chunks to process!", Logger.PrefixType.WARNING);
            return;
        }

        this.active = true;
        this.task = FactionsPlugin.getScheduler().runGlobalRepeating(1L, 1L, this);
        Logger.print("[SpiralTask] Started with " + progressTracker.getTotalChunks() + " chunks.", Logger.PrefixType.DEFAULT);
    }

    @Override
    public void run() {
        if (!active || spiralQueue == null || spiralQueue.isEmpty()) {
            stop();
            return;
        }

        int processed = 0;
        long startTime = System.nanoTime();

        while (processed < batchExecutor.getBatchSize() && !spiralQueue.isEmpty()) {
            ChunkCoord coord = spiralQueue.poll();
            if (coord == null) break;

            FLocation fLoc = FLocation.wrap(worldName, coord.x, coord.z);
            ChunkProcessingContext context = new ChunkProcessingContext(coord, fLoc);

            try {
                if (!work(context)) {
                    stop();
                    return;
                }
            } catch (Exception ex) {
                Logger.print("[SpiralTask] Error at chunk " + coord.x + "," + coord.z, Logger.PrefixType.WARNING);
            }

            progressTracker.markProcessed();
            processed++;
        }

        long elapsed = System.nanoTime() - startTime;
        batchExecutor.adapt(elapsed);

        if (spiralQueue.isEmpty()) {
            finish();
        }
    }

    public abstract boolean work(ChunkProcessingContext context);

    public void finish() {
        Logger.print("[SpiralTask] Completed in " + progressTracker.getElapsedTimeMillis() + "ms at " +
                String.format("%.1f", progressTracker.getChunksPerSecond()) + " chunks/s", Logger.PrefixType.DEFAULT);
        stop();
    }

    public void stop() {
        if (!active) return;
        active = false;
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        if (spiralQueue != null) spiralQueue.clear();
    }

    public boolean isActive() {
        return active;
    }

    public TaskProgressTracker getProgressTracker() {
        return progressTracker;
    }

    public AdaptiveBatchExecutor getBatchExecutor() {
        return batchExecutor;
    }

    /**
     * Converts a spiral generator's output into a Queue of FLocations for use
     * with FoliaSpiralTask (region-threaded spiral processing on Folia).
     */
    public static Queue<FLocation> buildFLocationQueue(FLocation center, int radius, SpiralGenerator generator) {
        Queue<ChunkCoord> coords = generator.generate(center.getIntX(), center.getIntZ(), radius);
        Queue<FLocation> result = new java.util.LinkedList<>();
        String worldName = center.getWorldName();
        for (ChunkCoord coord : coords) {
            result.add(FLocation.wrap(worldName, coord.x, coord.z));
        }
        return result;
    }
}