package com.massivecraft.factions.cmd.roster.struct;

import com.massivecraft.factions.Faction;

import java.util.UUID;

/**
 * @Author: Driftay
 * @Date: 7/21/2024 6:29 PM
 */
public class RosterPlayerManager {

    public static RosterPlayer getRosterPlayerFromUUID(UUID uuid, Faction faction) {
        return faction.getRoster().stream().filter(rp -> rp.getUuid().equals(uuid)).findFirst().orElse(null);
    }
}
