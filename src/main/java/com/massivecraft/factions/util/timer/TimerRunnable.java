package com.massivecraft.factions.util.timer;

import com.massivecraft.factions.FactionsPlugin;

import java.util.UUID;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/7/2020
 */

public class TimerRunnable {
    private final Timer timer;
    private long expiryMillis;
    private long pauseMillis;
    private boolean cancelled = false;

    /**
     * Constructs a TimerRunnable without a specific player UUID.
     *
     * @param timer    the timer object controlling this runnable
     * @param duration the time in milliseconds until expiry
     */
    public TimerRunnable(Timer timer, long duration) {
        this.timer = timer;
        setRemaining(duration);

        // Safely add to the TimerManager’s list if present
        FactionsPlugin plugin = FactionsPlugin.getInstance();
        if (plugin.getTimerManager() != null) {
            plugin.getTimerManager().getTimerRunnableList().add(this);
        } else {
            // Optional: log a warning if TimerManager is unexpectedly null
            // Logger.print("TimerManager is null, cannot add TimerRunnable to the list.", Logger.PrefixType.WARNING);
        }
    }

    /**
     * Constructs a TimerRunnable associated with a specific player (by UUID).
     *
     * @param playerUUID the player’s UUID
     * @param timer      the timer object controlling this runnable
     * @param duration   the time in milliseconds until expiry
     */
    public TimerRunnable(UUID playerUUID, Timer timer, long duration) {
        this.timer = timer;
        setRemaining(duration);

        // Safely add to the TimerManager’s list if present
        FactionsPlugin plugin = FactionsPlugin.getInstance();
        if (plugin.getTimerManager() != null) {
            plugin.getTimerManager().getTimerRunnableList().add(this);
        } else {
            // Optional: log a warning if TimerManager is unexpectedly null
            // Logger.print("TimerManager is null, cannot add TimerRunnable to the list.", Logger.PrefixType.WARNING);
        }
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public Timer getTimer() {
        return this.timer;
    }

    // Time manipulation
    public long getRemaining() {
        return getRemaining(false);
    }

    public long getRemaining(long now) {
        return getRemaining(false, now);
    }

    /**
     * Returns the remaining time in milliseconds, optionally ignoring if paused.
     *
     * @param ignorePaused if true, ignores pauseMillis
     * @return the remaining time in milliseconds
     */
    public long getRemaining(boolean ignorePaused) {
        if (!ignorePaused && this.pauseMillis != 0L) {
            return this.pauseMillis;
        }
        return this.expiryMillis - System.currentTimeMillis();
    }

    /**
     * Returns the remaining time in milliseconds, optionally ignoring if paused, using a custom 'now'.
     *
     * @param ignorePaused if true, ignores pauseMillis
     * @param now          a custom time (millis) for calculations
     * @return the remaining time in milliseconds
     */
    public long getRemaining(boolean ignorePaused, long now) {
        if (!ignorePaused && this.pauseMillis != 0L) {
            return this.pauseMillis;
        }
        return this.expiryMillis - now;
    }

    public void setRemaining(long remaining) {
        setExpiryMillis(remaining);
    }

    private void setExpiryMillis(long remainingMillis) {
        long expiry = System.currentTimeMillis() + remainingMillis;
        if (expiry == this.expiryMillis) {
            return;
        }
        this.expiryMillis = expiry;
    }

    public boolean check(long now) {
        if (cancelled) {
            return true;
        }
        return getRemaining(false, now) <= 0;
    }

    // Pause / Resume
    public long getPauseMillis() {
        return this.pauseMillis;
    }

    public void setPauseMillis(long pauseMillis) {
        this.pauseMillis = pauseMillis;
    }

    public boolean isPaused() {
        return this.pauseMillis != 0L;
    }

    public void setPaused(boolean paused) {
        if (paused == isPaused()) {
            return;
        }
        if (paused) {
            // Store how much time is left
            this.pauseMillis = getRemaining(true);
            cancel();
        } else {
            // Resume from the stored leftover
            setExpiryMillis(this.pauseMillis);
            this.pauseMillis = 0L;
        }
    }

    public void cancel() {
        cancelled = true;
    }
}
