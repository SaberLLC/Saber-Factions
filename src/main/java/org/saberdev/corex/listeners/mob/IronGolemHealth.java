package org.saberdev.corex.listeners.mob;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.saberdev.corex.CoreX;

public class IronGolemHealth implements Listener {

    //Changes the health of the iron golem so it dies faster. I can't think of a class name...

    @EventHandler
    public void onIronGolemSpawn(CreatureSpawnEvent event) {
        if (event.getEntity().getType() != EntityType.IRON_GOLEM)
            return;

        event.getEntity().setHealth(CoreX.getConfig().fetchDouble("ironGolemHealth"));
    }
}
