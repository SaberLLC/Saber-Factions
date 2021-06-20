package com.massivecraft.factions.boosters.task;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.boosters.BoosterTypes;
import com.massivecraft.factions.boosters.struct.CurrentBoosters;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.util.TL;

import java.util.Map;

public class BoosterTask implements Runnable {

    public void run() {
        FactionsPlugin.getInstance().getBoosterManager().getFactionBoosters().forEach((fId, boosters) -> {

            for (Map.Entry<BoosterTypes, CurrentBoosters> boosterTypesCurrentBoostersEntry : boosters.entrySet()) {

                CurrentBoosters boost = boosterTypesCurrentBoostersEntry.getValue();

                if (boost.getSecondsElapsed() >= boost.getMaxSeconds()) {

                    boosters.remove(boosterTypesCurrentBoostersEntry.getKey());
                    Faction faction = Factions.getInstance().getFactionById(fId);

                    if (faction != null) {
                        faction.sendMessage(CC.translate(TL.BOOSTER_EXPIRED.toString()
                                .replace("{multiplier}", String.valueOf(boost.getMultiplier()))
                                .replace("{boosterType}", boosterTypesCurrentBoostersEntry.getKey().getItemName())
                                .replace("{player}", boost.getWhoApplied())));

                    }
                    if (boosters.isEmpty()) {
                        FactionsPlugin.getInstance().getBoosterManager().getFactionBoosters().remove(fId);
                        FactionsPlugin.getInstance().getBoosterManager().saveActiveBoosters();
                    }
                } else {
                    boost.setSecondsElapsed(boost.getSecondsElapsed() + 1);
                }
            }
        });
    }
}
