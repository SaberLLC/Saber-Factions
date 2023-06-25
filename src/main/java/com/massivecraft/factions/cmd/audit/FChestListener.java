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


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer == null || !fPlayer.getFaction().isNormal() && fPlayer.isInFactionsChest()) {
            player.closeInventory();
            player.sendMessage(CC.RedB + "(!) " + CC.Red + "You are no longer in your faction!");
            return;
        }

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || !event.getView().getTitle().equalsIgnoreCase(CC.translate(FactionsPlugin.getInstance().getConfig().getString("fchest.Inventory-Title")))) {
            return;
        }

        if (event.getClick() == ClickType.UNKNOWN) {
            event.setCancelled(true);
            player.sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot use that click type inside the /f chest!");
            return;
        }

        ItemStack currentItem = event.getClick() == ClickType.NUMBER_KEY ? clickedInventory.getItem(event.getSlot()) : event.getCurrentItem();
        Material currentItemType = currentItem != null ? currentItem.getType() : Material.AIR;

        ItemStack cursorItem = event.getClick() == ClickType.NUMBER_KEY ? player.getInventory().getItem(event.getHotbarButton()) : event.getCursor();
        Material cursorItemType = cursorItem != null ? cursorItem.getType() : Material.AIR;

        Faction faction = fPlayer.getFaction();
        Inventory factionChestInventory = faction.getChestInventory();

        if (event.getView().getTitle().equalsIgnoreCase(CC.translate(FactionsPlugin.getInstance().getConfig().getString("fchest.Inventory-Title"))) && !event.getClick().isShiftClick()) {
            if (currentItemType != Material.AIR) {
                if (factionChestInventory == null || !factionChestInventory.contains(currentItem)) {
                    event.setCancelled(true);
                    player.sendMessage(CC.RedB + "(!) That item no longer exists!");
                    Bukkit.getLogger().info("[FactionChest] " + player.getName() + " tried to remove " + currentItem + " from /f chest when it didn't contain! Items: " + (factionChestInventory == null ? "none" : Arrays.toString(factionChestInventory.getContents())));
                    player.closeInventory();
                    return;
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
