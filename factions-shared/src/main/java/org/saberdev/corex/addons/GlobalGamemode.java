package org.saberdev.corex.addons;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.saberdev.corex.CoreAddon;

import java.util.Objects;


@CoreAddon(configVariable = "Global-Gamemode")
public class GlobalGamemode implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (!Objects.equals(e.getFrom().getWorld(), Objects.requireNonNull(e.getTo()).getWorld())) {
            final GameMode gm = e.getPlayer().getGameMode();
            final Player p = e.getPlayer();
            if (gm == GameMode.CREATIVE) {
                Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> {
                    if (p.isOnline()) {
                        if (p.getGameMode() != gm) {
                            p.setGameMode(gm);
                        }
                    }
                }, 2L);
            }
        }
    }
}