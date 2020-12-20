package com.massivecraft.factions.shop.utils;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FactionsPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemUtils {

    public static ItemStack[] getLootTableItems() {
        List<String> iteration = FactionsPlugin.getInstance().getFileManager().getShop().getConfig().getStringList("items");
        ItemStack[] items = new ItemStack[iteration.size()];
        for (int i = 0; i < iteration.size(); ++i) {
            String l = iteration.get(i);
            items[i] = ItemUtils.getItem(l);
        }
        return items;
    }

    public static ItemStack getItem(String line) {
        return BaseUtils.fromBase64(line.split(":")[0]).getContents()[0];
    }

    public static String getDisplayName(ItemStack item) {
        @SuppressWarnings("deprecation")
        String itemName = (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : StringUtils.capitaliseAllWords(item.getType().name().toLowerCase().replace("_", " "));
        if (item.getType() == Material.ENCHANTED_BOOK) {
            itemName = "Depth Strider Enchantment Book";
        }
        return itemName;
    }

    public static int getCost(String line) {
        String[] split = line.split(":");
        return Integer.parseInt(split[1]);
    }

    public static int getAmount(String line) {
        String[] split = line.split(":");
        return Integer.parseInt(split[2]);
    }
}
