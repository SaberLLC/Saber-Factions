package org.saberdev.corex.addons;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.saberdev.corex.CoreAddon;


@CoreAddon(configVariable = "No-Cursor-Drop")
public class NoCursorDrop implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        Player player = (Player) e.getPlayer();
        if(player.getInventory().firstEmpty() != -1 && !player.isDead() && player.getHealth() > 0.0) {
            ItemStack itemOnCursor = player.getItemOnCursor().clone();
            player.setItemOnCursor(null);
            player.getInventory().addItem(itemOnCursor);
            player.updateInventory();
        }
    }
}
