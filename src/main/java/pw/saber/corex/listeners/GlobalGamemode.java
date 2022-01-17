package pw.saber.corex.listeners;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class GlobalGamemode implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (!e.getFrom().getWorld().equals(e.getTo().getWorld())) {
            final GameMode gm = e.getPlayer().getGameMode();
            final Player p = e.getPlayer();
            if (gm == GameMode.CREATIVE) {
                Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> {
                    if (p.getGameMode() != gm)
                        p.setGameMode(gm);
                }, 2L);
            }
        }
    }
}
