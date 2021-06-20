package com.massivecraft.factions.boosters;


public enum BoosterTypes {

    EXP("EXP", "Experience"),
    MOB("MOB", "Mob Drops"),
    MCMMO("MCMMO", "mcMMO");

    String name;
    String itemName;

    BoosterTypes(String name, String itemName) {
        this.name = name;
        this.itemName = itemName;
    }

    public static BoosterTypes fromItemName(String name) {
        for (BoosterTypes type : values()) {
            if (type.getItemName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public static BoosterTypes fromName(String name) {
        for (BoosterTypes type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public String getItemName() {
        return this.itemName;
    }

}
