package pw.saber.corex.listeners;

import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.Cooldown;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pw.saber.corex.CoreX;

public class EnderPearlCooldown implements Listener {


    @EventHandler
    public void onPearl(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.ENDER_PEARL) {
            return;
        }

        if (Cooldown.isOnCooldown(player, "enderPearlCooldown")) {
            event.setCancelled(true);
            long remaining = player.getMetadata("enderPearlCooldown").get(0).asLong() - System.currentTimeMillis();
            int remainingSeconds = (int) (remaining / 1000L);
            player.sendMessage(CC.translate(TL.ENDER_PEARL_COOLDOWN.toString().replace("{seconds}", TimeUtil.formatSeconds(remainingSeconds))));
        } else {
            Cooldown.setCooldown(player, "enderPearlCooldown", CoreX.getConfig().fetchInt("Cooldowns.EnderPearl"));
        }
    }
}
