package org.saberdev.corex.addons;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.saberdev.corex.CoreAddon;

@CoreAddon(configVariable = "Anti-Nether-Roof")
public class AntiNetherRoof implements Listener {

    @EventHandler
    public void onBlockPistonExtendEvent(BlockPistonExtendEvent e) {
        if (e.getBlock().getWorld().getEnvironment() == World.Environment.NETHER && e.getBlock().getY() >= 120 &&
                e.getDirection() == BlockFace.UP)
            for (Block b : e.getBlocks()) {
                if (b.getLocation().getY() >= 124.0D)
                    e.setCancelled(true);
            }
    }
}
