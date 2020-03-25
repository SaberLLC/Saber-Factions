package com.massivecraft.factions.util;


import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private ItemMeta meta;
    private ItemStack item;

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
        List<String> colored = new ArrayList<>();
        for (String line : string) {
            colored.add(FactionsPlugin.instance.color(line));
        }
        return colored;
    }

    public ItemBuilder durability(short durability) {
        this.item.setDurability(durability);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        if (lore != null) {
            ArrayList<String> arrayList = new ArrayList<>();
            for (String line : lore) {
                arrayList.add(FactionsPlugin.instance.color(line));
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
        this.meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
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
            this.meta.addEnchant(Enchantment.DURABILITY, 1, true);
        } else {
            this.meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            this.meta.removeEnchant(Enchantment.DURABILITY);
        }
        return this;
    }

    public ItemBuilder addLineToLore(String line) {
        List<String> lore = this.meta.getLore();
        lore.add(FactionsPlugin.instance.color(line));
        this.meta.setLore(lore);
        return this;
    }
}
