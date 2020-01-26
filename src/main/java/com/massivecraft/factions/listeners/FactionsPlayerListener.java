package com.massivecraft.factions.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.CmdFGlobal;
import com.massivecraft.factions.cmd.CmdFly;
import com.massivecraft.factions.cmd.CmdSeeChunk;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.cmd.logout.LogoutHandler;
import com.massivecraft.factions.cmd.wild.CmdWild;
import com.massivecraft.factions.discord.Discord;
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
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.FactionGUI;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.factions.util.XMaterial;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.persist.MemoryFPlayer;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TagUtil;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
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
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Level;


public class FactionsPlayerListener implements Listener {

    /**
     * @author FactionsUUID Team
     */

    HashMap<Player, Boolean> fallMap = new HashMap<>();
    public static Set<FLocation> corners;
    // Holds the next time a player can have a map shown.
    private HashMap<UUID, Long> showTimes = new HashMap<>();

    public FactionsPlayerListener() {
        for (Player player : FactionsPlugin.getInstance().getServer().getOnlinePlayers()) initPlayer(player);
        if (positionTask == null) startPositionCheck();
        if (!FactionsPlugin.getInstance().mc17) loadCorners();
    }

    public static void loadCorners() {
        FactionsPlayerListener.corners = new HashSet<>();
        for (World world : FactionsPlugin.getInstance().getServer().getWorlds()) {
            WorldBorder border = world.getWorldBorder();
            if (border != null) {
                int cornerCoord = (int) ((border.getSize() - 1.0) / 2.0);
                FactionsPlayerListener.corners.add(new FLocation(world.getName(), FLocation.blockToChunk(cornerCoord), FLocation.blockToChunk(cornerCoord)));
                FactionsPlayerListener.corners.add(new FLocation(world.getName(), FLocation.blockToChunk(cornerCoord), FLocation.blockToChunk(-cornerCoord)));
                FactionsPlayerListener.corners.add(new FLocation(world.getName(), FLocation.blockToChunk(-cornerCoord), FLocation.blockToChunk(cornerCoord)));
                FactionsPlayerListener.corners.add(new FLocation(world.getName(), FLocation.blockToChunk(-cornerCoord), FLocation.blockToChunk(-cornerCoord)));
            }
        }
    }

    public static Boolean isSystemFaction(Faction faction) {
        return faction.isSafeZone() ||
                faction.isWarZone() ||
                faction.isWilderness();
    }

