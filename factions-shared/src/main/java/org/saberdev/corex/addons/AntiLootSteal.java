package org.saberdev.corex.addons;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.saberdev.corex.CoreAddon;
import org.saberdev.corex.CoreX;

import java.util.List;
import java.util.UUID;

/**
 * @Author: Driftay
 * @Date: 4/1/2024 9:36 AM
 */
@CoreAddon(configVariable = "Anti-Loot-Steal")
public class AntiLootSteal implements Listener {
    private final List<String> disabledWorldNames = CoreX.getConfig().fetchStringList("Anti-Loot-Steal.Disabled-Worlds");


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(disabledWorldNames.contains(event.getEntity().getWorld().getName())) return;

        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getPlayer().getKiller();
            Player player = event.getEntity().getPlayer();

            for (ItemStack stack : event.getDrops()) {
                Item item = player.getWorld().dropItemNaturally(player.getLocation(), stack);
                long time = System.currentTimeMillis();
                item.setMetadata("antiLoot_uuid", new FixedMetadataValue(FactionsPlugin.getInstance(), killer.getUniqueId()));
                item.setMetadata("antiLoot_time", new FixedMetadataValue(FactionsPlugin.getInstance(), time));
            }
            event.getDrops().clear();
        }
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onItemPickup(PlayerPickupItemEvent event) {
        if(disabledWorldNames.contains(event.getPlayer().getWorld().getName())) return;

        if (event.getItem().hasMetadata("antiLoot_uuid")) {
            Player player = event.getPlayer();
            if (!player.hasPermission("anti.loot.bypass")) {
                UUID killerUUID = UUID.fromString(event.getItem().getMetadata("antiLoot_uuid").get(0).asString());
                long timeDifference = System.currentTimeMillis() - event.getItem().getMetadata("antiLoot_time").get(0).asLong();
                if (player.getUniqueId().equals(killerUUID)) return;

                int pickupDelayInMiliseconds = CoreX.getConfig().fetchInt("Anti-Loot-Steal.Protection-Time") * 1000;
                if (timeDifference >= pickupDelayInMiliseconds) return;

                event.setCancelled(true);
                if (!player.hasMetadata("antiLoot_nextWarning") || player.getMetadata("antiLoot_nextWarning").get(0).asLong() < System.currentTimeMillis()) {
                    player.setMetadata("antiLoot_nextWarning", new FixedMetadataValue(FactionsPlugin.getInstance(), System.currentTimeMillis() + 10000L));
                    player.sendMessage(TextUtil.parse(TL.ANTI_LOOT_PICKUP_FAILED.toString().replace("{seconds}", String.valueOf((pickupDelayInMiliseconds - timeDifference) / 1000L))));
                }
            }
        }
    }
}
