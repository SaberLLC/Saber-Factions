package org.saberdev.corex.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class AntiBowBoosting implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBowBoost(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (!(damager instanceof Arrow))
            return;
        Arrow arrow = (Arrow)damager;
        ProjectileSource shooter = arrow.getShooter();
        if (!(shooter instanceof org.bukkit.entity.Player))
            return;
        if (e.getEntity() != shooter)
            return;
        arrow.setKnockbackStrength(0);
    }
}
