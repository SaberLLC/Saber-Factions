package com.massivecraft.factions.listeners;

import com.massivecraft.factions.util.SaberGUI;
import com.massivecraft.factions.util.serializable.InventoryItem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;


public class SaberGUIListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        SaberGUI active = SaberGUI.getActiveGUI(event.getWhoClicked().getUniqueId());
        if (active != null) {
            event.setCancelled(true);
            if (event.getRawSlot() < event.getInventory().getSize()) {
                int slot = event.getSlot();
                InventoryItem item = active.getInventoryItems().get(slot);
                if (item != null) {
                    item.handleClick(event);
                }
                return;
            }
            active.onUnknownItemClick(event);
        } else if (SaberGUI.allGUINames.contains(event.getView().getTitle())) {
            event.setCancelled(true);
            Bukkit.getLogger().info("Cancelling Inventory CLICKED: " + event.getView().getTitle() + " DUE TO IT NOT BEING TRACKED FOR " + event.getWhoClicked().getName() + ", MASSIVE LAG??");
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        SaberGUI active = SaberGUI.getActiveGUI(event.getPlayer().getUniqueId());
        if (active != null) {
            active.onInventoryClose();
            SaberGUI.removeGUI(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        SaberGUI active = SaberGUI.getActiveGUI(event.getPlayer().getUniqueId());
        if (active != null) {
            active.close();
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        for (SaberGUI active : SaberGUI.activeGUIs.values()) {
            if (!active.getOwningPluginName().equals(event.getPlugin().getName())) continue;
            Bukkit.getLogger().info("Closing GUI due to " + event.getPlugin().getName() + " disabling!");
            try {
                active.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}