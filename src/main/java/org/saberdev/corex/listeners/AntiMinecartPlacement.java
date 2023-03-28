package org.saberdev.corex.listeners;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class AntiMinecartPlacement implements Listener {

    @EventHandler
    public void onBoatPlace(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getItemInHand().getType() == XMaterial.MINECART.parseMaterial()) {
            event.setCancelled(true);
        }
    }
}
