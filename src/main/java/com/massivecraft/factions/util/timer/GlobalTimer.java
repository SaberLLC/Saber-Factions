package com.massivecraft.factions.util.timer;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/7/2020
 */
public abstract class GlobalTimer extends Timer {
    private TimerRunnable runnable;


    public GlobalTimer(String name, long defaultCooldown) {
        super(name, defaultCooldown);
    }


    public boolean clearCooldown() {
        if (this.runnable != null) {
            this.runnable.cancel();
            this.runnable = null;
            return true;
        }
        return false;

    }


    public boolean isPaused() {
        return (this.runnable != null) && (this.runnable.isPaused());
    }


    public void setPaused(boolean paused) {
        if ((this.runnable != null) && (this.runnable.isPaused() != paused)) {
            this.runnable.setPaused(paused);
        }
    }


    public long getRemaining() {
        return this.runnable == null ? 0L : this.runnable.getRemaining();
    }

    public long getRemaining(long now) {
        return this.runnable == null ? 0L : this.runnable.getRemaining(now);
    }


    public boolean setRemaining() {
        return setRemaining(this.defaultCooldown, false);
    }


    public boolean setRemaining(long duration, boolean overwrite) {
        boolean hadCooldown = false;
        if (this.runnable != null) {
            if (!overwrite) {
                return false;
            }
            hadCooldown = this.runnable.getRemaining() > 0L;
            this.runnable.setRemaining(duration);
        } else {
            this.runnable = new TimerRunnable(this, duration);
        }
        return !hadCooldown;
    }
}
