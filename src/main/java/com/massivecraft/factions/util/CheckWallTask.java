package com.massivecraft.factions.util;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import org.bukkit.scheduler.BukkitRunnable;

public class CheckWallTask extends BukkitRunnable {

    private int overtime;

    public CheckWallTask() {
        overtime = 0;
    }

    public void run() {
        ++overtime;
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (faction.getCheckNotifier() != 0L && overtime % faction.getCheckNotifier() == 0L) {
                faction.sendCheckNotify();
            }
        }
    }
}
