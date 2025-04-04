package org.saberdev.corex.addons;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.saberdev.corex.CoreAddon;
import org.saberdev.corex.CoreX;

import java.util.List;

@CoreAddon(configVariable = "Anti-Block-Placemen")
public class AntiBlockPlace implements Listener {

    private final List<String> deniedMatList = CoreX.getConfig().fetchStringList("Denied-Blocks");


    @EventHandler
    public void onDeniedPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if(!player.isOp() && deniedMatList.contains(XMaterial.matchXMaterial(e.getBlockPlaced().getType().name()).get().name())) {
            e.setCancelled(true);
        }
    }
}