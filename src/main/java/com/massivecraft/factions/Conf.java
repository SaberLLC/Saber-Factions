package com.massivecraft.factions;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableMap;
import com.massivecraft.factions.integration.dynmap.DynmapStyle;
import com.massivecraft.factions.zcore.fperms.DefaultPermissions;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.*;

public class Conf {

    // Region Style
    public static final transient String DYNMAP_STYLE_LINE_COLOR = "#00FF00";
    public static final transient double DYNMAP_STYLE_LINE_OPACITY = 0.8D;
    public static final transient int DYNMAP_STYLE_LINE_WEIGHT = 3;
    public static final transient String DYNMAP_STYLE_FILL_COLOR = "#00FF00";
    public static final transient double DYNMAP_STYLE_FILL_OPACITY = 0.35D;
    public static final transient String DYNMAP_STYLE_HOME_MARKER = "greenflag";
    public static final transient boolean DYNMAP_STYLE_BOOST = false;
    public static List<String> baseCommandAliases = new ArrayList<>();
    public static String serverTimeZone = "EST";
    public static boolean allowNoSlashCommand = true;

    // Colors
    public static ChatColor colorMember = ChatColor.GREEN;
    public static ChatColor colorAlly = ChatColor.LIGHT_PURPLE;
    public static ChatColor colorTruce = ChatColor.DARK_PURPLE;
    public static ChatColor colorNeutral = ChatColor.WHITE;
    public static ChatColor colorEnemy = ChatColor.RED;
    public static ChatColor colorPeaceful = ChatColor.GOLD;
    public static ChatColor colorWilderness = ChatColor.GRAY;
    public static ChatColor colorSafezone = ChatColor.GOLD;
    public static ChatColor colorWar = ChatColor.DARK_RED;
    // Power
    public static double powerPlayerMax = 10.0;
    public static double powerPlayerMin = -10.0;
    public static double powerPlayerStarting = 0.0;
    public static double deathToPlayerPowerLoss = 0.0;
    public static double powerPerMinute = 0.2; // Default health rate... it takes 5 min to heal one power
    public static double powerPerDeath = 4.0; // A death makes you lose 4 power
    public static boolean powerRegenOffline = false;  // does player power regenerate even while they're offline?
    public static double powerOfflineLossPerDay = 0.0;  // players will lose this much power per day offline
    public static double powerOfflineLossLimit = 0.0;  // players will no longer lose power from being offline once their power drops to this amount or less
    public static double powerFactionMax = 0.0;  // if greater than 0, the cap on how much power a faction can have (additional power from players beyond that will act as a "buffer" of sorts)
    public static String prefixLeader = "***";
    public static String prefixCoLeader = "**";
    public static String prefixMod = "*";
    public static String prefixRecruit = "-";
    public static String prefixNormal = "+";
    public static int factionTagLengthMin = 3;
    public static int factionTagLengthMax = 10;
    public static boolean factionTagForceUpperCase = false;
    public static boolean newFactionsDefaultOpen = false;
    // when faction membership hits this limit, players will no longer be able to join using /f join; default is 0, no limit
    public static int factionMemberLimit = 30;
    public static int factionAltMemberLimit = 10;
    public static int totalAllowedFactionsOnServer = 0;
    // what faction ID to start new players in when they first join the server; default is 0, "no faction"
    public static String newPlayerStartingFactionID = "0";
    public static boolean showMapFactionKey = true;
    public static boolean showNeutralFactionsOnMap = true;
    public static boolean showEnemyFactionsOnMap = true;
    public static boolean showTrucesFactionsOnMap = true;
    // Disallow joining/leaving/kicking while power is negative
    public static boolean canLeaveWithNegativePower = true;
    // Configuration for faction-only chat
    public static boolean factionOnlyChat = true;
    // Configuration on the Faction tag in chat messages.
    public static boolean chatTagEnabled = true;
    public static transient boolean chatTagHandledByAnotherPlugin = false;
    public static boolean chatTagRelationColored = true;
    public static List<String> blacklistedFactionNames = new ArrayList<>();
    public static String allowedFactionNameCharacters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static String chatTagReplaceString = "[FACTION]";
    public static String chatTagInsertAfterString = "";
    public static String chatTagInsertBeforeString = "";
    public static int chatTagInsertIndex = 0;
    public static boolean chatTagPadBefore = false;
    public static boolean chatTagPadAfter = true;
    public static String chatTagFormat = "%s" + ChatColor.WHITE;
    public static String factionChatFormat = "%s:" + ChatColor.WHITE + " %s";
    public static String allianceChatFormat = ChatColor.LIGHT_PURPLE + "%s:" + ChatColor.WHITE + " %s";
    public static String truceChatFormat = ChatColor.DARK_PURPLE + "%s:" + ChatColor.WHITE + " %s";
    public static String modChatFormat = ChatColor.RED + "%s:" + ChatColor.WHITE + " %s";
    public static int stealthFlyCheckRadius = 32;
    public static int factionBufferSize = 20;
    public static boolean useCheckSystem = true;
    public static boolean spawnerLock = false;
    public static boolean useGraceSystem = true;
    public static boolean broadcastGraceToggles = true;
    public static int gracePeriodTimeDays = 7;
    public static boolean noEnderpearlsInFly = false;
    public static boolean broadcastDescriptionChanges = false;
    public static boolean broadcastTagChanges = false;
    public static double saveToFileEveryXMinutes = 30.0;
    public static boolean useAutoLeaveAndDisbandSystem = false; //whether we enable or disable the AutoLeaveTask
    public static double autoLeaveAfterDaysOfInactivity = 10.0;
    public static double autoLeaveRoutineRunsEveryXMinutes = 5.0;
    public static int autoLeaveRoutineMaxMillisecondsPerTick = 5;  // 1 server tick is roughly 50ms, so default max 10% of a tick
    public static boolean removePlayerDataWhenBanned = true;
    public static String removePlayerDataWhenBannedReason = "Banned by admin.";
    public static boolean autoLeaveDeleteFPlayerData = true; // Let them just remove player from Faction.
    public static boolean removeInvalidClaims = false;
    public static boolean worldGuardChecking = false;
    public static boolean worldGuardBuildPriority = false;

