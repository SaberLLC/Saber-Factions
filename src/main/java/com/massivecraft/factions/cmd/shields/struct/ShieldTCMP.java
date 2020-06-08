package com.massivecraft.factions.cmd.shields.struct;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.cmd.shields.struct.frame.ShieldFramePersistence;

import java.util.ArrayList;
import java.util.List;


/**
 * Factions - Developed by ImCarib.
 * All rights reserved 2020.
 * Creation Date: 5/23/2020
 */

public class ShieldTCMP {
    private static ShieldTCMP instance;

    private List<ShieldFramePersistence> frames = new ArrayList<>();

    public ShieldTCMP() {
        instance = this;
        for (int x = 0; x <= 23; x++) {
            int end = (x + Conf.shieldTimeHours > 23) ? (x + Conf.shieldTimeHours - 24) : (x + Conf.shieldTimeHours);
            this.frames.add(new ShieldFramePersistence(x, end));
        }
    }

    public static ShieldTCMP getInstance() {
        return instance;
    }

    public ShieldFramePersistence getByStart(int start) {
        if (start > 23) return null;
        return this.frames.get(start);
    }

    public List<ShieldFramePersistence> getFrames() {
        return this.frames;
    }
}
