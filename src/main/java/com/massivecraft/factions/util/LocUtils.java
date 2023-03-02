package com.massivecraft.factions.util;

import org.bukkit.ChatColor;
import com.massivecraft.factions.util.CC;
import org.bukkit.Location;

public class LocUtils {

    public static String printPretty(Location location, ChatColor color, boolean bold) {
        String boldText = bold ? CC.Bold : "";
        return color + "x" + color + boldText + location.getBlockX() +
                " " + color + "y" + color + boldText + location.getBlockY() +
                " " + color + "z" + color + boldText + location.getBlockZ();
    }
}