    //RADIUS CLAIMING
    public static boolean useRadiusClaimSystem = true;

    //FRIENDLY FIRE
    public static boolean friendlyFireFPlayersCommand = false;

    //Claim Fill
    public static int maxFillClaimCount = 25;
    public static int maxFillClaimDistance = 5;

    //Drain
    public static boolean factionsDrainEnabled = false;
    public static int factionDrainCooldown = 60;
    //RESERVE
    public static boolean useReserveSystem = true;
    //AUDIT
    public static boolean useAuditSystem = true;

    //INSPECT
    public static boolean useInspectSystem = true;

    //GUI's
    public static boolean useDisbandGUI = true;

    //SEALTH
    public static boolean useStealthSystem = true;

    //STRIKES
    public static boolean useStrikeSystem = true;

    //Denies User Join BEFORE Faction Data is Fully Loaded!
    public static boolean usePreStartupKickSystem = true;

    //SpawnerChunks
    public static boolean userSpawnerChunkSystem = false;
    public static int allowedSpawnerChunks = 2;
    public static boolean allowSpawnerChunksUpgrade = true;
    public static boolean allowSpawnersPlacedInWilderness = false;
    public static boolean allowUnclaimSpawnerChunksWithSpawnersInChunk = true;
    public static ChatColor spawnerChunkColor = ChatColor.BLUE;
    public static String spawnerChunkString = "Spawner Chunk";

    // server logging options
    public static boolean logFactionCreate = true;
    public static boolean logFactionDisband = true;
    public static boolean logFactionJoin = true;
    public static boolean logFactionKick = true;
    public static boolean logFactionLeave = true;
    public static boolean logLandClaims = true;
    public static boolean logLandUnclaims = true;
    public static boolean logMoneyTransactions = true;
    public static boolean logPlayerCommands = true;
    // prevent some potential exploits

