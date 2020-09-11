package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.event.FPlayerStoppedFlying;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;
import me.lucko.helper.bucket.Bucket;
import me.lucko.helper.bucket.factory.BucketFactory;
import me.lucko.helper.bucket.partitioning.PartitioningStrategies;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Factions - Developed by FactionsUUID Team.
 */
public final class FlightUtil implements Trackable<FPlayer> {

    private static FlightUtil instance;

    private FlightUtil() {
    }

    public static FlightUtil get() {
        if (instance == null) {
            synchronized (FlightUtil.class) {
                if (instance == null) {
                    instance = new FlightUtil();
                }
            }
        }
        return instance;
    }

    public static FlightUtil instance() {
        return instance;
    }

    public void start() {
        double enemyCheck = FactionsPlugin.getInstance().getConfig().getInt("ffly.enemy-radius-check", 1) * 20;
        if (enemyCheck > 0) {
            EnemiesTask.get().start();
        }
    }

    public boolean enemiesNearby(FPlayer target, int radius) {
        return !EnemiesTask.get().isClosed() && EnemiesTask.get().enemiesNearby(target, radius);
    }

    @Override
    public boolean track(FPlayer fPlayer) {
        return EnemiesTask.get().track(fPlayer);
    }

    @Override
    public boolean untrack(FPlayer fPlayer) {
        return EnemiesTask.get().untrack(fPlayer);
    }

    public void wipe() {
        EnemiesTask.get().wipe();
    }

    private static class EnemiesTask extends BukkitRunnable implements AutoCloseable {

        private static EnemiesTask instance;
        private final Bucket<FPlayer> players = BucketFactory.newHashSetBucket(20, PartitioningStrategies.lowestSize());
        private int task = -1;

        public static EnemiesTask get() {
            if (instance == null) {
                synchronized (EnemiesTask.class) {
                    if (instance == null) {
                        instance = new EnemiesTask();
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
            if (FPlayers.getInstance().getOnlinePlayers().size() == 1) {
                return;
            }
            for (FPlayer pilot : this.players.asCycle().next()) {
                if (!pilot.isFlying() || pilot.isAdminBypassing()) {
                    continue;
                }
                if (enemiesNearby(pilot, Conf.stealthFlyCheckRadius)) {
                    pilot.msg(TL.COMMAND_FLY_ENEMY_NEAR);
                    pilot.setFlying(false);
                    if (pilot.isAutoFlying()) {
                        pilot.setAutoFlying(false);
                    }
                }
            }
        }

        public boolean enemiesNearby(FPlayer target, int radius) {
            if (FPlayers.getInstance().getOnlinePlayers().size() == 1) {
                return false;
            }
            Player player = target.getPlayer();

            for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                if (entity.getType() != EntityType.PLAYER || entity == player) {
                    continue;
                }
                FPlayer playerNearby = FPlayers.getInstance().getByPlayer((Player) entity);

                if (playerNearby.isAdminBypassing() || playerNearby.isVanished() || playerNearby.isStealthEnabled() || playerNearby.getPlayer().getGameMode() == GameMode.SPECTATOR || playerNearby.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    continue;
                }
                if (playerNearby.getRelationTo(target) == Relation.ENEMY) {
                    Bukkit.getServer().getPluginManager().callEvent(new FPlayerStoppedFlying(target));
                    return true;
                }
            }
            return false;
        }
    }
}
