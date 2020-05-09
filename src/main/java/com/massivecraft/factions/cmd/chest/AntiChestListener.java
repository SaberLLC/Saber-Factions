package com.massivecraft.factions.cmd.chest;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AntiChestListener implements Listener {

    /**
     * @author Driftay
     */

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (!fPlayer.isInFactionsChest()) return;

        if (e.isCancelled()) return;

        Inventory clicked = e.getClickedInventory();
        Inventory clicker = e.getWhoClicked().getInventory();

        if (e.getClick().isShiftClick()) {
            if (clicked == clicker) {
                ItemStack clickedOn = e.getCurrentItem();
                if (clickedOn != null && FactionsPlugin.getInstance().itemList.contains(clickedOn.getType().toString())) {
                    fPlayer.msg(TL.CHEST_ITEM_DENIED_TRANSFER, clickedOn.getType().toString());
                    e.setCancelled(true);
                }
            }
        }

        if (clicked != clicker) {
            ItemStack onCursor = e.getCursor();
            if (onCursor != null && FactionsPlugin.getInstance().itemList.contains(onCursor.getType().toString())) {
                fPlayer.msg(TL.CHEST_ITEM_DENIED_TRANSFER, onCursor.getType().toString());
                e.setCancelled(true);
            } else if (e.getClick().isKeyboardClick()) {
                ItemStack item = clicker.getItem(e.getHotbarButton());
                if (item != null && FactionsPlugin.getInstance().itemList.contains(item.getType().toString())) {
                    fPlayer.msg(TL.CHEST_ITEM_DENIED_TRANSFER, item.getType().toString());
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        Player p = (Player) e.getWhoClicked();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(p);

        if (!fPlayer.isInFactionsChest()) return;
        if (e.isCancelled()) return;

        ItemStack dragged = e.getOldCursor();
        if (FactionsPlugin.getInstance().itemList.contains(dragged.getType().toString())) {
            int inventorySize = e.getInventory().getSize();
            for (int i : e.getRawSlots()) {
                if (i < inventorySize) {
                    fPlayer.msg(TL.CHEST_ITEM_DENIED_TRANSFER, dragged.getType().toString());
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }
}