    public static boolean handleExploitObsidianGenerators = true;
    public static boolean handleExploitEnderPearlClipping = true;
    public static boolean handleExploitInteractionSpam = true;
    public static boolean handleExploitTNTWaterlog = false;
    public static boolean handleExploitLiquidFlow = false;
    public static boolean homesEnabled = true;
    public static boolean homesMustBeInClaimedTerritory = true;
    public static boolean homesTeleportToOnDeath = true;
    public static boolean homesRespawnFromNoPowerLossWorlds = true;
    public static boolean homesTeleportCommandEnabled = true;
    public static boolean homesTeleportCommandEssentialsIntegration = true;
    public static boolean homesTeleportCommandSmokeEffectEnabled = true;
    public static float homesTeleportCommandSmokeEffectThickness = 3f;
    public static boolean homesTeleportAllowedFromEnemyTerritory = true;
    public static boolean homesTeleportAllowedFromDifferentWorld = true;
    public static double homesTeleportAllowedEnemyDistance = 32.0;
    public static boolean homesTeleportIgnoreEnemiesIfInOwnTerritory = true;
    public static boolean homesTeleportIgnoreEnemiesIfInNoClaimingWorld = true;
    public static boolean disablePVPBetweenNeutralFactions = false;
    public static boolean disablePVPForFactionlessPlayers = false;
    public static boolean enablePVPAgainstFactionlessInAttackersLand = false;
    public static int noPVPDamageToOthersForXSecondsAfterLogin = 3;
    public static boolean allFactionsPeaceful = false;
    public static boolean peacefulTerritoryDisablePVP = true;
    public static boolean peacefulTerritoryDisableMonsters = false;
    public static boolean peacefulTerritoryDisableBoom = false;
    public static boolean peacefulMembersDisablePowerLoss = true;
    public static boolean permanentFactionsDisableLeaderPromotion = false;
    public static boolean claimsMustBeConnected = false;
    public static boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = true;
    public static int claimsRequireMinFactionMembers = 1;
    public static int claimedLandsMax = 0;
    public static int lineClaimLimit = 5;
    public static int claimRadiusLimit = 30;
    // if someone is doing a radius claim and the process fails to claim land this many times in a row, it will exit
    public static int radiusClaimFailureLimit = 9;
    public static double considerFactionsReallyOfflineAfterXMinutes = 0.0;
    public static int actionDeniedPainAmount = 1;
    // commands which will be prevented if the player is a member of a permanent faction
    public static Set<String> permanentFactionMemberDenyCommands = new LinkedHashSet<>();
    // commands which will be prevented when in claimed territory of another faction
    public static Set<String> territoryNeutralDenyCommands = new LinkedHashSet<>();
    public static Set<String> territoryEnemyDenyCommands = new LinkedHashSet<>();
    public static Set<String> territoryAllyDenyCommands = new LinkedHashSet<>();
    public static Set<String> warzoneDenyCommands = new LinkedHashSet<>();
    public static Set<String> wildernessDenyCommands = new LinkedHashSet<>();
    public static boolean protectAnimalsInFactionTerritory = false; //This is a boolean to allow people to damage other animals in territory.
    public static boolean territoryDenyBuild = true;
    public static boolean territoryDenyBuildWhenOffline = true;
    public static boolean territoryPainBuild = false;
    public static boolean territoryPainBuildWhenOffline = false;
    public static boolean territoryDenyUsage = true;
    public static boolean territoryEnemyDenyBuild = true;
    public static boolean territoryEnemyDenyBuildWhenOffline = true;
    public static boolean territoryEnemyPainBuild = false;
    public static boolean territoryEnemyPainBuildWhenOffline = false;
    public static boolean territoryEnemyDenyUsage = true;
    public static boolean territoryAllyDenyBuild = true;
    public static boolean territoryAllyDenyBuildWhenOffline = true;
    public static boolean territoryAllyPainBuild = false;
    public static boolean territoryAllyPainBuildWhenOffline = false;
    public static boolean territoryAllyDenyUsage = true;
    public static boolean territoryTruceDenyBuild = true;
    public static boolean territoryTruceDenyBuildWhenOffline = true;
    public static boolean territoryTrucePainBuild = false;
    public static boolean territoryTrucePainBuildWhenOffline = false;
    public static boolean territoryTruceDenyUsage = true;
    public static boolean territoryBlockCreepers = false;
    public static boolean territoryBlockCreepersWhenOffline = false;
    public static boolean territoryBlockFireballs = false;
    public static boolean territoryBlockFireballsWhenOffline = false;
    public static boolean territoryBlockTNT = false;
    public static boolean territoryBlockTNTWhenOffline = false;
    public static boolean territoryDenyEndermanBlocks = true;
    public static boolean territoryDenyEndermanBlocksWhenOffline = true;
    public static boolean safeZoneDenyBuild = true;
    public static boolean safeZoneDenyUsage = true;
    public static boolean safeZoneBlockTNT = true;
    public static boolean safeZonePreventAllDamageToPlayers = false;
    public static boolean safeZoneDenyEndermanBlocks = true;
    public static boolean warZoneDenyBuild = true;
    public static boolean warZoneDenyUsage = true;
    public static boolean warZoneBlockCreepers = false;
    public static boolean warZoneBlockFireballs = false;
    public static boolean warZoneBlockTNT = true;
    public static boolean warZonePowerLoss = true;
    public static boolean warZoneFriendlyFire = false;
    public static boolean warZoneDenyEndermanBlocks = true;
    public static boolean wildernessDenyBuild = false;
    public static boolean wildernessDenyUsage = false;
    public static boolean wildernessBlockCreepers = false;
    public static boolean wildernessBlockFireballs = false;
    public static boolean wildernessBlockTNT = false;
    public static boolean wildernessPowerLoss = true;
    public static boolean wildernessDenyEndermanBlocks = false;
    // for claimed areas where further faction-member ownership can be defined
    public static boolean ownedAreasEnabled = true;
    public static int ownedAreasLimitPerFaction = 0;
    public static boolean ownedAreasModeratorsCanSet = false;
    public static boolean ownedAreaModeratorsBypass = true;
    public static boolean ownedAreaDenyBuild = true;
    public static boolean ownedAreaPainBuild = false;
    public static boolean ownedMessageOnBorder = true;
    public static boolean ownedMessageInsideTerritory = true;
    public static boolean ownedMessageByChunk = false;
    public static boolean pistonProtectionThroughDenyBuild = true;
    public static Set<Material> territoryProtectedMaterials = EnumSet.noneOf(Material.class);
    public static Set<Material> territoryDenyUsageMaterials = EnumSet.noneOf(Material.class);
    //public static Set<Material> territoryProtectedMaterialsWhenOffline = EnumSet.noneOf(Material.class);
    //public static Set<Material> territoryDenyUsageMaterialsWhenOffline = EnumSet.noneOf(Material.class);
    public static transient Set<EntityType> safeZoneNerfedCreatureTypes = EnumSet.noneOf(EntityType.class);
    /// <summary>
    /// This defines a set of materials which should always be allowed to use, regardless of factions permissions.
    /// Useful for HCF features.
    /// </summary>
    public static Set<Material> territoryBypassProtectedMaterials = EnumSet.noneOf(Material.class);

