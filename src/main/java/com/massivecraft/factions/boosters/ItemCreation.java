package com.massivecraft.factions.boosters;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.factions.zcore.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemCreation {

    public static ItemStack createBoosterItem(BoosterTypes type, int duration, double multiplier) {
        ItemStack itemStack = new ItemStack(XMaterial.matchXMaterial(FactionsPlugin.getInstance().getConfig().getString("Boosters.BoosterItem.Type")).get().parseMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(CC.translate(FactionsPlugin.getInstance().getConfig().getString("Boosters.BoosterItem.Name").replace("{boosterType}", type.getItemName())));
        List<String> configLore = FactionsPlugin.getInstance().getConfig().getStringList("Boosters.BoosterItem.Lore");
        List<String> lore = new ArrayList<>();
        for (String s : configLore) {
            lore.add(CC.translate(s).replace("{duration}", TimeUtil.formatDifference(duration)).replace("{multiplier}", String.valueOf(multiplier)));
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("BoosterType", type.getName());
        nbtItem.setDouble("Multiplier", multiplier);
        nbtItem.setInteger("Duration", duration);
        return nbtItem.getItem();
    }

}

