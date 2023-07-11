package org.saberdev.corex.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.util.Lazy;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AntiPistonGlitch implements Listener {

    private final Lazy<Set<Material>> materials = Lazy.of(() -> Collections.unmodifiableSet(new HashSet<Material>(){{
        add(XMaterial.SUGAR_CANE.parseMaterial());
        add(XMaterial.MELON.parseMaterial());
        add(XMaterial.MELON_STEM.parseMaterial());
        add(XMaterial.GLISTERING_MELON_SLICE.parseMaterial());
    }}));

    @EventHandler
    public void onRetract(BlockPistonExtendEvent event) {
        BlockFace direction = event.getDirection();
        Block to = event.getBlock().getRelative(direction);
        Block nextBlock = to.getRelative(direction);

        Set<Material> against = this.materials.get();
        if (against.contains(nextBlock.getType()) || against.contains(to.getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFluxPatch(BlockPistonExtendEvent event) {
        BlockFace direction = event.getDirection();
        Block to = event.getBlock().getRelative(direction);

        String toBlockName = to.getType().toString();
        String nextBlockName = to.getRelative(direction).getType().toString();

        if ((toBlockName.endsWith("_GATE") || toBlockName.endsWith("_FENCE"))
                || (nextBlockName.endsWith("_GATE")
                || nextBlockName.endsWith("_FENCE"))) {
            event.setCancelled(true);
        }
    }
}