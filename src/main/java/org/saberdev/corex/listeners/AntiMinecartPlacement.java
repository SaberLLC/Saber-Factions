package org.saberdev.corex.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AntiMinecartPlacement implements Listener {



    @EventHandler
    public void onBoatPlace(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemInHand = event.getPlayer().getItemInHand();
            if (itemInHand != null && itemInHand.getType() == Material.MINECART) {
                event.setCancelled(true);
            }
        }
    }
}
