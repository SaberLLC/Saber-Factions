package com.massivecraft.factions.util;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class LocUtils {

    public static String printPretty(Location location, ChatColor color, boolean bold) {
        String xyzBefore = "%sx%s %sy%s %sz%s";
        String boldText = bold ? CC.Bold : "";
        return String.format(xyzBefore, color, color + boldText + location.getBlockX(), color, color + boldText + location
                .getBlockY(), color, color + boldText + location.getBlockZ());
    }
}
