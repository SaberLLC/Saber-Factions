package com.massivecraft.factions.util.flight;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CmdFly;
import com.massivecraft.factions.listeners.FactionsEntityListener;
import org.bukkit.GameMode;

/**
 * SaberFactions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 9/15/2020
 */
public class FlightEnhance implements Runnable {

    @Override
    public void run() {
        for (FPlayer player : FPlayers.getInstance().getOnlinePlayers()) {
            if (player.isAdminBypassing()
                    || player.getPlayer().isOp()
                    || player.getPlayer().getGameMode() == GameMode.CREATIVE
                    || player.getPlayer().getGameMode() == GameMode.SPECTATOR) continue;

            FLocation fLocation = FLocation.wrap(player.getPlayer().getLocation());

            player.checkIfNearbyEnemies();

            if (!player.hasEnemiesNearby()) {
                if (player.isFlying()) {
                    if (!player.canFlyAtLocation(fLocation)) {
                        player.setFlying(false, false);
                    }
                } else if (player.canFlyAtLocation()
                        && FactionsPlugin.getInstance().getConfig().getBoolean("ffly.AutoEnable")
                        && !FactionsEntityListener.combatList.contains(player.getPlayer().getUniqueId())) {
                    player.setFlying(true);
                }
            }
        }
    }
}