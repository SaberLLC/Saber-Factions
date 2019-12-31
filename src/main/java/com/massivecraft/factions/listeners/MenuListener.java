package com.massivecraft.factions.listeners;

import com.massivecraft.factions.util.serializable.ClickableItemStack;
import com.massivecraft.factions.util.serializable.GUIMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Saser
 */
public class MenuListener implements Listener {

    public MenuListener() {
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Faction Logs")) {
            event.setCancelled(true);
        }

        Player player = (Player) event.getWhoClicked();
        GUIMenu menu = GUIMenu.getMenus().get(player.getUniqueId());
        if (menu != null) {
            event.setCancelled(true);
            if (!menu.getName().equals(event.getView().getTitle())) {
                event.getView().close();
                return;
            }

            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            if (event.getRawSlot() >= event.getInventory().getSize()) return;
            ClickableItemStack found = menu.getMenuItems().get(event.getRawSlot());
            if (found != null && found.getType() == item.getType() && found.getDurability() == item.getDurability()) {
                if (found.getItemCallback() == null) {
                    return;
                }
                found.getItemCallback().accept(event);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        GUIMenu menu = GUIMenu.getMenus().remove(event.getPlayer().getUniqueId());
        if (menu != null && menu.getCloseCallback() != null) {
            menu.getCloseCallback().accept(event);
        }
    }

    @EventHandler
    public void onPLayerLeave(PlayerQuitEvent event) {
        GUIMenu menu = GUIMenu.getMenus().remove(event.getPlayer().getUniqueId());
        if (menu != null && menu.getCloseCallback() != null) {
            menu.getCloseCallback().accept(new InventoryCloseEvent(event.getPlayer().getOpenInventory()));
        }
    }
}
