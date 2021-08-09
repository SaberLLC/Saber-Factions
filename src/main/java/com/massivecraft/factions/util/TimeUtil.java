package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static Calendar calender = getCalenderTimeZone();


    public static String formatSeconds(int timeInSeconds) {
        int hours = timeInSeconds / 3600;
        int secondsLeft = timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;
        String formattedTime = "";
        if (hours < 10)
            formattedTime = formattedTime + "0";
        formattedTime = formattedTime + hours + ":";
        if (minutes < 10)
            formattedTime = formattedTime + "0";
        formattedTime = formattedTime + minutes + ":";
        if (seconds < 10)
            formattedTime = formattedTime + "0";
        formattedTime = formattedTime + seconds;
        return formattedTime;
    }

    public static String formatDifference(long time) {
        if (time == 0L) {
            return "Never";
        }
        long day = TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time) - day * 24L;
        long minutes = TimeUnit.SECONDS.toMinutes(time) - TimeUnit.SECONDS.toHours(time) * 60L;
        long seconds = TimeUnit.SECONDS.toSeconds(time) - TimeUnit.SECONDS.toMinutes(time) * 60L;
        StringBuilder sb = new StringBuilder();
        if (day > 0L) {
            sb.append(day).append((day == 1L) ? "day" : "days").append(" ");
        }
        if (hours > 0L) {
            sb.append(hours).append((hours == 1L) ? "h" : "h").append(" ");
        }
        if (minutes > 0L) {
            sb.append(minutes).append((minutes == 1L) ? "m" : "m").append(" ");
        }
        if (seconds > 0L) {
            sb.append(seconds).append((seconds == 1L) ? "s" : "s");
        }
        String diff = sb.toString().trim();
        return diff.isEmpty() ? "Now" : diff;
    }

    private static Calendar getCalenderTimeZone() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(Conf.serverTimeZone));
        return calendar;
    }

    public static int getYear() {
        calender = Calendar.getInstance(calender.getTimeZone());
        return calender.get(Calendar.YEAR);
    }

    public static int getMonth() {
        calender = Calendar.getInstance(calender.getTimeZone());
        return calender.get(Calendar.MONTH) + 1;
    }

    public static int getDay() {
        calender = Calendar.getInstance(calender.getTimeZone());
        return calender.get(Calendar.DAY_OF_MONTH);
    }

    public static Calendar addDay(int days) {
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_MONTH, days);
        return calendar2;
    }

    public static int getDay(boolean incrementBy1) {
        if (!incrementBy1) {
            return getDay();
        }
        calender = Calendar.getInstance(calender.getTimeZone());

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_MONTH, 1);

        return calendar2.get(Calendar.DAY_OF_MONTH);
    }

    public static int getTimeHours() {
        calender = Calendar.getInstance(calender.getTimeZone());
        return calender.get(Calendar.HOUR_OF_DAY);
    }

    public static int getTimeMinutes() {
        calender = Calendar.getInstance(calender.getTimeZone());
        return calender.get(Calendar.MINUTE);
    }

    public static int getTimeSeconds() {
        calender = Calendar.getInstance(calender.getTimeZone());
        return calender.get(Calendar.SECOND);
    }

    public static int getTimeMilliseconds() {
        calender = Calendar.getInstance(calender.getTimeZone());
        return calender.get(Calendar.MILLISECOND);
    }

    public static String getTimeString() {
        int time = getTimeHours();
        int minutes = getTimeMinutes();
        return time > 12 ? time - 12 + ":" + (minutes < 10 ? "0" + minutes : minutes) + "PM" : time + ":" + (minutes < 10 ? "0" + minutes : minutes) + "AM";
    }

    public static String getTimeStringSeconds() {
        int time = getTimeHours();
        int minutes = getTimeMinutes();
        int seconds = getTimeSeconds();
        return time > 12 ? time - 12 + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds) + "PM" : time + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds) + "AM";
    }

    public static String formatSecondsAsTime(int seconds) {
        int minutes = seconds / 60;
        if (minutes < 1)
            return seconds + "s";
        else {
            int secs = seconds - minutes * 60;
            return minutes + "m " + secs + "s";
        }
    }

    public static String formatTimeFormat(long timePeriod) {
        long millis = timePeriod;

        String output = "";

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);

        if (days > 1) output += days + " d ";
        else if (days == 1) output += days + " d ";

        if (hours > 1) output += hours + " h ";
        else if (hours == 1) output += hours + " h ";

        if (minutes > 1) output += minutes + " m ";
        else if (minutes == 1) output += minutes + " m ";

        if (output.isEmpty()) return "None";

        return output.trim();
    }

    public static String formatPlayTime(long playTime) {
        long millis = playTime;

        String output = "";

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        if (days > 1) output += days + " days ";
        else if (days == 1) output += days + " day ";

        if (hours > 1) output += hours + " hours ";
        else if (hours == 1) output += hours + " hour ";

        if (minutes > 1) output += minutes + " minutes ";
        else if (minutes == 1) output += minutes + " minute ";

        if (seconds > 1) output += seconds + " seconds ";
        else if (seconds == 1) output += seconds + " second ";

        if (output.isEmpty()) return "0 seconds ";

        return output;
    }

    public static String formatTime(long timePeriod) {
        long millis = timePeriod;

        String output = "";

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        if (days > 1) output += days + " days ";
        else if (days == 1) output += days + " day ";

        if (hours > 1) output += hours + " hours ";
        else if (hours == 1) output += hours + " hour ";

        if (minutes > 1) output += minutes + " minutes ";
        else if (minutes == 1) output += minutes + " minute ";

        if (seconds > 1) output += seconds + " seconds ";
        else if (seconds == 1) output += seconds + " second ";

        if (output.isEmpty()) return "just now ";

        return output;
    }

    public static String formatTime(int seconds) {
        int days = seconds / 86400;
        int hours = seconds % 86400 / 3600;
        int minutes = seconds % 86400 % 3600 / 60;

        StringBuilder sb = new StringBuilder();

        if (days != 0) {
            if (days > 1) sb.append(days).append(" days ");
            else if (days == 1) sb.append("1 day ");
        }

        if (hours != 0) {
            if (hours > 1) sb.append(hours).append(" hours ");
            else if (hours == 1) sb.append("1 hour ");
        }

        if (minutes != 0) {
            if (minutes > 1) sb.append(minutes).append(" minutes ");
            else if (minutes == 1) sb.append("1 minute ");
        }

        if (sb.toString().isEmpty()) return "just now ";

        return sb.toString();
    }

}