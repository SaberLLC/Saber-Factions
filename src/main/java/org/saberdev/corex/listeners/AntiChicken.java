package org.saberdev.corex.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;

/**
 * @Author: Driftay
 * @Date: 3/28/2023 12:38 PM
 */
public class AntiChicken implements Listener {

    @EventHandler
    public void onEgg(PlayerEggThrowEvent e) {
        e.setHatching(false);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG)
            event.setCancelled(true);
    }
}
