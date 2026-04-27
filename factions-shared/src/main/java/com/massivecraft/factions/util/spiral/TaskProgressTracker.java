package com.massivecraft.factions.util.spiral;

public class TaskProgressTracker {
    private final long startTimeMillis;
    private int processedChunks;
    private int totalChunks;

    public TaskProgressTracker(int totalChunks) {
        this.startTimeMillis = System.currentTimeMillis();
        this.totalChunks = totalChunks;
    }

    public void markProcessed() {
        processedChunks++;
    }

    public double getProgress() {
        return totalChunks == 0 ? 1.0 : (double) processedChunks / totalChunks;
    }

    public long getElapsedTimeMillis() {
        return System.currentTimeMillis() - startTimeMillis;
    }

    public double getChunksPerSecond() {
        long elapsed = getElapsedTimeMillis();
        return elapsed < 100 ? 0.0 : processedChunks / (elapsed / 1000.0);
    }

    public int getProcessedChunks() {
        return processedChunks;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public int getRemainingChunks() {
        return totalChunks - processedChunks;
    }
}