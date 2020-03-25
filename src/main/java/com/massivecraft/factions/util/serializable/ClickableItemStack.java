package com.massivecraft.factions.util.serializable;

/**
 * @author Saser
 */

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;

public class ClickableItemStack extends ItemStack {
    private Consumer<InventoryClickEvent> itemCallback;

    public ClickableItemStack(ItemStack clone) {
        super(clone);
    }

    public ClickableItemStack setClickCallback(Consumer<InventoryClickEvent> callback) {
        this.itemCallback = callback;
        return this;
    }

    public ClickableItemStack setDisplayName(String name) {
        ItemMeta im = this.getItemMeta();
        im.setDisplayName(name);
        this.setItemMeta(im);
        return this;
    }


    public ClickableItemStack setLore(List<String> lore) {
        ItemMeta im = this.getItemMeta();
        im.setLore(lore);
        this.setItemMeta(im);
        return this;
    }

    public ClickableItemStack setDura(short dura) {
        this.setDurability(dura);
        return this;
    }

    public Consumer<InventoryClickEvent> getItemCallback() {
        return this.itemCallback;
    }
}
