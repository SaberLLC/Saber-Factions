package com.massivecraft.factions.cmd.banner.listener;

import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.banner.struct.BannerManager;
import com.massivecraft.factions.cmd.banner.struct.FactionBanner;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.util.TL;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class BannerListener implements Listener {
    private List<String> bannerAllowedWorlds = FactionsPlugin.getInstance().getFileManager().getBanners().fetchStringList("Banners.allowedWorldNames");

    public List<String> getBannerAllowedWorlds() {
        return this.bannerAllowedWorlds;
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        if (type.name().contains("BANNER")) {
            for (FactionBanner banner : FactionsPlugin.getInstance().getBannerManager().getFactionBannerMap().values()) {
                Location l = banner.getActiveLocation();
                if (l == null)
                    continue;
                if (l.getBlockX() == block.getX() && l.getBlockY() == block.getY() && l.getBlockZ() == block.getZ()) {
                    FPlayer fp = FPlayers.getInstance().getByPlayer(event.getPlayer());
                    if (fp.getFaction().getRelationTo(banner.getFaction()).isAtLeast(Relation.ALLY)) {
                        event.setCancelled(true);
                        fp.msg(TL.FACTION_BANNER_CANNOT_DESTROY_1, banner.getWhoPlacedUsername());
                        fp.msg(TL.FACTION_BANNER_CANNOT_DESTROY_2, banner.getSecondsLeft());
                        return;
                    }
                    event.setCancelled(true);
                    banner.removeBanner();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        final Player player = e.getPlayer();
        if (player.hasMetadata("bannerTp") && e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN)
            player.removeMetadata("bannerTp", FactionsPlugin.getInstance());
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        final ItemStack item = event.getItem();
        if (!isWarBanner(item))
            return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block placingOn = event.getClickedBlock().getRelative(event.getBlockFace());
            if (placingOn.getType() != Material.AIR) {
                event.setCancelled(true);
                return;
            }
        }
        event.setUseItemInHand(Event.Result.ALLOW);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onBannerPlace(BlockPlaceEvent e) {
        if (FactionsPlugin.getInstance().version == 7) return;

        final Player player = e.getPlayer();
        final FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        final Faction fac = fPlayer.getFaction();
        final ItemStack item = e.getItemInHand();

        if (!isWarBanner(item))
            return;

        if (fPlayer.getFaction().isWilderness()) {
            fPlayer.msg(TL.WARBANNER_NOFACTION);
            e.setCancelled(true);
            return;
        }

        Block placedOn = e.getBlockPlaced();
        if (!getBannerAllowedWorlds().contains(placedOn.getWorld().getName())) {
            fPlayer.msg(TL.FACTION_BANNER_CANNOT_PLACE);
            e.setCancelled(true);
            return;
        }

        Location placedLoc = placedOn.getLocation();
        FLocation fplacedLoc = FLocation.wrap(placedLoc);
        if (Board.getInstance().getFactionAt(fplacedLoc).isWarZone() && FactionsPlugin.getInstance().getFileManager().getBanners().fetchBoolean("Banners.placeable-warzone") || fPlayer.getFaction().getRelationTo(Board.getInstance().getFactionAt(fplacedLoc)) == Relation.ENEMY && FactionsPlugin.getInstance().getFileManager().getBanners().fetchBoolean("Banners.placeable-enemy")) {

            Location playerLoc = player.getLocation();
            if (playerLoc.getBlockX() != placedOn.getX() || playerLoc.getBlockZ() != placedOn.getZ() ||
                    Math.abs(playerLoc.getBlockY() - placedOn.getY()) > 1) {
                fPlayer.msg(TL.FACTION_BANNER_MUST_PLACE);
                return;
            }

            BannerManager manager = FactionsPlugin.getInstance().getBannerManager();
            Map<String, FactionBanner> bannerMap = manager.getFactionBannerMap();
            FactionBanner banner = bannerMap.get(fac.getId());

            if (banner != null && !banner.hasExpired()) {
                fPlayer.msg(TL.FACTION_BANNER_ALREADY_PLACED_1);
                fPlayer.msg(TL.FACTION_BANNER_ALREADY_PLACED_2, banner.getSecondsLeft());
                return;
            }

            Material type = placedOn.getType();
            String typeString = type.name().toUpperCase();
            if (typeString.contains("AIR") || typeString.contains("BANNER") && (placedOn.getRelative(BlockFace.UP).getType() == Material.AIR || placedOn.getY() == 255)) {
                player.sendMessage(CC.DarkPurpleB + "(!) " + CC.DarkPurple + "You have placed a Faction Banner!");
                player.sendMessage(CC.Gray + "Faction Members have " + FactionBanner.secondCooldown + "s to teleport to it using " + CC.LightPurple + "/f assist");
                banner = bannerMap.computeIfAbsent(fac.getId(), elseMake -> new FactionBanner());
                banner.removeBanner();
                banner.placeBanner(fac, player, placedLoc);
                e.setCancelled(false);
            } else {
                player.sendMessage(CC.RedB + "(!) " + CC.Red + "You must place your /f banner in an valid location!");
                e.setCancelled(true);
            }
        } else {
            fPlayer.msg(TL.WARBANNER_INVALIDLOC);
            e.setCancelled(true);
        }
    }

    private boolean isWarBanner(ItemStack itemStack) {
        return itemStack != null && itemStack.getType().name().contains("BANNER") && NBT.get(itemStack, nbt -> {
            return nbt.hasTag("WarBanner");
        });
    }
}
