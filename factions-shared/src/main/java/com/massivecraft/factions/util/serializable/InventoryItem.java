package com.massivecraft.factions.util.serializable;

import com.massivecraft.factions.util.ItemBuilder;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Driftay
 */
public class InventoryItem {
    private ItemStack item;
    private Map<ClickType, Runnable> clickMap;
    private Runnable runnable;

    public InventoryItem(ItemStack original) {
        this.clickMap = new EnumMap<>(ClickType.class);
        this.item = original;
    }

    public InventoryItem(ItemBuilder original) {
        this(original.build());
    }

    public InventoryItem click(ClickType type, Runnable runnable) {
        this.clickMap.put(type, runnable);
        return this;
    }

    public InventoryItem click(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    public void handleClick(InventoryClickEvent event) {
        if (clickMap.isEmpty() && runnable != null) {
            runnable.run();
        } else {
            Runnable found = this.clickMap.get(event.getClick());
            if (found != null) {
                found.run();
            }
        }
    }

    public ItemStack getItem() {
        return this.item;
    }

    public Map<ClickType, Runnable> getClickMap() {
        return this.clickMap;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }
}
