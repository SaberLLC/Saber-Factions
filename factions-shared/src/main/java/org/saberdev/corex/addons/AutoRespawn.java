package org.saberdev.corex.addons;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.saberdev.corex.CoreAddon;


@CoreAddon(configVariable = "Auto-Respawn")
public class AutoRespawn implements Listener {

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> {
            if (player.isOnline()) {
                player.spigot().respawn();
            }
        }, 2L);
    }
}