    public static boolean enableClickToClaim = true;

    //public static Set<Material> territoryCancelAndAllowItemUseMaterial = new HashSet<>();
    //public static Set<Material> territoryDenySwitchMaterials = new HashSet<>();
    public static boolean allowCreeperEggingChests = true;

    // Economy settings
    public static boolean econEnabled = false;
    public static String econUniverseAccount = "";
    public static double econCostClaimWilderness = 30.0;
    public static double econCostClaimFromFactionBonus = 0.0;
    public static double econOverclaimRewardMultiplier = 0.0;
    public static double econClaimAdditionalMultiplier = 0.5;
    public static double econClaimRefundMultiplier = 0.7;
    public static double econClaimUnconnectedFee = 0.0;
    public static double econCostCreate = 100.0;
    public static double econCostOwner = 15.0;
    public static double econCostSethome = 30.0;
    public static double econCostJoin = 0.0;
    public static double econCostLeave = 0.0;
    public static double econCostKick = 0.0;
    public static double econCostInvite = 0.0;
    public static double econCostHome = 0.0;
    public static double econCostTag = 0.0;
    public static double econCostDesc = 0.0;
    public static double econCostTitle = 0.0;
    public static double econCostList = 0.0;
    public static double econCostMap = 0.0;
    public static double econCostPower = 0.0;
    public static double econCostShow = 0.0;
    public static double econFactionStartingBalance = 0.0;
    public static double econDenyWithdrawWhenMinutesAgeLessThan = 2880; // 2 days
    public static String dateFormat = "HH:mm dd/MM/yyyy";


