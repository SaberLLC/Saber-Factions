package pw.saber.corex.listeners;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import pw.saber.corex.CoreX;

import java.util.Map;

public class BlockedEnchantments implements Listener {

    @EventHandler
    public void onEnchantItem(EnchantItemEvent e) {
        Map<Enchantment, Integer> enchants = e.getEnchantsToAdd();
        try {
            for (String enchant : CoreX.getConfig().fetchStringList("BlockedEnchants")) {
                String[] parse = enchant.split(":");
                Enchantment enchantment = Enchantment.getByName(parse[0]);
                int level = Integer.parseInt(parse[1]);
                if ((enchants.containsKey(enchantment)) && (enchants.get(enchantment) > level)) {
                    enchants.remove(enchantment);
                    if (level > 0) {
                        enchants.put(enchantment, level);
                    }
                }
            }
        } catch (Exception ex) {
        }
    }
}
