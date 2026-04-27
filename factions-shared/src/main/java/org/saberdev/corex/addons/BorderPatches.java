package org.saberdev.corex.addons;

import com.cryptomorin.xseries.XEntityType;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.saberdev.corex.CoreAddon;


@CoreAddon(configVariable = "Border-Patches")
public class BorderPatches implements Listener {

    @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCancelled = true
    )
    private void onBlockFlow(BlockFromToEvent evt) {
        Block block = evt.getToBlock();
        if (this.isOutsideWorldBorder(block, false)) {
            evt.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCancelled = true
    )
    private void onEntityExplode(EntityExplodeEvent evt) {

        evt.blockList().removeIf(o -> this.isOutsideWorldBorder(o, false));

    }

    @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCancelled = true
    )
    private void onEntitySpawn(EntitySpawnEvent evt) {
        if (this.isOutsideWorldBorder(evt.getLocation(), false)) {
            evt.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCancelled = true
    )
    private void onProjectileHit(ProjectileHitEvent evt) {
        Entity entity = evt.getEntity();
        if (this.isOutsideWorldBorder(entity.getLocation(), false)) {
            entity.remove();
        }

    }

    @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCancelled = true
    )
    private void onEntityTeleport(EntityTeleportEvent evt) {
        Location location = evt.getTo();
        if (location != null && this.isOutsideWorldBorder(location, false)) {
            evt.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCancelled = true
    )
    private void onEntityChangeBlock(EntityChangeBlockEvent evt) {
        if (evt.getEntity() instanceof FallingBlock) {
            Block block = evt.getBlock();
            if (this.isOutsideWorldBorder(block, true)) {
                evt.setCancelled(true);
                block.breakNaturally();
            }
        }

    }

    @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCancelled = true
    )
    private void onBucketFill(PlayerBucketFillEvent evt) {
        Block block = evt.getBlockClicked();
        if (this.isOutsideWorldBorder(block, false)) {
            evt.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCancelled = true
    )
    private void onBucketEmpty(PlayerBucketEmptyEvent evt) {
        Block block = evt.getBlockClicked();
        if (this.isOutsideWorldBorder(block, false)) {
            evt.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCancelled = true
    )
    private void onBlockBreak(BlockBreakEvent evt) {
        Block block = evt.getBlock();
        if (this.isOutsideWorldBorder(block, false)) {
            evt.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCancelled = true
    )
    private void onBlockPlace(BlockPlaceEvent evt) {
        Block block = evt.getBlock();
        if (this.isOutsideWorldBorder(block, false)) {
            evt.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCancelled = true
    )
    private void onPlayerTeleport(PlayerTeleportEvent evt) {
        PlayerTeleportEvent.TeleportCause cause = evt.getCause();
        if (cause.equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            evt.getPlayer().closeInventory();
            Location location = evt.getTo();
            if (location != null && this.isOutsideWorldBorder(location, false)) {
                evt.setCancelled(true);

            }
        }

    }

    @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCancelled = true
    )
    public void onTntExplode(ExplosionPrimeEvent e) {
        if (e.getEntity().getType() == XEntityType.TNT.get() && this.isOutsideWorldBorder(e.getEntity(), true)) {
            e.setCancelled(true);
            e.getEntity().remove();
        }
    }

    private boolean isOutsideWorldBorder(Entity entity, boolean inside) {
        return this.isOutsideWorldBorder(entity.getLocation(), inside);
    }

    private boolean isOutsideWorldBorder(Block block, boolean inside) {
        return this.isOutsideWorldBorder(block.getLocation(), inside);
    }

    private boolean isOutsideWorldBorder(Location location, boolean inside) {
        WorldBorder worldBorder = location.getWorld().getWorldBorder();
        double radius = worldBorder.getSize() / 2.0D;
        if (inside) {
            --radius;
        }

        double x = location.getX();
        double z = location.getZ();
        double lowerX = (double) worldBorder.getCenter().getBlockX() - radius;
        double lowerZ = (double) worldBorder.getCenter().getBlockZ() - radius;
        double upperX = (double) worldBorder.getCenter().getBlockX() + radius;
        double upperZ = (double) worldBorder.getCenter().getBlockZ() + radius;
        return x >= upperX || x < lowerX || z >= upperZ || z < lowerZ;
    }
}

