package org.saberdev.corex.listeners;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class AntiPistonGlitch implements Listener {

    @EventHandler
    public void onRetract(BlockPistonExtendEvent event) {
        Block to = event.getBlock().getRelative(event.getDirection());
        Block nextBlock = to.getRelative(event.getDirection());
        if (nextBlock.getType() == XMaterial.SUGAR_CANE.parseMaterial()
                || nextBlock.getType() == XMaterial.MELON.parseMaterial()
                || nextBlock.getType() == XMaterial.MELON_STEM.parseMaterial() || nextBlock.getType() == XMaterial.GLISTERING_MELON_SLICE.parseMaterial()) {
            event.setCancelled(true);
        }
        if (to.getType() == XMaterial.SUGAR_CANE.parseMaterial()
                || (to.getType() == XMaterial.MELON.parseMaterial())
                || to.getType() == XMaterial.MELON_STEM.parseMaterial() || to.getType() == XMaterial.GLISTERING_MELON_SLICE.parseMaterial()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFluxPatch(BlockPistonExtendEvent event) {
        Block to = event.getBlock().getRelative(event.getDirection());
        Block nextBlock = to.getRelative(event.getDirection());
        if ((to.getType().toString().endsWith("_GATE") || to.getType().toString().endsWith("_FENCE"))
                || (nextBlock.getType().toString().endsWith("_GATE")
                || nextBlock.getType().toString().endsWith("_FENCE"))) {
            event.setCancelled(true);
        }
    }
}


