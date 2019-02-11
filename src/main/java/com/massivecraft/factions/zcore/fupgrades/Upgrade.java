package com.massivecraft.factions.zcore.fupgrades;

public enum Upgrade {

    CHEST("Chest"), SPAWNER("Spawner"), EXP("Exp"), CROP("Crop");


    private String id;

    Upgrade(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
