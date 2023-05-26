package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.timer.TimerManager;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

/**
 * Link between config and in-game messages<br> Changes based on faction / player<br> Interfaces the config lists with
 * {} variables to plugin
 */
public enum TagReplacer {

    /**
     * Fancy variables, used by f show
     */
    NEUTRAL_LIST(TagType.FANCY, "{neutral-list}"),
    ALLIES_LIST(TagType.FANCY, "{allies-list}"),
    ONLINE_LIST(TagType.FANCY, "{online-list}"),
    ENEMIES_LIST(TagType.FANCY, "{enemies-list}"),
    TRUCES_LIST(TagType.FANCY, "{truces-list}"),
    OFFLINE_LIST(TagType.FANCY, "{offline-list}"),
    ALTS(TagType.FANCY, "{alts}"),

    /**
     * Player variables, require a player
     */
    PLAYER_GROUP(TagType.PLAYER, "{group}"),
    LAST_SEEN(TagType.PLAYER, "{lastSeen}"),
    PLAYER_BALANCE(TagType.PLAYER, "{balance}"),
    PLAYER_POWER(TagType.PLAYER, "{player-power}"),
    PLAYER_MAXPOWER(TagType.PLAYER, "{player-maxpower}"),
    PLAYER_KILLS(TagType.PLAYER, "{player-kills}"),
    PLAYER_DEATHS(TagType.PLAYER, "{player-deaths}"),

    /**
     * Faction variables, require at least a player
     */
    HOME_X(TagType.FACTION, "{x}"),
    HOME_Y(TagType.FACTION, "{y}"),
    HOME_Z(TagType.FACTION, "{z}"),
    CHUNKS(TagType.FACTION, "{chunks}"),
    WARPS(TagType.FACTION, "{warps}"),
    HEADER(TagType.FACTION, "{header}"),
    POWER(TagType.FACTION, "{power}"),
    MAX_POWER(TagType.FACTION, "{maxPower}"),
    POWER_BOOST(TagType.FACTION, "{power-boost}"),
    LEADER(TagType.FACTION, "{leader}"),
    JOINING(TagType.FACTION, "{joining}"),
    FACTION(TagType.FACTION, "{faction}"),
    PLAYER_NAME(TagType.FACTION, "{name}"),
    HOME_WORLD(TagType.FACTION, "{world}"),
    RAIDABLE(TagType.FACTION, "{raidable}"),
    RAW_TAG(TagType.FACTION, "{faction-tag}"),
    PEACEFUL(TagType.FACTION, "{peaceful}"),
    PERMANENT(TagType.FACTION, "permanent"), // no braces needed
    TIME_LEFT(TagType.FACTION, "{time-left}"),
    LAND_VALUE(TagType.FACTION, "{land-value}"),
    DESCRIPTION(TagType.FACTION, "{description}"),
    CREATE_DATE(TagType.FACTION, "{create-date}"),
    LAND_REFUND(TagType.FACTION, "{land-refund}"),
    BANK_BALANCE(TagType.FACTION, "{faction-balance}"),
    ALLIES_COUNT(TagType.FACTION, "{allies}"),
    ENEMIES_COUNT(TagType.FACTION, "{enemies}"),
    TRUCES_COUNT(TagType.FACTION, "{truces}"),
    ALT_COUNT(TagType.FACTION, "{alt-count}"),
    ONLINE_COUNT(TagType.FACTION, "{online}"),
    OFFLINE_COUNT(TagType.FACTION, "{offline}"),
    FACTION_SIZE(TagType.FACTION, "{members}"),
    FACTION_KILLS(TagType.FACTION, "{faction-kills}"),
    FACTION_DEATHS(TagType.FACTION, "{faction-deaths}"),
    FACTION_BANCOUNT(TagType.FACTION, "{faction-bancount}"),
    FACTION_STRIKES(TagType.FACTION, "{strikes}"),
    FACTION_POINTS(TagType.FACTION, "{faction-points}"),
    SHIELD(TagType.FACTION, "{shield}"),


    /**
     * General variables, require no faction or player
     */
    GRACE_TIMER(TagType.GENERAL, "{grace-time}"),
    MAX_WARPS(TagType.GENERAL, "{max-warps}"),
    MAX_ALLIES(TagType.GENERAL, "{max-allies}"),
    MAX_ALTS(TagType.GENERAL, "{max-alts}"),
    MAX_ENEMIES(TagType.GENERAL, "{max-enemies}"),
    MAX_TRUCES(TagType.GENERAL, "{max-truces}"),
    FACTIONLESS(TagType.GENERAL, "{factionless}"),
    TOTAL_ONLINE(TagType.GENERAL, "{total-online}");

    private TagType type;
    private String tag;

    public static final TagReplacer[] VALUES = values();

    TagReplacer(TagType type, String tag) {
        this.type = type;
        this.tag = tag;
    }

