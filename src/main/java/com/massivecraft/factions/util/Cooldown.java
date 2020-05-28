package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.concurrent.TimeUnit;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/6/2020
 */
public class Cooldown {

    public static void setCooldown(Player player, String name, int seconds) {
        player.setMetadata(name, new FixedMetadataValue(FactionsPlugin.getInstance(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds)));
    }

    public static boolean isOnCooldown(Player player, String name) {
        if (!player.hasMetadata(name) || player.getMetadata(name).size() <= 0) return false;
        long time = player.getMetadata(name).get(0).asLong();
        return (time > System.currentTimeMillis());
    }

}
