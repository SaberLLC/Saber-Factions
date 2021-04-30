package com.massivecraft.factions.cloak.struct;

import static com.massivecraft.factions.util.MiscUtil.formatDifference;

/**
 * @author Saser
 */
public class CurrentCloaks {

    String whoApplied;
    double multiplier;
    long timeApplied;
    int secondsElapsed;
    int maxSeconds;

    public CurrentCloaks(String whoApplied, double multiplier, long timeApplied, int secondsElapsed, int maxSeconds) {
        this.whoApplied = whoApplied;
        this.multiplier = multiplier;
        this.timeApplied = timeApplied;
        this.secondsElapsed = secondsElapsed;
        this.maxSeconds = maxSeconds;
    }

    public String getFormattedTimeLeft() {
        return formatDifference(this.maxSeconds - this.secondsElapsed);
    }

    @Override
    public String toString() {
        return this.getSecondsElapsed() + ":" + this.getWhoApplied() + ":" + this.getMultiplier() + ":" + this.getTimeApplied() + ":" + this.getMaxSeconds();
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


}
