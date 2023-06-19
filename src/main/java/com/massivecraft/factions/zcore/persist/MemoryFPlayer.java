package com.massivecraft.factions.zcore.persist;

import cc.javajobs.wgbridge.WorldGuardBridge;
import cc.javajobs.wgbridge.infrastructure.struct.WGRegionSet;
import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.event.*;
import com.massivecraft.factions.event.FactionDisbandEvent.PlayerDisbandReason;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.Essentials;
import com.massivecraft.factions.scoreboards.FScoreboard;
import com.massivecraft.factions.scoreboards.sidebar.FInfoSidebar;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.FastUUID;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

import static com.massivecraft.factions.integration.Econ.moneyString;


/**
 * Logged in players always have exactly one FPlayer instance. Logged out players may or may not have an FPlayer
 * instance. They will always have one if they are part of a faction. This is because only players with a faction are
 * saved to disk (in order to not waste disk space).
 * The FPlayer is linked to a minecraft player using the player name.
 * The same instance is always returned for the same player. This means you can use the == operator. No .equals method
 * necessary.
 */

public abstract class MemoryFPlayer implements FPlayer {
    public boolean enemiesNearby = false;
    public boolean inChest = false;
    public boolean discordSetup = false;
    public String discordUserID = "";
    public boolean inVault = false;
    protected HashMap<String, Long> commandCooldown = new HashMap<>();
    protected String factionId;
    protected Role role;
    protected String title;
    protected double power;
    protected double powerBoost;
    protected long lastPowerUpdateTime;
    protected long millisPassed;
    protected long lastLoginTime;
    protected ChatMode chatMode;
    protected boolean ignoreAllianceChat = false;
    protected String id;
    protected String name;
    protected boolean monitorJoins;
    protected boolean spyingChat = false;
    protected boolean showScoreboard = true;
    protected WarmUpUtil.Warmup warmup;
    protected int warmupTask;
    protected boolean isAdminBypassing = false;
    protected int kills, deaths;
    protected boolean willAutoLeave = true;
    protected int mapHeight = 8; // default to old value
    protected boolean isFlying = false;
    protected boolean isAutoFlying = false;
    protected boolean isAlt;
    protected boolean enteringPassword = false;
    protected String enteringPasswordWarp = "";
    protected transient FLocation lastStoodAt = FLocation.empty(); // Where did this player stand the last time we checked?
    protected transient boolean mapAutoUpdating;
    protected transient Faction autoClaimFor;
    protected transient Faction autoUnclaimFor;
    protected transient boolean loginPvpDisabled;
    protected transient long lastFrostwalkerMessage;
    protected transient boolean shouldTakeFallDamage = true;
    protected boolean isStealthEnabled = false;
    protected boolean notificationsEnabled;
    protected boolean titlesEnabled = true;
    protected boolean seeingChunk = false;
    boolean inspectMode = false;
    boolean friendlyFire = false;

    public MemoryFPlayer() {
    }

    public MemoryFPlayer(String id) {
        this.id = id;
        this.resetFactionData(false);
        this.power = Conf.powerPlayerStarting;
        this.lastPowerUpdateTime = System.currentTimeMillis();
        this.lastLoginTime = System.currentTimeMillis();
        this.isAlt = false;
        this.mapAutoUpdating = false;
        this.autoClaimFor = null;
        this.notificationsEnabled = true;
        this.loginPvpDisabled = Conf.noPVPDamageToOthersForXSecondsAfterLogin > 0;
        this.powerBoost = 0.0;
        this.getKills();
        this.getDeaths();
        this.showScoreboard = FactionsPlugin.getInstance().getConfig().getBoolean("scoreboard.default-enabled", false);
        this.mapHeight = Conf.mapHeight;
        this.notificationsEnabled = true;

        if (!Conf.newPlayerStartingFactionID.equals("0") && Factions.getInstance().isValidFactionId(Conf.newPlayerStartingFactionID)) {
            this.factionId = Conf.newPlayerStartingFactionID;
        }
    }

    public MemoryFPlayer(MemoryFPlayer other) {
        this.factionId = other.factionId;
        this.id = other.id;
        this.power = other.power;
        this.lastLoginTime = other.lastLoginTime;
        this.mapAutoUpdating = other.mapAutoUpdating;
        this.autoClaimFor = other.autoClaimFor;
        this.loginPvpDisabled = other.loginPvpDisabled;
        this.powerBoost = other.powerBoost;
        this.role = other.role;
        this.title = other.title;
        this.isAlt = other.isAlt;
        this.chatMode = other.chatMode;
        this.spyingChat = other.spyingChat;
        this.lastStoodAt = other.lastStoodAt;
        this.getKills();
        this.getDeaths();
        this.isAdminBypassing = other.isAdminBypassing;
        this.notificationsEnabled = other.notificationsEnabled;
        this.showScoreboard = FactionsPlugin.getInstance().getConfig().getBoolean("scoreboard.default-enabled", true);
        this.mapHeight = Conf.mapHeight;
        this.notificationsEnabled = true;
    }

    public boolean isAlt() {
        return isAlt;
    }

    public void setAlt(boolean alt) {
        this.isAlt = alt;
    }

    public boolean isStealthEnabled() {
        return this.isStealthEnabled;
    }

    public void setStealth(boolean stealth) {
        this.isStealthEnabled = stealth;
    }

    public void login() {
        this.kills = getPlayer().getStatistic(Statistic.PLAYER_KILLS);
        this.deaths = getPlayer().getStatistic(Statistic.DEATHS);
    }

    public void logout() {
        this.kills = getPlayer().getStatistic(Statistic.PLAYER_KILLS);
        this.deaths = getPlayer().getStatistic(Statistic.DEATHS);
    }

    public int getCooldown(String cmd) {
        int seconds = 0;
        if (this.getPlayer().isOp())
            return 0;
        if (commandCooldown.containsKey(cmd))
            seconds = (int) ((this.commandCooldown.get(cmd) - System.currentTimeMillis()) / 1000);
        return seconds;
    }

    public void setCooldown(String cmd, long cooldown) {
        if (this.getPlayer().isOp())
            return;

        this.commandCooldown.put(cmd, cooldown);
    }

    public boolean isCooldownEnded(String cmd) {
        if (this.getPlayer().isOp())
            return true;
        if (!commandCooldown.containsKey(cmd))
            return true;
        else return commandCooldown.containsKey(cmd) && commandCooldown.get(cmd) <= System.currentTimeMillis();
    }


    public Faction getFaction() {
        if (this.factionId == null) {
            this.factionId = "0";
        }
        return Factions.getInstance().getFactionById(this.factionId);
    }

