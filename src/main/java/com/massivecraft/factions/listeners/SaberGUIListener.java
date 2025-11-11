package com.massivecraft.factions.listeners;

import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.util.ReflectionUtils;
import com.massivecraft.factions.util.SaberGUI;
import com.massivecraft.factions.util.SaberGUIHolder;
import com.massivecraft.factions.util.serializable.InventoryItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SaberGUIListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory == null) return;

        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof SaberGUIHolder) {
            SaberGUI active = ((SaberGUIHolder) holder).getGUI();
            if (active == null) return;

            event.setCancelled(true);

            int raw = event.getRawSlot();
            int topSize = inventory.getSize();
            if (raw >= 0 && raw < topSize) {
                Map<Integer, InventoryItem> items = active.getInventoryItems();
                if (items != null) {
                    InventoryItem item = items.get(raw);
                    if (item != null) {
                        try {
                            item.handleClick(event);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
            } else {
                try {
                    active.onUnknownItemClick(event);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            return;
        }

        String title = ReflectionUtils.resolveInventoryTitleCompat(event);
        if (title != null && SaberGUI.allGUINames.contains(title)) {
            event.setCancelled(true);
            Logger.print("Cancelling Inventory CLICKED: " + title + " DUE TO IT NOT BEING TRACKED FOR " + event.getWhoClicked().getName() + ", MASSIVE LAG??", Logger.PrefixType.WARNING);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() == null) return;
        try {
            SaberGUI active = SaberGUI.getActiveGUI(event.getPlayer().getUniqueId());
            if (active != null) {
                try {
                    active.onInventoryClose();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                SaberGUI.removeGUI(event.getPlayer().getUniqueId());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            SaberGUI active = SaberGUI.getActiveGUI(event.getPlayer().getUniqueId());
            if (active != null) {
                try {
                    active.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                } finally {
                    SaberGUI.removeGUI(event.getPlayer().getUniqueId());
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        List<SaberGUI> snapshot = new ArrayList<>(SaberGUI.activeGUIs.values());
        for (SaberGUI active : snapshot) {
            try {
                if (!event.getPlugin().getName().equals(active.getOwningPluginName())) continue;
                Logger.print("Closing GUI due to " + event.getPlugin().getName() + " disabling!", Logger.PrefixType.WARNING);
                try {
                    active.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}