    // -------------------------------------------- //
    // INTEGRATION: DYNMAP
    // -------------------------------------------- //
    public static double econCostStuck = 0.0;
    public static double econCostOpen = 0.0;
    public static double econCostAlly = 0.0;
    public static double econCostTruce = 0.0;
    public static double econCostEnemy = 0.0;
    public static double econCostNeutral = 0.0;
    public static double econCostNoBoom = 0.0;
    // Should the dynmap intagration be used?
    public static boolean dynmapUse = false;
    // Name of the Factions layer
    public static String dynmapLayerName = "Factions";
    // Should the layer be visible per default
    public static boolean dynmapLayerVisible = true;
    // Ordering priority in layer menu (low goes before high - default is 0)
    public static int dynmapLayerPriority = 2;
    // (optional) set minimum zoom level before layer is visible (0 = default, always visible)
    public static int dynmapLayerMinimumZoom = 0;
    // Format for popup - substitute values for macros
    public static String dynmapDescription =
            "<div class=\"infowindow\">\n"
                    + "<span style=\"font-weight: bold; font-size: 150%;\">%name%</span><br>\n"
                    + "<span style=\"font-style: italic; font-size: 110%;\">%description%</span><br>"
                    + "<br>\n"
                    + "<span style=\"font-weight: bold;\">Leader:</span> %players.leader%<br>\n"
                    + "<span style=\"font-weight: bold;\">Admins:</span> %players.admins.count%<br>\n"
                    + "<span style=\"font-weight: bold;\">Moderators:</span> %players.moderators.count%<br>\n"
                    + "<span style=\"font-weight: bold;\">Members:</span> %players.normals.count%<br>\n"
                    + "<span style=\"font-weight: bold;\">TOTAL:</span> %players.count%<br>\n"
                    + "</br>\n"
                    + "<span style=\"font-weight: bold;\">Bank:</span> %money%<br>\n"
                    + "<br>\n"
                    + "</div>";
    // Enable the %money% macro. Only do this if you know your economy manager is thread-safe.
    public static boolean dynmapDescriptionMoney = false;
    // Allow players in faction to see one another on Dynmap (only relevant if Dynmap has 'player-info-protected' enabled)
    public static boolean dynmapVisibilityByFaction = true;
    // Optional setting to limit which regions to show.
    // If empty all regions are shown.
    // Specify Faction either by name or UUID.
    // To show all regions on a given world, add 'world:<worldname>' to the list.
    public static Set<String> dynmapVisibleFactions = new HashSet<>();
    // Optional setting to hide specific Factions.
    // Specify Faction either by name or UUID.
    // To hide all regions on a given world, add 'world:<worldname>' to the list.
    public static Set<String> dynmapHiddenFactions = new HashSet<>();
    public static DynmapStyle dynmapDefaultStyle = new DynmapStyle()
            .setStrokeColor(DYNMAP_STYLE_LINE_COLOR)
            .setLineOpacity(DYNMAP_STYLE_LINE_OPACITY)
            .setLineWeight(DYNMAP_STYLE_LINE_WEIGHT)
            .setFillColor(DYNMAP_STYLE_FILL_COLOR)
            .setFillOpacity(DYNMAP_STYLE_FILL_OPACITY)
            .setHomeMarker(DYNMAP_STYLE_HOME_MARKER)
            .setBoost(DYNMAP_STYLE_BOOST);

    // Optional per Faction style overrides. Any defined replace those in dynmapDefaultStyle.
    // Specify Faction either by name or UUID.
    public static Map<String, DynmapStyle> dynmapFactionStyles = ImmutableMap.of(
            "SafeZone", new DynmapStyle().setStrokeColor("#FF00FF").setFillColor("#FF00FF").setBoost(false),
            "WarZone", new DynmapStyle().setStrokeColor("#FF0000").setFillColor("#FF0000").setBoost(false)
    );


    //Faction banks, to pay for land claiming and other costs instead of individuals paying for them
    public static boolean bankEnabled = true;
    public static boolean bankMembersCanWithdraw = false; //Have to be at least moderator to withdraw or pay money to another faction
    public static boolean bankFactionPaysCosts = true; //The faction pays for faction command costs, such as sethome
    public static boolean bankFactionPaysLandCosts = true; //The faction pays for land claiming costs.

    // mainly for other plugins/mods that use a fake player to take actions, which shouldn't be subject to our protections
    public static Set<String> playersWhoBypassAllProtection = new LinkedHashSet<>();

    public static boolean useWorldConfigurationsAsWhitelist = false;
    public static Set<String> worldsNoClaiming = new LinkedHashSet<>();
    public static Set<String> worldsNoFactionsPlugin = new LinkedHashSet<>();

    public static Set<String> worldsNoPowerLoss = new LinkedHashSet<>();
    public static Set<String> worldsIgnorePvP = new LinkedHashSet<>();
    public static Set<String> worldsNoWildernessProtection = new LinkedHashSet<>();

