package com.massivecraft.factions.util.timer;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.timer.type.GraceTimer;
import com.massivecraft.factions.zcore.file.CustomFile;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TimerManager implements Listener, Runnable {
    private static final long MINUTE = TimeUnit.MINUTES.toMillis(1L);
    private static final long HOUR = TimeUnit.HOURS.toMillis(1L);
    private static final long MULTI_HOUR = TimeUnit.HOURS.toMillis(10);

    private final FactionsPlugin plugin;
    private final Set<Timer> timers = new HashSet<>();
    private final List<TimerRunnable> timerRunnableList = new ArrayList<>();

    // Example Timers
    public GraceTimer graceTimer;
    public static boolean graceEnabled;

    // For storing timer data to disk
    private CustomFile config;

    // Optional: store the ScheduledTask if you need to cancel it later
    private io.papermc.paper.threadedregions.scheduler.ScheduledTask repeatingTask;

    public TimerManager(FactionsPlugin plugin) {
        this.plugin = plugin;
        // Register ourselves as an event listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        // Initialize and register your timers
        this.registerTimer(this.graceTimer = new GraceTimer());

        // Folia repeating task: run every 4 ticks, after an initial 5 ticks
        // so that the initial delay is definitely not <= 0.
        this.repeatingTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(
            plugin,
            scheduledTask -> this.run(), // 'this' is a Runnable, so call run()
            5L,  // initial delay in ticks
            4L   // period in ticks
        );
    }

    /**
     * Called every 4 ticks by runAtFixedRate.
     * We remove expired TimerRunnables, and optionally update graceEnabled.
     */
    @Override
    public void run() {
        long now = System.currentTimeMillis();
        // removeIf(...) returns true if the item was removed
        timerRunnableList.removeIf(next -> next.check(now));

        if (this.graceTimer != null) {
            // If graceTimer's time is up, set graceEnabled accordingly
            graceEnabled = (this.graceTimer.getRemaining() <= 0);
        }
    }

    /**
     * Register a new Timer in memory (and possibly as an event listener).
     */
    public void registerTimer(Timer timer) {
        this.timers.add(timer);
        if (timer instanceof Listener) {
            plugin.getServer().getPluginManager().registerEvents((Listener) timer, plugin);
        }
    }

    /**
     * Unregister/remove a Timer from memory.
     */
    public void unregisterTimer(Timer timer) {
        this.timers.remove(timer);
    }

    /**
     * Reload the timer data from your config file (e.g., timers.yml).
     */
    public void reloadTimerData() {
        this.config = FactionsPlugin.getInstance().getFileManager().getTimers();
        for (Timer timer : this.timers) {
            timer.load(this.config);
        }
    }

    /**
     * Save the timer data back to your config file.
     */
    public void saveTimerData() {
        for (Timer timer : this.timers) {
            timer.save(this.config);
        }
        this.config.saveFile();
    }

    /**
     * Returns an unmodifiable snapshot of your currently registered timers.
     */
    public Collection<Timer> getTimers() {
        return Collections.unmodifiableSet(this.timers);
    }

    /**
     * Returns the list of active TimerRunnables for advanced usage.
     */
    public List<TimerRunnable> getTimerRunnableList() {
        return this.timerRunnableList;
    }

    /**
     * Utility method to format durations as a human-readable string.
     */
    public static String getRemaining(long duration, boolean milliseconds) {
        return getRemaining(duration, milliseconds, true);
    }

    public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
        if (milliseconds && (duration < MINUTE)) {
            // If under 1 minute, show in decimal seconds
            double seconds = duration * 0.001D;
            String format = (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().toPattern();
            return String.format(format, seconds) + 's';
        }
        // For durations >= 1 minute, use a standard H:M:S format
        String pattern = (duration >= HOUR ? (duration >= MULTI_HOUR ? "d" : "") + "d:" : "") + "HH:mm:ss";
        return DurationFormatUtils.formatDuration(duration, pattern);
    }
}
