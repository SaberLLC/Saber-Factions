package com.massivecraft.factions.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.CmdFGlobal;
import com.massivecraft.factions.cmd.CmdSeeChunk;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.cmd.logout.LogoutHandler;
import com.massivecraft.factions.event.FPlayerEnteredFactionEvent;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.scoreboards.FScoreboard;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.scoreboards.sidebar.FDefaultSidebar;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.frame.FactionGUI;
import com.massivecraft.factions.zcore.persist.MemoryFPlayer;
import com.massivecraft.factions.zcore.persist.MemoryFPlayers;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;


public class FactionsPlayerListener implements Listener {

    public static Set<FLocation> corners;
    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    // Holds the next time a player can have a map shown.
    private HashMap<UUID, Long> showTimes = new HashMap<>();

    public FactionsPlayerListener() {
        for (Player player : FactionsPlugin.getInstance().getServer().getOnlinePlayers()) initPlayer(player);
        if (FactionsPlugin.getInstance().version != 7) loadCorners();
    }

    public static void loadCorners() {
        List<World> worlds = FactionsPlugin.getInstance().getServer().getWorlds();
        FactionsPlayerListener.corners = new HashSet<>(worlds.size() * 4);
        for (World world : worlds) {
            WorldBorder border = world.getWorldBorder();

            Location center = border.getCenter();
            double centerX = center.getX();
            double centerZ = center.getZ();

            int borderSize = (int) border.getSize();

            double cornerX = centerX - (borderSize - 1) / 2.0D;
            double cornerZ = centerZ - (borderSize - 1) / 2.0D;

            int cornerChunkX = WorldUtil.blockToChunk((int) cornerX);
            int cornerChunkZ = WorldUtil.blockToChunk((int) cornerZ);

            int borderChunk = WorldUtil.blockToChunk(borderSize);
            
            String worldName = world.getName();

            corners.add(FLocation.wrap(worldName, cornerChunkX, cornerChunkZ));
            corners.add(FLocation.wrap(worldName, cornerChunkX, cornerChunkZ + borderChunk));
            corners.add(FLocation.wrap(worldName, cornerChunkX + borderChunk, cornerChunkZ));
            corners.add(FLocation.wrap(worldName, cornerChunkX + borderChunk, cornerChunkZ + borderChunk));
        }
    }
    public static Boolean isSystemFaction(Faction faction) {
        return faction.isSafeZone() ||
                faction.isWarZone() ||
                faction.isWilderness();
    }

