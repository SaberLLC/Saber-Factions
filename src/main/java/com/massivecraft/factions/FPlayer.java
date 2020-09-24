package com.massivecraft.factions;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.WarmUpUtil;
import mkremins.fanciful.FancyMessage;
import net.dv8tion.jda.core.entities.User;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;


/**
 * Logged in players always have exactly one FPlayer instance. Logged out players may or may not have an FPlayer
 * instance. They will always have one if they are part of a faction. This is because only players with a faction are
 * saved to disk (in order to not waste disk space).
 * The FPlayer is linked to a minecraft player using the player name.
 * The same instance is always returned for the same player. This means you can use the == operator. No .equals method
 * necessary.
 */

public interface FPlayer extends EconomyParticipator {

    void setNotificationsEnabled(boolean notifications);

    boolean hasNotificationsEnabled();

    /**
     * Determine if a player has enemies nearby based on the enemy check task in CmdFly
     * NOTE: THIS VALUE IS ONLY UPDATED WHEN A USER IS USING FLY
     *
     * @return enemiesNearby as a boolean
     */
    boolean hasEnemiesNearby();

    /**
     * Set if this FPlayer has an enemy nearby
     *
     * @param b enemiesNearby
     */
    void setEnemiesNearby(Boolean b);

    /**
     * Get if a player has setup their Discord before
     *
     * @return if the player setup Discord as a boolean
     */
    boolean discordSetup();

    /**
     * Get the players Discord user ID
     *
     * @return players Discord user ID as a String
     */
    String discordUserID();

    /**
     * Set the players Boolean defining if the player has setup their Discord
     *
     * @param b Boolean for discordSetup to be defined to
     */
    void setDiscordSetup(Boolean b);

    /**
     * Set the players Discord user ID
     *
     * @param s String for their user ID to be set to
     */
    void setDiscordUserID(String s);

    /**
     * Get the players Discord user (If the player has not setup Discord it will return null!)
     *
     * @return User from players set Discord User ID
     */
    User discordUser();

    /**
     * Used to check if this player should be served titles
     *
     * @return if this FPlayer has titles enabled as a boolean
     */
    boolean hasTitlesEnabled();

    /**
     * Used to set if player should be served titles
     *
     * @param b Boolean to titlesEnabled to
     */
    void setTitlesEnabled(Boolean b);

    /**
     * Used to determine if a player is in their faction's chest
     *
     * @return if player is in their faction's as a boolean
     */
    boolean isInFactionsChest();

    /**
     * Set if the player is inside of their faction's chest
     */
    void setInFactionsChest(boolean b);

    boolean isAlt();

    void setAlt(boolean alt);

    /**
     * Used to know if stealth is toggled on or off
     *
     * @return if stealth mode is on or not.
     */
    boolean isStealthEnabled();

    /**
     * Toggles the stealth of the FPlayer depending on the parameter
     *
     * @param stealthToggle - toggles stealth
     */
    void setStealth(boolean stealthToggle);

    /**
     * Sets the kills and deaths of a player.
     */
    void login();

    /**
     * Caches the kills and deaths of a player.
     */
    void logout();

    /**
     * gets the faction of a FPlayer.
     *
     * @return Faction of the FPlayer.
     */
    Faction getFaction();

    /**
     * Sets the faction of the FPlayer
     *
     * @param faction faction to set.
     */
    void setFaction(Faction faction, boolean alt);

    /**
     * Gets the faction ID of the player.
     *
     * @return FactionsID string
     */
    String getFactionId();

    /**
     * Check if a player has a faction
     *
     * @return boolean
     */
    boolean hasFaction();

    /**
     * Gets autoleave status
     *
     * @return boolean of the autoleave
     */
    boolean willAutoLeave();

    void setAutoLeave(boolean autoLeave);

    long getLastFrostwalkerMessage();

    void setLastFrostwalkerMessage();

    void setMonitorJoins(boolean monitor);

    boolean isMonitoringJoins();

    Role getRole();

    void setRole(Role role);

    boolean shouldTakeFallDamage();

    void setTakeFallDamage(boolean fallDamage);

    double getPowerBoost();

    void setPowerBoost(double powerBoost);

    Faction getAutoClaimFor();

    void setAutoClaimFor(Faction faction);

    boolean isAutoSafeClaimEnabled();

    void setIsAutoSafeClaimEnabled(boolean enabled);

    boolean isAutoWarClaimEnabled();

    void setIsAutoWarClaimEnabled(boolean enabled);

    boolean isAdminBypassing();

    boolean isVanished();

    void setIsAdminBypassing(boolean val);

    ChatMode getChatMode();

    void setChatMode(ChatMode chatMode);

    boolean isIgnoreAllianceChat();

    void setIgnoreAllianceChat(boolean ignore);

    boolean isSpyingChat();

    void setSpyingChat(boolean chatSpying);

    boolean showScoreboard();

    void setShowScoreboard(boolean show);

