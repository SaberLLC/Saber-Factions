package com.massivecraft.factions.util.serializable;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;

/**
 * A "clickable" item wrapper which stores a callback to run when clicked.
 * Uses composition instead of extending ItemStack, so we can avoid
 * collisions with new Paper API methods.
 */
public class ClickableItemStack {

    private final ItemStack item;
    private Consumer<InventoryClickEvent> itemCallback;

    /**
     * Create a new ClickableItemStack from an existing ItemStack.
     * @param base The base ItemStack to wrap
     */
    public ClickableItemStack(ItemStack base) {
        // clone to avoid modifying the original reference
        this.item = base.clone();
    }

    /**
     * Set the click callback for this item.
     * @param callback a Consumer which handles InventoryClickEvent
     * @return this ClickableItemStack for chaining
     */
    public ClickableItemStack setClickCallback(Consumer<InventoryClickEvent> callback) {
        this.itemCallback = callback;
        return this;
    }

    /**
     * Set the display name of the item.
     * @param name The new display name
     * @return this ClickableItemStack for chaining
     */
    public ClickableItemStack setDisplayName(String name) {
        ItemMeta im = this.item.getItemMeta();
        if (im != null) {
            im.setDisplayName(name);
            this.item.setItemMeta(im);
        }
        return this;
    }

    /**
     * Set the lore of the item. (Replaces the entire lore)
     * @param lore The list of strings to set as the lore
     * @return this ClickableItemStack for chaining
     */
    public ClickableItemStack setLore(List<String> lore) {
        ItemMeta im = this.item.getItemMeta();
        if (im != null) {
            im.setLore(lore);
            this.item.setItemMeta(im);
        }
        return this;
    }

    /**
     * Set the item damage/durability. (For older item-based damage)
     * @param dura the short durability value
     * @return this ClickableItemStack for chaining
     */
    public ClickableItemStack setDura(short dura) {
        // For older versions of Minecraft that use durability
        this.item.setDurability(dura);
        return this;
    }

    /**
     * @return the callback for this item, or null if none set
     */
    public Consumer<InventoryClickEvent> getItemCallback() {
        return this.itemCallback;
    }

    /**
     * Retrieve the underlying ItemStack for adding to inventories, etc.
     * @return the internal ItemStack
     */
    public ItemStack getItemStack() {
        return this.item;
    }
}
