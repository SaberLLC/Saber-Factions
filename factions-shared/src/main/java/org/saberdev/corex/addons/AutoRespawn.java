package org.saberdev.corex.addons;

import com.massivecraft.factions.FactionsPlugin;
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
        FactionsPlugin.getScheduler().runEntityLater(player, 2L, () -> player.spigot().respawn(), null);
    }
}