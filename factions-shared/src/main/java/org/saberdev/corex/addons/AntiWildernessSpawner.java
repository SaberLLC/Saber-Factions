package org.saberdev.corex.addons;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.saberdev.corex.CoreAddon;


@CoreAddon(configVariable = "Anti-Wilderness-Spawner")
public class AntiWildernessSpawner implements Listener {

    @EventHandler
    public void onSpawner(SpawnerSpawnEvent e) {
        Faction faction = Board.getInstance().getFactionAt(FLocation.wrap(e.getSpawner().getLocation()));
        if (faction == null) {
            return;
        }
        if (faction.isWilderness()) {
            e.setCancelled(true);
        }
    }
}