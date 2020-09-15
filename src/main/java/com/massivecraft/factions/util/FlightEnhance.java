package com.massivecraft.factions.util;

import com.massivecraft.factions.*;
import com.massivecraft.factions.listeners.FactionsEntityListener;
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

            if (fPlayer.isFlying()) {
                if (!fPlayer.canFlyAtLocation(fLocation)) {
                    fPlayer.setFlying(false, false);
                }
            } else if(fPlayer.canFlyAtLocation()
                    && FactionsPlugin.getInstance().getConfig().getBoolean("ffly.AutoEnable")
                    && !FactionsEntityListener.combatList.contains(player.getUniqueId())){
                fPlayer.setFlying(true);
            }
        }
    }
}


