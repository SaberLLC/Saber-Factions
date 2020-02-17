package com.massivecraft.factions.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 1/30/2020
 */
public class TimeUtil {

    public static long parseDateDiff(String time, boolean future) throws Exception {
        Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2);
        Matcher m = timePattern.matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() != null) {
                if (m.group().isEmpty()) continue;
                for (int i = 0; i < m.groupCount(); ++i) {
                    if (m.group(i) != null && !m.group(i).isEmpty()) {
                        found = true;
                        break;
                    }
                }
                if (!found) continue;

                if (m.group(1) != null && !m.group(1).isEmpty()) years = Integer.parseInt(m.group(1));

                if (m.group(2) != null && !m.group(2).isEmpty()) months = Integer.parseInt(m.group(2));

                if (m.group(3) != null && !m.group(3).isEmpty()) weeks = Integer.parseInt(m.group(3));

                if (m.group(4) != null && !m.group(4).isEmpty()) days = Integer.parseInt(m.group(4));

                if (m.group(5) != null && !m.group(5).isEmpty()) hours = Integer.parseInt(m.group(5));

                if (m.group(6) != null && !m.group(6).isEmpty()) minutes = Integer.parseInt(m.group(6));

                if (m.group(7) != null && !m.group(7).isEmpty()) {
                    seconds = Integer.parseInt(m.group(7));
                    break;
                }
                break;
            }
        }
        if (!found) throw new Exception("Illegal Date");

        if (years > 20) throw new Exception("Illegal Date");

        Calendar c = new GregorianCalendar();
        if (years > 0) c.add(Calendar.YEAR, years * (future ? 1 : -1));

        if (months > 0) c.add(Calendar.MONTH, months * (future ? 1 : -1));

        if (weeks > 0) c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));

        if (days > 0) c.add(Calendar.DATE, days * (future ? 1 : -1));

        if (hours > 0) c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));

        if (minutes > 0) c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));

        if (seconds > 0) c.add(Calendar.SECOND, seconds * (future ? 1 : -1));

        System.out.println("current: " + c.getTimeInMillis() + " Time: " + System.currentTimeMillis() + " Form: " + formatTime(c.getTimeInMillis() / 1000L));
        return c.getTimeInMillis() / 1000L;
    }

    public static String formatDifference(long time) {
        if (time == 0L) return "Never";

        long day = TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time) - day * 24L;
        long minutes = TimeUnit.SECONDS.toMinutes(time) - TimeUnit.SECONDS.toHours(time) * 60L;
        long seconds = TimeUnit.SECONDS.toSeconds(time) - TimeUnit.SECONDS.toMinutes(time) * 60L;
        StringBuilder sb = new StringBuilder();
        if (day > 0L) sb.append(day).append((day == 1L) ? "day" : "days").append(" ");

        if (hours > 0L) sb.append(hours).append((hours == 1L) ? "h" : "h").append(" ");

        if (minutes > 0L) sb.append(minutes).append((minutes == 1L) ? "m" : "m").append(" ");

        if (seconds > 0L) sb.append(seconds).append((seconds == 1L) ? "s" : "s");

        String diff = sb.toString().trim();
        return diff.isEmpty() ? "Now" : diff;
    }

    public static String formatTime(long time) {
        if (time == System.currentTimeMillis()) return "Now";

        if (time == -1L) return "Never";

        return formatDifference(time - System.currentTimeMillis() / 1000L);
    }
}

