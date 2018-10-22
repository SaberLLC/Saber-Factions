package com.massivecraft.factions.zcore.fupgrades;

import com.massivecraft.factions.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class SpawnerUpgrades implements Listener {
    @EventHandler
    public void onSpawn(SpawnerSpawnEvent e) {
        FLocation floc = new FLocation(e.getLocation());
        Faction factionAtLoc = Board.getInstance().getFactionAt(floc);
        if (factionAtLoc != Factions.getInstance().getWilderness()) {
            int level = factionAtLoc.getUpgrade("Spawner");
            if (level != 0) {
                if (level == 1) {
                  int rate = SavageFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Spawners.Spawner-Boost.level-1");
                    lowerSpawnerDelay(e, rate);
                }
                if (level == 2) {
                  int rate = SavageFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Spawners.Spawner-Boost.level-2");
                    lowerSpawnerDelay(e, rate);
                }
                if (level == 3) {
                  int rate = SavageFactions.plugin.getConfig().getInt("fupgrades.MainMenu.Spawners.Spawner-Boost.level-3");
                    lowerSpawnerDelay(e, rate);
                }

            }
        }
    }

    private void lowerSpawnerDelay(SpawnerSpawnEvent e, double multiplier) {
        int lowerby = (int) Math.round(e.getSpawner().getDelay() * multiplier);
        e.getSpawner().setDelay(e.getSpawner().getDelay() - lowerby);
    }


}
