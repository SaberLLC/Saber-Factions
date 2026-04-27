package org.saberdev.corex.addons;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.saberdev.corex.CoreAddon;
import org.saberdev.corex.CoreX;

import java.util.Map;


@CoreAddon(configVariable = "Blocked-Enchantments")
public class BlockedEnchantments implements Listener {

    @EventHandler
    public void onEnchantItem(EnchantItemEvent e) {
        Map<Enchantment, Integer> enchants = e.getEnchantsToAdd();

        try {
            for (String enchant : CoreX.getConfig().fetchStringList("BlockedEnchants")) {
                String[] parse = enchant.split(":");

                Enchantment enchantment = Enchantment.getByName(parse[0]);
                int level = Integer.parseInt(parse[1]);

                Integer found = enchants.get(enchantment);
                if (found != null && found > level && level > 0) {
                    enchants.put(enchantment, level);
                }
            }
        } catch (NumberFormatException ignored) {}
    }
}