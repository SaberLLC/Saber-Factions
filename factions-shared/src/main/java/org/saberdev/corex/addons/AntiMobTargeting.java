package org.saberdev.corex.addons;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.saberdev.corex.CoreAddon;
import org.saberdev.corex.CoreX;

import java.util.List;


@CoreAddon(configVariable = "Anti-Mob-Targeting")
public class AntiMobTargeting implements Listener {

    public List<String> entList = CoreX.getConfig().fetchStringList("Anti-Mob-Targeting.Mob-List");

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityTarget(EntityTargetEvent event) {
        if (entList.isEmpty()) return;
        if (!entList.contains(event.getEntity().getType().toString())) return;
        event.setCancelled(true);
    }
}
