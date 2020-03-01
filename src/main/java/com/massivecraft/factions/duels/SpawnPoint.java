package com.massivecraft.factions.duels;

import org.bukkit.Location;

public class SpawnPoint {
    private Integer team;
    private Integer id;
    private Location location;

    public SpawnPoint(Integer team, Location location, Integer id) {
        this.team = team;
        this.location = location;
        this.id = id;
    }

    public Integer getTeam() {
        return team;
    }

    public Location getLocation() {
        return location;
    }

    public Integer getID() {
        return id;
    }
}
