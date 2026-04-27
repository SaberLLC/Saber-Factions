package com.massivecraft.factions.missions;

public class Mission {

    /**
     * @author Driftay
     */

    private long progress;
    private final String name;
    private final MissionType type;
    private final long startTime;

    public Mission(String name, MissionType type, long startTime) {
        this.name = name;
        this.type = type;
        this.startTime = startTime;

    }

    public void incrementProgress(long increment) {
        progress += increment;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getProgress() {
        return progress;
    }

    public void incrementProgress() {
        ++progress;
    }

    public String getName() {
        return name;
    }

    public MissionType getType() {
        return type;
    }
}
