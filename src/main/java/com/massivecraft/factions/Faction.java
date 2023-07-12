package com.massivecraft.factions;

import com.massivecraft.factions.event.FactionDisbandEvent.PlayerDisbandReason;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.missions.Mission;
import com.massivecraft.factions.struct.BanInfo;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.FastChunk;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public interface Faction extends EconomyParticipator {

    double getFactionBalance();

    void setFactionBalance(double money);

    String getDiscord();

    void setDiscord(String link);

    void checkPerms();

    double getReinforcedArmor();

    void setReinforcedArmor(double percent);

    int getWarpsLimit();

    void setWarpsLimit(int limit);

    int getTntBankLimit();

    void setTntBankLimit(int limit);

    int getWallCheckMinutes();

    void setWallCheckMinutes(int minutes);

    int getBufferCheckMinutes();

    void setBufferCheckMinutes(int minutes);

    Map<Long, String> getChecks();

    Map<UUID, Integer> getPlayerBufferCheckCount();

    Map<UUID, Integer> getPlayerWallCheckCount();

    boolean isWeeWoo();

    void setWeeWoo(boolean weeWoo);

    boolean altInvited(FPlayer fplayer);

    Map<String, Mission> getMissions();

    List<String> getCompletedMissions();

    Set<FastChunk> getSpawnerChunks();

    void setSpawnerChunks(Set<FastChunk> fastChunks);

    void clearSpawnerChunks();

    int getSpawnerChunkCount();

    int getAllowedSpawnerChunks();

    void setAllowedSpawnerChunks(int chunks);

    boolean isProtected();

    void setProtected(boolean b);

    void deinviteAlt(FPlayer alt);

    void deinviteAllAlts();

    void altInvite(FPlayer fplayer);

    boolean addAltPlayer(FPlayer fplayer);

    boolean removeAltPlayer(FPlayer fplayer);

    Set<FPlayer> getAltPlayers();

    HashMap<String, List<String>> getAnnouncements();

    ConcurrentHashMap<String, LazyLocation> getWarps();

    LazyLocation getWarp(String name);

    void setWarp(String name, LazyLocation loc);

    boolean isWarp(String name);

    boolean hasWarpPassword(String warp);

    boolean isWarpPassword(String warp, String password);

    void setWarpPassword(String warp, String password);

    boolean removeWarp(String name);

    void clearWarps();

    int getMaxVaults();

    void setMaxVaults(int value);

    void addAnnouncement(FPlayer fPlayer, String msg);

    void sendUnreadAnnouncements(FPlayer fPlayer);

    void removeAnnouncements(FPlayer fPlayer);

    Set<String> getInvites();

    String getFocused();

    void setFocused(String setFocused);

    String getId();

    void setId(String id);

    void invite(FPlayer fplayer);

    void deinvite(FPlayer fplayer);

    void setUpgrade(String upgradeId, int level);

    int getUpgrade(String upgradeId);

    boolean isInvited(FPlayer fplayer);

    void ban(FPlayer target, FPlayer banner);

    int getPoints();

    void setPoints(int points);

    int getStrikes();

    void setStrikes(int strikes);

    void unban(FPlayer player);

    boolean isBanned(FPlayer player);

    Set<BanInfo> getBannedPlayers();

    HashMap<Integer, String> getRulesMap();

    void addRule(String rule);

    void removeRule(int index);

    void clearRules();

    Location getCheckpoint();

    void setCheckpoint(Location location);

    void addTnt(int amt);

    void takeTnt(int amt);

    Location getVault();

    void setVault(Location vaultLocation);

    Inventory getChestInventory();

    void setChestSize(int chestSize);

    void setBannerPattern(ItemStack banner);

    ItemStack getBanner();

    int getTnt();

    void setTnt(int amount);

    String getRule(int index);

    boolean getOpen();

    void setOpen(boolean isOpen);

    boolean isPeaceful();

    void setPeaceful(boolean isPeaceful);

    boolean getPeacefulExplosionsEnabled();

    void setPeacefulExplosionsEnabled(boolean val);

    boolean noExplosionsInTerritory();

    boolean isPermanent();

    void setPermanent(boolean isPermanent);

    String getTag();

    void setTag(String str);

    String getTag(String prefix);

    String getTag(Faction otherFaction);

    String getTag(FPlayer otherFplayer);

    String getComparisonTag();

    String getDescription();

    void setDescription(String value);

    boolean hasHome();

    Location getHome();

    void setHome(Location home);

    void deleteHome();

    long getFoundedDate();

    void setFoundedDate(long newDate);

    void confirmValidHome();

    String getAccountId();

    Integer getPermanentPower();

    void setPermanentPower(Integer permanentPower);

    boolean hasPermanentPower();

    double getPowerBoost();

    void setPowerBoost(double powerBoost);

    boolean noPvPInTerritory();

    boolean noMonstersInTerritory();

    boolean isNormal();

    boolean isSystemFaction();

    @Deprecated
    boolean isNone();

    boolean isWilderness();

    boolean isSafeZone();

    boolean isWarZone();

    boolean isPlayerFreeType();

    boolean isPowerFrozen();

    void setLastDeath(long time);

    int getKills();

    int getDeaths();

    Access getAccess(Permissable permissable, PermissableAction permissableAction);

    Access getAccess(FPlayer player, PermissableAction permissableAction);

    boolean setPermission(Permissable permissable, PermissableAction permissableAction, Access access);

    boolean setPermission(Permissable permissable, PermissableAction permissableAction, Access access, FPlayer fPlayer);

    void resetPerms();

    @Deprecated
    void setDefaultPerms();

    void disband(Player disbander);

    void disband(Player disbander, PlayerDisbandReason reason);

    // -------------------------------
    // Relation and relation colors
    // -------------------------------

    Map<Permissable, Map<PermissableAction, Access>> getPermissions();

    @Override
    String describeTo(RelationParticipator that, boolean ucfirst);

    @Override
    String describeTo(RelationParticipator that);

    @Override
    Relation getRelationTo(RelationParticipator rp);

    @Override
    Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful);

    @Override
    ChatColor getColorTo(RelationParticipator rp);

    Relation getRelationWish(Faction otherFaction);

    void setRelationWish(Faction otherFaction, Relation relation);

    int getRelationCount(Relation relation);

    // ----------------------------------------------//
    // Power
    // ----------------------------------------------//
    double getPower();

    double getPowerMax();

    int getPowerRounded();

    int getPowerMaxRounded();

    int getLandRounded();

    int getLandRoundedInWorld(String worldName);

    // -------------------------------
    // FPlayers
    // -------------------------------

    boolean hasLandInflation();

    // maintain the reference list of FPlayers in this faction
    void refreshFPlayers();

    boolean addFPlayer(FPlayer fplayer);

    boolean removeFPlayer(FPlayer fplayer);

    int getSize();

    Set<FPlayer> getFPlayers();

    Set<FPlayer> getFPlayersWhereOnline(boolean online);

    Set<FPlayer> getFPlayersWhereOnline(boolean online, FPlayer viewer);

    FPlayer getFPlayerAdmin();

    FPlayer getFPlayerLeader();

    ArrayList<FPlayer> getFPlayersWhereRole(Role role);

    ArrayList<Player> getOnlinePlayers();

    // slightly faster check than getOnlinePlayers() if you just want to see if
    // there are any players online
    boolean hasPlayersOnline();

    void memberLoggedOff();

    // used when current leader is about to be removed from the faction;
    // promotes new leader, or disbands faction if no other members left
    void promoteNewLeader();

    void promoteNewLeader(boolean autoLeave);

    Role getDefaultRole();

    void setDefaultRole(Role role);

    // ----------------------------------------------//
    // Messages
    // ----------------------------------------------//
    void msg(String message, Object... args);

    void sendMessage(String message);

    // ----------------------------------------------//
    // Ownership of specific claims
    // ----------------------------------------------//

    void sendMessage(List<String> messages);

    Map<FLocation, Set<String>> getClaimOwnership();

    void clearAllClaimOwnership();

    void clearClaimOwnership(FLocation loc);

    void clearClaimOwnership(FPlayer player);

    int getCountOfClaimsWithOwners();

    boolean doesLocationHaveOwnersSet(FLocation loc);

    boolean isPlayerInOwnerList(FPlayer player, FLocation loc);

    void setPlayerAsOwner(FPlayer player, FLocation loc);

    void removePlayerAsOwner(FPlayer player, FLocation loc);

    Set<String> getOwnerList(FLocation loc);

    String getOwnerListString(FLocation loc);

    boolean playerHasOwnershipRights(FPlayer fplayer, FLocation loc);

    // ----------------------------------------------//
    // Persistance and entity management
    // ----------------------------------------------//
    void remove();

    Set<FLocation> getAllClaims();

    String getPaypal();

    void paypalSet(String paypal);

    // -------------------------------
    // Shields
    // -------------------------------
}
