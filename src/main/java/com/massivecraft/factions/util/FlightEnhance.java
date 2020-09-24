package com.massivecraft.factions.util;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CmdFly;
import com.massivecraft.factions.listeners.FactionsEntityListener;
import me.lucko.helper.bucket.Bucket;
import me.lucko.helper.bucket.factory.BucketFactory;
import me.lucko.helper.bucket.partitioning.PartitioningStrategies;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * SaberFactions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 9/15/2020
 */
public class FlightEnhance implements Trackable<FPlayer> {

    private static FlightEnhance instance;

    private FlightEnhance() {

    }

    public static FlightEnhance get() {
        if (instance == null) {
            synchronized (FlightEnhance.class) {
                if (instance == null) {
                    instance = new FlightEnhance();
                }
            }
        }
        return instance;
    }

    public void start() {
        FlightTask.get().start();
    }

    public static FlightEnhance instance() {
        return instance;
    }


    @Override
    public boolean track(FPlayer fPlayer) {
        return FlightTask.get().track(fPlayer);
    }

    @Override
    public boolean untrack(FPlayer fPlayer) {
        return FlightTask.get().untrack(fPlayer);
    }

    public void wipe() {
        FlightEnhance.FlightTask.get().wipe();
    }

    private static class FlightTask extends BukkitRunnable implements AutoCloseable {

        private static FlightTask instance;

        private int task = -1;

        private final Bucket<FPlayer> players = BucketFactory.newHashSetBucket(20, PartitioningStrategies.lowestSize());

        public static FlightTask get() {
            if (instance == null) {
                synchronized (FlightTask.class) {
                    if (instance == null) {
                        instance = new FlightTask();
                    }
                }
            }
            return instance;
        }

        public boolean track(FPlayer fPlayer) {
            return this.players.add(fPlayer);
        }

        public boolean untrack(FPlayer fPlayer) {
            return this.players.remove(fPlayer);
        }

        public void wipe() {
            this.players.clear();
        }

        public void start() {
            this.task = this.runTaskTimer(FactionsPlugin.getInstance(), 1L, 1L).getTaskId();
        }

        @Override
        public void close() {
            if (this.task != -1) {
                Bukkit.getScheduler().cancelTask(this.task);
                this.task = -1;
            }
        }

        public boolean isClosed() {
            return this.task == -1;
        }

        @Override
        public void run() {
            for (FPlayer player : this.players.asCycle().next()) {
                if (player.isAdminBypassing()
                        || player.getPlayer().isOp()
                        || player.getPlayer().getGameMode() == GameMode.CREATIVE
                        || player.getPlayer().getGameMode() == GameMode.SPECTATOR) continue;

                FLocation fLocation = new FLocation(player.getPlayer().getLocation());

                if (player.hasEnemiesNearby()) continue;

                player.checkIfNearbyEnemies();

                if (player.isFlying()) {
                    if (!player.canFlyAtLocation(fLocation)) {
                        player.setFlying(false, false);
                    }
                } else if (player.canFlyAtLocation()
                        && FactionsPlugin.getInstance().getConfig().getBoolean("ffly.AutoEnable")
                        && !FactionsEntityListener.combatList.contains(player.getPlayer().getUniqueId())
                        && !CmdFly.falseList.contains(player.getPlayer().getUniqueId())) {
                    player.setFlying(true);
                }
            }
        }
    }
}