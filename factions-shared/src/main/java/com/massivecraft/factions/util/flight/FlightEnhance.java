package com.massivecraft.factions.util.flight;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.listeners.FactionsEntityListener;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * SaberFactions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 9/15/2020
 */
public class FlightEnhance implements Runnable {

    @Override
    public void run() {
        for (FPlayer player : FPlayers.getInstance().getOnlinePlayers()) {
            if (shouldSkipPlayer(player)) continue;

            FLocation fLocation = FLocation.wrap(player.getPlayer().getLocation());
            player.checkIfNearbyEnemies();

            if (!player.hasEnemiesNearby()) {
                handleFlightStatusForPlayer(player, fLocation);
            }
        }
    }

    private boolean shouldSkipPlayer(FPlayer player) {
        Player p = player.getPlayer();

        return player.isAdminBypassing()
                || p == null
                || p.isOp()
                || p.getGameMode() == GameMode.CREATIVE
                || p.getGameMode() == GameMode.SPECTATOR;
    }

    private void handleFlightStatusForPlayer(FPlayer player, FLocation fLocation) {
        if (player.isFlying() && !player.canFlyAtLocation(fLocation)) {
            player.setFlying(false, false);
            return;
        }

        if (!player.isFlying()
                && player.canFlyAtLocation()
                && FactionsPlugin.getInstance().getConfig().getBoolean("ffly.AutoEnable")
                && !FactionsEntityListener.combatList.contains(player.getPlayer().getUniqueId())) {
            player.setFlying(true);
        }
    }
}