package com.massivecraft.factions.util;


import com.cryptomorin.xseries.XEnchantment;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private final ItemMeta meta;
    private final ItemStack item;

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public static List<String> color(List<String> string) {
        List<String> colored = new ArrayList<>(string.size());
        for (String line : string) {
            colored.add(TextUtil.parse(line));
        }
        return colored;
    }

    public ItemBuilder durability(short durability) {
        this.item.setDurability(durability);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        if (lore != null) {
            ArrayList<String> arrayList = new ArrayList<>(lore.length);
            for (String line : lore) {
                arrayList.add(TextUtil.parse(line));
            }
            this.meta.setLore(arrayList);
        }
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        this.meta.setLore(color(lore));
        return this;
    }

    public ItemBuilder name(String name) {
        this.meta.setDisplayName(TextUtil.parse(name));
        return this;
    }

    public ItemStack build() {
        this.item.setItemMeta(this.meta);
        return this.item;
    }

    public ItemBuilder amount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder glowing(boolean status) {
        if (status) {
            this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            this.meta.addEnchant(XEnchantment.UNBREAKING.getEnchant(), 1, true);
        } else {
            this.meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            this.meta.removeEnchant(XEnchantment.UNBREAKING.getEnchant());
        }
        return this;
    }

    public ItemBuilder addLineToLore(String line) {
        List<String> lore = this.meta.getLore();
        lore.add(TextUtil.parse(line));
        this.meta.setLore(lore);
        return this;
    }
}
