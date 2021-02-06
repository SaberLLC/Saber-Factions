package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;
import org.bukkit.entity.Player;

public class DisableUtils {

    public static boolean isEnabled(Player player) {
        return Conf.worldsNoFactionsPlugin.contains(player.getWorld().getName());
    }

}