    public static boolean playerCanUseItemHere(Player player, Location location, Material material, boolean justCheck, PermissableAction permissableAction) {
        material = XMaterial.matchXMaterial(material).parseMaterial();

        if (Conf.playersWhoBypassAllProtection.contains(player.getName())) {
            return true;
        }

        FPlayer me = FPlayers.getInstance().getByPlayer(player);
        if (me.isAdminBypassing()) {
            return true;
        }



        FLocation loc = FLocation.wrap(location);
        Faction otherFaction = Board.getInstance().getFactionAt(loc);
        Faction myFaction = me.getFaction();

        // Also cancel if player doesn't have ownership rights for this claim
        if (Conf.ownedAreasEnabled && myFaction == otherFaction && !myFaction.playerHasOwnershipRights(me, loc)) {
            if (!justCheck) {
                me.msg(TextUtil.replace(TL.ACTIONS_OWNEDTERRITORYDENY.toString(), "{owners}", myFaction.getOwnerListString(loc)));
            }
            return false;
        }


        //if (me.getFaction() == otherFaction) return true;

        if (FactionsPlugin.getInstance().getConfig().getBoolean("hcf.raidable", false) && otherFaction.getLandRounded() > otherFaction.getPowerRounded()) {
            return true;
        }



        if (otherFaction.hasPlayersOnline()) {
            if (!Conf.territoryDenyUsageMaterials.contains(material)) {
                return true; // Item isn't one we're preventing for online factions.
            }
        }



        if (otherFaction.isWilderness()) {
            if (!Conf.wildernessDenyUsage || ((Conf.worldsNoWildernessProtection.contains(location.getWorld().getName()) && !Conf.useWorldConfigurationsAsWhitelist) || (!Conf.worldsNoWildernessProtection.contains(location.getWorld().getName()) && Conf.useWorldConfigurationsAsWhitelist)) ) {
                return true; // This is not faction territory. Use whatever you like here.
            }

            if (!justCheck) {
                me.msg(TL.PLAYER_USE_WILDERNESS, TextUtil.getMaterialName(material));
            }

            return false;
        } else if (otherFaction.isSafeZone()) {
            if (!Conf.safeZoneDenyUsage || Permission.MANAGE_SAFE_ZONE.has(player)) {
                return true;
            }

            if (!justCheck) {
                me.msg(TL.PLAYER_USE_SAFEZONE, TextUtil.getMaterialName(material));
            }

            return false;
        } else if (otherFaction.isWarZone()) {
            if (!Conf.warZoneDenyUsage || Permission.MANAGE_WAR_ZONE.has(player)) {
                return true;
            }

            if (!justCheck) {
                me.msg(TL.PLAYER_USE_WARZONE, TextUtil.getMaterialName(material));
            }

            return false;
        }

        Relation rel = myFaction.getRelationTo(otherFaction);
        // Cancel if we are not in our own territory
        if (rel.confDenyUseage()) {
            if (!justCheck) {
                me.msg(TL.PLAYER_USE_TERRITORY, TextUtil.getMaterialName(material), otherFaction.getTag(myFaction));
            }
            return false;
        }

        Access access = otherFaction.getAccess(me, permissableAction);
        return CheckPlayerAccess(player, me, loc, otherFaction, access, permissableAction, false);
    }

