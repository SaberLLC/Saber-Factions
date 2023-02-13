package com.massivecraft.factions.missions.impl;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.missions.MissionHandler;
import com.massivecraft.factions.missions.MissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

/**
 * @Author: Driftay
 * @Date: 12/20/2022 3:41 AM
 */
public class MissionHandlerModern implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerBreed(EntityBreedEvent e) {
        if (!(e.getBreeder() instanceof Player)) {
            return;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player) e.getBreeder());
        if (fPlayer == null) {
            return;
        }

        MissionHandler.handleMissionsOfType(fPlayer, MissionType.BREED, (mission, section) -> {
            String entity = section.getString("Mission.Entity", MissionHandler.matchAnythingRegex);
            return e.getEntityType().toString().matches(entity) ? 1 : -1;
        });
    }
}
