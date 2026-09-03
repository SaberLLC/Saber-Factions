package com.massivecraft.factions.scheduler;

public interface FactionTask {
    void cancel();
    boolean isCancelled();
}
