package pw.saber.corex.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.Cooldown;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pw.saber.corex.CoreX;

import javax.swing.*;

public class EnderPearlCooldown implements Listener {


    @EventHandler
    public void onPearl(PlayerInteractEvent e){
        Action action = e.getAction();
        Player player = e.getPlayer();
        if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem() != null && e.getItem().getType() == XMaterial.ENDER_PEARL.parseMaterial()) {
                if (Cooldown.isOnCooldown(player, "enderPearlCooldown")) {
                    e.setCancelled(true);
                    long remaining = player.getMetadata("enderPearlCooldown").get(0).asLong() - System.currentTimeMillis();
                    int remainSec = (int) (remaining / 1000L);
                    e.getPlayer().sendMessage(CC.translate(TL.ENDER_PEARL_COOLDOWN.toString().replace("{seconds}", TimeUtil.formatSeconds(remainSec))));
                } else {
                    Cooldown.setCooldown(player, "enderPearlCooldown", CoreX.getConfig().fetchInt("Cooldowns.EnderPearl"));
                }
            }
        }
    }
}
