package com.massivecraft.factions.util.timer;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.timer.type.GraceTimer;
import com.massivecraft.factions.zcore.file.CustomFile;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/7/2020
 */
public class TimerManager implements Listener, Runnable {
    private static final long MINUTE = TimeUnit.MINUTES.toMillis(1L);
    private static final long HOUR = TimeUnit.HOURS.toMillis(1L);
    private static final long MULTI_HOUR = TimeUnit.HOURS.toMillis(10);
    private final Set<Timer> timers;
    private final FactionsPlugin plugin;
    private final List<TimerRunnable> timerRunnableList = new ArrayList<>();
    public GraceTimer graceTimer;
    private CustomFile config;
    public static boolean graceEnabled;


    public TimerManager(FactionsPlugin plugin) {
        this.timers = new HashSet<>();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.registerTimer(this.graceTimer = new GraceTimer());
        plugin.getServer().getScheduler().runTaskTimer(plugin, this, 4, 4);
    }

    public static String getRemaining(long millis, boolean milliseconds) {
        return getRemaining(millis, milliseconds, true);
    }

    public static String getRemaining(long duration, boolean milliseconds, boolean trail) {
        if ((milliseconds) && (duration < MINUTE)) {
            return ((trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get()).format(duration * 0.001D) + 's';
        }
        return DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? (duration >= MULTI_HOUR ? "d" : "") + "d:" : "") + "HH:mm:ss");
    }

    public Collection<Timer> getTimers() {
        return this.timers;
    }

    public void registerTimer(Timer timer) {
        this.timers.add(timer);
        if (timer instanceof Listener) {
            this.plugin.getServer().getPluginManager().registerEvents((Listener) timer, this.plugin);
        }
    }

    public void unregisterTimer(Timer timer) {
        this.timers.remove(timer);
    }

    public void reloadTimerData() {
        this.config = FactionsPlugin.getInstance().getFileManager().getTimers();
        for (Timer timer : this.timers) {
            timer.load(this.config);
        }
    }

    public void saveTimerData() {
        for (Timer timer : this.timers) {
            timer.save(this.config);
        }
        this.config.saveFile();
    }

    public void run() {
        long now = System.currentTimeMillis();
        timerRunnableList.removeIf(next -> next.check(now));
        if (this.graceTimer != null) {
            graceEnabled = this.graceTimer.getRemaining() <= 0;
        }
    }

    public List<TimerRunnable> getTimerRunnableList() {
        return timerRunnableList;
    }
}
