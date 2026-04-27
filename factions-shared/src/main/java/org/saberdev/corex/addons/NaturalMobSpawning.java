package org.saberdev.corex.addons;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.saberdev.corex.CoreAddon;


@CoreAddon(configVariable = "Anti-Natural-Mobs")
public class NaturalMobSpawning implements Listener {

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            e.setCancelled(true);
        }
    }
}
