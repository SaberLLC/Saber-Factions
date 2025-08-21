package com.massivecraft.factions.util.spiral;

public class AdaptiveBatchExecutor {
    private static final long MAX_EXECUTION_TIME_NANOS = 15000000L;
    private static final long ADAPTIVE_TIME_THRESHOLD = 5000000L;

    private int batchSize = 16;
    private long lastExecutionTime;
    private int consecutiveSlowRuns = 0;

    public int getBatchSize() {
        return batchSize;
    }

    public long getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void adapt(long executionTime) {
        this.lastExecutionTime = executionTime;

        if (executionTime > MAX_EXECUTION_TIME_NANOS) {
            batchSize = Math.max(1, batchSize - 2);
            consecutiveSlowRuns++;
        } else if (executionTime < ADAPTIVE_TIME_THRESHOLD && consecutiveSlowRuns == 0) {
            batchSize = Math.min(64, batchSize + 1);
        } else if (consecutiveSlowRuns > 0) {
            consecutiveSlowRuns--;
        }
    }
}