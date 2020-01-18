package com.massivecraft.factions.cmd.audit;

/**
 * @author Saser
 */

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class FChestListener implements Listener {

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {

        Player player = (Player) e.getWhoClicked();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (!fPlayer.isInFactionsChest()) return;
        if (e.isCancelled()) return;
        e.setCancelled(true);
        e.getWhoClicked().sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot drag items while viewing a /f chest!");
    }


    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onPlayerClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Faction faction;
        if (!event.getView().getTitle().equalsIgnoreCase(FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fchest.Inventory-Title"))))
            return;
        if (event.getClick() == ClickType.UNKNOWN) {
            event.setCancelled(true);
            player.sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot use that click type inside the /f chest!");
            return;
        }
        ItemStack currentItem = event.getCurrentItem();
        if (event.getClick() == ClickType.NUMBER_KEY)
            currentItem = event.getClickedInventory().getItem(event.getSlot());
        Material currentItemType = currentItem != null ? currentItem.getType() : Material.AIR;
        ItemStack cursorItem = event.getCursor();
        if (event.getClick() == ClickType.NUMBER_KEY)
            cursorItem = player.getInventory().getItem(event.getHotbarButton());
        Material cursorItemType = cursorItem != null ? cursorItem.getType() : Material.AIR;
        if (fPlayer == null || !(faction = fPlayer.getFaction()).isNormal()) {
            player.closeInventory();
            player.sendMessage(CC.RedB + "(!) " + CC.Red + "You are no longer in your faction!");
            return;
        }
        if (event.getClickedInventory() == null) return;
        if (event.getView().getTitle().equalsIgnoreCase(FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fchest.Inventory-Title"))) && !event.getClick().isShiftClick()) {
            if (currentItemType != Material.AIR) {
                Inventory ours = faction.getChestInventory();
                if (event.getClickedInventory() == ours) {
                    if (ours == null || !ours.contains(currentItem)) {
                        event.setCancelled(true);
                        player.sendMessage(CC.RedB + "(!) That item not longer exists!");
                        Bukkit.getLogger().info("[FactionChest] " + player.getName() + " tried to remove " + currentItem + " from /f chest when it didnt contain! Items: " + (ours == null ? "none" : Arrays.toString(ours.getContents())));
                        player.closeInventory();
                        return;
                    }
                }
                logRemoveItem(currentItem, fPlayer, player);
            } else if (cursorItemType != Material.AIR && !event.isShiftClick()) {
                logAddItem(cursorItem, fPlayer, player);
            }
        } else if (event.isShiftClick() && currentItemType != Material.AIR) {
            logAddItem(currentItem, fPlayer, player);
        }
    }

    private void logAddItem(ItemStack cursorItem, FPlayer fplayer, Player player) {
        String itemName = cursorItem.hasItemMeta() && cursorItem.getItemMeta().hasDisplayName() ? cursorItem.getItemMeta().getDisplayName() : StringUtils.capitaliseAllWords(cursorItem.getType().name().replace("_", " ").toLowerCase());
        FactionsPlugin.instance.logFactionEvent(fplayer.getFaction(), FLogType.FCHEST_EDIT, player.getName(), CC.GreenB + "ADDED", itemName);
    }

    private void logRemoveItem(ItemStack currentItem, FPlayer fplayer, Player player) {
        String itemName = currentItem.hasItemMeta() && currentItem.getItemMeta().hasDisplayName() ? currentItem.getItemMeta().getDisplayName() : StringUtils.capitaliseAllWords(currentItem.getType().name().replace("_", " ").toLowerCase());
        FactionsPlugin.instance.logFactionEvent(fplayer.getFaction(), FLogType.FCHEST_EDIT, player.getName(), CC.RedB + "TOOK", itemName);
    }
}
