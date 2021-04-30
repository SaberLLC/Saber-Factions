package com.massivecraft.factions.cloak.struct;

/**
 * @author Saser
 */
public enum CloakType {

    NORMAL("NORMAL", "Cloak");

    String name;
    String itemName;

    CloakType(String name, String itemName) {
        this.name = name;
        this.itemName = itemName;
    }

    public static CloakType fromItemName(String name) {
        for(CloakType type : values()) {
            if(type.getItemName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public static CloakType fromName(String name) {
        for(CloakType type : values()){
            if(type.getName().equals(name)) {
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
