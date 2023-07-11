package org.saberdev.corex.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * @Author: Driftay
 * @Date: 4/5/2023 4:24 PM
 */
public class AntiMobFactionTerritory implements Listener {


    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        FLocation fLocation = FLocation.wrap(e.getLocation());
        Faction faction = Board.getInstance().getFactionAt(fLocation);
        if (!faction.isNormal()) return;

        if(e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            e.setCancelled(true);
        }
    }
}
