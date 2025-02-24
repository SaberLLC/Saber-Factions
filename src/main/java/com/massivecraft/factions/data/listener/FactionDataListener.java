package com.massivecraft.factions.data.listener;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.data.FactionData;
import com.massivecraft.factions.data.helpers.FactionDataHelper;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.IOException;

/**
 * @Author: Driftay
 * @Date: 2/11/2022 4:50 PM
 */
public class FactionDataListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFPlayerCreate(FPlayerJoinEvent e) {
        Faction faction = e.getFaction();
        if (e.getReason() == FPlayerJoinEvent.PlayerJoinReason.CREATE) {
            // Instead of runTaskAsynchronously
            Bukkit.getAsyncScheduler().runDelayed(
                FactionsPlugin.getInstance(),
                scheduledTask -> {
                    if (!FactionDataHelper.doesConfigurationExist(faction)) {
                        FactionDataHelper.createConfiguration(faction);
                        Bukkit.getLogger().info("[FactionData] Creating Faction Data for " + faction.getTag());
                    }
                    FactionDataHelper.addFactionData(new FactionData(faction));
                },
                0L, // zero delay
                java.util.concurrent.TimeUnit.MILLISECONDS
            );
        }
    }
    

    @EventHandler(priority = EventPriority.LOW)
    public void onFactionDisband(FactionDisbandEvent e) {
        FactionData data = FactionDataHelper.findFactionData(e.getFaction());
        if (data == null) return;
    
        Bukkit.getAsyncScheduler().runDelayed(
            FactionsPlugin.getInstance(),
            scheduledTask -> {
                try {
                    data.deleteFactionData(e.getFaction());
                } catch (Exception ex) {
                    Bukkit.getLogger().severe("Error deleting faction data: " + ex.getMessage());
                }
            },
            0L,
            java.util.concurrent.TimeUnit.MILLISECONDS
        );
    }    
}
