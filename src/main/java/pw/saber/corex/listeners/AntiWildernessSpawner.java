package pw.saber.corex.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class AntiWildernessSpawner implements Listener {

    @EventHandler
    public void onSpawner(SpawnerSpawnEvent e) {
        FLocation floc = new FLocation(e.getSpawner().getLocation());

        if (floc == null) {
            return;
        }

        Faction faction = Board.getInstance().getFactionAt(floc);

        if (faction == null) {
            return;
        }
        if (faction.isWilderness()) {
            e.setCancelled(true);
        }
    }
}

