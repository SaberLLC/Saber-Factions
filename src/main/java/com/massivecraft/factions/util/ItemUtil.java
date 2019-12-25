package com.massivecraft.factions.util;

/**
 * @author Saser
 */
import com.massivecraft.factions.util.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;

public class ItemUtil {
    private static Map<String, ItemStack> cachedSkulls = new HashMap<>();

    public ItemUtil() {
    }

    public static int getItemCount(Inventory inventory) {
        if (inventory == null) {
            return 0;
        } else {
            int itemsFound = 0;

            for(int i = 0; i < inventory.getSize(); ++i) {
                ItemStack item = inventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    ++itemsFound;
                }
            }

            return itemsFound;
        }
    }

    public static ItemStack createPlayerHead(String name) {
        ItemStack skull = cachedSkulls.get(name);
        if (skull != null) {
            return skull.clone();
        } else {
            skull = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial());
            SkullMeta sm = (SkullMeta)skull.getItemMeta();
            sm.setOwner(name);
            skull.setItemMeta(sm);
            cachedSkulls.put(name, skull.clone());
            return skull;
        }
    }
}
