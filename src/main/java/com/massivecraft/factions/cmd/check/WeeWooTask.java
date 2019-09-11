package com.massivecraft.factions.cmd.check;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.util.TL;

public class WeeWooTask implements Runnable {

    private P plugin;

    public WeeWooTask(P plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (!faction.isWeeWoo()) {
                continue;
            }
            faction.msg(TL.WEE_WOO_MESSAGE);
        }
    }
}
