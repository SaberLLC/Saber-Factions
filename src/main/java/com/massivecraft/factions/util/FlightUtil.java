package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

/**
 * Factions - Developed by FactionsUUID Team.
 */
public class FlightUtil {

    private static FlightUtil instance;

    private EnemiesTask enemiesTask;

    private FlightUtil() {
        double enemyCheck = Conf.stealthFlyCheckRadius * 20;
        if (enemyCheck > 0) {
            if (FactionsPlugin.getInstance().getConfig().getBoolean("ffly.enemies-near-disable-flight")) {
                enemiesTask = new EnemiesTask();
                enemiesTask.runTaskTimer(FactionsPlugin.getInstance(), 0, (long) enemyCheck);
            }
        }
    }

    public static void start() {
        instance = new FlightUtil();
    }

    public static FlightUtil instance() {
        return instance;
    }

    public boolean enemiesNearby(FPlayer target, int radius) {
        if (this.enemiesTask == null) {
            return false;
        } else {
            return this.enemiesTask.enemiesNearby(target, radius);
        }
    }

    public static class EnemiesTask extends BukkitRunnable {

        @Override
        public void run() {
            Collection<FPlayer> players = FPlayers.getInstance().getOnlinePlayers();
            for (Player player : Bukkit.getOnlinePlayers()) {
                FPlayer pilot = FPlayers.getInstance().getByPlayer(player);
                if (pilot.isFlying() && !pilot.isAdminBypassing()) {
                    if (enemiesNearby(pilot, Conf.stealthFlyCheckRadius, players)) {
                        pilot.msg(TL.COMMAND_FLY_ENEMY_NEAR);
                        pilot.setFlying(false);
                        if (pilot.isAutoFlying()) {
                            pilot.setAutoFlying(false);
                        }
                    }
                }
            }
        }

        public boolean enemiesNearby(FPlayer target, int radius) {
            return this.enemiesNearby(target, radius, FPlayers.getInstance().getOnlinePlayers());
        }

        public boolean enemiesNearby(FPlayer target, int radius, Collection<FPlayer> players) {
            int radiusSquared = radius * radius;
            Location loc = target.getPlayer().getLocation();
            Location cur = new Location(loc.getWorld(), 0, 0, 0);
            for (FPlayer player : players) {
                if (player == target || player.isAdminBypassing()) {
                    continue;
                }

                player.getPlayer().getLocation(cur);
                if (cur.getWorld().getUID().equals(loc.getWorld().getUID()) &&
                        cur.distanceSquared(loc) <= radiusSquared &&
                        player.getRelationTo(target) == Relation.ENEMY &&
                        target.getPlayer().canSee(player.getPlayer())) {
                    return true;
                }
            }
            return false;
        }
    }
}
