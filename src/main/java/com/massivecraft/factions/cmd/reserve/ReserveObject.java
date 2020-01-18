package com.massivecraft.factions.cmd.reserve;

/**
 * @author Saser
 */
public class ReserveObject {
    private String name;
    private String factionName;

    public ReserveObject(String name, String factionName) {
        this.name = name;
        this.factionName = factionName;
    }

    public String getName() {
        return this.name;
    }

    public String getFactionName() {
        return this.factionName;
    }
}
