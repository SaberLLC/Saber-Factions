package com.massivecraft.factions.data.listener;

import com.massivecraft.factions.data.helpers.FactionDataHelper;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @Author: Driftay
 * @Date: 2/11/2022 4:50 PM
 */
public class FactionDataListener implements Listener {
    private final FactionDataHelper factionDataHelper;

    public FactionDataListener(FactionDataHelper factionDataHelper) {
        this.factionDataHelper = factionDataHelper;
    }

    @EventHandler
    public void onFactionCreate(FPlayerJoinEvent event) {
        if (event.getReason() == FPlayerJoinEvent.PlayerJoinReason.CREATE) {
            factionDataHelper.getOrLoadFactionData(event.getFaction());
        }
    }

    @EventHandler
    public void onFactionDisband(FactionDisbandEvent event) {
        factionDataHelper.deleteFactionData(event.getFaction());
    }
}
