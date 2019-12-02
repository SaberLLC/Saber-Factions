package com.massivecraft.factions.zcore.fupgrades;

public enum UpgradeType {

    /**
     * @author Illyria Team
     */

    CHEST("Chest"),
    SPAWNER("Spawner"),
    EXP("Exp"),
    CROP("Crop"),
    POWER("Power"),
    REDSTONE("Redstone"),
    MEMBERS("Members"),
    TNT("TNT"),
    WARP("Warps"),
    DAMAGEINCREASE("DamageIncrease"),
    DAMAGEDECREASE("DamageDecrease"),
    REINFORCEDARMOR("ReinforcedArmor");

    private String id;

    UpgradeType(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
