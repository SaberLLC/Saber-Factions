package com.massivecraft.factions.cmd.banner.listener;

import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.banner.struct.BannerManager;
import com.massivecraft.factions.cmd.banner.struct.FactionBanner;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.util.TL;
import de.tr7zw.nbtapi.NBTItem;
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
        if (e.getPlayer().hasMetadata("bannerTp") && e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN)
            e.getPlayer().removeMetadata("bannerTp", FactionsPlugin.getInstance());
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getType().name().contains("BANNER") && (
                new NBTItem(item)).hasTag("WarBanner")) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block placingOn = event.getClickedBlock().getRelative(event.getBlockFace());
                if (placingOn.getType() != Material.AIR) {
                    event.setCancelled(true);
                    return;
                }
            }
            event.setUseItemInHand(Event.Result.ALLOW);
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onBannerPlace(BlockPlaceEvent e) {
        if (FactionsPlugin.getInstance().version == 7) return;

        Player player = e.getPlayer();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Faction fac = fPlayer.getFaction();
        ItemStack item = e.getItemInHand();

        if (item != null && item.getType().name().contains("BANNER")) {
            NBTItem nbtItem = new NBTItem(item);
            if (nbtItem.hasTag("WarBanner")) {
                if (fPlayer.getFaction().isWilderness()) {
                    fPlayer.msg(TL.WARBANNER_NOFACTION);
                    e.setCancelled(true);
                    return;
                }

                Block placedOn = e.getBlockPlaced();
                if (!getBannerAllowedWorlds().contains(placedOn.getWorld().getName())) {
                    fPlayer.msg(TL.FACTION_BANNER_CANNOT_PLACE);
                    return;
                }

                Location placedLoc = e.getBlockPlaced().getLocation();
                FLocation fplacedLoc = FLocation.wrap(placedLoc);
                if (Board.getInstance().getFactionAt(fplacedLoc).isWarZone() && FactionsPlugin.getInstance().getFileManager().getBanners().fetchBoolean("Banners.placeable-warzone") || fPlayer.getFaction().getRelationTo(Board.getInstance().getFactionAt(fplacedLoc)) == Relation.ENEMY &&FactionsPlugin.getInstance().getFileManager().getBanners().fetchBoolean("Banners.placeable-enemy")) {

                    Location playerLoc = e.getPlayer().getLocation();
                    if (playerLoc.getBlockX() != placedOn.getX() || playerLoc.getBlockZ() != placedOn.getZ() ||
                            Math.abs(playerLoc.getBlockY() - placedOn.getY()) > 1) {
                        fPlayer.msg(TL.FACTION_BANNER_MUST_PLACE);
                        return;
                    }

                    BannerManager manager = FactionsPlugin.getInstance().getBannerManager();
                    FactionBanner banner = manager.getFactionBannerMap().get(fac.getId());
                    if (banner != null && !banner.hasExpired()) {
                        fPlayer.msg(TL.FACTION_BANNER_ALREADY_PLACED_1);
                        fPlayer.msg(TL.FACTION_BANNER_ALREADY_PLACED_2, banner.getSecondsLeft());
                        return;
                    }

                    Material type = placedOn.getType();
                    if (type == Material.AIR || type.name().contains("BANNER") && (placedOn.getRelative(BlockFace.UP).getType() == Material.AIR || placedOn.getY() == 255)) {
                        e.getPlayer().sendMessage(CC.DarkPurpleB + "(!) " + CC.DarkPurple + "You have placed a Faction Banner!");
                        e.getPlayer().sendMessage(CC.Gray + "Faction Members have " + FactionBanner.secondCooldown + "s to teleport to it using " + CC.LightPurple + "/f assist");
                        banner = FactionsPlugin.getInstance().getBannerManager().getFactionBannerMap().computeIfAbsent(fac.getId(), e1 -> new FactionBanner());
                        banner.removeBanner();
                        banner.placeBanner(fac, e.getPlayer(), placedOn.getLocation());
                        e.setCancelled(false);
                    } else {
                        e.getPlayer().sendMessage(CC.RedB + "(!) " + CC.Red + "You must place your /f banner in an valid location!");
                        e.setCancelled(true);
                    }
                }
            } else {
                fPlayer.msg(TL.WARBANNER_INVALIDLOC);
                e.setCancelled(true);
            }
        }
    }
}
