package com.massivecraft.factions.discord.json;

public class JSONGuild {

    /**
     * @author Driftay
     */

    private String prefix;

    public JSONGuild() {
        this.prefix = null;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
}
