package com.massivecraft.factions.cmd.check;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TL;

public class WeeWooTask implements Runnable {

    /**
     * @author Driftay
     */

    private FactionsPlugin plugin;

    public WeeWooTask(FactionsPlugin plugin) {
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
