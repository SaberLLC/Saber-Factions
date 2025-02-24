package com.massivecraft.factions.cmd.roster.struct;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Role;

import java.util.UUID;

/**
 * @Author: Driftay
 * @Date: 7/21/2024 2:26 PM
 */
public class RosterPlayer {
    private final UUID uuid;
    private final Role rosterRole;
    private long lastJoinTime;

    public RosterPlayer(UUID uuid, Role rosterRole, long lastJoinTime) {
        this.uuid = uuid;
        this.rosterRole = rosterRole;
        this.lastJoinTime = lastJoinTime;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Role getRole() {
        return rosterRole;
    }

    public long getLastJoinTime() {
        return lastJoinTime;
    }

    public void setLastJoinTime(long lastJoinTime) {
        this.lastJoinTime = lastJoinTime;
    }

    public boolean isOnJoinCooldown() {
        int cooldown = FactionsPlugin.getInstance().getFileManager().getRoster().fetchInt("join-faction-cooldown");
        if(cooldown <= 0) return false;
        return System.currentTimeMillis() - lastJoinTime <= 1000L * 60 * cooldown;
        //237812837128 - 0 <= 500
    }

}