    // faction-<factionId>
    public static int defaultMaxVaults = 0;
    public static boolean disableFlightOnFactionClaimChange = true;

    public static Backend backEnd = Backend.JSON;

    // Taller and wider for "bigger f map"
    public static int mapHeight = 17;
    public static int mapWidth = 49;
    public static transient char[] mapKeyChrs = "\\/#$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz?".toCharArray();


    //Cooldown for /f logout in seconds
    public static long logoutCooldown = 30;

    // -------------------------------------------- //
    // Persistance
    // ----------------------------------------- //


    // Default Faction Permission Settings.
    public static boolean useLockedPermissions = false;
    public static boolean useCustomDefaultPermissions = true;
    public static boolean usePermissionHints = false;
    public static HashMap<String, DefaultPermissions> defaultFactionPermissions = new HashMap<>();
    public static HashSet<PermissableAction> lockedPermissions = new HashSet<>();

    private static transient Conf i = new Conf();

    static {
        lockedPermissions.add(PermissableAction.CHEST);
    }

    static {
        baseCommandAliases.add("f");

        blacklistedFactionNames.add("somenamehere");

        worldsNoFactionsPlugin.add("someworldnamehere");

        territoryEnemyDenyCommands.add("home");
        territoryEnemyDenyCommands.add("ehome");
        territoryEnemyDenyCommands.add("homes");
        territoryEnemyDenyCommands.add("sethome");
        territoryEnemyDenyCommands.add("esethome");
        territoryEnemyDenyCommands.add("createhome");
        territoryEnemyDenyCommands.add("ecreatehome");
        territoryEnemyDenyCommands.add("spawn");
        territoryEnemyDenyCommands.add("espawn");
        territoryEnemyDenyCommands.add("tpahere");
        territoryEnemyDenyCommands.add("etpahere");
        territoryEnemyDenyCommands.add("tpaccept");
        territoryEnemyDenyCommands.add("etpaccept");
        territoryEnemyDenyCommands.add("tpyes");
        territoryEnemyDenyCommands.add("etpyes");
        territoryEnemyDenyCommands.add("call");
        territoryEnemyDenyCommands.add("ecall");
        territoryEnemyDenyCommands.add("tpa");
        territoryEnemyDenyCommands.add("etpa");
        territoryEnemyDenyCommands.add("etpask");

        if (FactionsPlugin.getInstance().version >= 17) {
            safeZoneNerfedCreatureTypes.add(EntityType.GLOW_SQUID);
            safeZoneNerfedCreatureTypes.add(EntityType.AXOLOTL);
            safeZoneNerfedCreatureTypes.add(EntityType.ZOMBIFIED_PIGLIN);
        } else if (FactionsPlugin.getInstance().version == 16) {
            safeZoneNerfedCreatureTypes.add(EntityType.ZOMBIFIED_PIGLIN);
        } else {
            safeZoneNerfedCreatureTypes.add(EntityType.valueOf("PIG_ZOMBIE"));
        }

        territoryDenyUsageMaterials.add(XMaterial.FLINT_AND_STEEL.parseMaterial());
        if(FactionsPlugin.getInstance().version != 8) {
            territoryDenyUsageMaterials.add(XMaterial.END_CRYSTAL.parseMaterial());
        }
        territoryDenyUsageMaterials.add(XMaterial.BUCKET.parseMaterial());
        territoryDenyUsageMaterials.add(XMaterial.WATER_BUCKET.parseMaterial());
        territoryDenyUsageMaterials.add(XMaterial.LAVA_BUCKET.parseMaterial());

        if(FactionsPlugin.getInstance().version == 8) {
            territoryDenyUsageMaterials.add(XMaterial.matchXMaterial("BOAT").get().parseMaterial());
        }

        if(FactionsPlugin.getInstance().version >= 9) {
            territoryDenyUsageMaterials.add(XMaterial.matchXMaterial("ACACIA_BOAT").get().parseMaterial());
            territoryDenyUsageMaterials.add(XMaterial.matchXMaterial("BIRCH_BOAT").get().parseMaterial());
            territoryDenyUsageMaterials.add(XMaterial.matchXMaterial("DARK_OAK_BOAT").get().parseMaterial());
            territoryDenyUsageMaterials.add(XMaterial.matchXMaterial("JUNGLE_BOAT").get().parseMaterial());
            territoryDenyUsageMaterials.add(XMaterial.matchXMaterial("OAK_BOAT").get().parseMaterial());
            territoryDenyUsageMaterials.add(XMaterial.matchXMaterial("SPRUCE_BOAT").get().parseMaterial());
        }

        if(FactionsPlugin.getInstance().version >= 19) {
            territoryDenyUsageMaterials.add(XMaterial.matchXMaterial("MANGROVE_BOAT").get().parseMaterial());
        }

        if(FactionsPlugin.getInstance().version >= 20) {
            territoryDenyUsageMaterials.add(XMaterial.matchXMaterial("BAMBOO_RAFT").get().parseMaterial());
            territoryDenyUsageMaterials.add(XMaterial.matchXMaterial("CHERRY_BOAT").get().parseMaterial());
        }

        if (FactionsPlugin.getInstance().version != 7) {
            territoryDenyUsageMaterials.add(XMaterial.ARMOR_STAND.parseMaterial());
        }

        if (FactionsPlugin.getInstance().version >= 13) {
            territoryDenyUsageMaterials.add(XMaterial.COD_BUCKET.parseMaterial());
            territoryDenyUsageMaterials.add(XMaterial.PUFFERFISH_BUCKET.parseMaterial());
            territoryDenyUsageMaterials.add(XMaterial.SALMON_BUCKET.parseMaterial());
            territoryDenyUsageMaterials.add(XMaterial.TROPICAL_FISH_BUCKET.parseMaterial());
        }

        if (FactionsPlugin.getInstance().version >= 17) {
            territoryDenyUsageMaterials.add(XMaterial.AXOLOTL_BUCKET.parseMaterial());
            territoryDenyUsageMaterials.add(XMaterial.POWDER_SNOW_BUCKET.parseMaterial());
        }


        /// TODO: Consider removing this in a future release, as permissions works just fine now
        territoryProtectedMaterials.add(Material.BEACON);

        // Config is not loading if value is empty ???
        territoryBypassProtectedMaterials.add(Material.COOKIE);
        territoryBypassProtectedMaterials.add(Material.CHEST);
        territoryBypassProtectedMaterials.add(Material.TRAPPED_CHEST);


        safeZoneNerfedCreatureTypes.add(EntityType.BLAZE);
        safeZoneNerfedCreatureTypes.add(EntityType.CAVE_SPIDER);
        safeZoneNerfedCreatureTypes.add(EntityType.CREEPER);
        safeZoneNerfedCreatureTypes.add(EntityType.ENDER_DRAGON);
        safeZoneNerfedCreatureTypes.add(EntityType.ENDERMAN);
        safeZoneNerfedCreatureTypes.add(EntityType.GHAST);
        safeZoneNerfedCreatureTypes.add(EntityType.MAGMA_CUBE);

        safeZoneNerfedCreatureTypes.add(EntityType.SILVERFISH);
        safeZoneNerfedCreatureTypes.add(EntityType.SKELETON);
        safeZoneNerfedCreatureTypes.add(EntityType.SPIDER);
        safeZoneNerfedCreatureTypes.add(EntityType.SLIME);
        safeZoneNerfedCreatureTypes.add(EntityType.WITCH);
        safeZoneNerfedCreatureTypes.add(EntityType.WITHER);
        safeZoneNerfedCreatureTypes.add(EntityType.ZOMBIE);

        // Is this called lazy load?
        defaultFactionPermissions.put("COLEADER", new DefaultPermissions(true));
        defaultFactionPermissions.put("MODERATOR", new DefaultPermissions(true));
        defaultFactionPermissions.put("NORMAL", new DefaultPermissions(false));
        defaultFactionPermissions.put("RECRUIT", new DefaultPermissions(false));
        defaultFactionPermissions.put("ALLY", new DefaultPermissions(false));
        defaultFactionPermissions.put("ENEMY", new DefaultPermissions(false));
        defaultFactionPermissions.put("TRUCE", new DefaultPermissions(false));
        defaultFactionPermissions.put("NEUTRAL", new DefaultPermissions(false));
    }

    public static void load() {
        FactionsPlugin.getInstance().persist.loadOrSaveDefault(i, Conf.class, "conf");
    }

    public static void save() {
        FactionsPlugin.getInstance().persist.save(i);
    }

    public static void saveSync() {
        FactionsPlugin.instance.persist.saveSync(i);
    }

    public enum Backend {
        JSON,
        //MYSQL,  TODO add MySQL storage
        ;
    }
}

