package pw.saber.corex.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class AntiDeathClip implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDeathClipDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager().isDead()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDeathClipCommand(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().isDead()) {
            e.setCancelled(true);
        }
    }
}
