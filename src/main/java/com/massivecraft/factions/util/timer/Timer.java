package com.massivecraft.factions.util.timer;

import com.massivecraft.factions.util.Config;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/7/2020
 */
public abstract class Timer {
    public final long defaultCooldown;
    protected final String name;


    public Timer(String name, long defaultCooldown) {
        this.name = name;
        this.defaultCooldown = defaultCooldown;
    }

    public String getName() {
        return this.name;
    }

    public void load(Config config) {
    }


    public void save(Config config) {
    }
}