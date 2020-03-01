package com.massivecraft.factions.util.Wait;

import org.bukkit.entity.Player;

/**
 * @author droppinganvil
 */
public interface WaitedTask {
    void handleSuccess(Player player);
    void handleFailure(Player player);
}