    public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck) {
        if (Conf.playersWhoBypassAllProtection.contains(player.getName()))
            return true;

        FPlayer me = FPlayers.getInstance().getByPlayer(player);
        if (me.isAdminBypassing())
            return true;
        // Dupe fix.
        FLocation loc = FLocation.wrap(block);
        Faction otherFaction = Board.getInstance().getFactionAt(loc);
        Faction myFaction = me.getFaction();

        // no door/chest/whatever protection in wilderness, war zones, or safe zones
        if (otherFaction.isSystemFaction()) return true;
        if (myFaction.isWilderness()) {
            if(block.getType().name().contains("PLATE")) {
                if(!Cooldown.isOnCooldown(player, "plateMessage")) {
                    Cooldown.setCooldown(player, "plateMessage", 3);
                } else {
                    return false;
                }
            }

            me.msg(TL.GENERIC_ACTION_NOPERMISSION, block.getType().toString().replace("_", " "));
            return false;
        }

        if (FactionsPlugin.getInstance().getConfig().getBoolean("hcf.raidable", false) && otherFaction.getLandRounded() > otherFaction.getPowerRounded())
            return true;

        if (otherFaction.getId().equals(myFaction.getId()) && me.getRole() == Role.LEADER) return true;
        PermissableAction action = GetPermissionFromUsableBlock(block);
        if (action == null) return false;
        // We only care about some material types.
        /// Who was the idiot?
        //if (otherFaction.hasPlayersOnline()) {
        //    if (Conf.territoryProtectedMaterials.contains(material)) {
        //        return false;
        //    }
        //} else {
        //    if (Conf.territoryProtectedMaterialsWhenOffline.contains(material)) {
        //        return false;
        //    }
        //}

        // Move up access check to check for exceptions
        if (!otherFaction.getId().equals(myFaction.getId())) { // If the faction target is not my own
            // Get faction pain build access relation to me
            boolean pain = !justCheck && otherFaction.getAccess(me, PermissableAction.PAIN_BUILD) == Access.ALLOW;
            return CheckPlayerAccess(player, me, loc, otherFaction, otherFaction.getAccess(me, action), action, pain);
        } else if (otherFaction.getId().equals(myFaction.getId())) {
            return CheckPlayerAccess(player, me, loc, myFaction, myFaction.getAccess(me, action), action, (!justCheck && myFaction.getAccess(me, PermissableAction.PAIN_BUILD) == Access.ALLOW));
        }
        return CheckPlayerAccess(player, me, loc, myFaction, otherFaction.getAccess(me, action), action, Conf.territoryPainBuild);
    }

    public static boolean preventCommand(String fullCmd, Player player) {
        if ((Conf.territoryNeutralDenyCommands.isEmpty() && Conf.territoryEnemyDenyCommands.isEmpty() && Conf.permanentFactionMemberDenyCommands.isEmpty() && Conf.warzoneDenyCommands.isEmpty())) {
            return false;
        }

        fullCmd = fullCmd.toLowerCase();

        FPlayer me = FPlayers.getInstance().getByPlayer(player);

        String shortCmd;  // command without the slash at the beginning
        if (fullCmd.startsWith("/")) {
            shortCmd = fullCmd.substring(1);
        } else {
            shortCmd = fullCmd;
            fullCmd = "/" + fullCmd;
        }

        if (me.hasFaction() &&
                !me.isAdminBypassing() &&
                !Conf.permanentFactionMemberDenyCommands.isEmpty() &&
                me.getFaction().isPermanent() &&
                isCommandInList(fullCmd, shortCmd, Conf.permanentFactionMemberDenyCommands.iterator())) {
            me.msg(TL.PLAYER_COMMAND_PERMANENT, fullCmd);
            return true;
        }

        Faction at = Board.getInstance().getFactionAt(FLocation.wrap(player.getLocation()));
        if (at.isWilderness() && !Conf.wildernessDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.wildernessDenyCommands.iterator())) {
            me.msg(TL.PLAYER_COMMAND_WILDERNESS, fullCmd);
            return true;
        }

        Relation rel = at.getRelationTo(me);
        if (at.isNormal() && rel.isAlly() && !Conf.territoryAllyDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.territoryAllyDenyCommands.iterator())) {
            me.msg(TL.PLAYER_COMMAND_ALLY, fullCmd);
            return true;
        }

        if (at.isNormal() && rel.isNeutral() && !Conf.territoryNeutralDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.territoryNeutralDenyCommands.iterator())) {
            me.msg(TL.PLAYER_COMMAND_NEUTRAL, fullCmd);
            return true;
        }

        if (at.isNormal() && rel.isEnemy() && !Conf.territoryEnemyDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.territoryEnemyDenyCommands.iterator())) {
            me.msg(TL.PLAYER_COMMAND_ENEMY, fullCmd);
            return true;
        }

        if (at.isWarZone() && !Conf.warzoneDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.warzoneDenyCommands.iterator())) {
            me.msg(TL.PLAYER_COMMAND_WARZONE, fullCmd);
            return true;
        }

        return false;
    }

    private static boolean isCommandInList(String fullCmd, String shortCmd, Iterator<String> iter) {
        String cmdCheck;
        while (iter.hasNext()) {
            cmdCheck = iter.next();
            if (cmdCheck == null) {
                iter.remove();
                continue;
            }

            cmdCheck = cmdCheck.toLowerCase();
            if (fullCmd.startsWith(cmdCheck) || shortCmd.startsWith(cmdCheck)) {
                return true;
            }
        }
        return false;
    }

    private static boolean CheckPlayerAccess(Player player, FPlayer me, FLocation loc, Faction factionToCheck, Access access, PermissableAction action, boolean pain) {
        boolean doPain = pain || Conf.handleExploitInteractionSpam; // Painbuild should take priority. But we want to use exploit interaction as well.
        if (access != null) {
            boolean landOwned = (factionToCheck.doesLocationHaveOwnersSet(loc) && !factionToCheck.getOwnerList(loc).isEmpty());
            if ((landOwned && factionToCheck.getOwnerListString(loc).contains(player.getName())) || (me.getRole() == Role.LEADER && me.getFactionId().equals(factionToCheck.getId()))) {
                return true;
            } else if (landOwned && !factionToCheck.getOwnerListString(loc).contains(player.getName())) {
                me.msg(TL.ACTIONS_OWNEDTERRITORYDENY.toString().replace("{owners}", factionToCheck.getOwnerListString(loc)));
                if (doPain) player.damage(Conf.actionDeniedPainAmount);
                return false;
            } else if (!landOwned && access == Access.ALLOW) {
                return true;
            } else {
                me.msg(TL.PLAYER_USE_TERRITORY, action, factionToCheck.getTag(me.getFaction()));
                return false;
            }
        }

        // Approves any permission check if the player in question is a leader AND owns the faction.
        if (me.getRole().equals(Role.LEADER) && me.getFaction().equals(factionToCheck)) return true;
        if (factionToCheck != null) {
            me.msg(TL.PLAYER_USE_TERRITORY, action, factionToCheck.getTag(me.getFaction()));
        }
        return false;
    }

    /// <summary>
    /// This will try to resolve a permission action based on the item material, if it's not usable, will return null
    /// </summary>
    private static PermissableAction GetPermissionFromUsableBlock(Block block) {
        return GetPermissionFromUsableBlock(block.getType());
    }

    private static PermissableAction GetPermissionFromUsableBlock(Material material) {
        if (material.name().contains("_BUTTON")
                || material.name().contains("COMPARATOR")
                || material.name().contains("PRESSURE")
                || material.name().contains("REPEATER")
                || material.name().contains("DIODE")) return PermissableAction.BUTTON;
        if (material.name().contains("_DOOR")
                || material.name().contains("_TRAPDOOR")
                || material.name().contains("_FENCE_GATE")
                || material.name().startsWith("FENCE_GATE")) return PermissableAction.DOOR;
        if (material.name().contains("SHULKER_BOX")
                || material.name().equals("SMOKER")
                || material.name().equals("FLOWER_POT")
                || material.name().startsWith("POTTED_")
                || material.name().endsWith("ANVIL")
                || material.name().startsWith("CHEST_MINECART")
                || material.name().endsWith("CHEST")
                || material.name().endsWith("JUKEBOX")
                || material.name().endsWith("CAULDRON")
                || material.name().endsWith("FURNACE")
                || material.name().endsWith("HOPPER")
                || material.name().endsWith("BEACON")
                || material.name().startsWith("TRAPPED_CHEST")
                || material.name().equalsIgnoreCase("ENCHANTING_TABLE")
                || material.name().equalsIgnoreCase("ENCHANTMENT_TABLE")
                || material.name().endsWith("BREWING_STAND")
                || material.name().equalsIgnoreCase("BARREL")) return PermissableAction.CONTAINER;
        if (material.name().endsWith("LEVER")) return PermissableAction.LEVER;
        switch (material) {
            case DISPENSER:
            case DROPPER:
                return PermissableAction.CONTAINER;
            default:
                return null;
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        initPlayer(event.getPlayer());
    }

    private void initPlayer(Player player) {
        // Make sure that all online players do have a fplayer.
        FPlayer me = FPlayers.getInstance().getByPlayer(player);
        ((MemoryFPlayer) me).setName(player.getName());

        // Update the lastLoginTime for this fplayer
        me.setLastLoginTime(System.currentTimeMillis());

        // Store player's current FLocation and notify them where they are
        me.setLastStoodAt(FLocation.wrap(player.getLocation()));

        me.login(); // set kills / deaths

        Bukkit.getScheduler().runTaskLater(FactionsPlugin.instance, () -> {
            if (me.isOnline()) me.getFaction().sendUnreadAnnouncements(me);
        }, 33L);

        if (FactionsPlugin.instance.getConfig().getBoolean("scoreboard.default-enabled", false)) {
            FScoreboard.init(me);
            FScoreboard.get(me).setDefaultSidebar(new FDefaultSidebar());
            FScoreboard.get(me).setSidebarVisibility(me.showScoreboard());
        }

        Faction myFaction = me.getFaction();
        if (!myFaction.isWilderness()) {
            for (FPlayer other : myFaction.getFPlayersWhereOnline(true)) {
                if (other != me && other.isMonitoringJoins()) other.msg(TL.FACTION_LOGIN, me.getName());
            }
        }

        if (me.isSpyingChat() && !player.hasPermission(Permission.CHATSPY.node)) {
            me.setSpyingChat(false);
            Logger.printArgs( "Found %s spying chat without permission on login. Disabled their chat spying.", Logger.PrefixType.DEFAULT, player.getName());
        }

        if (me.isAdminBypassing() && !player.hasPermission(Permission.BYPASS.node)) {
            me.setIsAdminBypassing(false);
            Logger.printArgs( "Found %s on admin Bypass without permission on login. Disabled it for them.", Logger.PrefixType.DEFAULT, player.getName());
        }

        me.setAutoLeave(!player.hasPermission(Permission.AUTO_LEAVE_BYPASS.node));
        me.setTakeFallDamage(true);

        if (FCmdRoot.instance.fFlyEnabled && me.isFlying()) {
            me.setFlying(false);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FPlayer me = FPlayers.getInstance().getByPlayer(player);

        // and update their last login time to point to when the logged off, for auto-remove routine
        me.setLastLoginTime(System.currentTimeMillis());

        me.logout(player.getStatistic(Statistic.PLAYER_KILLS), player.getStatistic(Statistic.DEATHS)); // cache kills / deaths

        CmdSeeChunk.seeChunkMap.remove(me.getPlayer().getName());

        // if player is waiting for fstuck teleport but leaves, remove
        Integer stuck = FactionsPlugin.getInstance().getStuckMap().remove(player.getUniqueId());

        if (stuck != null) {
            FPlayers.getInstance().getByPlayer(player).msg(TL.COMMAND_STUCK_CANCELLED);
            FactionsPlugin.instance.getTimers().remove(player.getUniqueId());
        }

        Faction myFaction = me.getFaction();
        if (!myFaction.isWilderness()) myFaction.memberLoggedOff();

        if (!myFaction.isWilderness()) {
            for (FPlayer found : myFaction.getFPlayersWhereOnline(true))
                if (found != me && found.isMonitoringJoins()) found.msg(TL.FACTION_LOGOUT, me.getName());

        }

        FScoreboard.remove(me, event.getPlayer());
        ((MemoryFPlayers) FPlayers.getInstance()).removeOnlinePlayer(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        FPlayer me = FPlayers.getInstance().getByPlayer(player);

        if(!ChunkReference.isSameBlock(event)) {
            VisualizeUtil.clear(event.getPlayer());
            if (me.isWarmingUp()) {
                me.clearWarmup();
                me.msg(TL.WARMUPS_CANCELLED);
            }
        }

        if(ChunkReference.isSameChunk(event)) {
            return;
        }

        // Did we change coord?
        FLocation from = me.getLastStoodAt();
        FLocation to = FLocation.wrap(event.getTo());

        me.setLastStoodAt(to);

        if(player.getGameMode() != GameMode.SPECTATOR) { //To Disable Roam Plugins w/AutoClaim On
            if (me.getAutoClaimFor() != null) {
                me.attemptClaim(me.getAutoClaimFor(), to, true);
            } else if (me.getAutoUnclaimFor() != null) {
                me.attemptUnclaim(me.getAutoUnclaimFor(), to, true);
            }
        }

        // Did we change "host"(faction)?
        Faction factionFrom = Board.getInstance().getFactionAt(from);
        Faction factionTo = Board.getInstance().getFactionAt(to);
        boolean changedFaction = (factionFrom != factionTo);

        if (changedFaction) {
            Bukkit.getServer().getPluginManager().callEvent(new FPlayerEnteredFactionEvent(factionTo, factionFrom, me));
            if (me.hasNotificationsEnabled() && FactionsPlugin.getInstance().getConfig().getBoolean("Title.Show-Title")) {
                if(FactionsPlugin.getInstance().getConfig().getBoolean("Title.Cached")) {
                    player.setMetadata("showFactionTitle", new FixedMetadataValue(FactionsPlugin.getInstance(), true));
                } else {
                    TitleUtil.sendFactionChangeTitle(me, factionTo);
                }
            }
        }

        if (me.isMapAutoUpdating()) {
            if (!showTimes.containsKey(player.getUniqueId()) || showTimes.get(player.getUniqueId()) < System.currentTimeMillis()) {
                me.sendComponent(Board.getInstance().getMap(me, to, player.getLocation().getYaw()));
                showTimes.put(player.getUniqueId(), System.currentTimeMillis() + FactionsPlugin.getInstance().getConfig().getInt("findfactionsexploit.cooldown"));
            }
        } else {
            Faction myFaction = me.getFaction();
            String ownersTo = myFaction.getOwnerListString(to);

            if (changedFaction) {
                me.sendFactionHereMessage(factionFrom);
                if (Conf.ownedAreasEnabled && Conf.ownedMessageOnBorder && myFaction == factionTo && !ownersTo.isEmpty()) {
                    me.sendMessage(TL.GENERIC_OWNERS.format(ownersTo));
                }
            } else if (Conf.ownedAreasEnabled && Conf.ownedMessageInsideTerritory && myFaction == factionTo && !myFaction.isWilderness()) {
                String ownersFrom = myFaction.getOwnerListString(from);
                if (Conf.ownedMessageByChunk || !ownersFrom.equals(ownersTo)) {
                    if (!ownersTo.isEmpty()) {
                        me.sendMessage(TL.GENERIC_OWNERS.format(ownersTo));
                    } else if (!TL.GENERIC_PUBLICLAND.toString().isEmpty()) {
                        me.sendMessage(TL.GENERIC_PUBLICLAND.toString());
                    }
                }
            }
        }
    }

    ////inspect
    //@EventHandler
    //public void onInspect(PlayerInteractEvent e) {
    //    if (e.getAction().name().contains("BLOCK")) {
    //        FPlayer fplayer = FPlayers.getInstance().getByPlayer(e.getPlayer());
    //        if (!fplayer.isInspectMode()) {
    //            return;
    //        }
    //        e.setCancelled(true);
    //        if (!fplayer.isAdminBypassing()) {
    //            if (!fplayer.hasFaction()) {
    //                fplayer.setInspectMode(false);
    //                fplayer.msg(TL.COMMAND_INSPECT_DISABLED_NOFAC);
    //                return;
    //            }
    //            if (fplayer.getFaction() != Board.getInstance().getFactionAt(new FLocation(e.getPlayer().getLocation()))) {
    //                fplayer.msg(TL.COMMAND_INSPECT_NOTINCLAIM);
    //                return;
    //            }
    //        } else {
    //            fplayer.msg(TL.COMMAND_INSPECT_BYPASS);
    //        }
    //        List<String[]> info = CoreProtect.getInstance().getAPI().blockLookup(e.getClickedBlock(), 0);
    //        if (info.size() == 0) {
    //            e.getPlayer().sendMessage(TL.COMMAND_INSPECT_NODATA.toString());
    //            return;
    //        }
    //        Player player = e.getPlayer();
    //        CoreProtectAPI coAPI = CoreProtect.getInstance().getAPI();
    //        player.sendMessage(TL.COMMAND_INSPECT_HEADER.toString().replace("{x}", e.getClickedBlock().getX() + "")
    //                .replace("{y}", e.getClickedBlock().getY() + "")
    //                .replace("{z}", e.getClickedBlock().getZ() + ""));
    //        String rowFormat = TL.COMMAND_INSPECT_ROW.toString();
    //        for (String[] strings : info) {
    //            CoreProtectAPI.ParseResult row = coAPI.parseResult(strings);
    //            player.sendMessage(rowFormat
    //                    .replace("{time}", convertTime(row.getTime()))
    //                    .replace("{action}", row.getActionString())
    //                    .replace("{player}", row.getPlayer())
    //                    .replace("{block-type}", row.getType().toString().toLowerCase()));
    //        }
    //    }
    //}

    //For disabling enderpearl throws
    @EventHandler
    public void onPearl(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (player.getItemInHand().getType() == Material.ENDER_PEARL) {
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            if (fPlayer.isFlying() && Conf.noEnderpearlsInFly) {
                fPlayer.msg(TL.COMMAND_FLY_NO_EPEARL);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        FPlayer fme = FPlayers.getInstance().getById(e.getPlayer().getUniqueId().toString());
        if (fme.isInVault()) fme.setInVault(false);
        if (fme.isInFactionsChest()) fme.setInFactionsChest(false);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
            return;

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (block == null) return;

        XMaterial type = null;
        try {
            type = event.getItem() == null ? null : XMaterial.matchXMaterial(event.getItem());
        } catch (IllegalArgumentException exception) {
            if (event.getItem() != null) {
                FactionsPlugin.getInstance().getLogger().info("Cannot find valid material for: " + event.getItem().getType().name());
            }
        }


        // Creeper Egg Bypass.
        if (Conf.allowCreeperEggingChests && block.getType() == Material.CHEST && type == XMaterial.CREEPER_SPAWN_EGG && event.getPlayer().isSneaking()) {
            return;
        }

        // territoryBypasssProtectedMaterials totally bypass the protection system
        if (Conf.territoryBypassProtectedMaterials.contains(block.getType())) return;
        // Do type null checks so if XMaterial has a parsing issue and fills null as a value it will not bypass.
        // territoryCancelAndAllowItemUseMaterial bypass the protection system but only if they're not clicking on territoryDenySwitchMaterials
        // if they're clicking on territoryDenySwitchMaterials, let the protection system handle the permissions
        //if (type != null && !Conf.territoryDenySwitchMaterials.contains(block.getType())) {
        //    if (Conf.territoryCancelAndAllowItemUseMaterial.contains(event.getPlayer().getItemInHand().getType()) && !Conf.territoryDenySwitchMaterials.contains(block.getType())) {
        //        return;
        //    }
        //}

        if (GetPermissionFromUsableBlock(block.getType()) != null) {
            if (!canPlayerUseBlock(player, block, false)) {
                event.setCancelled(true);
                event.setUseInteractedBlock(Event.Result.DENY);
                return;
            }
        }

        if (type != null && !playerCanUseItemHere(player, block.getLocation(), event.getItem().getType(), false, PermissableAction.ITEM)) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
        }

    }

    @EventHandler
    public void onInventorySee(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (!e.getView().getTitle().endsWith("'s Player Inventory")) return;
        e.setCancelled(true);
    }


    @EventHandler
    public void onPlayerBoneMeal(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == XMaterial.GRASS_BLOCK.parseMaterial()
                && event.hasItem() && event.getItem().getType() == XMaterial.BONE_MEAL.parseMaterial()) {
            if (!FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), block.getLocation(), "build", true)) {
                FPlayer me = FPlayers.getInstance().getById(event.getPlayer().getUniqueId().toString());
                Faction myFaction = me.getFaction();

                me.msg(TL.ACTIONS_NOPERMISSION.toString().replace("{faction}", myFaction.getTag(me.getFaction())).replace("{action}", "use bone meal"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        FPlayer me = FPlayers.getInstance().getByPlayer(event.getPlayer());

        me.getPower();  // update power, so they won't have gained any while dead

        Location home = me.getFaction().getHome();
        if (Conf.homesEnabled &&
                Conf.homesTeleportToOnDeath &&
                home != null &&
                (Conf.homesRespawnFromNoPowerLossWorlds || ( (!Conf.worldsNoPowerLoss.contains(event.getPlayer().getWorld().getName()) && !Conf.useWorldConfigurationsAsWhitelist) || (Conf.worldsNoPowerLoss.contains(event.getPlayer().getWorld().getName()) && Conf.useWorldConfigurationsAsWhitelist) ) )) {
            event.setRespawnLocation(home);
        }
    }

    // For some reason onPlayerInteract() sometimes misses bucket events depending on distance (something like 2-3 blocks away isn't detected),
    // but these separate bucket events below always fire without fail
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Block block = event.getBlockClicked();
        Player player = event.getPlayer();

        if (!playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false, PermissableAction.BUILD)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Block block = event.getBlockClicked();
        Player player = event.getPlayer();

        if (!playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false, PermissableAction.DESTROY)) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            LogoutHandler handler = LogoutHandler.getByName(player.getName());
            if (handler.isLogoutActive(player)) {
                handler.cancelLogout(player);
                player.sendMessage(String.valueOf(TL.COMMAND_LOGOUT_DAMAGE_TAKEN));
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTeleportChange(PlayerTeleportEvent event) {
        FPlayer me = FPlayers.getInstance().getByPlayer(event.getPlayer());

        FLocation to = FLocation.wrap(Objects.requireNonNull(event.getTo()));
        me.setLastStoodAt(to);

        // Check the location they're teleporting to and check if they can fly there.
        if (FCmdRoot.instance.fFlyEnabled && !me.isAdminBypassing()) {
            boolean canFly = me.canFlyAtLocation(to);
            if (me.isFlying() && !canFly) {
                me.setFlying(false, false);
            } else if (me.isAutoFlying() && !me.isFlying() && canFly) {
                me.setFlying(true);
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();

        if (player == null) return;
        LogoutHandler handler = LogoutHandler.getByName(player.getName());
        if (handler.isLogoutActive(player)) {
            handler.cancelLogout(player);
            player.sendMessage(String.valueOf(TL.COMMAND_LOGOUT_TELEPORTED));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractGUI(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getHolder() instanceof FactionGUI) {
            event.setCancelled(true);
            ((FactionGUI) event.getClickedInventory().getHolder()).onClick(event.getRawSlot(), event.getClick());
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCloseGUI(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof FactionGUI) {
            ((FactionGUI) event.getInventory().getHolder()).onClose(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMoveGUI(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof FactionGUI) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
        FPlayer badGuy = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (badGuy == null) return;

        // if player was banned (not just kicked), get rid of their stored info
        if (Conf.removePlayerDataWhenBanned && event.getReason().equals(Conf.removePlayerDataWhenBannedReason)) {
            if (badGuy.getRole() == Role.LEADER) badGuy.getFaction().promoteNewLeader();
            badGuy.leave(false);
            badGuy.remove();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    final public void onFactionJoin(FPlayerJoinEvent event) {
        FTeamWrapper.applyUpdatesLater(event.getFaction());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFactionLeave(FPlayerLeaveEvent event) {
        FTeamWrapper.applyUpdatesLater(event.getFaction());
    }

    public Set<FLocation> getCorners() {
        return corners;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (CmdFGlobal.toggled.contains(player.getUniqueId())) {
            if (FPlayers.getInstance().getByPlayer(player).getFaction() == null ||
                    !FPlayers.getInstance().getByPlayer(player).getChatMode().isAtLeast(ChatMode.ALLIANCE)) {
                event.setCancelled(true);
                return;
            }
        }

        Set<Player> mutedRecipients = new HashSet<>(event.getRecipients());

        for (Player recipient : event.getRecipients()) {
            if (CmdFGlobal.toggled.contains(recipient.getUniqueId())) {
                mutedRecipients.remove(recipient);
            }
        }

        event.getRecipients().retainAll(mutedRecipients);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(e.getPlayer());
        if (fPlayer.isInFactionsChest()) fPlayer.setInFactionsChest(false);
    }
}
