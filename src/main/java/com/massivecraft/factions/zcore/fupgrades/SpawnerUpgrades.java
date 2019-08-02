package com.massivecraft.factions.zcore.fupgrades;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class SpawnerUpgrades implements Listener {

	@EventHandler
	public void onSpawn(SpawnerSpawnEvent e) {
		FLocation floc = new FLocation(e.getLocation());
		Faction factionAtLoc = Board.getInstance().getFactionAt(floc);

		if (!factionAtLoc.isWilderness()) {
			int level = factionAtLoc.getUpgrade(UpgradeType.SPAWNER);
			if (level != 0) {
				switch (level) {
					case 1:
						lowerSpawnerDelay(e, P.p.getConfig().getInt("fupgrades.MainMenu.Spawners.Spawner-Boost.level-1"));
						break;
					case 2:
						lowerSpawnerDelay(e, P.p.getConfig().getInt("fupgrades.MainMenu.Spawners.Spawner-Boost.level-2"));
						break;
					case 3:
						lowerSpawnerDelay(e, P.p.getConfig().getInt("fupgrades.MainMenu.Spawners.Spawner-Boost.level-3"));
						break;
				}
			}
		}
	}

	private void lowerSpawnerDelay(SpawnerSpawnEvent e, double multiplier) {
		int lowerby = (int) Math.round(e.getSpawner().getDelay() * multiplier);
		e.getSpawner().setDelay(e.getSpawner().getDelay() - lowerby);
	}

}
