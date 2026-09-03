package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.scheduler.FactionTask;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.entity.Player;

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
    public static void process(final FPlayer player, Warmup warmup, TL translationKey, String action, final Runnable runnable, long delay) {
        if (delay > 0) {
            if (player.isWarmingUp()) {
                player.msg(TL.WARMUPS_ALREADY);
                return;
            }

            player.msg(translationKey.format(action, delay));
            Player bukkitPlayer = player.getPlayer();
            if (bukkitPlayer == null) return;
            FactionTask task = FactionsPlugin.getScheduler().runEntityLater(
                bukkitPlayer, delay * 20L, () -> {
                    player.stopWarmup();
                    runnable.run();
                }, () -> player.stopWarmup());
            player.addWarmup(warmup, task);
        } else {
            runnable.run();
        }
    }

    public enum Warmup {
        HOME, WARP, FLIGHT, BANNER, CHECKPOINT, WILD
    }

}
