package org.saberdev.corex.addons;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saberdev.corex.CoreAddon;

@CoreAddon(configVariable = "Anti-Boat-Placement")
public class AntiBoatPlacement implements Listener {

    @EventHandler
    public void onBoatPlace(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getItemInHand().getType().name().contains("BOAT")) {
            event.setCancelled(true);
        }
    }
}
