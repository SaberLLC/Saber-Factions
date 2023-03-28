package org.saberdev.corex.listeners;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.saberdev.corex.CoreX;

import java.util.EnumSet;
import java.util.Set;

public class AntiBlockPlace implements Listener {

    private final Set<Material> deniedMatList = EnumSet.noneOf(Material.class);

    public AntiBlockPlace() {
        for (String attempt : CoreX.getConfig().fetchStringList("Denied-Blocks")) {
            XMaterial.matchXMaterial(attempt).map(XMaterial::parseMaterial).ifPresent(deniedMatList::add);
        }
    }

    @EventHandler
    public void onDeniedPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if(!player.isOp() && deniedMatList.contains(e.getBlockPlaced().getType())) {
            e.setCancelled(true);
        }
    }
}