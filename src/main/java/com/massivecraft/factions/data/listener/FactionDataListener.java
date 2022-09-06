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
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @Author: Driftay
 * @Date: 2/11/2022 4:50 PM
 */
public class FactionDataListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFPlayerCreate(FPlayerJoinEvent e) {
        Faction faction = e.getFaction();
        if (e.getReason() == FPlayerJoinEvent.PlayerJoinReason.CREATE) {
            Bukkit.getScheduler().runTaskAsynchronously(FactionsPlugin.getInstance(),
                    (Runnable) new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!FactionDataHelper.doesConfigurationExist(faction)) {
                                FactionDataHelper.createConfiguration(faction);
                                Bukkit.getLogger().info("[FactionData] Creating Faction Data for " + faction.getTag());
                            }
                            final FactionData data = new FactionData(faction);
                            new FactionDataHelper(data);
                        }
                    });
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onFactionDisband(FactionDisbandEvent e) {
        FactionData data = FactionDataHelper.findFactionData(e.getFaction());

        if (data == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(FactionsPlugin.getInstance(),
                (Runnable) new BukkitRunnable() {
                    @Override
                    public void run() {
                        data.deleteFactionData(e.getFaction());
                        data.remove();
                    }
                });
    }
}
