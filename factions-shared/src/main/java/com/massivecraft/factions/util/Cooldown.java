package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/6/2020
 */
public class Cooldown {
    
    private static final long MILLIS_IN_SECOND = TimeUnit.SECONDS.toMillis(1);

    public static void setCooldown(Player player, String name, int seconds) {
        player.setMetadata(name, new FixedMetadataValue(FactionsPlugin.getInstance(), System.currentTimeMillis() + seconds * MILLIS_IN_SECOND));
    }

    public static void setCooldown(Faction fac, String name, int seconds) {
        long expiration = System.currentTimeMillis() + seconds * MILLIS_IN_SECOND;
        for (FPlayer fPlayer : fac.getFPlayersWhereOnline(true)) {
            Player player = fPlayer.getPlayer();
            if (player == null) continue;
            player.setMetadata(name, new FixedMetadataValue(FactionsPlugin.getInstance(), expiration));
        }
    }

    public static String sendCooldownLeft(Player player, String name) {
        List<MetadataValue> values = player.getMetadata(name);
        if (values.isEmpty()) return "";

        long remaining = values.get(0).asLong() - System.currentTimeMillis();
        int remainSec = (int) (remaining / MILLIS_IN_SECOND);
        return TimeUtil.formatSeconds(remainSec);
    }

    public static boolean isOnCooldown(Player player, String name) {
        List<MetadataValue> values = player.getMetadata(name);
        if (values.isEmpty()) return false;

        long time = values.get(0).asLong();
        return time > System.currentTimeMillis();
    }
}