    public void setFaction(Faction faction, boolean alt) {
        Faction oldFaction = this.getFaction();
        if (oldFaction != null) {
            if (this.isAlt()) oldFaction.removeAltPlayer(this);
            oldFaction.removeFPlayer(this);
        }
        if (alt) faction.addAltPlayer(this);
        else faction.addFPlayer(this);
        this.factionId = faction.getId();
    }

    @Override
    public void setNotificationsEnabled(boolean enabled) {
        this.notificationsEnabled = enabled;
    }

    @Override
    public boolean hasNotificationsEnabled() {
        return this.notificationsEnabled;
    }

    public boolean hasEnemiesNearby() {
        return this.enemiesNearby;
    }

    public void setEnemiesNearby(Boolean b) {
        this.enemiesNearby = b;
    }

    public boolean discordSetup() {
        return this.discordSetup;
    }

    public String discordUserID() {
        return this.discordUserID;
    }

    public void setDiscordSetup(Boolean b) {
        this.discordSetup = b;
    }

    public void setDiscordUserID(String s) {
        this.discordUserID = s;
    }

    public boolean hasTitlesEnabled() {
        return this.titlesEnabled;
    }

    public void setTitlesEnabled(Boolean b) {
        this.titlesEnabled = b;
    }


    public String getFactionId() {
        return this.factionId;
    }

    public boolean hasFaction() {
        return !factionId.equals("0");
    }

    public void setMonitorJoins(boolean monitor) {
        this.monitorJoins = monitor;
    }

    public boolean isMonitoringJoins() {
        return this.monitorJoins;
    }

    public Role getRole() {
        // Hack to fix null roles..
        if (role == null) this.role = Role.NORMAL;
        return this.role;
    }

