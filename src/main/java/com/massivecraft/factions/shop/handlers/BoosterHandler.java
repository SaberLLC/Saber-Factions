package com.massivecraft.factions.shop.handlers;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoosterHandler implements Runnable {

    @Override
    public void run() {
        final long currentTime = System.currentTimeMillis();
        for (final Faction faction : Factions.getInstance().getAllFactions()) {
            final List<String> remove = new ArrayList<>();
            for (final Map.Entry<String, Long> entry : faction.getBoosters().entrySet()) {
                if (entry.getValue() < currentTime) {
                    remove.add(entry.getKey());
                }
            }

            remove.forEach((r) ->{
                Long n = faction.getBoosters().remove(r);
            });
        }
    }
}
