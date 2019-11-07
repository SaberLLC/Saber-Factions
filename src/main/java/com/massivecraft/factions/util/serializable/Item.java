package com.massivecraft.factions.util.serializable;


import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.XMaterial;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Item {
    private String name;
    private List<String> lore;
    private XMaterial material;
    private int amount;
    private int slot;

    public Item(String name, List<String> lore, XMaterial material, int amount, int slot) {
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.amount = amount;
        this.slot = slot;
    }

    public ItemStack buildItemStack(boolean isSelected) {
        return new ItemBuilder(material.parseItem()).name(name).lore(lore).glowing(isSelected).amount(amount).build();
    }

    public int getAmount() {
        return this.amount;
    }

    public String getName() {
        return this.name;
    }

    public int getSlot() {
        return this.slot;
    }
}
