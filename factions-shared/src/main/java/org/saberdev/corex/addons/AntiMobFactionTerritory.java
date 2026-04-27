package org.saberdev.corex.addons;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.saberdev.corex.CoreAddon;

/**
 * @Author: Driftay
 * @Date: 4/5/2023 4:24 PM
 */

@CoreAddon(configVariable = "Anti-Natural-Spawn-Faction")
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
