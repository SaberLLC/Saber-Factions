package pw.saber.corex.listeners.mob;

import org.bukkit.entity.Blaze;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class WaterProofBlazes implements Listener {

    @EventHandler
    public void onBlazeDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Blaze && e.getCause().equals(EntityDamageEvent.DamageCause.DROWNING)) {
            e.setCancelled(true);
        }
    }
}