    public static boolean playerCanUseItemHere(Player player, Location location, Material material, boolean justCheck, PermissableAction permissableAction) {
        String name = player.getName();
        if (Conf.playersWhoBypassAllProtection.contains(name)) {
            return true;
        }


        FPlayer me = FPlayers.getInstance().getByPlayer(player);
        if (me.isAdminBypassing()) {
            return true;
        }

        FLocation loc = new FLocation(location);
        Faction otherFaction = Board.getInstance().getFactionAt(loc);
        Faction myFaction = me.getFaction();
        Relation rel = myFaction.getRelationTo(otherFaction);

        // Also cancel if player doesn't have ownership rights for this claim
        if (Conf.ownedAreasEnabled && myFaction == otherFaction && !myFaction.playerHasOwnershipRights(me, loc)) {
            if (!justCheck) {
                me.msg(TL.ACTIONS_OWNEDTERRITORYDENY.toString().replace("{owners}", myFaction.getOwnerListString(loc)));
            }
            return false;
        }

        if (me.getFaction() == otherFaction) return true;

        if (FactionsPlugin.getInstance().getConfig().getBoolean("hcf.raidable", false) && otherFaction.getLandRounded() > otherFaction.getPowerRounded()) {
            return true;
        }

        if (otherFaction.hasPlayersOnline()) {
            if (!Conf.territoryDenyUsageMaterials.contains(material)) {
                return true; // Item isn't one we're preventing for online factions.
            }
        } else {
            if (!Conf.territoryDenyUsageMaterialsWhenOffline.contains(material)) {
                return true; // Item isn't one we're preventing for offline factions.
            }
        }

        if (otherFaction.isWilderness()) {
            if (!Conf.wildernessDenyUsage || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName())) {
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

        Material material = block.getType();

        // Dupe fix.
        FLocation loc = new FLocation(block);
        Faction otherFaction = Board.getInstance().getFactionAt(loc);
        Faction myFaction = me.getFaction();
        Relation rel = myFaction.getRelationTo(otherFaction);

        // no door/chest/whatever protection in wilderness, war zones, or safe zones
        if (otherFaction.isSystemFaction()) return true;
        if (myFaction.isWilderness()) {
            me.msg(TL.GENERIC_NOPERMISSION, TL.GENERIC_DOTHAT);
            return false;
        }

        if (FactionsPlugin.getInstance().getConfig().getBoolean("hcf.raidable", false) && otherFaction.getLandRounded() > otherFaction.getPowerRounded())
            return true;

        if (otherFaction.getId().equals(myFaction.getId()) && me.getRole() == Role.LEADER) return true;
        PermissableAction action = GetPermissionFromUsableBlock(block);
        if (action == null) return false;
        // We only care about some material types.
        /// Who was the idiot?
        if (otherFaction.hasPlayersOnline()) {
            if (Conf.territoryProtectedMaterials.contains(material)) {
                return false;
            }
        } else {
            if (Conf.territoryProtectedMaterialsWhenOffline.contains(material)) {
                return false;
            }
        }

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

        Faction at = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));
        if (at.isWilderness() && !Conf.wildernessDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.wildernessDenyCommands.iterator())) {
            me.msg(TL.PLAYER_COMMAND_WILDERNESS, fullCmd);
            return true;
        }

        Relation rel = at.getRelationTo(me);
        if (at.isNormal() && rel.isAlly() && !Conf.territoryAllyDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.territoryAllyDenyCommands.iterator())) {
            me.msg(TL.PLAYER_COMMAND_ALLY, fullCmd);
            return false;
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
                me.msg(TL.ACTIONS_OWNEDTERRITORYDENY, factionToCheck.getOwnerListString(loc));
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
        // Check for doors that might have diff material name in old version.
        if (material.name().contains("DOOR") || material.name().contains("FENCE_GATE"))
            return PermissableAction.DOOR;
        if (material.name().toUpperCase().contains("BUTTON") || material.name().toUpperCase().contains("PRESSURE") || material.name().contains("DIODE") || material.name().contains("COMPARATOR"))
            return PermissableAction.BUTTON;
        if (FactionsPlugin.instance.mc113 || FactionsPlugin.instance.mc114 || FactionsPlugin.getInstance().mc115) {
            switch (material) {
                case LEVER:
                    return PermissableAction.LEVER;
                case ACACIA_BUTTON:
                case BIRCH_BUTTON:
                case DARK_OAK_BUTTON:
                case JUNGLE_BUTTON:
                case OAK_BUTTON:
                case SPRUCE_BUTTON:
                case STONE_BUTTON:
                case COMPARATOR:
                case REPEATER:
                    return PermissableAction.BUTTON;

                case ACACIA_DOOR:
                case BIRCH_DOOR:
                case IRON_DOOR:
                case JUNGLE_DOOR:
                case OAK_DOOR:
                case SPRUCE_DOOR:
                case DARK_OAK_DOOR:

                case ACACIA_TRAPDOOR:
                case BIRCH_TRAPDOOR:
                case DARK_OAK_TRAPDOOR:
                case IRON_TRAPDOOR:
                case JUNGLE_TRAPDOOR:
                case OAK_TRAPDOOR:
                case SPRUCE_TRAPDOOR:

                case ACACIA_FENCE_GATE:
                case BIRCH_FENCE_GATE:
                case DARK_OAK_FENCE_GATE:
                case JUNGLE_FENCE_GATE:
                case OAK_FENCE_GATE:
                case SPRUCE_FENCE_GATE:
                    return PermissableAction.DOOR;

                case CHEST:
                case TRAPPED_CHEST:
                case CHEST_MINECART:

                case BARREL:

                case SHULKER_BOX:
                case BLACK_SHULKER_BOX:
                case BLUE_SHULKER_BOX:
                case BROWN_SHULKER_BOX:
                case CYAN_SHULKER_BOX:
                case GRAY_SHULKER_BOX:
                case GREEN_SHULKER_BOX:
                case LIGHT_BLUE_SHULKER_BOX:
                case LIGHT_GRAY_SHULKER_BOX:
                case LIME_SHULKER_BOX:
                case MAGENTA_SHULKER_BOX:
                case ORANGE_SHULKER_BOX:
                case PINK_SHULKER_BOX:
                case PURPLE_SHULKER_BOX:
                case RED_SHULKER_BOX:
                case WHITE_SHULKER_BOX:
                case YELLOW_SHULKER_BOX:

                case FURNACE:
                case DROPPER:
                case DISPENSER:
                case ENCHANTING_TABLE:
                case BREWING_STAND:
                case CAULDRON:
                case HOPPER:
                case BEACON:
                case JUKEBOX:
                case ANVIL:
                case CHIPPED_ANVIL:
                case DAMAGED_ANVIL:
                    return PermissableAction.CONTAINER;
                default:
                    return null;
            }
        } else {
            switch (material) {
                case LEVER:
                    return PermissableAction.LEVER;
                case DARK_OAK_DOOR:
                case ACACIA_DOOR:
                case BIRCH_DOOR:
                case IRON_DOOR:
                case JUNGLE_DOOR:
                case SPRUCE_DOOR:
                case ACACIA_FENCE_GATE:
                case BIRCH_FENCE_GATE:
                case OAK_FENCE_GATE:
                case DARK_OAK_FENCE_GATE:
                case JUNGLE_FENCE_GATE:
                case SPRUCE_FENCE_GATE:
                    return PermissableAction.DOOR;
                case CHEST:
                case ENDER_CHEST:
                case TRAPPED_CHEST:
                case DISPENSER:
                case ENCHANTING_TABLE:
                case DROPPER:
                case FURNACE:
                case HOPPER:
                case ANVIL:
                case CHIPPED_ANVIL:
                case DAMAGED_ANVIL:
                case BREWING_STAND:
                    return PermissableAction.CONTAINER;
                default:
                    return null;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        initPlayer(event.getPlayer());
    }

    private void initPlayer(Player player) {
        // Make sure that all online players do have a fplayer.
        final FPlayer me = FPlayers.getInstance().getByPlayer(player);
        ((MemoryFPlayer) me).setName(player.getName());

        // Update the lastLoginTime for this fplayer
        me.setLastLoginTime(System.currentTimeMillis());

        // Store player's current FLocation and notify them where they are
        me.setLastStoodAt(new FLocation(player.getLocation()));

        me.login(); // set kills / deaths

        Bukkit.getScheduler().runTaskLater(FactionsPlugin.instance, () -> {
            if (me.isOnline()) me.getFaction().sendUnreadAnnouncements(me);
        }, 33L);

        if (FactionsPlugin.instance.getConfig().getBoolean("scoreboard.default-enabled", false)) {
            FScoreboard.init(me);
            FScoreboard.get(me).setDefaultSidebar(new FDefaultSidebar(), FactionsPlugin.instance.getConfig().getInt("scoreboard.default-update-interval", 20));
            FScoreboard.get(me).setSidebarVisibility(me.showScoreboard());
        }

        Faction myFaction = me.getFaction();
        if (!myFaction.isWilderness()) {
            for (FPlayer other : myFaction.getFPlayersWhereOnline(true)) {
                if (other != me && other.isMonitoringJoins()) other.msg(TL.FACTION_LOGIN, me.getName());
            }
        }

        fallMap.put(me.getPlayer(), false);
        Bukkit.getScheduler().scheduleSyncDelayedTask(FactionsPlugin.instance, () -> fallMap.remove(me.getPlayer()), 180L);

        if (me.isSpyingChat() && !player.hasPermission(Permission.CHATSPY.node)) {
            me.setSpyingChat(false);
            FactionsPlugin.instance.log(Level.INFO, "Found %s spying chat without permission on login. Disabled their chat spying.", player.getName());
        }

        if (me.isAdminBypassing() && !player.hasPermission(Permission.BYPASS.node)) {
            me.setIsAdminBypassing(false);
            FactionsPlugin.instance.log(Level.INFO, "Found %s on admin Bypass without permission on login. Disabled it for them.", player.getName());
        }


        // If they have the permission, don't let them autoleave. Bad inverted setter :\
        me.setAutoLeave(!player.hasPermission(Permission.AUTO_LEAVE_BYPASS.node));
        me.setTakeFallDamage(true);
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                Player player = (Player) e.getEntity();
                if (fallMap.containsKey(player)) {
                    e.setCancelled(true);
                    fallMap.remove(player);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        FPlayer me = FPlayers.getInstance().getByPlayer(event.getPlayer());

        // Make sure player's power is up to date when they log off.
        me.getPower();
        // and update their last login time to point to when the logged off, for auto-remove routine
        me.setLastLoginTime(System.currentTimeMillis());

        me.logout(); // cache kills / deaths

        // if player is waiting for fstuck teleport but leaves, remove
        if (FactionsPlugin.instance.getStuckMap().containsKey(me.getPlayer().getUniqueId())) {
            FPlayers.getInstance().getByPlayer(me.getPlayer()).msg(TL.COMMAND_STUCK_CANCELLED);
            FactionsPlugin.instance.getStuckMap().remove(me.getPlayer().getUniqueId());
            FactionsPlugin.instance.getTimers().remove(me.getPlayer().getUniqueId());
        }

        Faction myFaction = me.getFaction();
        if (!myFaction.isWilderness()) myFaction.memberLoggedOff();

        if (!myFaction.isWilderness()) {
            for (FPlayer player : myFaction.getFPlayersWhereOnline(true))
                if (player != me && player.isMonitoringJoins()) player.msg(TL.FACTION_LOGOUT, me.getName());

        }

        CmdSeeChunk.seeChunkMap.remove(event.getPlayer().getName());

        FScoreboard.remove(me);
    }

    public String parseAllPlaceholders(String string, Faction faction, Player player) {
        string = TagUtil.parsePlaceholders(player, string);
        string = string.replace("{Faction}", faction.getTag())
                .replace("{online}", faction.getOnlinePlayers().size() + "")
                .replace("{offline}", faction.getFPlayers().size() - faction.getOnlinePlayers().size() + "")
                .replace("{chunks}", faction.getAllClaims().size() + "")
                .replace("{power}", faction.getPower() + "")
                .replace("{leader}", faction.getFPlayerAdmin() + "");
        return string;
    }

    public void checkCanFly(FPlayer me) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("enable-faction-flight")) return;
        if (me.isFlying() && (!me.canFlyAtLocation() || me.checkIfNearbyEnemies())) {
            me.setFFlying(false, false);
            me.msg(TL.COMMAND_FLY_NO_ACCESS, Board.getInstance().getFactionAt(me.getLastStoodAt()).getTag());
            return;
        }
        if (me.isFlying() || !FactionsPlugin.instance.getConfig().getBoolean("ffly.AutoEnable")) return;
        me.setFFlying(true, false);
        CmdFly.flyMap.put(me.getName(), true);
        if (CmdFly.particleTask == null)
            CmdFly.startParticles();
    }

    //inspect
    @EventHandler
    public void onInspect(PlayerInteractEvent e) {
        if (e.getAction().name().contains("BLOCK")) {
            FPlayer fplayer = FPlayers.getInstance().getByPlayer(e.getPlayer());
            if (!fplayer.isInspectMode()) {
                return;
            }
            e.setCancelled(true);
            if (!fplayer.isAdminBypassing()) {
                if (!fplayer.hasFaction()) {
                    fplayer.setInspectMode(false);
                    fplayer.msg(TL.COMMAND_INSPECT_DISABLED_NOFAC);
                    return;
                }
                if (fplayer.getFaction() != Board.getInstance().getFactionAt(new FLocation(e.getPlayer().getLocation()))) {
                    fplayer.msg(TL.COMMAND_INSPECT_NOTINCLAIM);
                    return;
                }
            } else {
                fplayer.msg(TL.COMMAND_INSPECT_BYPASS);
            }
            List<String[]> info = CoreProtect.getInstance().getAPI().blockLookup(e.getClickedBlock(), 0);
            if (info.size() == 0) {
                e.getPlayer().sendMessage(TL.COMMAND_INSPECT_NODATA.toString());
                return;
            }
            Player player = e.getPlayer();
            CoreProtectAPI coAPI = CoreProtect.getInstance().getAPI();
            player.sendMessage(TL.COMMAND_INSPECT_HEADER.toString().replace("{x}", e.getClickedBlock().getX() + "")
                    .replace("{y}", e.getClickedBlock().getY() + "")
                    .replace("{z}", e.getClickedBlock().getZ() + ""));
            String rowFormat = TL.COMMAND_INSPECT_ROW.toString();
            for (String[] strings : info) {
                CoreProtectAPI.ParseResult row = coAPI.parseResult(strings);
                player.sendMessage(rowFormat
                        .replace("{time}", convertTime(row.getTime()))
                        .replace("{action}", row.getActionString())
                        .replace("{player}", row.getPlayer())
                        .replace("{block-type}", row.getType().toString().toLowerCase()));
            }
        }
    }

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

    private String convertTime(int time) {
        String result = String.valueOf(Math.round((System.currentTimeMillis() / 1000L - time) / 36.0D) / 100.0D);
        return (result.length() == 3 ? result + "0" : result) + "/hrs ago";
    }

    public static BukkitTask positionTask = null;
    public static Map<UUID, Location> lastLocations = new HashMap<>();

    public void startPositionCheck() {
        positionTask = Bukkit.getScheduler().runTaskTimer(FactionsPlugin.instance, () -> {
            if (Bukkit.getOnlinePlayers().size() > 0) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!lastLocations.containsKey(player.getUniqueId())) {
                        lastLocations.put(player.getUniqueId(), player.getLocation());
                        continue;
                    }
                    refreshPosition(player, lastLocations.get(player.getUniqueId()), player.getLocation());
                    lastLocations.put(player.getUniqueId(), player.getLocation());
                    if (CmdFly.flyMap.containsKey(player.getName())) {
                        String name = player.getName();
                        if (!player.isFlying()
                                || player.getGameMode() == GameMode.CREATIVE
                                || !FactionsPlugin.instance.mc17 && player.getGameMode() == GameMode.SPECTATOR) {
                            continue;
                        }
                        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
                        Faction myFaction = fPlayer.getFaction();
                        if (myFaction.isWilderness()) {
                            Bukkit.getScheduler().runTask(FactionsPlugin.instance, () -> fPlayer.setFlying(false));
                            CmdFly.flyMap.remove(player.getName());
                            continue;
                        }
                        Bukkit.getScheduler().runTask(FactionsPlugin.instance, () -> {
                            if (!fPlayer.checkIfNearbyEnemies()) {
                                FLocation myFloc = new FLocation(player.getLocation());
                                if (Board.getInstance().getFactionAt(myFloc) != myFaction) {
                                    if (!CmdFly.checkFly(fPlayer, player, Board.getInstance().getFactionAt(myFloc))) {
                                        fPlayer.setFFlying(false, false);
                                        CmdFly.flyMap.remove(name);
                                    }
                                }
                            }
                        });
                    }

                }
            }
        }, 5L, 10L);
    }

    public void refreshPosition(Player player, Location oldLocation, Location newLocation) {
        FPlayer me = FPlayers.getInstance().getByPlayer(player);

        // clear visualization
        if (oldLocation.getBlockX() != newLocation.getBlockX()
                || oldLocation.getBlockY() != newLocation.getBlockY()
                || oldLocation.getBlockZ() != newLocation.getBlockZ()) {
            VisualizeUtil.clear(player);
            if (me.isWarmingUp()) {
                me.clearWarmup();
                me.msg(TL.WARMUPS_CANCELLED);
            }
        }

        // quick check to make sure player is moving between chunks; good performance boost
        if (oldLocation.getBlockX() >> 4 == newLocation.getBlockX() >> 4
                && oldLocation.getBlockZ() >> 4 == newLocation.getBlockZ() >> 4
                && oldLocation.getWorld() == newLocation.getWorld()) {
            return;
        }


        // Did we change coord?
        FLocation from = me.getLastStoodAt();
        FLocation to = new FLocation(player.getLocation());

        if (from.equals(to)) return;

        // Yes we did change coord (:
        me.setLastStoodAt(to);

        // Did we change "host"(faction)?
        Faction factionFrom = Board.getInstance().getFactionAt(from);
        Faction factionTo = Board.getInstance().getFactionAt(to);
        boolean changedFaction = (factionFrom != factionTo);


        if (changedFaction) {
            Bukkit.getScheduler().runTask(FactionsPlugin.instance, () -> Bukkit.getServer().getPluginManager().callEvent(new FPlayerEnteredFactionEvent(factionTo, factionFrom, me)));
            if (FactionsPlugin.instance.getConfig().getBoolean("Title.Show-Title") && me.hasTitlesEnabled()) {
                String title = FactionsPlugin.instance.getConfig().getString("Title.Format.Title");
                title = title.replace("{Faction}", factionTo.getColorTo(me) + factionTo.getTag());
                title = parseAllPlaceholders(title, factionTo, player);
                String subTitle = FactionsPlugin.instance.getConfig().getString("Title.Format.Subtitle").replace("{Description}", factionTo.getDescription()).replace("{Faction}", factionTo.getColorTo(me) + factionTo.getTag());
                subTitle = parseAllPlaceholders(subTitle, factionTo, player);
                final String finalTitle = title;
                final String finalsubTitle = subTitle;
                if (!FactionsPlugin.instance.mc17) {
                    Bukkit.getScheduler().runTaskLater(FactionsPlugin.instance, () -> {
                        if (!FactionsPlugin.instance.mc18) {
                            me.getPlayer().sendTitle(FactionsPlugin.instance.color(finalTitle), FactionsPlugin.instance.color(finalsubTitle), FactionsPlugin.instance.getConfig().getInt("Title.Options.FadeInTime"),
                                    FactionsPlugin.instance.getConfig().getInt("Title.Options.ShowTime"),
                                    FactionsPlugin.instance.getConfig().getInt("Title.Options.FadeOutTime"));
                        } else {
                            me.getPlayer().sendTitle(FactionsPlugin.instance.color(finalTitle), FactionsPlugin.instance.color(finalsubTitle));
                        }
                    }, 5);
                }
            }
            this.checkCanFly(me);

            if (me.getAutoClaimFor() != null) {
                me.attemptClaim(me.getAutoClaimFor(), newLocation, true);
                FactionsPlugin.instance.logFactionEvent(me.getAutoClaimFor(), FLogType.CHUNK_CLAIMS, me.getName(), CC.GreenB + "CLAIMED", String.valueOf(1), (new FLocation(player.getLocation())).formatXAndZ(","));
                if (Conf.disableFlightOnFactionClaimChange) CmdFly.disableFlight(me);
            } else if (me.isAutoSafeClaimEnabled()) {
                if (!Permission.MANAGE_SAFE_ZONE.has(player)) {
                    me.setIsAutoSafeClaimEnabled(false);
                } else {
                    if (!Board.getInstance().getFactionAt(to).isSafeZone()) {
                        Board.getInstance().setFactionAt(Factions.getInstance().getSafeZone(), to);
                        me.msg(TL.PLAYER_SAFEAUTO);
                    }
                }
            } else if (me.isAutoWarClaimEnabled()) {
                if (!Permission.MANAGE_WAR_ZONE.has(player)) {
                    me.setIsAutoWarClaimEnabled(false);
                } else {
                    if (!Board.getInstance().getFactionAt(to).isWarZone()) {
                        Board.getInstance().setFactionAt(Factions.getInstance().getWarZone(), to);
                        me.msg(TL.PLAYER_WARAUTO);
                    }
                }
            }
        }

        if (me.isMapAutoUpdating()) {
            if (showTimes.containsKey(player.getUniqueId()) && (showTimes.get(player.getUniqueId()) > System.currentTimeMillis())) {
                if (FactionsPlugin.instance.getConfig().getBoolean("findfactionsexploit.log", false)) {
                    FactionsPlugin.instance.log(Level.WARNING, "%s tried to show a faction map too soon and triggered exploit blocker.", player.getName());
                }
            } else {
                me.sendFancyMessage(Board.getInstance().getMap(me, to, player.getLocation().getYaw()));
                showTimes.put(player.getUniqueId(), System.currentTimeMillis() + FactionsPlugin.instance.getConfig().getLong("findfactionsexploit.cooldown", 2000));
            }
        } else {
            Faction myFaction = me.getFaction();
            String ownersTo = myFaction.getOwnerListString(to);
            if (changedFaction) {
                if (Conf.sendFactionChangeMessage) me.sendFactionHereMessage(factionFrom);
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


        Material type;
        if (event.getItem() != null) {
            // Convert 1.8 Material Names -> 1.14
            type = XMaterial.matchXMaterial(event.getItem().getType().toString()).get().parseMaterial();
        } else {
            type = null;
        }

        // Creeper Egg Bypass.
        if (Conf.allowCreeperEggingChests && block.getType() == Material.CHEST && type == XMaterial.CREEPER_SPAWN_EGG.parseMaterial() && event.getPlayer().isSneaking()) {
            return;
        }


        // territoryBypasssProtectedMaterials totally bypass the protection system
        if (Conf.territoryBypassProtectedMaterials.contains(block.getType())) return;
        // Do type null checks so if XMaterial has a parsing issue and fills null as a value it will not bypass.
        // territoryCancelAndAllowItemUseMaterial bypass the protection system but only if they're not clicking on territoryDenySwitchMaterials
        // if they're clicking on territoryDenySwitchMaterials, let the protection system handle the permissions
        if (type != null && !Conf.territoryDenySwitchMaterials.contains(block.getType())) {
            if (Conf.territoryCancelAndAllowItemUseMaterial.contains(type)) {
                return;
            }
        }

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
        if (e.getCurrentItem() == null)
            return;

        if (!e.getView().getTitle().endsWith("'s Inventory"))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBoneMeal(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == XMaterial.GRASS_BLOCK.parseMaterial()
                && event.hasItem() && event.getItem().getType() == XMaterial.BONE_MEAL.parseMaterial()) {
            if (!FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), block.getLocation(), PermissableAction.BUILD.name(), true)) {
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
                (Conf.homesRespawnFromNoPowerLossWorlds || !Conf.worldsNoPowerLoss.contains(event.getPlayer().getWorld().getName()))) {
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
    public void onLogoutMove(PlayerMoveEvent e) {
        LogoutHandler handler = LogoutHandler.getByName(e.getPlayer().getName());
        if (handler.isLogoutActive(e.getPlayer())) {
            handler.cancelLogout(e.getPlayer());
            e.getPlayer().sendMessage(String.valueOf(TL.COMMAND_LOGOUT_MOVED));
        }
        if (CmdWild.waitingTeleport.containsKey(e.getPlayer())) {
            CmdWild.waitingTeleport.remove(e.getPlayer());
            FPlayers.getInstance().getByPlayer(e.getPlayer()).msg(TL.COMMAND_WILD_INTERUPTED);
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
            if (CmdWild.waitingTeleport.containsKey(player)) {
                CmdWild.waitingTeleport.remove(player);
                FPlayers.getInstance().getByPlayer(player).msg(TL.COMMAND_WILD_INTERUPTED);
            }
            if (CmdWild.teleporting.contains(player)) {
                if (!FactionsPlugin.getInstance().getConfig().getBoolean("Wild.FallDamage") && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    e.setCancelled(true);
                }
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
    public void onPlayerMoveGUI(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof FactionGUI) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
        FPlayer badGuy = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (badGuy == null) return;

        // if player was banned (not just kicked), get rid of their stored info
        if (Conf.removePlayerDataWhenBanned && event.getReason().equals("Banned by admin.")) {
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
    public void AsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if (CmdFGlobal.toggled.contains(p.getUniqueId())) {
            //they're muted, check status of Faction Chat
            if (FPlayers.getInstance().getByPlayer(p).getFaction() == null) {
                //they're muted, and not in a faction, cancel and return
                e.setCancelled(true);
                return;
            } else {
                //are in a faction that's not Wilderness, SafeZone, or Warzone, check their chat status
                if (!FPlayers.getInstance().getByPlayer(p).getChatMode().isAtLeast(ChatMode.ALLIANCE)) {
                    //their Faction Chat Mode is not at-least a Alliance, cancel and return
                    e.setCancelled(true);
                    return;
                }
            }
        }

        //we made it this far, since we didn't return yet, we must have sent the chat event through
        //iterate through all of recipients and check if they're muted, then remove them from the event list

        List<Player> l = new ArrayList<>(e.getRecipients());

        for (int i = l.size() - 1; i >= 0; i--) { // going backwards in the list to prevent a ConcurrentModificationException
            Player recipient = l.get(i);
            if (recipient != null) {
                if (CmdFGlobal.toggled.contains(recipient.getUniqueId())) {
                    e.getRecipients().remove(recipient);
                }
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(e.getPlayer());
        if (fPlayer.isInFactionsChest()) fPlayer.setInFactionsChest(false);
    }

    @EventHandler
    public void onTab(PlayerChatTabCompleteEvent e) {
        if (!Discord.useDiscord) {
            return;
        }

        String[] msg = e.getChatMessage().split(" ");
        if (msg.length == 0 | !msg[msg.length - 1].contains("@")) {
            return;
        }
        FPlayer fp = FPlayers.getInstance().getByPlayer(e.getPlayer());

        if(fp == null) return;

        if (fp.getChatMode() != ChatMode.FACTION) {
            return;
        }
        Faction f = fp.getFaction();
        if(f == null) return;
        if (f.isSystemFaction()) {
            return;
        }
        if (f.getGuildId() == null | f.getFactionChatChannelId() == null) {
            return;
        }
        if (Discord.jda.getGuildById(f.getGuildId()) == null | Discord.jda.getGuildById(f.getGuildId()).getTextChannelById(f.getFactionChatChannelId()) == null) {
            return;
        }
        TextChannel t = Discord.jda.getGuildById(f.getGuildId()).getTextChannelById(f.getFactionChatChannelId());
        String target = msg[msg.length - 1].replace("@", "");
        List<String> targets = new ArrayList<>();
        if (target.equals("")) {
            for (Member m : t.getMembers()) {
                targets.add("@" + m.getUser().getName() + "#" + m.getUser().getDiscriminator());
            }
        } else {
            for (Member m : t.getMembers()) {
                if (m.getEffectiveName().contains(target) | m.getUser().getName().contains(target)){
                    targets.add("@" + m.getUser().getName() + "#" + m.getUser().getDiscriminator());
                }
            }
        }
        e.getTabCompletions().clear();
        e.getTabCompletions().addAll(targets);
    }
}
