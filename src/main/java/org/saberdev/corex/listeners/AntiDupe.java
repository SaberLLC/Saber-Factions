package org.saberdev.corex.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.util.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;

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

    // Bed-Crop-Dupe prevention start
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    private void bedCropDupeGlitch(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
        if (heldItem == null) return;

        // Check if player is holding a bed
        String heldItemTypeName = heldItem.getType().name();
        if (!heldItemTypeName.endsWith("_BED") && !heldItemTypeName.equals("BED")) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return; // Shouldn't be null since we're making sure the action is right click block but just in case

        HashSet<Block> blocksToSearch = new HashSet<>();
        blocksToSearch.addAll(getAdjacentBlocks(clickedBlock.getLocation(), event.getBlockFace()));
        blocksToSearch.addAll(getAdjacentBlocks(clickedBlock.getLocation().add(0,0,1), getYawBlockFace(player))); // Search adjacent to top-side of bed

        for (Block block : blocksToSearch) {
            String blockTypeName = block.getType().name();
            if (
                    blockTypeName.equals("WHEAT")
                    || blockTypeName.equals("CARROTS")
                    || blockTypeName.equals("CARROT")
                    || blockTypeName.equals("NETHER_WARTS")
                    || blockTypeName.equals("POTATOES")
                    || blockTypeName.equals("POTATO")
                    || blockTypeName.equals("BEETROOTS")
            ) {
                event.setCancelled(true);
                Logger.print(
                        "Player " + player.getName() + " tried duplicating crops with beds at" +
                        " X:" + player.getLocation().getBlockX() + " Y:" + player.getLocation().getBlockY() + " Z:" + player.getLocation().getBlockZ() +
                        " in world: " + player.getLocation().getWorld().getName()
                );
                return;
            }
        }
    }

    private HashSet<Block> getAdjacentBlocks(Location clickedBlockLocation, BlockFace blockFace) {
        HashSet<Block> adjacentBlocks = new HashSet<>();
        Location searchLocation = clickedBlockLocation.add(getDirection(blockFace));
        adjacentBlocks.add(searchLocation.add(0,-1,0).getBlock()); // Down
        adjacentBlocks.add(searchLocation.add(0,2,0).getBlock()); // Up
        adjacentBlocks.add(searchLocation.add(-1,-1,0).getBlock()); // Left
        adjacentBlocks.add(searchLocation.add(2,0,0).getBlock()); // Right
        adjacentBlocks.add(searchLocation.add(-1,0,1).getBlock()); // Forward
        adjacentBlocks.add(searchLocation.add(0,0,-2).getBlock()); // Downward
        return adjacentBlocks;
    }

    // getDirection is not available in 1.8 API, so adding it here separately
    private Vector getDirection(BlockFace blockFace) {
        int modX = blockFace.getModX();
        int modY = blockFace.getModY();
        int modZ = blockFace.getModZ();
        Vector direction = new Vector(modX, modY, modZ);
        if (modX != 0 || modY != 0 || modZ != 0) {
            direction.normalize();
        }
        return direction;
    }

    private BlockFace getYawBlockFace(Player player) {
        double rotation = (player.getLocation().getYaw() - 90) % 360;
        if (rotation < 0) rotation += 360.0;
        if (0 <= rotation && rotation < 45) {
            return BlockFace.WEST;
        } else if (45 <= rotation && rotation < 135) {
            return BlockFace.NORTH;
        } else if (135 <= rotation && rotation < 225) {
            return BlockFace.EAST;
        } else if (225 <= rotation && rotation < 315) {
            return BlockFace.SOUTH;
        } else {
            return BlockFace.WEST;
        }
    }
    // Bed-Crop-Dupe prevention stop
}