    public void setRole(Role role) {
        if (this.role == role) return;
        FPlayerRoleChangeEvent event = new FPlayerRoleChangeEvent(getFaction(), this, role);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.role = event.getTo();
        }
    }

    public double getPowerBoost() {
        return this.powerBoost;
    }

    public void setPowerBoost(double powerBoost) {
        this.powerBoost = powerBoost;
    }

    public boolean willAutoLeave() {
        return this.willAutoLeave;
    }

    public void setAutoLeave(boolean willLeave) {
        this.willAutoLeave = willLeave;
        //Logger.print(name + " set autoLeave to " + willLeave, Logger.PrefixType.DEFAULT);
    }

    public long getLastFrostwalkerMessage() {
        return this.lastFrostwalkerMessage;
    }

    public void setLastFrostwalkerMessage() {
        this.lastFrostwalkerMessage = System.currentTimeMillis();
    }

    public Faction getAutoClaimFor() {
        return autoClaimFor;
    }

    public void setAutoClaimFor(Faction faction) {
        this.autoClaimFor = faction;
        if (faction != null) {
            this.autoUnclaimFor = null;
        }
    }

    public Faction getAutoUnclaimFor() {
        return autoUnclaimFor;
    }

    public void setAutoUnclaimFor(Faction faction) {
        this.autoUnclaimFor = faction;
        if (faction != null) {
            this.autoClaimFor = null;
        }
    }

    @Deprecated
    public boolean isAutoSafeClaimEnabled() {
        return autoClaimFor != null && autoClaimFor.isSafeZone();
    }

    @Deprecated
    public void setIsAutoSafeClaimEnabled(boolean enabled) {
        this.setAutoClaimFor(enabled ? Factions.getInstance().getSafeZone() : null);
    }

    @Deprecated
    public boolean isAutoWarClaimEnabled() {
        return autoClaimFor != null && autoClaimFor.isWarZone();
    }

    public void setIsAutoWarClaimEnabled(boolean enabled) {
        this.setAutoClaimFor(enabled ? Factions.getInstance().getWarZone() : null);
    }

    public boolean isAdminBypassing() {
        return this.isAdminBypassing;
    }

    public boolean isVanished() {
        return Essentials.isVanished(getPlayer());
    }

    public void setIsAdminBypassing(boolean val) {
        this.isAdminBypassing = val;
    }

    public ChatMode getChatMode() {
        if (this.factionId.equals("0") || !Conf.factionOnlyChat) this.chatMode = ChatMode.PUBLIC;
        return chatMode;
    }

    public void setChatMode(ChatMode chatMode) {
        this.chatMode = chatMode;
    }

    public boolean isIgnoreAllianceChat() {
        return ignoreAllianceChat;
    }

    public void setIgnoreAllianceChat(boolean ignore) {
        this.ignoreAllianceChat = ignore;
    }

    public boolean isSpyingChat() {
        return spyingChat;
    }

    public void setSpyingChat(boolean chatSpying) {
        this.spyingChat = chatSpying;
    }

    // -------------------------------------------- //
    // Getters And Setters
    // -------------------------------------------- //

    // FIELD: account
    public String getAccountId() {
        return this.getId();
    }

    public void resetFactionData(boolean doSpoutUpdate) {
        // clean up any territory ownership in old faction, if there is one
        if (factionId != null && Factions.getInstance().isValidFactionId(this.getFactionId())) {
            Faction currentFaction = this.getFaction();

            currentFaction.removeFPlayer(this);
            if (currentFaction.isNormal()) {
                currentFaction.clearClaimOwnership(this);
            }
        }

        this.factionId = "0"; // The default neutral faction
        this.chatMode = ChatMode.PUBLIC;
        this.role = Role.NORMAL;
        this.title = "";
        this.autoClaimFor = null;
        this.isAlt = false;
    }

    public void resetFactionData() {
        this.resetFactionData(true);
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        losePowerFromBeingOffline();
        this.lastLoginTime = lastLoginTime;
        this.lastPowerUpdateTime = lastLoginTime;
        if (Conf.noPVPDamageToOthersForXSecondsAfterLogin > 0) this.loginPvpDisabled = true;

    }

    public boolean isMapAutoUpdating() {
        return mapAutoUpdating;
    }

    public void setMapAutoUpdating(boolean mapAutoUpdating) {
        this.mapAutoUpdating = mapAutoUpdating;
    }

    //----------------------------------------------//
    // Title, Name, Faction Tag and Chat
    //----------------------------------------------//

    // Base:

    public boolean hasLoginPvpDisabled() {
        if (!loginPvpDisabled) return false;
        if (this.lastLoginTime + (Conf.noPVPDamageToOthersForXSecondsAfterLogin * 1000L) < System.currentTimeMillis()) {
            this.loginPvpDisabled = false;
            return false;
        }
        return true;
    }

    public FLocation getLastStoodAt() {
        return this.lastStoodAt;
    }

    public void setLastStoodAt(FLocation flocation) {
        this.lastStoodAt = flocation;
    }

    public String getTitle() {
        return this.hasFaction() ? title : TL.NOFACTION_PREFIX.toString();
    }

    public void setTitle(CommandSender sender, String title) {
        // Check if the setter has it.
        if (sender.hasPermission(Permission.TITLE_COLOR.node))
            title = CC.translate(title);
        this.title = title;
    }

    // Base concatenations:

    public String getName() {
        if (this.name == null) {
            // Older versions of FactionsUUID don't save the name,
            // so `name` will be null the first time it's retrieved
            // after updating
            OfflinePlayer offline = Bukkit.getOfflinePlayer(FastUUID.parseUUID(getId()));
            this.name = offline.getName() != null ? offline.getName() : getId();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return this.hasFaction() ? this.getFaction().getTag() : "";
    }

    // Colored concatenations:
    // These are used in information messages

    public String getNameAndSomething(String something) {
        return this.role.getPrefix() + (something.length() > 0 ? something + " " : "") + this.getName();
    }

    public String getNameAndTitle() {
        return this.getNameAndSomething(this.getTitle());
    }

    // Chat Tag:
    // These are injected into the format of global chat messages.

    public String getNameAndTag() {
        return this.getNameAndSomething(this.getTag());
    }

    public String getNameAndTitle(Faction faction) {
        return this.getColorTo(faction) + this.getNameAndTitle();
    }

    public String getNameAndTitle(MemoryFPlayer fplayer) {
        return this.getColorTo(fplayer) + this.getNameAndTitle();
    }

    public String getChatTag() {
        return this.hasFaction() ? String.format(Conf.chatTagFormat, this.getRole().getPrefix() + this.getTag()) : TL.NOFACTION_PREFIX.toString();
    }

    // Colored Chat Tag
    public String getChatTag(Faction faction) {
        return this.hasFaction() ? this.getRelationTo(faction).getColor() + getChatTag() : "";
    }

    // -------------------------------
    // Relation and relation colors
    // -------------------------------

    public String getChatTag(MemoryFPlayer fplayer) {
        return this.hasFaction() ? this.getColorTo(fplayer) + getChatTag() : "";
    }

    public int getKills() {
        return isOnline() ? getPlayer().getStatistic(Statistic.PLAYER_KILLS) : this.kills;
    }

    public int getDeaths() {
        return isOnline() ? getPlayer().getStatistic(Statistic.DEATHS) : this.deaths;
    }

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

    public Relation getRelationToLocation() {
        return Board.getInstance().getFactionAt(FLocation.wrap(this)).getRelationTo(this);
    }

    @Override
    public ChatColor getColorTo(RelationParticipator rp) {
        return RelationUtil.getColorOfThatToMe(this, rp);
    }

    //----------------------------------------------//
    // Health
    //----------------------------------------------//
    public void heal(int amnt) {
        Player player = this.getPlayer();
        if (player == null) return;
        player.setHealth(player.getHealth() + amnt);
    }

    //----------------------------------------------//
    // Power
    //----------------------------------------------//
    public double getPower() {
        if (this.isAlt() && !FactionsPlugin.getInstance().getConfig().getBoolean("f-alts.Have-Power")) {
            return 0.0;
        }

        this.updatePower();
        return this.power;
    }

    public void alterPower(double delta) {
        this.power = Math.max(Math.min(this.power + delta, this.getPowerMax()), this.getPowerMin());
    }

    public double getPowerMax() {
        if (this.isAlt() && !FactionsPlugin.getInstance().getConfig().getBoolean("f-alts.Have-Power")) {
            return 0.0;
        }
        return Conf.powerPlayerMax + this.powerBoost;
    }

    public double getPowerMin() {
        if (this.isAlt() && !FactionsPlugin.getInstance().getConfig().getBoolean("f-alts.Have-Power")) {
            return 0.0;
        }
        return Conf.powerPlayerMin + this.powerBoost;
    }

    public int getPowerRounded() {
        if (this.isAlt() && !FactionsPlugin.getInstance().getConfig().getBoolean("f-alts.Have-Power")) {
            return 0;
        }
        return FastMath.round(this.getPower());
    }

    public void setPowerRounded(int power) {
        this.power = power;
    }

    public int getPowerMaxRounded() {
        if (this.isAlt() && !FactionsPlugin.getInstance().getConfig().getBoolean("f-alts.Have-Power")) {
            return 0;
        }
        return FastMath.round(this.getPowerMax());
    }

    public int getPowerMinRounded() {
        if (this.isAlt() && !FactionsPlugin.getInstance().getConfig().getBoolean("f-alts.Have-Power")) {
            return 0;
        }
        return FastMath.round(this.getPowerMin());
    }

    public long getMillisPassed() {
        return this.millisPassed;
    }

    public long getLastPowerUpdateTime() {
        return this.lastPowerUpdateTime;
    }

    public void updatePower() {
        if (this.isAlt() && !FactionsPlugin.getInstance().getConfig().getBoolean("f-alts.Have-Power")) {
            return;
        }

        if (this.isOffline()) {
            losePowerFromBeingOffline();
            if (!Conf.powerRegenOffline) {
                return;
            }
        } else if (hasFaction() && getFaction().isPowerFrozen()) {
            return; // Don't let power regen if faction power is frozen.
        }

        long now = System.currentTimeMillis();
        long millisPassed = now - this.lastPowerUpdateTime;
        this.lastPowerUpdateTime = now;

        Player thisPlayer = this.getPlayer();
        if (thisPlayer != null && thisPlayer.isDead()) {
            return;  // don't let dead players regain power until they respawn
        }

        double delta = millisPassed * Conf.powerPerMinute / 60000; // millisPerMinute : 60 * 1000
        if (Bukkit.getPluginManager().getPlugin("FactionsPlugin") != null) {
            Bukkit.getScheduler().runTask(FactionsPlugin.getInstance(), () -> {
                PowerRegenEvent powerRegenEvent = new PowerRegenEvent(getFaction(), this, delta);
                Bukkit.getServer().getPluginManager().callEvent(powerRegenEvent);
                if (!powerRegenEvent.isCancelled()) {
                    this.alterPower(powerRegenEvent.getDelta());
                }
            });
        } else {
            this.alterPower(delta);
        }
    }

    public void losePowerFromBeingOffline() {
        if (Conf.powerOfflineLossPerDay > 0.0 && this.power > Conf.powerOfflineLossLimit) {
            long now = System.currentTimeMillis();
            long millisPassed = now - this.lastPowerUpdateTime;
            this.lastPowerUpdateTime = now;

            double loss = millisPassed * Conf.powerOfflineLossPerDay / (24 * 60 * 60 * 1000);
            if (this.power - loss < Conf.powerOfflineLossLimit) loss = this.power;
            this.alterPower(-loss);
        }
    }

    public void onDeath() {
        this.updatePower();

        if (this.getPlayer().hasMetadata("diedToPlayer")) {
            this.alterPower(-Conf.deathToPlayerPowerLoss);
            this.getPlayer().removeMetadata("diedToPlayer", FactionsPlugin.getInstance());
        }

        this.alterPower(-Conf.powerPerDeath);
        if (hasFaction()) getFaction().setLastDeath(System.currentTimeMillis());
    }

    //----------------------------------------------//
    // Territory
    //----------------------------------------------//
    public boolean isInOwnTerritory() {
        return Board.getInstance().getFactionAt(FLocation.wrap(this)) == this.getFaction();
    }

    public boolean isInOthersTerritory() {
        Faction factionHere = Board.getInstance().getFactionAt(FLocation.wrap(this));
        return factionHere != null && factionHere.isNormal() && factionHere != this.getFaction();
    }

    public boolean isInAllyTerritory() {
        return Board.getInstance().getFactionAt(FLocation.wrap(this)).getRelationTo(this).isAlly();
    }

    public boolean isInNeutralTerritory() {
        return Board.getInstance().getFactionAt(FLocation.wrap(this)).getRelationTo(this).isNeutral();
    }

    public boolean isInEnemyTerritory() {
        return Board.getInstance().getFactionAt(FLocation.wrap(this)).getRelationTo(this).isEnemy();
    }

    public void sendFactionHereMessage(Faction from) {
        Faction toShow = Board.getInstance().getFactionAt(getLastStoodAt());

        if ((Conf.worldsNoClaiming.contains(getLastStoodAt().getWorldName()) && !Conf.useWorldConfigurationsAsWhitelist) || (!Conf.worldsNoClaiming.contains(getLastStoodAt().getWorldName()) && Conf.useWorldConfigurationsAsWhitelist))
            return;

        if (showInfoBoard(toShow)) {
            FScoreboard.get(this).setTemporarySidebar(new FInfoSidebar(toShow));
        }
        if (FactionsPlugin.getInstance().getConfig().getBoolean("scoreboard.also-send-chat", true))
            this.sendMessage(TextUtil.parse(TL.FACTION_LEAVE.format(from.getTag(this), toShow.getTag(this))));
    }

    // -------------------------------
    // Actions
    // -------------------------------

    /**
     * Check if the scoreboard should be shown. Simple method to be used by above method.
     *
     * @param toShow Faction to be shown.
     * @return true if should show, otherwise false.
     */
    public boolean showInfoBoard(Faction toShow) {
        return showScoreboard && !toShow.isWarZone() && !toShow.isWilderness() && !toShow.isSafeZone() && FactionsPlugin.getInstance().getConfig().contains("scoreboard.finfo") && FactionsPlugin.getInstance().getConfig().getBoolean("scoreboard.finfo-enabled", false) && FScoreboard.get(this) != null;
    }

    @Override
    public boolean showScoreboard() {
        return this.showScoreboard;
    }

    @Override
    public void setShowScoreboard(boolean show) {
        this.showScoreboard = show;
    }

    public void leave(boolean makePay) {
        Faction myFaction = this.getFaction();
        makePay = makePay && Econ.shouldBeUsed() && !this.isAdminBypassing();

        if (myFaction == null) {
            resetFactionData();
            return;
        }

        boolean perm = myFaction.isPermanent();

        if (!perm && this.getRole() == Role.LEADER && myFaction.getFPlayers().size() > 1) {
            msg(TL.LEAVE_PASSADMIN);
            return;
        }

        if (!Conf.canLeaveWithNegativePower && this.getPower() < 0) {
            msg(TL.LEAVE_NEGATIVEPOWER);
            return;
        }

        // if economy is enabled and they're not on the bypass list, make sure they can pay
        if (makePay && !Econ.hasAtLeast(this, Conf.econCostLeave, TL.LEAVE_TOLEAVE.toString())) return;
        FPlayerLeaveEvent leaveEvent = new FPlayerLeaveEvent(this, myFaction, FPlayerLeaveEvent.PlayerLeaveReason.LEAVE);
        Bukkit.getServer().getPluginManager().callEvent(leaveEvent);
        if (leaveEvent.isCancelled()) return;


        // then make 'em pay (if applicable)
        if (makePay && !Econ.modifyMoney(this, -Conf.econCostLeave, TL.LEAVE_TOLEAVE.toString(), TL.LEAVE_FORLEAVE.toString()))
            return;

        // Am I the last one in the faction?
        if (myFaction.getFPlayers().size() == 1) {
            // Transfer all money
            if (Econ.shouldBeUsed())
                Econ.transferMoney(this, myFaction, this, myFaction.getFactionBalance());

        }

        if (myFaction.isNormal()) {
            for (FPlayer fplayer : myFaction.getFPlayersWhereOnline(true))
                FactionsPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(FactionsPlugin.instance, () -> fplayer.msg(TL.LEAVE_LEFT, this.describeTo(fplayer, true), myFaction.describeTo(fplayer)));
            if (Conf.logFactionLeave)
                FactionsPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(FactionsPlugin.instance, () -> Logger.print(TL.LEAVE_LEFT.format(this.getName(), myFaction.getTag()), Logger.PrefixType.DEFAULT));
        }
        myFaction.removeAnnouncements(this);
        if (this.isAlt()) {
            myFaction.removeAltPlayer(this);
            this.msg(TL.LEAVE_LEFT, this.describeTo(this, true), myFaction.describeTo(this));
        }

        this.resetFactionData();


        FactionsPlugin.instance.logFactionEvent(myFaction, FLogType.INVITES, this.getName(), CC.Red + "left", "the faction");
        setFlying(false);
        if (myFaction.isNormal() && !perm && myFaction.getFPlayers().isEmpty()) {
            // Remove this faction

            if (FactionsPlugin.getInstance().getConfig().getBoolean("faction-disband-broadcast")) {

                String message = TL.LEAVE_DISBANDED.toString()
                        .replace("{claims}", myFaction.getAllClaims().size() + "");

                for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers())
                    fplayer.msg(message, myFaction.describeTo(fplayer, true));
            }


            FactionDisbandEvent disbandEvent = new FactionDisbandEvent(getPlayer(), myFaction.getId(), PlayerDisbandReason.LEAVE);
            Bukkit.getPluginManager().callEvent(disbandEvent);

            Factions.getInstance().removeFaction(myFaction.getId());
            if (Conf.logFactionDisband)
                FactionsPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(FactionsPlugin.instance,
                        () -> Logger.print(TL.LEAVE_DISBANDEDLOG.format(myFaction.getTag(), myFaction.getId(),
                                this.getName()).replace("{claims}", myFaction.getAllClaims().size() + ""), Logger.PrefixType.DEFAULT));
        }
    }

    public boolean canClaimForFaction(Faction forFaction) {
        return this.isAdminBypassing() || !forFaction.isWilderness() && (forFaction == this.getFaction() && this.getRole().isAtLeast(Role.MODERATOR)) || (forFaction.isSafeZone() && Permission.MANAGE_SAFE_ZONE.has(getPlayer())) || (forFaction.isWarZone() && Permission.MANAGE_WAR_ZONE.has(getPlayer()));
    }

    public boolean canClaimForFactionAtLocation(Faction forFaction, Location location, boolean notifyFailure) {
        return canClaimForFactionAtLocation(forFaction, FLocation.wrap(location), notifyFailure);
    }

    public boolean hasRegionsInChunk(Chunk chunk) {
        WorldGuardBridge worldGuardBridge = WorldGuardBridge.getInstance();
        if (worldGuardBridge == null || worldGuardBridge.getAPI() == null) {
            return false;
        }
        WGRegionSet regions = worldGuardBridge.getAPI().getRegions(new Location(chunk.getWorld(), chunk.getX() << 4, 0, chunk.getZ() << 4));
        return regions != null;
    }

    public boolean canClaimForFactionAtLocation(Faction forFaction, FLocation flocation, boolean notifyFailure) {
        FactionsPlugin plugin = FactionsPlugin.getInstance();
        String error = null;
        Faction myFaction = getFaction();
        Faction currentFaction = Board.getInstance().getFactionAt(flocation);
        int ownedLand = forFaction.getLandRounded();
        int factionBuffer = plugin.getConfig().getInt("hcf.buffer-zone", 0);
        int worldBuffer = plugin.getConfig().getInt("world-border.buffer", 0);

        if (Conf.worldGuardChecking && hasRegionsInChunk(flocation.getChunk())) {
            // Checks for WorldGuard regions in the chunk attempting to be claimed
            error = TextUtil.parse(TL.CLAIM_PROTECTED.toString());
        } else if (flocation.isOutsideWorldBorder(worldBuffer)) {
            error = TextUtil.parse(TL.CLAIM_OUTSIDEWORLDBORDER.toString());
        } else if ((Conf.worldsNoClaiming.contains(flocation.getWorldName()) && !Conf.useWorldConfigurationsAsWhitelist) || (!Conf.worldsNoClaiming.contains(flocation.getWorldName()) && Conf.useWorldConfigurationsAsWhitelist)) {
            error = TextUtil.parse(TL.CLAIM_DISABLED.toString());
        } else if (this.isAdminBypassing()) {
            return true;
        } else if (forFaction.isSafeZone() && Permission.MANAGE_SAFE_ZONE.has(getPlayer())) {
            return true;
        } else if (forFaction.isWarZone() && Permission.MANAGE_WAR_ZONE.has(getPlayer())) {
            return true;
        } else if (currentFaction.getAccess(this, PermissableAction.TERRITORY) == Access.ALLOW && forFaction != currentFaction) {
            return true;
        } else if (myFaction != forFaction) {
            error = TextUtil.parse(TL.CLAIM_CANTCLAIM.toString(), forFaction.describeTo(this));
        } else if (forFaction == currentFaction) {
            error = TextUtil.parse(TL.CLAIM_ALREADYOWN.toString(), forFaction.describeTo(this, true));
        } else if (forFaction.getFPlayers().size() < Conf.claimsRequireMinFactionMembers) {
            error = TextUtil.parse(TL.CLAIM_MEMBERS.toString(), Conf.claimsRequireMinFactionMembers);
        } else if (currentFaction.isSafeZone()) {
            error = TextUtil.parse(TL.CLAIM_SAFEZONE.toString());
        } else if (currentFaction.isWarZone()) {
            error = TextUtil.parse(TL.CLAIM_WARZONE.toString());
        } else if (plugin.getConfig().getBoolean("hcf.allow-overclaim", true) && ownedLand >= forFaction.getPowerRounded()) {
            error = TextUtil.parse(TL.CLAIM_POWER.toString());
        } else if (Conf.claimedLandsMax != 0 && ownedLand >= Conf.claimedLandsMax && forFaction.isNormal()) {
            error = TextUtil.parse(TL.CLAIM_LIMIT.toString());
        } else if (currentFaction.getRelationTo(forFaction) == Relation.ALLY) {
            error = TextUtil.parse(TL.CLAIM_ALLY.toString());
        } else if (Conf.claimsMustBeConnected && !this.isAdminBypassing() && myFaction.getLandRoundedInWorld(flocation.getWorldName()) > 0 && !Board.getInstance().isConnectedLocation(flocation, myFaction) && (!Conf.claimsCanBeUnconnectedIfOwnedByOtherFaction || !currentFaction.isNormal())) {
            if (Conf.claimsCanBeUnconnectedIfOwnedByOtherFaction) {
                error = TextUtil.parse(TL.CLAIM_CONTIGIOUS.toString());
            } else {
                error = TextUtil.parse(TL.CLAIM_FACTIONCONTIGUOUS.toString());
            }
        } else if (factionBuffer > 0 && Board.getInstance().hasFactionWithin(flocation, myFaction, factionBuffer)) {
            error = TextUtil.parse(TL.CLAIM_TOOCLOSETOOTHERFACTION.format(factionBuffer));
        } else if (flocation.isOutsideWorldBorder(worldBuffer)) {
            if (worldBuffer > 0) {
                error = TextUtil.parse(TL.CLAIM_OUTSIDEBORDERBUFFER.format(worldBuffer));
            } else {
                error = TextUtil.parse(TL.CLAIM_OUTSIDEWORLDBORDER.toString());
            }
        } else if (currentFaction.isNormal()) {
            if (myFaction.isPeaceful()) {
                error = TextUtil.parse(TL.CLAIM_PEACEFUL.toString(), currentFaction.getTag(this));
            } else if (currentFaction.isPeaceful()) {
                error = TextUtil.parse(TL.CLAIM_PEACEFULTARGET.toString(), currentFaction.getTag(this));
            } else if (!currentFaction.hasLandInflation()) {
                // TODO more messages WARN current faction most importantly
                error = TextUtil.parse(TL.CLAIM_THISISSPARTA.toString(), currentFaction.getTag(this));
            } else if (currentFaction.hasLandInflation() && !plugin.getConfig().getBoolean("hcf.allow-overclaim", true)) {
                // deny over claim when it normally would be allowed.
                error = TextUtil.parse(TL.CLAIM_OVERCLAIM_DISABLED.toString());
            } else if (!Board.getInstance().isBorderLocation(flocation)) {
                error = TextUtil.parse(TL.CLAIM_BORDER.toString());
            }
        }
        // TODO: Add more else if statements.

        if (notifyFailure && error != null) {
            msg(error);
        }
        return error == null;
    }

    public boolean attemptClaim(Faction forFaction, Location location, boolean notifyFailure) {
        return attemptClaim(forFaction, FLocation.wrap(location), notifyFailure);
    }

    public boolean attemptUnclaim(Faction forFaction, FLocation flocation, boolean notifyFailure) {
        Faction targetFaction = Board.getInstance().getFactionAt(flocation);

        if (!targetFaction.equals(forFaction)) {
            this.msg(TL.COMMAND_UNCLAIM_WRONGFACTIONOTHER);
            return false;
        }

        if (targetFaction.isSafeZone()) {
            if (Permission.MANAGE_SAFE_ZONE.has(this.getPlayer())) {
                Board.getInstance().removeAt(flocation);
                this.msg(TL.COMMAND_UNCLAIM_SAFEZONE_SUCCESS);

                if (Conf.logLandUnclaims) {
                    Logger.print(TL.COMMAND_UNCLAIM_LOG.format(this.getName(), flocation.getCoordString(), targetFaction.getTag()), Logger.PrefixType.DEFAULT);
                }
                return true;
            } else {
                this.msg(TL.COMMAND_UNCLAIM_SAFEZONE_NOPERM);
                return false;
            }
        } else if (targetFaction.isWarZone()) {
            if (Permission.MANAGE_WAR_ZONE.has(this.getPlayer())) {
                Board.getInstance().removeAt(flocation);
                this.msg(TL.COMMAND_UNCLAIM_WARZONE_SUCCESS);

                if (Conf.logLandUnclaims) {
                    Logger.print(TL.COMMAND_UNCLAIM_LOG.format(this.getName(), flocation.getCoordString(), targetFaction.getTag()), Logger.PrefixType.DEFAULT);
                }
                return true;
            } else {
                this.msg(TL.COMMAND_UNCLAIM_WARZONE_NOPERM);
                return false;
            }
        }

        if (this.isAdminBypassing()) {
            LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(flocation, targetFaction, this);
            Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
            if (unclaimEvent.isCancelled()) {
                return false;
            }

            Board.getInstance().removeAt(flocation);

            targetFaction.msg(TL.COMMAND_UNCLAIM_UNCLAIMED, this.describeTo(targetFaction, true));
            this.msg(TL.COMMAND_UNCLAIM_UNCLAIMS);

            if (Conf.logLandUnclaims) {
                Logger.print(TL.COMMAND_UNCLAIM_LOG.format(this.getName(), flocation.getCoordString(), targetFaction.getTag()), Logger.PrefixType.DEFAULT);
            }
            return true;
        }

        if (!this.hasFaction()) {
            if (notifyFailure) {
                this.msg("You are not member of any faction.");
            }
            return false;
        }


        if (targetFaction.getAccess(this, PermissableAction.TERRITORY) == Access.DENY && this.getRole() != Role.LEADER) {
            this.msg(TL.GENERIC_FPERM_NOPERMISSION, "unclaim");
            return false;
        }

        if (this.getFaction() != targetFaction) {
            if (notifyFailure) {
                this.msg(TL.COMMAND_UNCLAIM_WRONGFACTION);
            }
            return false;
        }

        if (Conf.userSpawnerChunkSystem) {
            FastChunk fastChunk = new FastChunk(flocation);
            if (this.getFaction().getSpawnerChunks().contains(fastChunk) && this.getFaction().getSpawnerChunks() != null) {
                if (Conf.allowUnclaimSpawnerChunksWithSpawnersInChunk) {
                    this.getFaction().getSpawnerChunks().remove(fastChunk);
                    this.msg(TL.SPAWNER_CHUNK_UNCLAIMED);
                } else if (ChunkReference.getSpawnerCount(flocation.getChunk()) > 0) {
                    this.msg(TL.COMMAND_UNCLAIM_SPAWNERCHUNK_SPAWNERS, ChunkReference.getSpawnerCount(flocation.getChunk()));
                }
                return false;
            }
        }

        LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(flocation, targetFaction, this);
        Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
        if (unclaimEvent.isCancelled()) {
            return false;
        }

        if (Econ.shouldBeUsed()) {
            double refund = Econ.calculateClaimRefund(this.getFaction().getLandRounded());

            if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts) {
                if (!Econ.modifyMoney(this.getFaction(), refund, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIM_FORUNCLAIM.toString())) {
                    return false;
                }
            } else {
                if (!Econ.modifyMoney(this, refund, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIM_FORUNCLAIM.toString())) {
                    return false;
                }
            }
        }

        Board.getInstance().removeAt(flocation);

        this.msg(TL.COMMAND_UNCLAIM_FACTIONUNCLAIMED, this.describeTo(this.getFaction(), true));

        if (Conf.logLandUnclaims) {
            Logger.print(TL.COMMAND_UNCLAIM_LOG.format(this.getName(), flocation.getCoordString(), targetFaction.getTag()), Logger.PrefixType.DEFAULT);
        }

        return true;
    }


    public boolean shouldBeSaved() {
        return this.hasFaction() || (this.getPowerRounded() != this.getPowerMaxRounded() && this.getPowerRounded() != FastMath.round(Conf.powerPlayerStarting));
    }

    public void msg(String str, Object... args) {
        this.sendMessage(TextUtil.parse(str, args));
    }

    public void msg(TL translation, Object... args) {
        this.msg(translation.toString(), args);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(FastUUID.parseUUID(this.getId()));
    }

    public boolean isOnline() {
        return this.getPlayer() != null;
    }

    // make sure target player should be able to detect that this player is online
    public boolean isOnlineAndVisibleTo(Player player) {
        Player target = this.getPlayer();
        return target != null && player.canSee(target);
    }

    public boolean isOffline() {
        return !isOnline();
    }

    public boolean isFlying() {
        return isFlying;
    }

    public void setFlying(boolean fly) {
        setFlying(fly, false);
    }

    public void setFlying(boolean fly, boolean damage) {
        if (FactionsPlugin.getInstance().getConfig().getBoolean("enable-faction-flight")) {
            Player player = getPlayer();
            if (player != null) {
                player.setAllowFlight(fly);
                player.setFlying(fly);
            }

            if (!damage) {
                msg(TL.COMMAND_FLY_CHANGE, fly ? TL.GENERAL_ENABLED.toString() : TL.GENERAL_DISABLED.toString());
            } else {
                msg(TL.COMMAND_FLY_DAMAGE);
            }

            // If leaving fly mode, don't let them take fall damage for x seconds.
            if (!fly) {
                int cooldown = FactionsPlugin.getInstance().getConfig().getInt("fly-falldamage-cooldown");

                // If the value is 0 or lower, make them take fall damage.
                // Otherwise, start a timer and have this cancel after a few seconds.
                // Short task so we're just doing it in method. Not clean but eh.
                if (cooldown > 0) {
                    setTakeFallDamage(false);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            setTakeFallDamage(true);
                        }
                    }.runTaskLater(FactionsPlugin.getInstance(), 20L * cooldown);
                }
            }
            isFlying = fly;
        }
    }

    public boolean isInFactionsChest() {
        return inChest;
    }

    public void setInFactionsChest(boolean b) {
        inChest = b;
    }

    public boolean isInVault() {
        return inVault;
    }

    public void setInVault(boolean status) {
        inVault = status;
    }

    public boolean canFlyAtLocation() {
        return canFlyAtLocation(lastStoodAt);
    }

    public boolean canFlyAtLocation(FLocation location) {
        Faction faction = Board.getInstance().getFactionAt(location);

        boolean access = faction.getAccess(this, PermissableAction.FLY) == Access.ALLOW;

        if (faction.isWilderness()) {
            return Permission.FLY_WILDERNESS.has(getPlayer());
        } else if (faction.isSafeZone()) {
            return Permission.FLY_SAFEZONE.has(getPlayer());
        } else if (faction.isWarZone()) {
            return Permission.FLY_WARZONE.has(getPlayer());
        } else if (faction.getRelationTo(getFaction()) == Relation.ENEMY && Permission.FLY_ENEMY.has(getPlayer())) {
            return true;
        } else if (faction.getRelationTo(getFaction()) == Relation.ALLY && Permission.FLY_ALLY.has(getPlayer())) {
            return true;
        } else if (faction.getRelationTo(getFaction()) == Relation.TRUCE && Permission.FLY_TRUCE.has(getPlayer())) {
            return true;
        } else if (faction.getRelationTo(getFaction()) == Relation.NEUTRAL && !faction.isSystemFaction() && Permission.FLY_NEUTRAL.has(getPlayer())) {
            return true;
        }

        // admin bypass (ops) can fly.
        if (isAdminBypassing) {
            return true;
        }

        return access;
    }

    public boolean isAutoFlying() {
        return isAutoFlying;
    }

    public void setAutoFlying(boolean autoFly) {
        this.isAutoFlying = autoFly;
    }

    public boolean shouldTakeFallDamage() {
        return this.shouldTakeFallDamage;
    }

    public void setTakeFallDamage(boolean fallDamage) {
        this.shouldTakeFallDamage = fallDamage;
    }

    public boolean isSeeingChunk() {
        return seeingChunk;
    }

    public void setSeeingChunk(boolean seeingChunk) {
        this.seeingChunk = seeingChunk;
    }


    public boolean isEnteringPassword() {
        return enteringPassword;
    }

    public void setEnteringPassword(boolean toggle, String warp) {
        enteringPassword = toggle;
        enteringPasswordWarp = warp;
    }

    // -------------------------------------------- //
    // Message Sending Helpers
    // -------------------------------------------- //

    public String getEnteringWarp() {
        return enteringPasswordWarp;
    }

    public void sendMessage(String msg) {
        if (msg.contains("{null}")) return; // user wants this message to not send

        if (msg.contains("/n/")) {
            for (String s : msg.split("/n/")) sendMessage(s);
            return;
        }
        Player player = this.getPlayer();
        if (player == null) return;
        player.sendMessage(msg);
    }

    public void sendMessage(List<String> msgs) {
        for (String msg : msgs) this.sendMessage(msg);
    }

    public void sendComponent(Component message) {
        Player player = getPlayer();
        if (player == null) {
            return;
        }
        TextUtil.AUDIENCES.player(player).sendMessage(message);
    }

    public void sendComponent(List<Component> messages) {
        messages.forEach(this::sendComponent);
    }

    public int getMapHeight() {
        if (this.mapHeight < 1) this.mapHeight = Conf.mapHeight;
        return this.mapHeight;
    }

    public void setMapHeight(int height) {
        this.mapHeight = Math.min(height, (Conf.mapHeight * 2));
    }

    public String getNameAndTitle(FPlayer fplayer) {
        return this.getColorTo(fplayer) + this.getNameAndTitle();
    }

    @Override
    public String getChatTag(FPlayer fplayer) {
        return this.hasFaction() ? this.getRelationTo(fplayer).getColor() + getChatTag() : "";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public abstract void remove();

    @Override
    public void clearWarmup() {
        if (warmup != null) {
            Bukkit.getScheduler().cancelTask(warmupTask);
            this.stopWarmup();
        }
    }

    @Override
    public void stopWarmup() {
        warmup = null;
    }

    @Override
    public boolean isWarmingUp() {
        return warmup != null;
    }

    @Override
    public WarmUpUtil.Warmup getWarmupType() {
        return warmup;
    }

    @Override
    public void addWarmup(WarmUpUtil.Warmup warmup, int taskId) {
        if (this.warmup != null) this.clearWarmup();
        this.warmup = warmup;
        this.warmupTask = taskId;
    }

    @Override
    public void checkIfNearbyEnemies() {
        Player me = this.getPlayer();

        if (me == null) return;
        if (me.hasPermission("factions.fly.bypassnearbyenemycheck")) return;
        int radius = Conf.stealthFlyCheckRadius;
        for (Entity e : me.getNearbyEntities(radius, 255, radius)) {
            if (e instanceof Player) {
                Player eplayer = (((Player) e).getPlayer());
                if (eplayer == null) continue;
                if (eplayer.hasMetadata("NPC")) continue;
                FPlayer efplayer = FPlayers.getInstance().getByPlayer(eplayer);
                if (efplayer == null) continue;
                if (!me.canSee(eplayer) || efplayer.isVanished()) continue;
                if (this.getRelationTo(efplayer).equals(Relation.ENEMY) && !efplayer.isStealthEnabled()) {
                    if (me.isFlying()) {
                        setFlying(false);
                        msg(TL.COMMAND_FLY_ENEMY_NEAR);
                        Bukkit.getServer().getPluginManager().callEvent(new FPlayerStoppedFlying(this));
                    }
                    this.enemiesNearby = true;
                    return;
                }
            }
        }
        this.enemiesNearby = false;
    }

    @Override
    public Boolean canflyinWilderness() {
        return getPlayer().hasPermission(Permission.FLY_WILDERNESS.node);
    }

    @Override
    public Boolean canflyinWarzone() {
        return getPlayer().hasPermission(Permission.FLY_WARZONE.node);

    }

    @Override
    public Boolean canflyinSafezone() {
        return getPlayer().hasPermission(Permission.FLY_SAFEZONE.node);

    }

    @Override
    public Boolean canflyinEnemy() {
        return getPlayer().hasPermission(Permission.FLY_ENEMY.node);

    }

    @Override
    public Boolean canflyinAlly() {
        return getPlayer().hasPermission(Permission.FLY_ALLY.node);

    }

    @Override
    public Boolean canflyinTruce() {
        return getPlayer().hasPermission(Permission.FLY_TRUCE.node);

    }

    @Override
    public Boolean canflyinNeutral() {
        return getPlayer().hasPermission(Permission.FLY_NEUTRAL.node);

    }


    public boolean hasFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean status) {
        friendlyFire = status;
    }

    @Override
    public boolean isInspectMode() {
        return inspectMode;
    }

    @Override
    public void setInspectMode(boolean status) {
        inspectMode = status;
    }

    public boolean attemptClaim(Faction forFaction, FLocation flocation, boolean notifyFailure) {
        // notifyFailure is false if called by auto-claim; no need to notify on every failure for it
        // return value is false on failure, true on success

        Faction currentFaction = Board.getInstance().getFactionAt(flocation);
        int ownedLand = forFaction.getLandRounded();

        if (!this.canClaimForFactionAtLocation(forFaction, flocation, notifyFailure)) {
            return false;
        }

        if (Conf.worldGuardChecking && hasRegionsInChunk(flocation.getChunk())) {
            this.msg(TL.GENERIC_WORLDGUARD);
            return false;
        }

        // if economy is enabled and they're not on the bypass list, make sure they can pay
        boolean mustPay = Econ.shouldBeUsed() && !this.isAdminBypassing() && !forFaction.isSafeZone() && !forFaction.isWarZone() && (Conf.econCostClaimWilderness != 0.0);
        double cost = 0.0;
        EconomyParticipator payee = null;
        if (mustPay) {
            cost = Econ.calculateClaimCost(ownedLand, currentFaction.isNormal());

            if (Conf.econClaimUnconnectedFee != 0.0 && forFaction.getLandRoundedInWorld(flocation.getWorldName()) > 0 && !Board.getInstance().isConnectedLocation(flocation, forFaction)) {
                cost += Conf.econClaimUnconnectedFee;
            }

            if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts && this.hasFaction()) {
                payee = this.getFaction();
            } else {
                payee = this;
            }

            if (!Econ.hasAtLeast(payee, cost, TL.CLAIM_TOCLAIM.toString())) {
                return false;
            }
        }

        if (forFaction.getClaimOwnership().containsKey(flocation) && !forFaction.isPlayerInOwnerList(this, flocation)) {
            this.msg(TL.GENERIC_FPERM_OWNER_NOPERMISSION, "claim");
            return false;
        }

        LandClaimEvent claimEvent = new LandClaimEvent(flocation, forFaction, this);
        Bukkit.getPluginManager().callEvent(claimEvent);

        if (claimEvent.isCancelled()) {
            return false;
        }

        // then make 'em pay (if applicable)
        if (mustPay) {
            if (payee == this) {
                if (!Econ.modifyMoney(payee, -cost, TL.CLAIM_TOCLAIM.toString(), TL.CLAIM_FORCLAIM.toString())) {
                    return false;
                }
            } else {
                if (Econ.withdrawFactionBalance(this.getFaction(), cost)) {
                    this.getFaction().msg("<h>%s<i> lost <h>%s<i> %s.", CC.translate("&aYour faction"), moneyString(cost), TL.CLAIM_FORCLAIM.toString());
                } else {
                    this.getFaction().msg("<h>%s<i> can't afford <h>%s<i> %s.", CC.translate("&aYour faction"), moneyString(cost), TL.CLAIM_TOCLAIM.toString());
                    return false;
                }
            }
        }

        // Was an over claim
        if (currentFaction.isNormal() && currentFaction.hasLandInflation()) {
            // Give them money for over claiming.
            if (payee == this) {
                Econ.modifyMoney(payee, Conf.econOverclaimRewardMultiplier, TL.CLAIM_TOOVERCLAIM.toString(), TL.CLAIM_FOROVERCLAIM.toString());
            } else if (Conf.econOverclaimRewardMultiplier > 0.0) {
                Econ.depositFactionBalance(this.getFaction(), Conf.econOverclaimRewardMultiplier);
                this.getFaction().msg(TL.COMMAND_MONEY_GAINED, CC.translate("&aYour faction"), Conf.econOverclaimRewardMultiplier, TL.CLAIM_FOROVERCLAIM.toString());
            }
        }


        // announce success
        if (!FactionsPlugin.cachedRadiusClaim) {
            Set<FPlayer> informTheseFPlayers = new HashSet<>();
            informTheseFPlayers.add(this);
            informTheseFPlayers.addAll(forFaction.getFPlayersWhereOnline(true));
            for (FPlayer fp : informTheseFPlayers) {
                FactionsPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(FactionsPlugin.instance, () -> fp.msg(TL.CLAIM_CLAIMED, this.describeTo(fp, true), forFaction.describeTo(fp), currentFaction.describeTo(fp)));
            }
        }

        Board.getInstance().setFactionAt(forFaction, flocation);

        if (Conf.logLandClaims) {
            FactionsPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(FactionsPlugin.instance, () -> Logger.printArgs(TL.CLAIM_CLAIMEDLOG.toString(), Logger.PrefixType.DEFAULT, this.getName(), flocation.getCoordString(), forFaction.getTag()));
        }

        return true;
    }


    @Override
    public String getRolePrefix() {

        switch (getRole()) {
            case RECRUIT:
                return Conf.prefixRecruit;
            case NORMAL:
                return Conf.prefixNormal;
            case MODERATOR:
                return Conf.prefixMod;
            case COLEADER:
                return Conf.prefixCoLeader;
            case LEADER:
                return Conf.prefixLeader;
        }
        return null;
    }

    @Override
    public boolean hasMoney(int amt) {
        Economy econ = FactionsPlugin.getInstance().getEcon();
        if (econ.getBalance(getPlayer()) >= amt) {
            return true;
        } else {
            getPlayer().closeInventory();
            msg(TL.GENERIC_NOTENOUGHMONEY);
            return false;
        }
    }

    public String commas(final double amount) {
        final DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(amount);
    }

    @Override
    public boolean takeMoney(int amt) {
        if (hasMoney(amt)) {
            Economy econ = FactionsPlugin.getInstance().getEcon();
            if (econ.withdrawPlayer(getPlayer(), amt).transactionSuccess()) {
                sendMessage(TL.GENERIC_MONEYTAKE.toString().replace("{amount}", commas(amt)));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryFPlayer that = (MemoryFPlayer) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}