    // FIELD: account
    String getAccountId();

    void resetFactionData(boolean doSpoutUpdate);

    void resetFactionData();

    long getLastLoginTime();

    void setLastLoginTime(long lastLoginTime);

    boolean isMapAutoUpdating();

    void setMapAutoUpdating(boolean mapAutoUpdating);

    boolean hasLoginPvpDisabled();

    FLocation getLastStoodAt();

    void setLastStoodAt(FLocation flocation);

    String getTitle();

    void setTitle(CommandSender sender, String title);

    String getName();

    String getTag();

    // Base concatenations:

    String getNameAndSomething(String something);

    String getNameAndTitle();

    String getNameAndTag();

    // Colored concatenations:
    // These are used in information messages

    String getNameAndTitle(Faction faction);

    String getNameAndTitle(FPlayer fplayer);

    // Chat Tag:
    // These are injected into the format of global chat messages.

    String getChatTag();

    // Colored Chat Tag
    String getChatTag(Faction faction);

    String getChatTag(FPlayer fplayer);

    int getKills();

    int getDeaths();

    boolean takeMoney(int amt);

    boolean hasMoney(int amt);

    //Fplayer specific friendly fire.
    boolean hasFriendlyFire();

    void setFriendlyFire(boolean status);

    //inspect Stuff

    boolean isInspectMode();

    void setInspectMode(boolean status);


    // Fly Checks

    Boolean canflyinWilderness();

    Boolean canflyinWarzone();

    Boolean canflyinSafezone();

    Boolean canflyinEnemy();

    Boolean canflyinAlly();

    Boolean canflyinTruce();

    Boolean canflyinNeutral();

    // -------------------------------
    // Relation and relation colors
    // -------------------------------

    @Override
    String describeTo(RelationParticipator that, boolean ucfirst);

    @Override
    String describeTo(RelationParticipator that);

    @Override
    Relation getRelationTo(RelationParticipator rp);

    @Override
    Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful);

    Relation getRelationToLocation();

    @Override
    ChatColor getColorTo(RelationParticipator rp);


    String getRolePrefix();

    //----------------------------------------------//
    // Health
    //----------------------------------------------//
    void heal(int amnt);


    //----------------------------------------------//
    // Power
    //----------------------------------------------//
    double getPower();

    void alterPower(double delta);

    double getPowerMax();

    double getPowerMin();

    int getPowerRounded();

    void setPowerRounded(int power);

    int getPowerMaxRounded();

    int getPowerMinRounded();

    long getMillisPassed();

    long getLastPowerUpdateTime();

    void updatePower();

    void losePowerFromBeingOffline();

    void onDeath();

    //----------------------------------------------//
    // Territory
    //----------------------------------------------//
    boolean isInOwnTerritory();

    boolean isInOthersTerritory();

    boolean isInAllyTerritory();

    boolean isInNeutralTerritory();

    boolean isInEnemyTerritory();

    void sendFactionHereMessage(Faction from);

    // -------------------------------
    // Actions
    // -------------------------------


    void leave(boolean makePay);

    boolean canClaimForFaction(Faction forFaction);

    boolean canClaimForFactionAtLocation(Faction forFaction, Location location, boolean notifyFailure);

    boolean canClaimForFactionAtLocation(Faction forFaction, FLocation location, boolean notifyFailure);

    boolean attemptClaim(Faction forFaction, Location location, boolean notifyFailure);

    boolean attemptClaim(Faction forFaction, FLocation location, boolean notifyFailure);

    boolean isInVault();

    void setInVault(boolean status);

    void msg(String str, Object... args);

    String getId();

    void setId(String id);

    Player getPlayer();

    boolean isOnline();

    void sendMessage(String message);

    void sendMessage(List<String> messages);

    void sendFancyMessage(FancyMessage message);

    void sendFancyMessage(List<FancyMessage> message);

    int getMapHeight();

    void setMapHeight(int height);

    boolean isOnlineAndVisibleTo(Player me);

    void remove();

    boolean isOffline();

    boolean isFlying();

    void setFlying(boolean fly);

    void setFlying(boolean fly, boolean damage);

    boolean isAutoFlying();

    void setAutoFlying(boolean autoFly);

    boolean canFlyAtLocation();

    boolean canFlyAtLocation(FLocation location);

    boolean isSeeingChunk();

    void setSeeingChunk(boolean seeingChunk);

    boolean isEnteringPassword();

    void setEnteringPassword(boolean toggle, String warp);

    String getEnteringWarp();

    void checkIfNearbyEnemies();

    int getCooldown(String cmd);

    void setCooldown(String cmd, long cooldown);

    boolean isCooldownEnded(String cmd);


    // -------------------------------
    // Warmups
    // -------------------------------

    boolean isWarmingUp();

    WarmUpUtil.Warmup getWarmupType();

    void addWarmup(WarmUpUtil.Warmup warmup, int taskId);

    void stopWarmup();

    void clearWarmup();


}