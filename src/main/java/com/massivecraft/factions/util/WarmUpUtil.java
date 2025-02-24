package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TL;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask; // Folia import
import org.bukkit.Bukkit;

public class WarmUpUtil {

    /**
     * @param player         The player to notify.
     * @param translationKey The translation key used for notifying.
     * @param action         The action, inserted into the notification message.
     * @param runnable       The task to run after the delay. If the delay is 0, the task is instantly ran.
     * @param delay          The time used, in seconds, for the delay.
     *                       <p>
     *                       note: for translations: %s = action, %d = delay
     */
    public static void process(
        final FPlayer player,
        Warmup warmup,
        TL translationKey,
        String action,
        final Runnable runnable,
        long delay
    ) {
        if (delay > 0) {
            if (player.isWarmingUp()) {
                player.msg(TL.WARMUPS_ALREADY);
                return;
            }

            player.msg(translationKey.format(action, delay));

            // Convert seconds to ticks => delay * 20
            // Then schedule a synchronous one-off task on the main thread
            ScheduledTask scheduledTask = Bukkit.getGlobalRegionScheduler().runDelayed(
                FactionsPlugin.getInstance(),
                task -> {
                    player.stopWarmup();
                    runnable.run();
                },
                delay * 20L
            );

            // This requires that FPlayer#addWarmup(...) accepts a ScheduledTask
            player.addWarmup(warmup, scheduledTask);

        } else {
            // If no delay, run immediately
            runnable.run();
        }
    }

    public enum Warmup {
        HOME, WARP, FLIGHT, BANNER, CHECKPOINT, WILD
    }
}