    /**
     * Returns a list of all the variables we can use for this type<br>
     *
     * @param type the type we want
     * @return a list of all the variables with this type
     */
    static Set<TagReplacer> getByType(TagType type) {
        Set<TagReplacer> tagReplacers = EnumSet.noneOf(TagReplacer.class);
        for (TagReplacer tagReplacer : VALUES) {
            if (type == TagType.FANCY) {
                if (tagReplacer.type == TagType.FANCY) {
                    tagReplacers.add(tagReplacer);
                }
            } else if (tagReplacer.type.id >= type.id) {
                tagReplacers.add(tagReplacer);
            }
        }
        return tagReplacers;
    }

    /**
     * Protected access to this generic server related variable
     *
     * @return value for this generic server related variable<br>
     */
    private String getValue() {
        switch (this) {
            case GRACE_TIMER:
                return String.valueOf(TimerManager.getRemaining(FactionsPlugin.getInstance().getTimerManager().graceTimer.getRemaining(), true));
            case TOTAL_ONLINE:
                return String.valueOf(Bukkit.getOnlinePlayers().size());
            case FACTIONLESS:
                return String.valueOf(FPlayers.getInstance().getAllFPlayers().stream().filter(p -> !p.hasFaction()).count());
            case MAX_ALLIES:
                if (FactionsPlugin.getInstance().getConfig().getBoolean("max-relations.enabled", true)) {
                    return String.valueOf(FactionsPlugin.getInstance().getConfig().getInt("max-relations.ally", 10));
                }
                return TL.GENERIC_INFINITY.toString();
            case MAX_ALTS:
                if (FactionsPlugin.getInstance().getConfig().getBoolean("f-alts.Enabled")) {
                    return String.valueOf(Conf.factionAltMemberLimit);
                }
                return TL.GENERIC_INFINITY.toString();
            case MAX_ENEMIES:
                if (FactionsPlugin.getInstance().getConfig().getBoolean("max-relations.enabled", true)) {
                    return String.valueOf(FactionsPlugin.getInstance().getConfig().getInt("max-relations.enemy", 10));
                }
                return TL.GENERIC_INFINITY.toString();
            case MAX_TRUCES:
                if (FactionsPlugin.getInstance().getConfig().getBoolean("max-relations.enabled", true)) {
                    return String.valueOf(FactionsPlugin.getInstance().getConfig().getInt("max-relations.truce", 10));
                }
                return TL.GENERIC_INFINITY.toString();
            case MAX_WARPS:
                return String.valueOf(FactionsPlugin.getInstance().getConfig().getInt("max-warps", 5));
            default:
        }
        return null;
    }

    /**
     * Gets the value for this (as in the instance this is called from) variable!
     *
     * @param fac Target faction
     * @param fp  Target player (can be null)
     * @return the value for this enum!
     */
    String getValue(Faction fac, FPlayer fp) {
        if (this.type == TagType.GENERAL) {
            return getValue();
        }

        boolean minimal = FactionsPlugin.getInstance().getConfig().getBoolean("minimal-show", false);

        if (fp != null) {
            switch (this) {
                case HEADER:
                    return TextUtil.titleize(fac.getTag(fp));
                case PLAYER_NAME:
                    return fp.getName();
                case FACTION:
                    return !fac.isWilderness() ? fac.getTag(fp) : TL.GENERIC_FACTIONLESS.toString();
                case LAST_SEEN:
                    String humanized = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - fp.getLastLoginTime(), true, true) + TL.COMMAND_STATUS_AGOSUFFIX;
                    return fp.isOnline() ? ChatColor.GREEN + TL.COMMAND_STATUS_ONLINE.toString() : (System.currentTimeMillis() - fp.getLastLoginTime() < 432000000 ? ChatColor.YELLOW + humanized : ChatColor.RED + humanized);
                case PLAYER_GROUP:
                    return FactionsPlugin.getInstance().getPrimaryGroup(Bukkit.getOfflinePlayer(UUID.fromString(fp.getId())));
                case PLAYER_BALANCE:
                    return Econ.isSetup() ? Econ.getFriendlyBalance(fp) : TL.ECON_OFF.format("balance");
                case PLAYER_POWER:
                    return String.valueOf(fp.getPowerRounded());
                case PLAYER_MAXPOWER:
                    return String.valueOf(fp.getPowerMaxRounded());
                case PLAYER_KILLS:
                    return String.valueOf(fp.getKills());
                case PLAYER_DEATHS:
                    return String.valueOf(fp.getDeaths());
                case RAW_TAG:
                    return ChatColor.stripColor(fac.getTag());
                default:
            }
        }

