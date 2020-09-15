package com.massivecraft.factions.util;

import com.massivecraft.factions.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * SaberFactionsX - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 9/15/2020
 */
public class FlightEnhance implements Runnable {


    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

            if (fPlayer.isAdminBypassing() || player.isOp()) continue;

            FLocation fLocation = new FLocation(player.getLocation());
            Faction at = Board.getInstance().getFactionAt(fLocation);

            if (at == null) {
                at = Factions.getInstance().getWilderness();
            }

            if (fPlayer.isFlying()) {
                if (!fPlayer.canFlyAtLocation(fLocation)) {
                    fPlayer.setFlying(false, false);
                }
            } else if(fPlayer.canFlyAtLocation() && FactionsPlugin.getInstance().getConfig().getBoolean("ffly.AutoEnable")){
                fPlayer.setFlying(true);
            }
        }
    }
}


