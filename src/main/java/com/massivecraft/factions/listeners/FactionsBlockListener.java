package com.massivecraft.factions.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.audit.FLogManager;
import com.massivecraft.factions.cmd.audit.LogTimer;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class FactionsBlockListener implements Listener {

    public static HashMap<String, Location> bannerLocations = new HashMap<>();
    private HashMap<String, Boolean> bannerCooldownMap = new HashMap<>();
    private long placeTimer = TimeUnit.SECONDS.toMillis(15L);


    public static boolean playerCanBuildDestroyBlock(Player player, Location location, String action, boolean justCheck) {
        if (Conf.playersWhoBypassAllProtection.contains(player.getName())) return true;

        FPlayer me = FPlayers.getInstance().getById(player.getUniqueId().toString());
        if (me.isAdminBypassing()) return true;

        FLocation loc = new FLocation(location);
        Faction otherFaction = Board.getInstance().getFactionAt(loc);
        Faction myFaction = me.getFaction();

        if (otherFaction.isWilderness()) {
            if (Conf.worldGuardBuildPriority && Worldguard.getInstance().playerCanBuild(player, location)) return true;
            if (location.getWorld() != null) {
                if (!Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName()))
                    return true;
            }
            if (!justCheck) me.msg(TL.ACTION_DENIED_WILDERNESS, action);
            return false;
        } else if (otherFaction.isSafeZone()) {
            if (Conf.worldGuardBuildPriority && Worldguard.getInstance().playerCanBuild(player, location)) return true;
            if (!Conf.safeZoneDenyBuild || Permission.MANAGE_SAFE_ZONE.has(player)) return true;
            if (!justCheck) me.msg(TL.ACTION_DENIED_SAFEZONE, action);
            return false;
        } else if (otherFaction.isWarZone()) {
            if (Conf.worldGuardBuildPriority && Worldguard.getInstance().playerCanBuild(player, location)) return true;
            if (!Conf.warZoneDenyBuild || Permission.MANAGE_WAR_ZONE.has(player)) return true;
            if (!justCheck) me.msg(TL.ACTION_DENIED_WARZONE, action);
            return false;
        } else if (!otherFaction.getId().equals(myFaction.getId())) { // If the faction target is not my own
            if (FactionsPlugin.getInstance().getConfig().getBoolean("hcf.raidable", false) && otherFaction.getLandRounded() > otherFaction.getPowerRounded())
                return true;
            boolean pain = !justCheck && otherFaction.getAccess(me, PermissableAction.PAIN_BUILD) == Access.ALLOW;
            return CheckActionState(otherFaction, loc, me, PermissableAction.fromString(action), pain);
        } else if (otherFaction.getId().equals(myFaction.getId())) {
            boolean pain = !justCheck && myFaction.getAccess(me, PermissableAction.PAIN_BUILD) == Access.ALLOW;
            return CheckActionState(myFaction, loc, me, PermissableAction.fromString(action), pain);
        }
        return false;
    }

    private static boolean CheckPlayerAccess(Player player, FPlayer me, FLocation loc, Faction myFaction, Access access, PermissableAction action, boolean shouldHurt) {
        boolean landOwned = (myFaction.doesLocationHaveOwnersSet(loc) && !myFaction.getOwnerList(loc).isEmpty());
        if ((landOwned && myFaction.getOwnerListString(loc).contains(player.getName())) || (me.getRole() == Role.LEADER && me.getFactionId().equals(myFaction.getId())))
            return true;
        else if (landOwned && !myFaction.getOwnerListString(loc).contains(player.getName())) {
            me.msg(TL.ACTIONS_OWNEDTERRITORYDENY.toString().replace("{owners}", myFaction.getOwnerListString(loc)));
            if (shouldHurt) {
                player.damage(Conf.actionDeniedPainAmount);
                me.msg(TL.ACTIONS_NOPERMISSIONPAIN.toString().replace("{action}", action.toString()).replace("{faction}", Board.getInstance().getFactionAt(loc).getTag(myFaction)));
            }
            return false;
        } else if (!landOwned && access == Access.DENY) { // If land is not owned but access is set to DENY anyway
            if (shouldHurt) {
                player.damage(Conf.actionDeniedPainAmount);
                if ((Board.getInstance().getFactionAt(loc).getTag(myFaction)) != null)
                    me.msg(TL.ACTIONS_NOPERMISSIONPAIN.toString().replace("{action}", action.toString()).replace("{faction}", Board.getInstance().getFactionAt(loc).getTag(myFaction)));
            }
            if (myFaction.getTag(me.getFaction()) != null && action != null)
                me.msg(TL.ACTIONS_NOPERMISSION.toString().replace("{faction}", myFaction.getTag(me.getFaction())).replace("{action}", action.toString()));
            return false;
        } else if (access == Access.ALLOW) return true;
        me.msg(TL.ACTIONS_NOPERMISSION.toString().replace("{faction}", myFaction.getTag(me.getFaction())).replace("{action}", action.toString()));
        return false;
    }

    private static boolean CheckActionState(Faction target, FLocation location, FPlayer me, PermissableAction action, boolean pain) {
        if (Conf.ownedAreasEnabled && target.doesLocationHaveOwnersSet(location) && !target.playerHasOwnershipRights(me, location)) {
            // If pain should be applied
            if (pain && Conf.ownedAreaPainBuild)
                me.msg(TL.ACTIONS_OWNEDTERRITORYPAINDENY.toString().replace("{action}", action.toString()).replace("{faction}", target.getOwnerListString(location)));
            if (Conf.ownedAreaDenyBuild && pain) return false;
            else if (Conf.ownedAreaDenyBuild) {
                me.msg(TL.ACTIONS_NOPERMISSION.toString().replace("{faction}", target.getTag(me.getFaction())).replace("{action}", action.toString()));
                return false;
            }
        }
        return CheckPlayerAccess(me.getPlayer(), me, location, target, target.getAccess(me, action), action, pain);
    }

    public void handleSpawnerUpdate(Faction at, Player player, ItemStack spawnerItem, LogTimer.TimerSubType subType) {
        FLogManager manager = FactionsPlugin.instance.getFlogManager();
        LogTimer logTimer = manager.getLogTimers().computeIfAbsent(player.getUniqueId(), e -> new LogTimer(player.getName(), at.getId()));
        LogTimer.Timer timer = logTimer.attemptLog(LogTimer.TimerType.SPAWNER_EDIT, subType, 0L);
        Map<MaterialData, AtomicInteger> currentCounts = (timer.getExtraData() == null) ? new HashMap<>() : ((Map) timer.getExtraData());
        currentCounts.computeIfAbsent(spawnerItem.getData(), e -> new AtomicInteger(0)).addAndGet(1);
        timer.setExtraData(currentCounts);
        if (timer.isReadyToLog(this.placeTimer)) {
            logTimer.pushLogs(at, LogTimer.TimerType.SPAWNER_EDIT);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGH,
            ignoreCancelled = true
    )
    public void onPlayerPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item != null && item.getType() == XMaterial.SPAWNER.parseMaterial()) {
            Faction at = Board.getInstance().getFactionAt(new FLocation(event.getBlockPlaced()));
            if (at != null && at.isNormal()) {
                FPlayer fplayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
                if (fplayer != null && at.getRelationTo(fplayer.getFaction()).isAtLeast(Relation.TRUCE)) {
                    this.handleSpawnerUpdate(at, event.getPlayer(), item, LogTimer.TimerSubType.SPAWNER_PLACE);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {

        if (!event.canBuild()) return;
        if (event.getBlockPlaced().getType() == Material.FIRE) return;
        boolean isSpawner = event.getBlock().getType().equals(XMaterial.SPAWNER.parseMaterial());
        if (!playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "build", false)) {
            event.setCancelled(true);
            return;
        }

        if (isSpawner) {
            if (Conf.spawnerLock) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(FactionsPlugin.getInstance().color(TL.COMMAND_SPAWNER_LOCK_CANNOT_PLACE.toString()));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!Conf.handleExploitLiquidFlow) return;

        if (event.getBlock().isLiquid()) {
            if (event.getToBlock().isEmpty()) {
                Faction from = Board.getInstance().getFactionAt(new FLocation(event.getBlock()));
                Faction to = Board.getInstance().getFactionAt(new FLocation(event.getToBlock()));
                if (from == to || to.isWilderness()) return;
                // from faction != to faction
                if (to.isSystemFaction()) {
                    event.setCancelled(true);
                    return;
                }

                if (to.isNormal()) {
                    if (from.isNormal() && from.getRelationTo(to).isAlly()) {
                        return;
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        if (event.getInstaBreak() && !playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "destroy", false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (!Conf.pistonProtectionThroughDenyBuild) return;
        Faction pistonFaction = Board.getInstance().getFactionAt(new FLocation(event.getBlock()));

        // target end-of-the-line empty (air) block which is being pushed into, including if piston itself would extend into air
        Block targetBlock = event.getBlock().getRelative(event.getDirection(), event.getLength() + 1);

        // if potentially pushing into air/water/lava in another territory, we need to check it out
        if ((targetBlock.isEmpty() || targetBlock.isLiquid()) && !canPistonMoveBlock(pistonFaction, targetBlock.getLocation()))
            event.setCancelled(true);
    }


    @EventHandler
    public void onVaultPlace(BlockPlaceEvent e) {
        if (e.getItemInHand().getType() == Material.CHEST) {

            ItemStack vault = new ItemBuilder(Material.CHEST)
                    .amount(1).name(FactionsPlugin.instance.getConfig().getString("fvault.Item.Name"))
                    .lore(FactionsPlugin.instance.getConfig().getStringList("fvault.Item.Lore"))
                    .build();

            if (e.getItemInHand().isSimilar(vault)) {
                FPlayer fme = FPlayers.getInstance().getByPlayer(e.getPlayer());
                if (fme.getFaction().getVault() != null) {
                    fme.msg(TL.COMMAND_GETVAULT_ALREADYSET);
                    e.setCancelled(true);
                    return;
                }
                FLocation flocation = new FLocation(e.getBlockPlaced().getLocation());
                if (Board.getInstance().getFactionAt(flocation) != fme.getFaction()) {
                    fme.msg(TL.COMMAND_GETVAULT_INVALIDLOCATION);
                    e.setCancelled(true);
                    return;
                }
                Block start = e.getBlockPlaced();
                int radius = 1;
                for (double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; x++) {
                    for (double y = start.getLocation().getY() - radius; y <= start.getLocation().getY() + radius; y++) {
                        for (double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; z++) {
                            Location blockLoc = new Location(e.getPlayer().getWorld(), x, y, z);
                            if (blockLoc.getX() == start.getLocation().getX() && blockLoc.getY() == start.getLocation().getY() && blockLoc.getZ() == start.getLocation().getZ()) {
                                continue;
                            }
                            Material blockMaterial = blockLoc.getBlock().getType();
                            if (blockMaterial == Material.CHEST || (FactionsPlugin.instance.getConfig().getBoolean("fvault.No-Hoppers-near-vault") && blockMaterial == Material.HOPPER)) {
                                e.setCancelled(true);
                                fme.msg(TL.COMMAND_GETVAULT_CHESTNEAR);
                                return;
                            }
                        }
                    }
                }
                fme.msg(TL.COMMAND_GETVAULT_SUCCESS);
                fme.getFaction().setVault(e.getBlockPlaced().getLocation());

            }
        }
    }

    @EventHandler
    public void onHopperPlace(BlockPlaceEvent e) {
        if (e.getItemInHand().getType() != Material.HOPPER && !FactionsPlugin.instance.getConfig().getBoolean("fvault.No-Hoppers-near-vault"))
            return;
        Faction factionAt = Board.getInstance().getFactionAt(new FLocation(e.getBlockPlaced().getLocation()));
        if (factionAt.isWilderness() || factionAt.getVault() == null) return;
        FPlayer fme = FPlayers.getInstance().getByPlayer(e.getPlayer());
        Block start = e.getBlockPlaced();
        int radius = 1;
        for (double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; x++) {
            for (double y = start.getLocation().getY() - radius; y <= start.getLocation().getY() + radius; y++) {
                for (double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; z++) {
                    Location blockLoc = new Location(e.getPlayer().getWorld(), x, y, z);
                    if (blockLoc.getX() == start.getLocation().getX() && blockLoc.getY() == start.getLocation().getY() && blockLoc.getZ() == start.getLocation().getZ()) {
                        continue;
                    }

                    if (blockLoc.getBlock().getType() == XMaterial.CHEST.parseMaterial()) {
                        if (factionAt.getVault().equals(blockLoc)) {
                            e.setCancelled(true);
                            fme.msg(TL.COMMAND_VAULT_NO_HOPPER);
                            return;
                        }
                    }
                }
            }
        }

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        // if not a sticky piston, retraction should be fine
        if (!event.isSticky() || !Conf.pistonProtectionThroughDenyBuild) return;

        Location targetLoc = event.getRetractLocation();
        Faction otherFaction = Board.getInstance().getFactionAt(new FLocation(targetLoc));

        // Check if the piston is moving in a faction's territory. This disables pistons entirely in faction territory.
        if (otherFaction.isNormal() && FactionsPlugin.instance.getConfig().getBoolean("disable-pistons-in-territory", false)) {
            event.setCancelled(true);
            return;
        }

        // if potentially retracted block is just air/water/lava, no worries
        if (targetLoc.getBlock().isEmpty() || targetLoc.getBlock().isLiquid()) return;
        Faction pistonFaction = Board.getInstance().getFactionAt(new FLocation(event.getBlock()));
        if (!canPistonMoveBlock(pistonFaction, targetLoc)) event.setCancelled(true);
    }

    @EventHandler
    public void onBannerBreak(BlockBreakEvent e) {
        FPlayer fme = FPlayers.getInstance().getByPlayer(e.getPlayer());
        if (FactionsPlugin.getInstance().mc17) {
            return;
        }

        if (bannerLocations.containsValue(e.getBlock().getLocation())) {
            if (e.getBlock().getType().name().contains("BANNER")) {
                e.setCancelled(true);
                fme.msg(TL.BANNER_CANNOT_BREAK);
            }
        }
    }

    @EventHandler
    public void onBannerPlace(BlockPlaceEvent e) {
        if (FactionsPlugin.getInstance().mc17) return;

        if (e.getItemInHand().getType().name().contains("BANNER")) {
            ItemStack bannerInHand = e.getItemInHand();
            FPlayer fme = FPlayers.getInstance().getByPlayer(e.getPlayer());
            ItemStack warBanner = fme.getFaction().getBanner();
            if (warBanner == null) return;
            ItemMeta warmeta = warBanner.getItemMeta();
            warmeta.setDisplayName(FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fbanners.Item.Name")));
            warmeta.setLore(FactionsPlugin.getInstance().colorList(FactionsPlugin.getInstance().getConfig().getStringList("fbanners.Item.Lore")));
            warBanner.setItemMeta(warmeta);
            if (warBanner.isSimilar(bannerInHand)) {
                if (fme.getFaction().isWilderness()) {
                    fme.msg(TL.WARBANNER_NOFACTION);
                    e.setCancelled(true);
                    return;
                }
                int bannerTime = FactionsPlugin.getInstance().getConfig().getInt("fbanners.Banner-Time") * 20;
                Location placedLoc = e.getBlockPlaced().getLocation();
                FLocation fplacedLoc = new FLocation(placedLoc);
                if ((Board.getInstance().getFactionAt(fplacedLoc).isWarZone() && FactionsPlugin.getInstance().getConfig().getBoolean("fbanners.Placeable.Warzone")) || (fme.getFaction().getRelationTo(Board.getInstance().getFactionAt(fplacedLoc)) == Relation.ENEMY && FactionsPlugin.getInstance().getConfig().getBoolean("fbanners.Placeable.Enemy"))) {
                    if (bannerCooldownMap.containsKey(fme.getTag())) {
                        fme.msg(TL.WARBANNER_COOLDOWN);
                        e.setCancelled(true);
                        return;
                    }
                    for (FPlayer fplayer : fme.getFaction().getFPlayers()) {
                        fplayer.getPlayer().sendTitle(FactionsPlugin.getInstance().color(fme.getTag() + " Placed A WarBanner!"), FactionsPlugin.getInstance().color("&7use &c/f tpbanner&7 to tp to the banner!"));
                    }
                    bannerCooldownMap.put(fme.getTag(), true);
                    FactionsBlockListener.bannerLocations.put(fme.getTag(), e.getBlockPlaced().getLocation());
                    int bannerCooldown = FactionsPlugin.getInstance().getConfig().getInt("fbanners.Banner-Place-Cooldown");
                    ArmorStand as = (ArmorStand) e.getBlockPlaced().getLocation().add(0.5, 1.0, 0.5).getWorld().spawnEntity(e.getBlockPlaced().getLocation().add(0.5, 1.0, 0.5), EntityType.ARMOR_STAND);
                    as.setVisible(false);
                    as.setGravity(false);
                    as.setCanPickupItems(false);
                    as.setCustomName(FactionsPlugin.getInstance().color(FactionsPlugin.getInstance().getConfig().getString("fbanners.BannerHolo").replace("{Faction}", fme.getTag())));
                    as.setCustomNameVisible(true);
                    String tag = fme.getTag();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.getInstance(), () -> bannerCooldownMap.remove(tag), Long.parseLong(bannerCooldown + ""));
                    Block banner = e.getBlockPlaced();
                    Material bannerType = banner.getType();
                    Faction bannerFaction = fme.getFaction();
                    banner.getWorld().strikeLightningEffect(banner.getLocation());
                    int radius = FactionsPlugin.getInstance().getConfig().getInt("fbanners.Banner-Effect-Radius");
                    List<String> effects = FactionsPlugin.getInstance().getConfig().getStringList("fbanners.Effects");
                    int affectorTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(FactionsPlugin.getInstance(), () -> {
                        for (Entity e1 : Objects.requireNonNull(banner.getLocation().getWorld()).getNearbyEntities(banner.getLocation(), radius, 255.0, radius)) {
                            if (e1 instanceof Player) {
                                Player player = (Player) e1;
                                FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
                                if (fplayer.getFaction() != bannerFaction) {
                                    continue;
                                }
                                for (String effect : effects) {
                                    String[] components = effect.split(":");
                                    player.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(components[0])), 100, Integer.parseInt(components[1])));
                                }
                                if (banner.getType() == bannerType) {
                                    continue;
                                }
                                banner.setType(bannerType);
                            }
                        }
                    }, 0L, 20L);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.getInstance(), () -> {
                        banner.setType(Material.AIR);
                        as.remove();
                        banner.getWorld().strikeLightningEffect(banner.getLocation());
                        Bukkit.getScheduler().cancelTask(affectorTask);
                        FactionsBlockListener.bannerLocations.remove(bannerFaction.getTag());
                    }, Long.parseLong(bannerTime + ""));
                } else {
                    fme.msg(TL.WARBANNER_INVALIDLOC);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFrostWalker(EntityBlockFormEvent event) {
        if (event.getEntity() == null || event.getEntity().getType() != EntityType.PLAYER || event.getBlock() == null)
            return;

        Player player = (Player) event.getEntity();
        Location location = event.getBlock().getLocation();

        // only notify every 10 seconds
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        boolean justCheck = fPlayer.getLastFrostwalkerMessage() + 10000 > System.currentTimeMillis();
        if (!justCheck) fPlayer.setLastFrostwalkerMessage();

        // Check if they have build permissions here. If not, block this from happening.
        if (!playerCanBuildDestroyBlock(player, location, PermissableAction.FROST_WALK.name(), justCheck))
            event.setCancelled(true);
    }

    @EventHandler
    public void onFallingBlock(EntityChangeBlockEvent event) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("Falling-Block-Fix.Enabled"))
            return;

        Faction faction = Board.getInstance().getFactionAt(new FLocation(event.getBlock()));
        if (faction.isWarZone() || faction.isSafeZone()) {
            event.getBlock().setType(Material.AIR);
            event.setCancelled(true);
        }
    }

    private boolean canPistonMoveBlock(Faction pistonFaction, Location target) {
        Faction otherFaction = Board.getInstance().getFactionAt(new FLocation(target));

        if (pistonFaction == otherFaction) return true;

        if (otherFaction.isWilderness())
            return !Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(target.getWorld().getName());
        else if (otherFaction.isSafeZone()) return !Conf.safeZoneDenyBuild;
        else if (otherFaction.isWarZone()) return !Conf.warZoneDenyBuild;

        Relation rel = pistonFaction.getRelationTo(otherFaction);
        return !rel.confDenyBuild(otherFaction.hasPlayersOnline());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        //If there is an error its much safer to not allow the block to be broken
        try {
            Block block = event.getBlock();

            Faction at = Board.getInstance().getFactionAt(new FLocation(block));
            boolean isSpawner = event.getBlock().getType().equals(XMaterial.SPAWNER.parseMaterial());
            if (!playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "destroy", false)) {
                event.setCancelled(true);
                return;
            }

            FPlayer fme = FPlayers.getInstance().getByPlayer(event.getPlayer());
            if (fme == null || !fme.hasFaction()) return;

            if (isSpawner) {
                Access access = fme.getFaction().getAccess(fme, PermissableAction.SPAWNER);
                if (access != Access.ALLOW && fme.getRole() != Role.LEADER) {
                    fme.msg(TL.GENERIC_FPERM_NOPERMISSION, "mine spawners");
                }
            }

            if (isSpawner && !fme.isAdminBypassing()) {
                ItemStack item = new ItemStack(block.getType(), 1, block.getData());
                if (at != null && at.isNormal()) {
                    FPlayer fplayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
                    if (fplayer != null) {
                        BlockState state = block.getState();
                        if (state instanceof CreatureSpawner) {
                            CreatureSpawner spawner = (CreatureSpawner) state;
                            item.setDurability(spawner.getSpawnedType().getTypeId());
                        }
                        handleSpawnerUpdate(at, event.getPlayer(), item, LogTimer.TimerSubType.SPAWNER_BREAK);
                    }
                }
            }
        } catch (Exception e) {
            event.setCancelled(true);
            e.printStackTrace();
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void FrameRemove(HangingBreakByEntityEvent event) {
        if (event.getRemover() == null) return;
        if ((event.getRemover() instanceof Player)) {
            if (event.getEntity().getType().equals(EntityType.ITEM_FRAME)) {
                Player p = (Player) event.getRemover();
                if (!playerCanBuildDestroyBlock(p, event.getEntity().getLocation(), "destroy", true)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onFarmLandDamage(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!playerCanBuildDestroyBlock(player, event.getBlock().getLocation(), "destroy", true)) {
                FPlayer me = FPlayers.getInstance().getByPlayer(player);
                Faction otherFaction = Board.getInstance().getFactionAt(new FLocation(event.getBlock().getLocation()));
                me.msg(TL.ACTION_DENIED_OTHER, otherFaction.getTag(), "trample crops");
                event.setCancelled(true);
            }
        }
    }
}