package com.massivecraft.factions.cmd.shields.struct.tasks;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.shields.struct.ShieldTCMP;
import org.bukkit.Bukkit;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Factions - Developed by ImCarib.
 * All rights reserved 2020.
 * Creation Date: 5/23/2020
 */

public class ShieldManagement implements Runnable {
    private static DateTimeZone zone = DateTimeZone.forID("Canada/Eastern");
    private int lastHour;

    public ShieldManagement() {
        new ShieldTCMP();
        this.lastHour = 25;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(FactionsPlugin.getInstance(), this, 20L, 1200L);
    }

    public static int getCurrentHour() {
        DateTime time = new DateTime(zone);
        return time.getHourOfDay();
    }

    public static String getCurrentTime() {
        DateTime time = new DateTime(zone);
        int hour = time.getHourOfDay();
        if (hour == 0) hour = 12;

        if (hour > 12)
            return (hour - 12) + ":" + ((time.getMinuteOfHour() < 10) ? ("0" + time.getMinuteOfHour()) : Integer.valueOf(time.getMinuteOfHour())) + " pm";

        return hour + ":" + ((time.getMinuteOfHour() < 10) ? ("0" + time.getMinuteOfHour()) : Integer.valueOf(time.getMinuteOfHour())) + " am";
    }

    public void run() {
        DateTime time = new DateTime(zone);
        int hour = time.getHourOfDay();
        if (this.lastHour == 25 || this.lastHour != hour) {
            long now = System.currentTimeMillis();
            Factions.getInstance().getAllFactions().forEach(faction -> {
                if (faction.pendingShieldChange() && faction.getShieldChangeTime() < now) faction.applyShield();
                if (faction.getShieldFrame() != null) {
                    if (faction.getShieldFrame().isHourProtected(hour)) faction.setProtected();
                } else {
                    faction.setUnprotected();
                }
            });
        }
    }
}
