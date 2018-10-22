package com.massivecraft.factions.zcore.fupgrades;

import com.massivecraft.factions.*;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EXPUpgrade implements Listener {
    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        Entity killer = e.getEntity().getKiller();
        Player player = (Player) killer;
        if (player == null) {
            return;
        }
        Location loc = e.getEntity().getLocation();
        Faction wild = Factions.getInstance().getWilderness();
        FLocation floc = new FLocation(loc);
        Faction faction = Board.getInstance().getFactionAt(floc);
        if (faction != wild) {
            int level = faction.getUpgrade("Exp");
            if (level != 0) {
                if (level == 1) {
                  double multiplier = SavageFactions.plugin.getConfig().getDouble("fupgrades.MainMenu.EXP.EXP-Boost.level-1");
                    spawnMoreExp(e, multiplier);
                }
                if (level == 2) {
                  double multiplier = SavageFactions.plugin.getConfig().getDouble("fupgrades.MainMenu.EXP.EXP-Boost.level-2");
                    spawnMoreExp(e, multiplier);
                }
                if (level == 3) {
                  double multiplier = SavageFactions.plugin.getConfig().getDouble("fupgrades.MainMenu.EXP.EXP-Boost.level-3");
                    spawnMoreExp(e, multiplier);
                }
            }
        }
    }

    private void spawnMoreExp(EntityDeathEvent e, double multiplier) {
        double newExp = e.getDroppedExp() * multiplier;
        int newExpInt = (int) newExp;
        e.setDroppedExp(newExpInt);
    }
}
