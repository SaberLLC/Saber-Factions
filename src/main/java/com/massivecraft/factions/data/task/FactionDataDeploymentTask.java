package com.massivecraft.factions.data.task;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.data.FactionData;
import com.massivecraft.factions.data.helpers.FactionDataHelper;
import com.massivecraft.factions.util.Logger;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Driftay
 * @Date: 9/6/2022 1:30 AM
 */
public class FactionDataDeploymentTask extends BukkitRunnable {
    private List<Faction> cachedList = new ArrayList<>();

    @Override
    public void run() {
        for(Faction faction : Factions.getInstance().getAllFactions()) {
            if(cachedList.contains(faction)) continue;
            if(faction.isSystemFaction()) continue;
            final FactionData data = new FactionData(faction);
            new FactionDataHelper(data);

            cachedList.add(faction);
            Logger.print("Cached Faction Data For Faction: " + faction.getTag(), Logger.PrefixType.DEFAULT);
        }
    }
}
