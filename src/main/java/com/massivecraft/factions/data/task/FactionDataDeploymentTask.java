package com.massivecraft.factions.data.task;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.data.FactionData;
import com.massivecraft.factions.data.helpers.FactionDataHelper;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Driftay
 * @Date: 9/6/2022 1:30 AM
 */
public class FactionDataDeploymentTask extends BukkitRunnable {
    private final Set<Faction> cachedList = new HashSet<>();

    @Override
    public void run() {
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (faction.isSystemFaction()) continue;

            // Check if FactionData for the faction already exists in the list
            FactionData existingData = FactionDataHelper.findFactionData(faction);
            if (existingData != null) {
                continue;  // Skip adding data for this faction if it already exists
            }

            FactionData data = new FactionData(faction);
            FactionDataHelper.addFactionData(data);
            cachedList.add(faction);
        }
    }
}
