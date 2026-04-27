package org.saberdev.corex.addons;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.saberdev.corex.CoreAddon;

/**
 * @Author: Driftay
 * @Date: 3/28/2023 12:38 PM
 */
@CoreAddon(configVariable = "Anti-Chicken")
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
