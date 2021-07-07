package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;
import org.bukkit.World;

public class WorldUtil {

    public static boolean isEnabledInWorld(World world) {
        return !Conf.worldsNoFactionsPlugin.contains(world.getName());
    }

}
