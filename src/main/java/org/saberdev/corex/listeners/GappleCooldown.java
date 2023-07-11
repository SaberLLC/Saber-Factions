package org.saberdev.corex.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.Cooldown;
import com.massivecraft.factions.util.Lazy;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.saberdev.corex.CoreX;

public class GappleCooldown implements Listener {

    private final Lazy<Material> enchantedGoldenApple = Lazy.of(XMaterial.ENCHANTED_GOLDEN_APPLE::parseMaterial);

    @EventHandler
    public void onEatGapple(PlayerItemConsumeEvent e){
        if(e.getItem().getType() == this.enchantedGoldenApple.get()) {
            if(Cooldown.isOnCooldown(e.getPlayer(), "godAppleCooldown")) {
                e.setCancelled(true);
                long remaining = e.getPlayer().getMetadata("godAppleCooldown").get(0).asLong() - System.currentTimeMillis();
                int remainSec = (int) (remaining / 1000L);
                e.getPlayer().sendMessage(CC.translate(TL.GOD_APPLE_COOLDOWN.toString().replace("{seconds}", TimeUtil.formatSeconds(remainSec))));
            } else {
                Cooldown.setCooldown(e.getPlayer(), "godAppleCooldown", CoreX.getConfig().fetchInt("Cooldowns.God_Apple"));
            }
        }
    }
}
