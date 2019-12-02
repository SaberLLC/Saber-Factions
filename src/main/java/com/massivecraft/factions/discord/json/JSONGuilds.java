package com.massivecraft.factions.discord.json;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JSONGuilds {

    /**
     * @author Driftay
     */

    private Map<String, JSONGuild> guilds;

    public JSONGuilds() {
        this.guilds = new ConcurrentHashMap<>();
    }

    public JSONGuild getGuildById(String id) {
        JSONGuild[] newGuild = new JSONGuild[1];
        return guilds.computeIfAbsent(id, i -> {
            newGuild[0] = new JSONGuild();
            guilds.put(i, newGuild[0]);
            return newGuild[0];
        });
    }

    public Map<String, JSONGuild> getAllGuilds() {
        return this.guilds;
    }
}
