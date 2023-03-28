package org.saberdev.corex.listeners;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AntiBookQuillCrash implements Listener {

    private static Material WRITTABLE_BOOK = XMaterial.WRITABLE_BOOK.parseMaterial();

    @EventHandler
    public void onAttemptCrash(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if (item != null && item.getType() == WRITTABLE_BOOK) {
            event.setCancelled(true);
        }
    }
}
