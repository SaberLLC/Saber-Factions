package com.massivecraft.factions.missions;

public enum MissionType {
    NONE,
    KILL,
    MINE,
    PLACE,
    SPAWNER_PLACE,
    FISH,
    TAME,
    ENCHANT,
    CONSUME,
    BREED,
    TRIBUTE;



    public static MissionType fromName(String name){
        if(name == null || name.isEmpty())
            return NONE;

        return MissionType.valueOf(name.toUpperCase());
    }
}
