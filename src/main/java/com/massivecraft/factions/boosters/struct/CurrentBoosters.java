package com.massivecraft.factions.boosters.struct;

import com.massivecraft.factions.boosters.BoosterTypes;
import com.massivecraft.factions.util.TimeUtil;

public class CurrentBoosters {
    String whoApplied;
    double multiplier;
    long timeApplied;
    int secondsElapsed;
    int maxSeconds;
    BoosterTypes boosterType;

    public CurrentBoosters(String whoApplied, double multiplier, long timeApplied, int secondsElapsed, int maxSeconds, BoosterTypes boosterType) {
        this.whoApplied = whoApplied;
        this.multiplier = multiplier;
        this.timeApplied = timeApplied;
        this.secondsElapsed = secondsElapsed;
        this.maxSeconds = maxSeconds;
        this.boosterType = boosterType;
    }

    public String getFormattedTimeLeft() {
        return TimeUtil.formatDifference(this.maxSeconds - this.secondsElapsed);
    }

    @Override
    public String toString() {
        return this.getSecondsElapsed() + ":" + this.getWhoApplied() + ":" + this.getMultiplier() + ":" + this.getTimeApplied() + ":" + this.getMaxSeconds() + ":" + this.boosterType.name();
    }

    public String getWhoApplied() {
        return this.whoApplied;
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public long getTimeApplied() {
        return this.timeApplied;
    }

    public void setTimeApplied(long timeApplied) {
        this.timeApplied = timeApplied;
    }

    public int getSecondsElapsed() {
        return this.secondsElapsed;
    }

    public void setSecondsElapsed(int secondsElapsed) {
        this.secondsElapsed = secondsElapsed;
    }

    public int getMaxSeconds() {
        return this.maxSeconds;
    }

    public void setMaxSeconds(int maxSeconds) {
        this.maxSeconds = maxSeconds;
    }

    public BoosterTypes getBoosterType() {
        return this.boosterType;
    }
}