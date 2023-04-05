package org.saberdev.corex.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Driftay
 * @Date: 3/28/2023 12:31 PM
 */
public class AutoLapisEnchant implements Listener {

    private final List<EnchantingInventory> inventories = new ArrayList<>();

    private final ItemStack lapis = XMaterial.LAPIS_LAZULI.parseItem();

    @EventHandler
    public void openInventoryEvent(InventoryOpenEvent e) {
        Inventory i = e.getInventory();
        if (i instanceof EnchantingInventory) {
            i.setItem(1, this.lapis);
            this.inventories.add((EnchantingInventory) i);
        }
    }

    @EventHandler
    public void closeInventoryEvent(InventoryCloseEvent e) {
        Inventory i = e.getInventory();
        if (i instanceof EnchantingInventory &&
                this.inventories.contains(i)) {
            i.setItem(1, null);
            this.inventories.remove(i);
        }
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent e) {
        Inventory i = e.getClickedInventory();
        if (i instanceof EnchantingInventory &&
                this.inventories.contains(i) && e.getSlot() == 1)
            e.setCancelled(true);
    }

    @EventHandler
    public void enchantItemEvent(EnchantItemEvent e) {
        Inventory i = e.getInventory();
        if (i instanceof EnchantingInventory &&
                this.inventories.contains(i))
            e.getInventory().setItem(1, this.lapis);
    }
}
