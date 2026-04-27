package org.saberdev.corex.addons;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.saberdev.corex.CoreAddon;


@CoreAddon(configVariable = "")
public class AntiDeathClip implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDeathClipDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getDamager().isDead()) {
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
