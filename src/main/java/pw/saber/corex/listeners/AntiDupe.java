package pw.saber.corex.listeners;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class AntiDupe implements Listener {


    @EventHandler
    public void playerVaultDupeGlitch(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/playervault") || event.getMessage().startsWith("/pv")) {
            Player player = event.getPlayer();
            Block headLocation = player.getEyeLocation().getBlock();
            if (headLocation != null) {
                Material type = headLocation.getType();
                if (type == XMaterial.LILY_PAD.parseMaterial() || type.name().contains("TRAPDOOR")) {
                    event.setCancelled(true);
                }
            }
            Block bodyLocation = player.getLocation().getBlock();
            if (bodyLocation != null) {
                Material type = bodyLocation.getType();
                if (type == XMaterial.LILY_PAD.parseMaterial() || type.name().contains("TRAPDOOR")) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
