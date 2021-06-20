package com.massivecraft.factions.boosters.struct;

import com.massivecraft.factions.boosters.BoosterTypes;

import java.util.concurrent.ConcurrentHashMap;

public class FactionBoosters extends ConcurrentHashMap<BoosterTypes, CurrentBoosters> {
    public boolean isBoosterActive(BoosterTypes type) {
        return this.containsKey(type);
    }
}