        switch (this) {
            case DESCRIPTION:
                return fac.getDescription();
            case FACTION:
                return fac.getTag();
            case JOINING:
                return (fac.getOpen() ? TL.COMMAND_SHOW_UNINVITED.toString() : TL.COMMAND_SHOW_INVITATION.toString());
            case PEACEFUL:
                return fac.isPeaceful() ? Conf.colorNeutral + TL.COMMAND_SHOW_PEACEFUL.toString() : "";
            case PERMANENT:
                return fac.isPermanent() ? "permanent" : "{notPermanent}";
            case CHUNKS:
                return String.valueOf(fac.getLandRounded());
            case POWER:
                return String.valueOf(fac.getPowerRounded());
            case MAX_POWER:
                return String.valueOf(fac.getPowerMaxRounded());
            case POWER_BOOST:
                double powerBoost = fac.getPowerBoost();
                return (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? TL.COMMAND_SHOW_BONUS.toString() : TL.COMMAND_SHOW_PENALTY.toString() + powerBoost + ")");
            case LEADER:
                FPlayer fAdmin = fac.getFPlayerAdmin();
                return fAdmin == null ? "Server" : fAdmin.getName().substring(0, fAdmin.getName().length() > 14 ? 13 : fAdmin.getName().length());
            case WARPS:
                return String.valueOf(fac.getWarps().size());
            case CREATE_DATE:
                return TL.sdf.format(fac.getFoundedDate());
            case RAIDABLE:
                boolean raid = FactionsPlugin.getInstance().getConfig().getBoolean("hcf.raidable", false) && fac.getLandRounded() >= fac.getPowerRounded();
                return raid ? TL.RAIDABLE_TRUE.toString() : TL.RAIDABLE_FALSE.toString();
            case HOME_WORLD:
                return fac.hasHome() ? fac.getHome().getWorld().getName() : minimal ? null : "{ig}";
            case HOME_X:
                return fac.hasHome() ? String.valueOf(fac.getHome().getBlockX()) : minimal ? null : "{ig}";
            case HOME_Y:
                return fac.hasHome() ? String.valueOf(fac.getHome().getBlockY()) : minimal ? null : "{ig}";
            case HOME_Z:
                return fac.hasHome() ? String.valueOf(fac.getHome().getBlockZ()) : minimal ? null : "{ig}";
            case SHIELD:
                return FactionsPlugin.getInstance().getShieldStatMap().get(fac);
            case LAND_VALUE:
                return Econ.shouldBeUsed() ? Econ.moneyString(Econ.calculateTotalLandValue(fac.getLandRounded())) : minimal ? null : TL.ECON_OFF.format("value");
            case LAND_REFUND:
                return Econ.shouldBeUsed() ? Econ.moneyString(Econ.calculateTotalLandRefund(fac.getLandRounded())) : minimal ? null : TL.ECON_OFF.format("refund");
            case BANK_BALANCE:
                if (Econ.shouldBeUsed()) {
                    return Conf.bankEnabled ? Econ.insertCommas(Econ.getFactionBalance(fac)) : minimal ? null : TL.ECON_OFF.format("balance");
                }
                return minimal ? null : TL.ECON_OFF.format("balance");
            case ALLIES_COUNT:
                return String.valueOf(fac.getRelationCount(Relation.ALLY));
            case ENEMIES_COUNT:
                return String.valueOf(fac.getRelationCount(Relation.ENEMY));
            case TRUCES_COUNT:
                return String.valueOf(fac.getRelationCount(Relation.TRUCE));
            case ALT_COUNT:
                return String.valueOf(fac.getAltPlayers().size());
            case ONLINE_COUNT:
                if (fp != null && fp.isOnline()) {
                    return String.valueOf(fac.getFPlayersWhereOnline(true, fp).size());
                } else {
                    // Only console should ever get here.
                    return String.valueOf(fac.getFPlayersWhereOnline(true).size());
                }
            case OFFLINE_COUNT:
                return String.valueOf(fac.getFPlayers().size() - fac.getOnlinePlayers().size());
            case FACTION_SIZE:
                return String.valueOf(fac.getFPlayers().size());
            case FACTION_KILLS:
                return String.valueOf(fac.getKills());
            case FACTION_DEATHS:
                return String.valueOf(fac.getDeaths());
            case FACTION_BANCOUNT:
                return String.valueOf(fac.getBannedPlayers().size());
            case FACTION_STRIKES:
                return String.valueOf(fac.getStrikes());
            case FACTION_POINTS:
                return String.valueOf(fac.getPoints());
            case RAW_TAG:
                return ChatColor.stripColor(fac.getTag());
            default:
        }
        return null;
    }

    /**
     * @param original raw line with variables
     * @param value    what to replace var in raw line with
     * @return the string with the new value
     */
    public String replace(String original, String value) {
        return (original != null && value != null) ? TextUtil.replace(original, tag, value) : original;
    }

    /**
     * @param toSearch raw line with variables
     * @return if the raw line contains this enums variable
     */
    public boolean contains(String toSearch) {
        if (tag == null) {
            return false;
        }
        return toSearch.contains(tag);
    }

    /**
     * Gets the tag associated with this enum that we should replace
     *
     * @return the {....} variable that is located in config
     */
    public String getTag() {
        return this.tag;
    }

    protected enum TagType {
        FANCY(0), PLAYER(1), FACTION(2), GENERAL(3);
        public final int id;

        TagType(int id) {
            this.id = id;
        }
    }
}
