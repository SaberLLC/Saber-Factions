package com.massivecraft.factions.shop.handlers;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.SaberFactions;
import com.massivecraft.factions.shop.Pair;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PotionHandler implements Runnable {
    private SaberFactions plugin;

    public PotionHandler(SaberFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            List<String> remove = new ArrayList<String>();
            for (Map.Entry<String, Pair<Integer, Long>> entry : faction.getActivePotions().entrySet()) {
                if (entry.getValue().getValue() < currentTime) {
                    remove.add(entry.getKey());
                } else {
                    PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(entry.getKey()), 400, entry.getValue().getKey(), false, false);
                    for (FPlayer fPlayer : faction.getFPlayersWhereOnline(true)) {
                        if (!fPlayer.isInOwnTerritory()) {
                            continue;
                        }
                        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> fPlayer.getPlayer().addPotionEffect(potionEffect, true));
                    }
                }
            }

            remove.forEach(key -> {
                Pair<Integer, Long> pair = faction.getActivePotions().remove(key);
            });
        }
    }
}
