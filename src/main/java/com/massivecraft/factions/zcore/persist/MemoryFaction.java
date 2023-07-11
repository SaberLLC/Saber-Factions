package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.FactionDisbandEvent.PlayerDisbandReason;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.missions.Mission;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.struct.BanInfo;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.DefaultPermissions;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.frame.fupgrades.UpgradeManager;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public abstract class MemoryFaction implements Faction, EconomyParticipator {
    public HashMap<Integer, String> rules = new HashMap<>();
    public int tnt;
    public Location checkpoint;
    public LazyLocation vault;
    public HashMap<String, Integer> upgrades = new HashMap<>();
    protected String id = null;
    protected boolean peacefulExplosionsEnabled;
    protected boolean permanent;
    protected String tag;
    protected String description;
    protected boolean open;
    protected boolean peaceful;
    protected Integer permanentPower;
    protected LazyLocation home;
    protected long foundedDate;
    protected transient long lastPlayerLoggedOffTime;
    protected double money;
    protected double powerBoost;
    protected String paypal;
    protected Map<String, Relation> relationWish = new HashMap<>();
    protected Map<FLocation, Set<String>> claimOwnership = new ConcurrentHashMap<>();
    protected transient Set<FPlayer> fplayers = new HashSet<>();
    protected transient Set<FPlayer> alts = new HashSet<>();
    protected Set<String> invites = new HashSet<>();
    protected Set<String> altinvites = new HashSet<>();
    protected HashMap<String, List<String>> announcements = new HashMap<>();
    protected ConcurrentHashMap<String, LazyLocation> warps = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<String, String> warpPasswords = new ConcurrentHashMap<>();
    protected int maxVaults;
    protected Role defaultRole;
    protected Map<Permissable, Map<PermissableAction, Access>> permissions = new HashMap<>();
    protected Set<BanInfo> bans = new HashSet<>();
    protected String player;
    protected String discord;
    Inventory chest;
    Map<String, Object> bannerSerialized;
    private long lastDeath;
    private int strikes = 0;
    private int points = 0;
    private Map<String, Mission> missions = new ConcurrentHashMap<>();
    private int wallCheckMinutes;
    private int bufferCheckMinutes;
    private Map<Long, String> checks;
    private Map<UUID, Integer> playerWallCheckCount;
    private Map<UUID, Integer> playerBufferCheckCount;
    private boolean weeWoo;
    private int tntBankSize;
    private int warpLimit;
    private double reinforcedArmor;
    private List<String> completedMissions;
    private int allowedSpawnerChunks;
    private Set<FastChunk> spawnerChunks;
    private boolean protectedfac = true;
    private boolean cloaked;


    // -------------------------------------------- //
    // Construct
    // -------------------------------------------- //
    public MemoryFaction() {
    }

    public MemoryFaction(String id) {
        this.id = id;
        this.open = Conf.newFactionsDefaultOpen;
        this.tag = "???";
        this.description = TL.GENERIC_DEFAULTDESCRIPTION.toString();
        this.lastPlayerLoggedOffTime = 0;
        this.peaceful = false;
        this.peacefulExplosionsEnabled = false;
        this.permanent = false;
        this.money = 0.0;
        this.powerBoost = 0.0;
        this.missions = new ConcurrentHashMap<>();
        this.foundedDate = System.currentTimeMillis();
        this.maxVaults = Conf.defaultMaxVaults;
        this.defaultRole = Role.RECRUIT;
        this.wallCheckMinutes = 0;
        this.bufferCheckMinutes = 0;
        this.weeWoo = false;
        this.checks = new ConcurrentHashMap<>();
        this.playerWallCheckCount = new ConcurrentHashMap<>();
        this.playerBufferCheckCount = new ConcurrentHashMap<>();
        this.completedMissions = new ArrayList<>();
        allowedSpawnerChunks = Conf.allowedSpawnerChunks;
        spawnerChunks = new HashSet<>();
        cloaked = false;
        resetPerms(); // Reset on new Faction so it has default values.
    }

    public MemoryFaction(MemoryFaction old) {
        id = old.id;
        peacefulExplosionsEnabled = old.peacefulExplosionsEnabled;
        permanent = old.permanent;
        tag = old.tag;
        description = old.description;
        open = old.open;
        foundedDate = old.foundedDate;
        peaceful = old.peaceful;
        permanentPower = old.permanentPower;
        home = old.home;
        lastPlayerLoggedOffTime = old.lastPlayerLoggedOffTime;
        money = old.money;
        powerBoost = old.powerBoost;
        missions = new ConcurrentHashMap<>();
        this.completedMissions = new ArrayList<>();
        relationWish = old.relationWish;
        claimOwnership = old.claimOwnership;
        fplayers = new HashSet<>();
        alts = new HashSet<>();
        invites = old.invites;
        announcements = old.announcements;
        this.defaultRole = Role.NORMAL;
        this.wallCheckMinutes = 0;
        this.bufferCheckMinutes = 0;
        this.weeWoo = false;
        this.cloaked = false;
        this.checks = new ConcurrentHashMap<>();
        allowedSpawnerChunks = Conf.allowedSpawnerChunks;
        spawnerChunks = new HashSet<>();
        this.playerWallCheckCount = new ConcurrentHashMap<>();
        this.playerBufferCheckCount = new ConcurrentHashMap<>();
        resetPerms(); // Reset on new Faction so it has default values.
    }


    public boolean isFactionCloaked() {
        return cloaked;
    }

    public void setIsFactionCloaked(boolean cloaked) {
        this.cloaked = cloaked;
    }

    public boolean isProtected() {
        return this.protectedfac;
    }

    public void setProtected(boolean protectedfac) {
        this.protectedfac = protectedfac;
    }

    public int getSpawnerChunkCount() {
        return this.spawnerChunks.size();
    }

    public void clearSpawnerChunks() {
        this.spawnerChunks.clear();
    }

    public int getAllowedSpawnerChunks() {
        return this.allowedSpawnerChunks;
    }

    public void setAllowedSpawnerChunks(int chunks) {
        this.allowedSpawnerChunks = chunks;
    }

    public Set<FastChunk> getSpawnerChunks() {
        return this.spawnerChunks;
    }

    public void setSpawnerChunks(Set<FastChunk> spawnerChunks) {
        this.spawnerChunks = spawnerChunks;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getStrikes() {
        return strikes;
    }

    public void setStrikes(int strikes) {
        this.strikes = strikes;
    }

    public HashMap<String, List<String>> getAnnouncements() {
        return this.announcements;
    }

    public void addAnnouncement(FPlayer fPlayer, String msg) {
        List<String> list = announcements.containsKey(fPlayer.getId()) ? announcements.get(fPlayer.getId()) : new ArrayList<>();
        list.add(msg);
        announcements.put(fPlayer.getId(), list);
    }

    public void sendUnreadAnnouncements(FPlayer fPlayer) {
        if (!announcements.containsKey(fPlayer.getId())) {
            return;
        }
        fPlayer.msg(TL.FACTIONS_ANNOUNCEMENT_TOP);
        for (String s : announcements.get(fPlayer.getPlayer().getUniqueId().toString())) {
            fPlayer.sendMessage(s);
        }
        fPlayer.msg(TL.FACTIONS_ANNOUNCEMENT_BOTTOM);
        announcements.remove(fPlayer.getId());
    }

    public void removeAnnouncements(FPlayer fPlayer) {
        announcements.remove(fPlayer.getId());
    }

    public ConcurrentHashMap<String, LazyLocation> getWarps() {
        return this.warps;
    }

    public LazyLocation getWarp(String name) {
        return this.warps.get(name);
    }

    public void setWarp(String name, LazyLocation loc) {
        this.warps.put(name, loc);
    }

    public boolean isWarp(String name) {
        return this.warps.containsKey(name);
    }

    public boolean removeWarp(String name) {
        warpPasswords.remove(name); // remove password no matter what.
        return warps.remove(name) != null;
    }

    public boolean isWarpPassword(String warp, String password) {
        return hasWarpPassword(warp) && warpPasswords.get(warp.toLowerCase()).equals(password);
    }

    public String getDiscord() {
        return this.discord;
    }

    public void setDiscord(String link) {
        this.discord = link;
    }

    public String getPaypal() {
        return this.paypal;
    }

    public void paypalSet(String paypal) {
        this.paypal = paypal;
    }

    public boolean hasWarpPassword(String warp) {
        return warpPasswords.containsKey(warp.toLowerCase());
    }

    public void setWarpPassword(String warp, String password) {
        warpPasswords.put(warp.toLowerCase(), password);
    }

    public void clearWarps() {
        warps.clear();
    }

    public int getMaxVaults() {
        return this.maxVaults;
    }

    public void setMaxVaults(int value) {
        this.maxVaults = value;
    }

    public String getFocused() {
        return this.player;
    }

    public void setFocused(String fp) {
        this.player = fp;
    }

    public Set<String> getInvites() {
        return invites;
    }

    public Set<String> getAltInvites() {
        return altinvites;
    }

    public void deinviteAlt(FPlayer fplayer) {
        altinvites.remove(fplayer.getId());
    }

    public void deinviteAllAlts() {
        altinvites.clear();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void invite(FPlayer fplayer) {
        this.invites.add(fplayer.getId());
    }

    public void altInvite(FPlayer fplayer) {
        this.altinvites.add(fplayer.getId());
    }

    public void deinvite(FPlayer fplayer) {
        this.invites.remove(fplayer.getId());
        this.altinvites.remove(fplayer.getId());
    }

    public boolean altInvited(FPlayer fplayer) {
        return this.altinvites.contains(fplayer.getId());
    }

    public boolean isInvited(FPlayer fplayer) {
        return this.invites.contains(fplayer.getId()) || this.altinvites.contains(fplayer.getId());
    }

    public void ban(FPlayer target, FPlayer banner) {
        BanInfo info = new BanInfo(banner.getId(), target.getId(), System.currentTimeMillis());
        this.bans.add(info);
    }

    public void unban(FPlayer player) {
        bans.removeIf(banInfo -> banInfo.getBanned().equalsIgnoreCase(player.getId()));
    }

    @Override
    public void disband(Player disbander) {
        disband(disbander, PlayerDisbandReason.PLUGIN);
    }

    @Override
    public void disband(Player disbander, PlayerDisbandReason reason) {

        boolean disbanderIsConsole = disbander == null;
        FPlayer fdisbander = null;
        if (!disbanderIsConsole) {
            fdisbander = FPlayers.getInstance().getByOfflinePlayer(disbander);
        }


        FactionDisbandEvent disbandEvent = new FactionDisbandEvent(disbander, this.getId(), reason);
        Bukkit.getServer().getPluginManager().callEvent(disbandEvent);
        if (disbandEvent.isCancelled()) {
            return;
        }

        // Send FPlayerLeaveEvent for each player in the faction and reset their Discord settings
        for (FPlayer fplayer : this.getFPlayers()) {
            if (fplayer.isInFactionsChest()) {
                fplayer.getPlayer().closeInventory();
            }
            Bukkit.getServer().getPluginManager().callEvent(new FPlayerLeaveEvent(fplayer, this, FPlayerLeaveEvent.PlayerLeaveReason.DISBAND));
        }

        if (Conf.logFactionDisband) {
            //TODO: Format this correctly and translate.
            Logger.print("The faction " + this.getTag() + " (" + this.getId() + ") was disbanded by " + (disbanderIsConsole ? "console command" : fdisbander.getName()) + ".", Logger.PrefixType.DEFAULT);
        }

        if (Econ.shouldBeUsed() && !disbanderIsConsole) {
            // Should we prevent to withdraw money if the faction was just created
            //Give all the faction's money to the disbander
            double amount = this.getFactionBalance();

            Econ.transferMoney(fdisbander, this, fdisbander, amount, false);

            if (amount > 0.0) {
                String amountString = Econ.moneyString(amount);
                msg(TL.COMMAND_DISBAND_HOLDINGS, amountString);
                //TODO: Format this correctly and translate
                Logger.print(fdisbander.getName() + " has been given bank holdings of " + amountString + " from disbanding " + this.getTag() + ".", Logger.PrefixType.DEFAULT);
            }
        }
        Factions.getInstance().removeFaction(this.getId());
        FTeamWrapper.applyUpdates(this);
    }

    public boolean isBanned(FPlayer player) {
        for (BanInfo info : bans) {
            if (info.getBanned().equalsIgnoreCase(player.getId())) {
                return true;
            }
        }

        return false;
    }


    public Set<BanInfo> getBannedPlayers() {
        return this.bans;
    }

    public String getRule(int index) {
        if (rules.size() == 0) return null;
        return rules.get(index);
    }

    public HashMap<Integer, String> getRulesMap() {
        return rules;
    }

    public void setRule(int index, String rule) {
        rules.put(index, rule);
    }

    public void removeRule(int index) {
        HashMap<Integer, String> newRule = rules;
        newRule.remove(index);
    }

    public void addTnt(int amt) {
        tnt += amt;
    }

    public void takeTnt(int amt) {
        tnt -= amt;
    }

    public int getTnt() {
        return tnt;
    }

    public void setTnt(int amt) {
        tnt = amt;
    }

    public Location getVault() {
        if (vault == null) {
            return null;
        }
        return vault.getLocation();
    }

    public void setVault(Location vaultLocation) {
        if (vaultLocation == null) {
            vault = null;
            return;
        }
        vault = new LazyLocation(vaultLocation);
    }

    public int getUpgrade(String upgradeName) {
        return upgrades.getOrDefault(upgradeName, 0);
    }

    @Override
    public Inventory getChestInventory() {
        if (chest == null) {
            this.chest = Bukkit.createInventory(null, getChestSize(), CC.translate(FactionsPlugin.getInstance().getConfig().getString("fchest.Inventory-Title")));
            return chest;
        }
        return chest;
    }

    private int getChestSize() {
        int size = FactionsPlugin.getInstance().getConfig().getInt("fchest.Default-Size");
        int chestUpgrade = getUpgrade("Chest");
        if (chestUpgrade > 0) {
            int upgradedSize = FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getInt("fupgrades.MainMenu.Chest.Chest-Size.level-" + chestUpgrade, -1);
            if (upgradedSize > -1) {
                size = upgradedSize;
            } else {
                FactionsPlugin.getInstance().getLogger().severe(TextUtil.parse(TL.COMMAND_UPGRADES_LEVEL_ERROR.toString(), "CHEST", chestUpgrade));
            }
        }
        return size * 9;
    }


    @Override
    public void setChestSize(int chestSize) {
        ItemStack[] contents = this.getChestInventory().getContents();
        chest = Bukkit.createInventory(null, chestSize, CC.translate(FactionsPlugin.getInstance().getConfig().getString("fchest.Inventory-Title")));
        chest.setContents(contents);
    }


    @Override
    public void setBannerPattern(ItemStack banner) {
        bannerSerialized = banner.serialize();
    }


    @Override
    public int getWarpsLimit() {
        if (warpLimit == 0) {
            return FactionsPlugin.getInstance().getConfig().getInt("max-warps");
        }
        return warpLimit;
    }

    @Override
    public void setWarpsLimit(int warpLimit) {
        this.warpLimit = warpLimit;
    }

    @Override
    public int getTntBankLimit() {
        if (tntBankSize == 0) {
            return FactionsPlugin.getInstance().getConfig().getInt("ftnt.Bank-Limit");
        }
        return tntBankSize;
    }

    @Override
    public void setTntBankLimit(int newLimit) {
        tntBankSize = newLimit;
    }

    @Override
    public double getReinforcedArmor() {
        return this.reinforcedArmor;
    }

    @Override
    public void setReinforcedArmor(double newPercent) {
        reinforcedArmor = newPercent;
    }

    @Override
    public ItemStack getBanner() {
        if (bannerSerialized == null) {
            return null;
        }
        return ItemStack.deserialize(bannerSerialized);
    }

    public void setUpgrade(String upgrade, int level) {
        upgrades.put(upgrade, level);
    }

    public int getWallCheckMinutes() {
        return this.wallCheckMinutes;
    }

    public void setWallCheckMinutes(int wallCheckMinutes) {
        this.wallCheckMinutes = wallCheckMinutes;
    }

    public int getBufferCheckMinutes() {
        return this.bufferCheckMinutes;
    }

    public void setBufferCheckMinutes(int bufferCheckMinutes) {
        this.bufferCheckMinutes = bufferCheckMinutes;
    }

    public Map<Long, String> getChecks() {
        return this.checks;
    }

    public Map<UUID, Integer> getPlayerBufferCheckCount() {
        return this.playerBufferCheckCount;
    }

    public Map<UUID, Integer> getPlayerWallCheckCount() {
        return this.playerWallCheckCount;
    }

    public boolean isWeeWoo() {
        return this.weeWoo;
    }

    public void setWeeWoo(boolean weeWoo) {
        this.weeWoo = weeWoo;
    }

    public Location getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(Location location) {
        checkpoint = location;
    }

    public void clearRules() {
        rules.clear();
    }

    public void addRule(String rule) {
        rules.put(rules.size(), rule);
    }

    public boolean getOpen() {
        return open;
    }

    public void setOpen(boolean isOpen) {
        open = isOpen;
    }

    public boolean isPeaceful() {
        return this.peaceful;
    }

    public void setPeaceful(boolean isPeaceful) {
        this.peaceful = isPeaceful;
    }

    public boolean getPeacefulExplosionsEnabled() {
        return this.peacefulExplosionsEnabled;
    }

    public void setPeacefulExplosionsEnabled(boolean val) {
        peacefulExplosionsEnabled = val;
    }

    public boolean noExplosionsInTerritory() {
        return this.peaceful && !peacefulExplosionsEnabled;
    }

    public boolean isPermanent() {
        return permanent || !this.isNormal();
    }

    public void setPermanent(boolean isPermanent) {
        permanent = isPermanent;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String str) {
        if (Conf.factionTagForceUpperCase) {
            str = str.toUpperCase();
        }
        this.tag = str;
    }

    public void checkPerms() {
        if (this.permissions == null || this.permissions.isEmpty()) {
            this.resetPerms();
        }
    }

    public String getTag(String prefix) {
        return prefix + this.tag;
    }

    public String getTag(Faction otherFaction) {
        if (otherFaction == null) {
            return getTag();
        }
        return this.getTag(this.getColorTo(otherFaction).toString());
    }

    public String getTag(FPlayer otherFplayer) {
        if (otherFplayer == null) {
            return getTag();
        }
        return this.getTag(this.getColorTo(otherFplayer).toString());
    }

    public String getComparisonTag() {
        return MiscUtil.getComparisonString(this.tag);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public boolean hasHome() {
        return this.getHome() != null;
    }

    public Location getHome() {
        confirmValidHome();
        return (this.home != null) ? this.home.getLocation() : null;
    }

    public void setHome(Location home) {
        this.home = new LazyLocation(home);
    }


    public void deleteHome() {
        this.home = null;
    }

    public long getFoundedDate() {
        if (this.foundedDate == 0) {
            setFoundedDate(System.currentTimeMillis());
        }
        return this.foundedDate;
    }

    public void setFoundedDate(long newDate) {
        this.foundedDate = newDate;
    }

    public void confirmValidHome() {
        if (!Conf.homesMustBeInClaimedTerritory || this.home == null || (this.home.getLocation() != null && Board.getInstance().getFactionAt(FLocation.wrap(this.home.getLocation())) == this)) {
            return;
        }

        msg(TL.COMMAND_HOME_UNSET);
        this.home = null;
    }

    public String getAccountId() {
        // We need to override the default money given to players.
        return "faction-" + this.getId();
    }

    public double getFactionBalance() {
        return this.money;
    }

    public void setFactionBalance(double money) {
        this.money = money;
    }

    public Integer getPermanentPower() {
        return this.permanentPower;
    }

    public void setPermanentPower(Integer permanentPower) {
        this.permanentPower = permanentPower;
    }

    public boolean hasPermanentPower() {
        return this.permanentPower != null;
    }

    public double getPowerBoost() {
        return this.powerBoost;
    }

    public void setPowerBoost(double powerBoost) {
        this.powerBoost = powerBoost;
    }

    public boolean isPowerFrozen() {
        int freezeSeconds = FactionsPlugin.getInstance().getConfig().getInt("hcf.powerfreeze", 0);
        return freezeSeconds != 0 && System.currentTimeMillis() - lastDeath < freezeSeconds * 1000L;

    }

    public long getLastDeath() {
        return this.lastDeath;
    }

    // -------------------------------------------- //
    // F Permissions stuff
    // -------------------------------------------- //

    public void setLastDeath(long time) {
        this.lastDeath = time;
    }

    public int getKills() {
        int kills = 0;
        for (FPlayer fp : getFPlayers()) {
            kills += fp.getKills();
        }

        return kills;
    }

    public int getDeaths() {
        int deaths = 0;
        for (FPlayer fp : getFPlayers()) {
            deaths += fp.getDeaths();
        }

        return deaths;
    }

    public Access getAccess(Permissable permissable, PermissableAction permissableAction) {
        if (permissable == null || permissableAction == null) {
            return Access.UNDEFINED;
        }

        return accessOrElse(permissable, permissableAction, Access.UNDEFINED);
    }

    /**
     * Get the Access of a player. Will use player's Role if they are a faction member. Otherwise, uses their Relation.
     *
     * @param player
     * @param permissableAction
     * @return
     */
    public Access getAccess(FPlayer player, PermissableAction permissableAction) {
        if (player == null || permissableAction == null) return Access.UNDEFINED;
        if (player.getFaction() == this && player.getRole() == Role.LEADER) return Access.ALLOW;

        Permissable perm = player.getFaction() == this ? player.getRole() : player.getFaction().getRelationTo(this);

        return accessOrElse(perm, permissableAction, Access.UNDEFINED);
    }

    private Access accessOrElse(Permissable permissable, PermissableAction permissableAction, Access orElse) {
        Map<PermissableAction, Access> accessMap = this.permissions.get(permissable);
        if (accessMap != null) {
            Access access = accessMap.get(permissableAction);
            if (access != null) {
                return access;
            }
        }
        return orElse;
    }

    public boolean setPermission(Permissable permissable, PermissableAction permissableAction, Access access) {
        if (Conf.useLockedPermissions && Conf.lockedPermissions.contains(permissableAction)) {
            return false;
        }
        Map<PermissableAction, Access> accessMap = permissions.computeIfAbsent(permissable, p -> new HashMap<>(PermissableAction.VALUES.length));
        accessMap.put(permissableAction, access);
        return true;
    }

    public boolean setPermission(Permissable permissable, PermissableAction permissableAction, Access access, FPlayer fPlayer) {
        if (Conf.useLockedPermissions && Conf.lockedPermissions.contains(permissableAction)) {
            fPlayer.msg(TL.COMMAND_PERM_LOCKED);
            return false;
        }
        Map<PermissableAction, Access> accessMap = permissions.computeIfAbsent(permissable, p -> new HashMap<>(PermissableAction.VALUES.length));
        accessMap.put(permissableAction, access);
        return true;
    }

    public void resetPerms() {
        if (!this.isSystemFaction()) {
            permissions.clear();

            upsertPermissions(Relation.VALUES, ignored -> ignored == Relation.MEMBER);
            upsertPermissions(Role.VALUES, ignored -> ignored == Role.LEADER);
        }
    }

    @Deprecated
    public void setDefaultPerms() {
        resetPerms();
    }

    private void upsertPermissions(Permissable[] values, Predicate<Permissable> ignored) {
        for (Permissable value : values) {
            if (ignored.test(value)) {
                continue;
            }
            DefaultPermissions defaultPermissions = Conf.defaultFactionPermissions.get(value.name());
            this.permissions.put(value, defaultPermissions != null && Conf.useCustomDefaultPermissions ? PermissableAction.fromDefaults(defaultPermissions) : PermissableAction.fromPredicated(permissableAction -> false));
        }
    }

    /**
     * Read only map of Permissions.
     *
     * @return
     */
    public Map<Permissable, Map<PermissableAction, Access>> getPermissions() {
        return Collections.unmodifiableMap(permissions);
    }

    public Role getDefaultRole() {
        return this.defaultRole;
    }

    public void setDefaultRole(Role role) {
        this.defaultRole = role;
    }

    // -------------------------------------------- //
    // Extra Getters And Setters
    // -------------------------------------------- //
    public boolean noPvPInTerritory() {
        return isSafeZone() || (peaceful && Conf.peacefulTerritoryDisablePVP);
    }

    public boolean noMonstersInTerritory() {
        return isSafeZone() || (peaceful && Conf.peacefulTerritoryDisableMonsters);
    }

    // -------------------------------
    // Understand the types
    // -------------------------------

    public boolean isNormal() {
        return !(this.isWilderness() || this.isSafeZone() || this.isWarZone());
    }

    @Deprecated
    public boolean isNone() {
        return isWilderness();
    }

    public boolean isWilderness() {
        return this.getId().equals("0");
    }

    public boolean isSafeZone() {
        return this.getId().equals("-1");
    }

    public boolean isWarZone() {
        return this.getId().equals("-2");
    }

    public boolean isSystemFaction() {
        return this.isSafeZone() || this.isWarZone() || this.isWilderness();
    }

    public boolean isPlayerFreeType() {
        return this.isSafeZone() || this.isWarZone();
    }

    // -------------------------------
    // Relation and relation colors
    // -------------------------------

    @Override
    public String describeTo(RelationParticipator that, boolean ucfirst) {
        return RelationUtil.describeThatToMe(this, that, ucfirst);
    }

    @Override
    public String describeTo(RelationParticipator that) {
        return RelationUtil.describeThatToMe(this, that);
    }

    @Override
    public Relation getRelationTo(RelationParticipator rp) {
        return RelationUtil.getRelationTo(this, rp);
    }

    @Override
    public Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful) {
        return RelationUtil.getRelationTo(this, rp, ignorePeaceful);
    }

    @Override
    public ChatColor getColorTo(RelationParticipator rp) {
        return RelationUtil.getColorOfThatToMe(this, rp);
    }

    public Relation getRelationWish(Faction otherFaction) {
        return this.relationWish.getOrDefault(otherFaction.getId(), Relation.fromString(FactionsPlugin.getInstance().getConfig().getString("default-relation", "neutral")));
    }

    public void setRelationWish(Faction otherFaction, Relation relation) {
        this.relationWish.compute(otherFaction.getId(), (s, r) -> relation == Relation.NEUTRAL ? null : relation);
    }

    public int getRelationCount(Relation relation) {
        int count = 0;
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (faction.getRelationTo(this) == relation) {
                count++;
            }
        }
        return count;
    }

    // ----------------------------------------------//
    // Power
    // ----------------------------------------------//
    public double getPower() {
        if (this.hasPermanentPower()) return this.getPermanentPower();
        double ret = 0;
        for (FPlayer fplayer : fplayers) ret += fplayer.getPower();
        if (FactionsPlugin.getInstance().getConfig().getBoolean("f-alts.Have-Power")) {
            for (FPlayer fplayer : alts) {
                ret += fplayer.getPower();
            }
        }
        if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax) {
            ret = Conf.powerFactionMax;
        }
        return ret + this.powerBoost;
    }

    public double getPowerMax() {
        if (this.hasPermanentPower()) return this.getPermanentPower();
        double ret = 0;
        for (FPlayer fplayer : fplayers) ret += fplayer.getPowerMax();
        for (FPlayer fplayer : alts) ret += fplayer.getPowerMax();
        if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax) ret = Conf.powerFactionMax;
        return ret + this.powerBoost;
    }

    public int getPowerRounded() {
        return FastMath.round(this.getPower());
    }

    public int getPowerMaxRounded() {
        return FastMath.round(this.getPowerMax());
    }

    public int getLandRounded() {
        return Board.getInstance().getFactionCoordCount(this);
    }

    public int getLandRoundedInWorld(String worldName) {
        return Board.getInstance().getFactionCoordCountInWorld(this, worldName);
    }

    public boolean hasLandInflation() {
        return this.getLandRounded() > this.getPowerRounded();
    }

    // -------------------------------
    // FPlayers
    // -------------------------------

    // maintain the reference list of FPlayers in this faction
    public void refreshFPlayers() {
        fplayers.clear();
        alts.clear();
        if (this.isPlayerFreeType()) return;
        for (FPlayer fplayer : FPlayers.getInstance().getAllFPlayers()) {
            if (fplayer.getFactionId().equalsIgnoreCase(id)) {
                if (fplayer.isAlt()) {
                    alts.add(fplayer);
                } else {
                    fplayers.add(fplayer);
                }
            }
        }
    }

    public boolean addFPlayer(FPlayer fplayer) {
        return !this.isPlayerFreeType() && fplayers.add(fplayer);
    }

    public boolean removeFPlayer(FPlayer fplayer) {
        return !this.isPlayerFreeType() && fplayers.remove(fplayer);
    }


    public boolean addAltPlayer(FPlayer fplayer) {
        return !this.isPlayerFreeType() && alts.add(fplayer);
    }

    public boolean removeAltPlayer(FPlayer fplayer) {
        return !this.isPlayerFreeType() && alts.remove(fplayer);
    }

    public int getSize() {
        return fplayers.size() + alts.size();
    }

    public Set<FPlayer> getFPlayers() {
        // return a shallow copy of the FPlayer list, to prevent tampering and
        // concurrency issues
        return new HashSet<>(fplayers);
    }

    public Set<FPlayer> getAltPlayers() {
        // return a shallow copy of the FPlayer list, to prevent tampering and
        // concurrency issues
        return new HashSet<>(alts);
    }

    public Set<FPlayer> getFPlayersWhereOnline(boolean online) {
        Set<FPlayer> ret = new HashSet<>(this.getSize());
        Set<FPlayer> onlinePlayers = FPlayers.getInstance().getOnlinePlayers();

        for (FPlayer fplayer : this.fplayers) {
            if (online == onlinePlayers.contains(fplayer)) {
                ret.add(fplayer);
            }
        }
        return ret;
    }

    public Set<FPlayer> getFPlayersWhereOnline(boolean online, FPlayer viewer) {
        Set<FPlayer> ret = new HashSet<>();
        if (!this.isNormal()) return ret;
        for (FPlayer viewed : fplayers) {
            // Add if their online status is what we want
            if (viewed.isOnline() == online) {
                // If we want online, check to see if we are able to see this player
                // This checks if they are in vanish.
                if (online
                        && viewed.getPlayer() != null
                        && viewer.getPlayer() != null
                        && viewer.getPlayer().canSee(viewed.getPlayer())) {
                    ret.add(viewed);
                    // If we want offline, just add them.
                    // Prob a better way to do this but idk.
                } else if (!online) {
                    ret.add(viewed);
                }
            }
        }
        return ret;
    }


    public FPlayer getFPlayerAdmin() {
        if (!this.isNormal()) return null;
        for (FPlayer fplayer : fplayers)
            if (fplayer.getRole() == Role.LEADER) return fplayer;
        return null;
    }

    public FPlayer getFPlayerLeader() {
        return getFPlayerAdmin();
    }

    public ArrayList<FPlayer> getFPlayersWhereRole(Role role) {
        if (!this.isNormal()) {
            return new ArrayList<>(0);
        }
        ArrayList<FPlayer> ret = new ArrayList<>(this.fplayers.size());
        for (FPlayer fplayer : fplayers)
            if (fplayer.getRole() == role) ret.add(fplayer);
        return ret;
    }


    public ArrayList<Player> getOnlinePlayers() {
        if (isPlayerFreeType()) {
            return new ArrayList<>(0);
        }
        ArrayList<Player> ret = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
            if (fplayer.getFaction() == this && !fplayer.isAlt()) {
                ret.add(player);
            }
        }
        return ret;
    }

    // slightly faster check than getOnlinePlayers() if you just want to see if
    // there are any players online
    public boolean hasPlayersOnline() {
        // only real factions can have players online, not safe zone / war zone
        if (this.isPlayerFreeType()) return false;

        boolean has = !Collections.disjoint(this.fplayers, FPlayers.getInstance().getOnlinePlayers());
        if (has) {
            return true;
        }
        return Conf.considerFactionsReallyOfflineAfterXMinutes > 0 && System.currentTimeMillis() < lastPlayerLoggedOffTime + (Conf.considerFactionsReallyOfflineAfterXMinutes * 60000);
    }

    public void memberLoggedOff() {
        if (this.isNormal()) lastPlayerLoggedOffTime = System.currentTimeMillis();
    }

    // used when current leader is about to be removed from the faction;
    // promotes new leader, or disbands faction if no other members left
    @Override
    public void promoteNewLeader() {
        promoteNewLeader(false);
    }

    @Override
    public void promoteNewLeader(boolean autoLeave) {
        if (!this.isNormal()) return;
        if (this.isPermanent() && Conf.permanentFactionsDisableLeaderPromotion) return;
        FPlayer oldLeader = this.getFPlayerAdmin();

        // get list of moderators, or list of normal members if there are no moderators
        ArrayList<FPlayer> replacements = this.getFPlayersWhereRole(Role.MODERATOR);
        if (replacements == null || replacements.isEmpty()) replacements = this.getFPlayersWhereRole(Role.NORMAL);


        if (replacements == null || replacements.isEmpty()) { // faction admin  is the only  member; one-man  faction
            if (this.isPermanent()) {
                if (oldLeader != null) oldLeader.setRole(Role.NORMAL);
                return;
            }

            // no members left and faction isn't permanent, so disband it
            if (Conf.logFactionDisband)
                Logger.print("The faction " + this.getTag() + " (" + this.getId() + ") has been disbanded since it has no members left" + (autoLeave ? " and by inactivity" : "") + ".", Logger.PrefixType.DEFAULT);

            if (FactionsPlugin.getInstance().getConfig().getBoolean("faction-disband-broadcast")) {
                String message = TL.COMMAND_DISBAND_BROADCAST_GENERIC.toString()
                        .replace("{claims}", this.getAllClaims().size() + "");
                for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers())
                    fplayer.msg(message, this.getTag(fplayer));
            }


            FactionDisbandEvent disbandEvent = new FactionDisbandEvent(null, getId(), autoLeave ? PlayerDisbandReason.INACTIVITY : PlayerDisbandReason.LEAVE);
            Bukkit.getPluginManager().callEvent(disbandEvent);

            Factions.getInstance().removeFaction(getId());
        } else { // promote new faction admin
            if (oldLeader != null) oldLeader.setRole(Role.NORMAL);
            replacements.get(0).setRole(Role.LEADER);
            this.msg(TL.AUTOLEAVE_ADMIN_PROMOTED, oldLeader == null ? "" : oldLeader.getName(), replacements.get(0).getName());
            Logger.print("Faction " + this.getTag() + " (" + this.getId() + ") admin was removed. Replacement admin: " + replacements.get(0).getName(), Logger.PrefixType.DEFAULT);
        }
    }

    // ----------------------------------------------//
    // Messages
    // ----------------------------------------------//
    public void msg(String message, Object... args) {
        message = TextUtil.parse(message, args);
        for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) fplayer.sendMessage(message);
    }

    public void msg(TL translation, Object... args) {
        msg(translation.toString(), args);
    }

    public void sendMessage(String message) {
        for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) fplayer.sendMessage(message);
    }

    public void sendMessage(List<String> messages) {
        for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) fplayer.sendMessage(messages);
    }

    // ----------------------------------------------//
    // Ownership of specific claims
    // ----------------------------------------------//

    public Map<FLocation, Set<String>> getClaimOwnership() {
        return claimOwnership;
    }


    @Override
    public Map<String, Mission> getMissions() {
        return this.missions;
    }

    @Override
    public List<String> getCompletedMissions() {
        return this.completedMissions;
    }

    public void clearAllClaimOwnership() {
        claimOwnership.clear();
    }

    public void clearClaimOwnership(FLocation loc) {
        claimOwnership.remove(loc);
    }

    public void clearClaimOwnership(FPlayer player) {
        if (id == null || id.isEmpty()) {
            return;
        }

        String playerId = player.getId();

        this.claimOwnership.entrySet().removeIf(entry -> {
            Set<String> ownerData = entry.getValue();
            return ownerData != null && ownerData.removeIf(s -> s.equals(playerId)) && ownerData.isEmpty();
        });
    }

    public int getCountOfClaimsWithOwners() {
        return claimOwnership.isEmpty() ? 0 : claimOwnership.size();
    }

    public boolean doesLocationHaveOwnersSet(FLocation loc) {
        if (claimOwnership.isEmpty() || !claimOwnership.containsKey(loc)) return false;
        Set<String> ownerData = claimOwnership.get(loc);
        return ownerData != null && !ownerData.isEmpty();
    }

    public boolean isPlayerInOwnerList(FPlayer player, FLocation loc) {
        if (claimOwnership.isEmpty()) return false;
        Set<String> ownerData = claimOwnership.get(loc);
        return ownerData != null && ownerData.contains(player.getId());
    }

    public void setPlayerAsOwner(FPlayer player, FLocation loc) {
        Set<String> ownerData = claimOwnership.get(loc);
        if (ownerData == null) ownerData = new HashSet<>();
        ownerData.add(player.getId());
        claimOwnership.put(loc, ownerData);
    }

    public void removePlayerAsOwner(FPlayer player, FLocation loc) {
        Set<String> ownerData = claimOwnership.get(loc);
        if (ownerData == null) return;
        ownerData.remove(player.getId());
        claimOwnership.put(loc, ownerData);
    }

    public Set<String> getOwnerList(FLocation loc) {
        return claimOwnership.get(loc);
    }

    public String getOwnerListString(FLocation loc) {
        Set<String> ownerData = claimOwnership.get(loc);
        if (ownerData == null || ownerData.isEmpty()) return "";
        StringBuilder ownerList = new StringBuilder();

        for (String anOwnerData : ownerData) {
            if (ownerList.length() > 0) ownerList.append(", ");
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(anOwnerData));
            ownerList.append(offlinePlayer != null ? offlinePlayer.getName() : TL.GENERIC_NULLPLAYER.toString());
        }
        return ownerList.toString();
    }

    public boolean playerHasOwnershipRights(FPlayer fplayer, FLocation loc) {
        // in own faction, with sufficient role or permission to bypass
        // ownership?
        if (fplayer.getFaction() == this && (fplayer.getRole().isAtLeast(Conf.ownedAreaModeratorsBypass ? Role.MODERATOR : Role.LEADER) || Permission.OWNERSHIP_BYPASS.has(fplayer.getPlayer())))
            return true;

        // make sure claimOwnership is initialized
        if (claimOwnership.isEmpty()) return true;

        // need to check the ownership list, then
        Set<String> ownerData = claimOwnership.get(loc);

        // if no owner list, owner list is empty, or player is in owner list,
        // they're allowed
        return ownerData == null || ownerData.isEmpty() || ownerData.contains(fplayer.getId());
    }

    // ----------------------------------------------//
    // Persistance and entity management
    // ----------------------------------------------//
    public void remove() {
        if (Econ.shouldBeUsed()) Econ.setBalance(getAccountId(), 0.0);
        // Clean the board
        ((MemoryBoard) Board.getInstance()).clean(id);
        for (FPlayer fPlayer : fplayers) fPlayer.resetFactionData(false);
        for (FPlayer fPlayer : alts) fPlayer.resetFactionData(false);

        try {
            if (FactionsPlugin.getInstance() != null && FactionsPlugin.getInstance().getFlogManager() != null && FactionsPlugin.getInstance().getFlogManager().getFactionLogMap() != null) {
                FactionsPlugin.getInstance().getFlogManager().getFactionLogMap().remove(this.getId());
            }
        } catch (Exception exception) {
            // empty catch block
        }
    }

    public Set<FLocation> getAllClaims() {
        return Board.getInstance().getAllClaims(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryFaction that = (MemoryFaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
