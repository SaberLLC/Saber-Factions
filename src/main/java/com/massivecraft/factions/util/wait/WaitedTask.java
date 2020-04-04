package com.massivecraft.factions.util.wait;

import org.bukkit.entity.Player;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/4/2020
 */
public interface WaitedTask {
    void handleSuccess(Player player);

    void handleFailure(Player player);
}
