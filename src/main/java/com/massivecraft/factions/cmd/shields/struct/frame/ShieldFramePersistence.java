package com.massivecraft.factions.cmd.shields.struct.frame;

import com.massivecraft.factions.cmd.shields.struct.tasks.ShieldManagement;

/**
 * Factions - Developed by ImCarib.
 * All rights reserved 2020.
 * Creation Date: 5/23/2020
 */

public class ShieldFramePersistence {
    private int start;

    private int end;

    public ShieldFramePersistence(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getEndParsed() {
        return this.end;
    }

    public int getStartParsed() {
        return this.start;
    }

    public String getStartTime() {
        return toClockFormat(this.start);
    }

    public String getEndTime() {
        return toClockFormat(this.end);
    }

    public boolean isHourProtected(int hour) {
        if (this.end < this.start) return hour >= this.start;

        return (hour >= this.start && hour < this.end);
    }

    public boolean isProtectedCurrent() {
        return isHourProtected(ShieldManagement.getCurrentHour());
    }

    public String toClockFormat(int x) {
        if (x == 0) return "12:00 am";
        if (x == 12) return "12:00 pm";
        if (x > 12) return (x - 12) + ":00 pm";
        return x + ":00 am";
    }
}

