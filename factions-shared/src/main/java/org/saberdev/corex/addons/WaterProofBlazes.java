package org.saberdev.corex.addons;

import org.bukkit.entity.Blaze;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.saberdev.corex.CoreAddon;

@CoreAddon(configVariable = "Water-Proof-Blazes")

public class WaterProofBlazes implements Listener {

    @EventHandler
    public void onBlazeDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Blaze && e.getCause().equals(EntityDamageEvent.DamageCause.DROWNING)) {
            e.setCancelled(true);
        }
    }
}
