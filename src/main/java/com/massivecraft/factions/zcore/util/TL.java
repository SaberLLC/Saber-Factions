/*
 * Copyright (C) 2019 Driftay
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.util.CC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.file.YamlConfiguration;

import java.text.SimpleDateFormat;

/**
 * An enum for requesting strings from the language file. The contents of this enum file may be subject to frequent
 * changes.
 */
public enum TL {
    /**
     * Translation meta
     */
    _AUTHOR("misc"),
    _RESPONSIBLE("misc"),
    _LANGUAGE("English"),
    _ENCODING("UTF-8"),
    _LOCALE("en_US"),
    _REQUIRESUNICODE("false"),
    _DEFAULT("true"),
    _STATE("complete"), //incomplete, limited, partial, majority, complete

    /**
     * Localised translation meta
     */
    _LOCAL_AUTHOR("misc"),
    _LOCAL_RESPONSIBLE("misc"),
    _LOCAL_LANGUAGE("English"),
    _LOCAL_REGION("US"),
    _LOCAL_STATE("complete"), //And this is the English version. It's not ever going to be not complete.
    /**
     * Actions translations
     */
    ACTIONS_NOPERMISSION("{faction} does not allow you to {action}"),
    ACTIONS_NOPERMISSIONPAIN("It is painful to try to {action} in the territory of {faction}"),
    ACTIONS_OWNEDTERRITORYDENY("You cant do that in this territory, it is owned by {owners}"),
    ACTIONS_OWNEDTERRITORYPAINDENY("It is painful to try to {action} in this territory, it is owned by {owners}"),
    ACTIONS_MUSTBE("You must be {role} to {action}."),
    ACTIONS_NOSAMEROLE("{role} can't control each other..."),
    ACTIONS_NOFACTION("You are not member of any faction."),

    ACTION_DENIED_SAFEZONE("You can't use %1$s in safezone!"),
    ACTION_DENIED_WARZONE("You can't use %1$s in warzone!"),
    ACTION_DENIED_WILDERNESS("You can't use %1$s in wilderness!"),
    ACTION_DENIED_OTHER("%1$s does not allow you to %2$s here!"),

    /**
     * Command translations
     */
    COMMAND_USEAGE_TEMPLATE_COLOR("&c"),

    /**
     * Messsges for /f help
     */
    COMMAND_HELP_NEXTCREATE("Learn how to create a faction on the next page."),
    COMMAND_HELP_INVITATIONS("command.help.invitations", "You might want to close it and use invitations:"),
    COMMAND_HELP_HOME("And don't forget to set your home:"),
    COMMAND_HELP_404("&c&l» &7This page does &cnot &7exist"),
    COMMAND_HELP_BANK_1("Your faction has a bank which is used to pay for certain"), //Move to last /f help page
    COMMAND_HELP_BANK_2("things, so it will need to have money deposited into it."), //Move to last /f help page
    COMMAND_HELP_BANK_3("To learn more, use the money command."), //Move to last /f help page
    COMMAND_HELP_PLAYERTITLES("Player titles are just for fun. No rules connected to them."), //Move to last /f help page
    COMMAND_HELP_OWNERSHIP_1("Claimed land with ownership set is further protected so"), //Move to last /f help page
    COMMAND_HELP_OWNERSHIP_2("that only the owner(s), faction admin, and possibly the"), //Move to last /f help page
    COMMAND_HELP_OWNERSHIP_3("faction moderators have full access."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_1("Set the relation you WISH to have with another faction."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_2("Your default relation with other factions will be neutral."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_3("If BOTH factions choose \"ally\" you will be allies."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_4("If ONE faction chooses \"enemy\" you will be enemies."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_5("You can never hurt members or allies."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_6("You can not hurt neutrals in their own territory."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_7("You can always hurt enemies and players without faction."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_8(""),
    COMMAND_HELP_RELATIONS_9("Damage from enemies is reduced in your own territory."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_10("When you die you lose power. It is restored over time."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_11("The power of a faction is the sum of all member power."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_12("The power of a faction determines how much land it can hold."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_13("You can claim land from factions with too little power."), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_1("Only faction members can build and destroy in their own"), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_2("territory. Usage of the following items is also restricted:"), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_3("Door, Chest, Furnace, Dispenser, Diode."), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_4(""),
    COMMAND_HELP_PERMISSIONS_5("Make sure to put pressure plates in front of doors for your"), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_6("guest visitors. Otherwise they can't get through. You can"), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_7("also use this to create member only areas."), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_8("As dispensers are protected, you can create traps without"), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_9("worrying about those arrows getting stolen."), //Move to last /f help page
    COMMAND_HELP_ADMIN_1("&a&l» &a/f claim safezone \n   &7claim land for the Safe Zone"),
    COMMAND_HELP_ADMIN_2("&a&l» &a/f claim warzone \n   &7claim land for the War Zone"),
    COMMAND_HELP_ADMIN_3("&a&l» &a/f autoclaim [safezone|warzone] \n   &7take a guess"),
    COMMAND_HELP_MOAR_1("Finally some commands for the server admins:"),
    COMMAND_HELP_MOAR_2("More commands for server admins:"),
    COMMAND_HELP_MOAR_3("Even more commands for server admins:"),
    COMMAND_HELP_DESCRIPTION("\n  &a&l» &7Display a &ahelp &7page"),
    WORLD_DISABLED_COMMAND_DENIED("&cFactions is disabled in this world!"),


    PRE_JOIN_KICK_MESSAGE("&c&lYour faction data is being loaded, please try again!"),

    COMMAND_NEAR_DESCRIPTION("Get nearby faction players in a radius."),
    COMMAND_NEAR_DISABLED_MSG("&cThis command is disabled!"),
    COMMAND_NEAR_FORMAT("{playername} &c({distance}m)"),
    COMMAND_NEAR_USE_MSG("&cFaction members nearby"),

    /**
     * Messsges for Faction Admins/Mods
     */

    COMMAND_CONTEXT_ADMINISTER_DIF_FACTION("&c[!] %1$s is not in the same faction as you."),
    COMMAND_CONTEXT_ADMINISTER_ADMIN_REQUIRED("&c[!] Only the faction admin can do that."),
    COMMAND_CONTEXT_ADMINISTER_SAME_RANK_CONTROL("&c[!] Moderators can't control each other..."),
    COMMAND_CONTEXT_ADMINISTER_MOD_REQUIRED("&c[!] You must be a faction moderator to do that."),

    COMMAND_UPGRADES_DESCRIPTION("&cOpen the Upgrades Menu"),
    COMMAND_UPGRADES_POINTS_TAKEN("&cFaction upgrade purchased for &e%1$s points! &cNew Point Balance: &e%2$s"),
    COMMAND_UPGRADES_NOT_ENOUGH_POINTS("&cYour faction does not have enough points to purchase this upgrade!"),
    COMMAND_UPGRADES_DISABLED("&c[!] &7Faction Upgrades are &cdisabled&7."),
    COMMAND_UPGRADES_LEVEL_ERROR("&c[!] &7Faction Upgrade &e%1$s &7of level &e%2$s &7led to an invalid value&7."),
    UPGRADE_TOUPGRADE("to buy the %1$s upgrade"),
    UPGRADE_FORUPGRADE("for buying the %1$s upgrade"),

    COMMAND_CORNER_CANT_CLAIM("&c&l[!] &cYou may not claim this corner!"),
    COMMAND_CORNER_CLAIMED("\n&2&l[!] &aYou have claimed the corner successfully, totalling in &b%1$d &achunks!\n"),
    COMMAND_CORNER_ATTEMPTING_CLAIM("&c&l[!] &7Attempting to claim corner..."),
    COMMAND_CORNER_FAIL_WITH_FEEDBACK("&c&l[!] &cOne or more claims in this corner could not be claimed! Total chunks claimed:&b "),
    COMMAND_CORNER_NOT_CORNER("&c&l[!] &7You must be in a corner to use this command!"),
    COMMAND_CORNER_DESCRIPTION("claim a corner at world border"),
    COMMAND_CORNERLIST_DESCRIPTION("list of all corners"),
    COMMAND_CORNERLIST_TITLE("&7Listing corner claims in &2{world}"),

    COMMAND_ADMIN_NOTMEMBER("&c&l[!] &7%1$s &cis not a member in your faction."),
    COMMAND_ADMIN_NOTADMIN("&c&l[!] &cYou are not the faction admin."),
    COMMAND_ADMIN_TARGETSELF("'&c&l[!] &cThe target player musn''t be yourself."),
    COMMAND_ADMIN_DEMOTES("&c&l[!] &cYou have demoted &7%1$s &cfrom the position of faction admin."),
    COMMAND_ADMIN_DEMOTED("&c&l[!] &cYou have been demoted from the position of faction admin by &7%1$s&c"),
    COMMAND_ADMIN_PROMOTES("&c&l[!] &7You have promoted &c%1$s &7to the position of faction admin."),
    COMMAND_ADMIN_PROMOTED("&c&l[!] &c%1$s &7gave &c%2$s &7the leadership of &c%3$s&7."),
    COMMAND_ADMIN_DESCRIPTION("Hand over your admin rights"),
    COMMAND_ADMIN_NOMEMBERS("&e&l[!] &cNo one else to promote, please disband faction."),

    COMMAND_AHOME_DESCRIPTION("Send a player to their f home no matter what."),
    COMMAND_AHOME_NOHOME("%1$s doesn't have an f home."),
    COMMAND_AHOME_SUCCESS("%1%s was sent to their f home."),
    COMMAND_AHOME_OFFLINE("%1$s is offline."),
    COMMAND_AHOME_TARGET("You were sent to your f home."),

    COMMAND_ANNOUNCE_DESCRIPTION("Announce a message to players in faction."),
    COMMAND_ALTS_DESCRIPTION("Faction Alts Commands"),

    COMMAND_ALTS_LIST_DESCRIPTION("List all alts in your faction"),

    ANTI_SPAWNER_MINE_PLAYERS_NEAR("&c&l[!] &7You may not break spawners while enemies are near!"),

    COMMAND_AUTOCLAIM_ENABLED("&c&l[!] &7Now &cauto-claiming&7 land for %1$s."),
    COMMAND_AUTOCLAIM_DISABLED("&c&l[!] Auto-claiming&7 of land is now &cdisabled."),
    COMMAND_AUTOCLAIM_REQUIREDRANK("&c&l[!] &7You must be &c%1$s&7 to claim land."),
    COMMAND_AUTOCLAIM_OTHERFACTION("&c&l[!]&7 You &ccan't &7claim land for &c%1$s&7."),
    COMMAND_AUTOCLAIM_DESCRIPTION("Auto-claim land as you walk around"),

    COMMAND_AUTOUNCLAIM_ENABLED("&eNow auto-unclaiming land for &d%1$s&e."),
    COMMAND_AUTOUNCLAIM_DISABLED("&eAuto-unclaiming of land disabled."),
    COMMAND_AUTOUNCLAIM_OTHERFACTION("&cYou can't unclaim land for &d%1$s&c."),
    COMMAND_AUTOUNCLAIM_DESCRIPTION("Auto-unclaim land as you walk around"),


    COMMAND_ALTINVITE_DESCRIPTION("Invite Alts to your faction."),
    COMMAND_ALTKICK_DESCRIPTION("Kick alts from your faction"),
    COMMAND_ALTKICK_NOTALT("&c&l[!] &7Player is not an alt."),
    COMMAND_ALTKICK_NOTMEMBER("&c&l[!] &7This player is not a member of your faction."),

    COMMAND_ALTS_LIST_NOALTS("&c&l[!] &7%s does not have any alts in their faction!"),
    COMMAND_AUTOHELP_HELPFOR("Help for command \""),
    COMMAND_HOME_OTHER_NOTSET("&c&l[!] &7%s does not have their faction home set!"),
    COMMAND_HOME_TELEPORT_OTHER("&c&l[!] &7You have teleported to %s's faction home!"),
    COMMAND_SHOP_DESCRIPTION("opens shop gui"),
    COMMAND_SHOP_NO_FACTION("&c&l[!] &7You must be in a faction to perform this command!"),


    COMMAND_BAN_DESCRIPTION("Ban players from joining your Faction."),
    COMMAND_BAN_TARGET("&c&l[!] &7You were &cbanned &7from &c%1$s"), // banned player perspective
    COMMAND_BAN_BANNED("&c&l[!] &7%1$s &cbanned &7%2$s"),
    COMMAND_BAN_SELF("&c&l[!] &7You may &cnot &7ban &cyourself&7."),
    COMMAND_BAN_INSUFFICIENTRANK("&c&l[!] &7Your &crank &7is too low to&c ban &7%1$s"),
    COMMAND_BAN_ALREADYBANNED("&c&l[!] &7This player is &calready banned&7!"),

    COMMAND_BANLIST_DESCRIPTION("View a Faction's ban list"),
    COMMAND_BANLIST_HEADER("&c&l[!] &7There are &c%d&7 bans for &c%s"),
    COMMAND_BANLIST_ENTRY("&7%d. &c%s &r&7// &c%s &r&7// &c%s"),
    COMMAND_BANLIST_NOFACTION("&c&l[!] &7You are &cnot &7in a Faction."),
    COMMAND_BANLIST_INVALID("&c&l[!] &7The faction &c%s &7does not exist"),

    COMMAND_BOOM_PEACEFULONLY("&c&l[!] &7This command is &conly &7usable by factions which are &cspecifically &7designated as &cpeaceful&7."),
    COMMAND_BOOM_TOTOGGLE("to toggle explosions"),
    COMMAND_BOOM_FORTOGGLE("for toggling explosions"),
    COMMAND_BOOM_ENABLED("&c&l[!] &c%1$s&7 has&c %2$s&7 explosions in your faction's territory."),
    COMMAND_BOOM_DESCRIPTION("Toggle explosions (peaceful factions only)"),

    COMMAND_GIVEBOOSTER_INVALID_DURATION("&c&l[!] &b%1$s &7is not a valid duration!"),
    COMMAND_GIVEBOOSTER_INVALID_BOOSTER("&c&l[!] &b%1$s &7is not a valid booster! &ePlease use: EXP, MOB, MCMMO."),
    COMMAND_GIVEBOOSTER_INVALID_MULTIPLIER("&c&l[!] &b%1$s &7is not a valid multiplier!"),
    COMMAND_GIVEBOOSTER_BOOSTER_GIVEN("&a&l[!] &7You have given a booster to &b%1$s&7."),
    COMMAND_GIVEBOOSTER_DESCRIPTION("Give a Player a Faction Booster"),

    COMMAND_SANDBOTS_DESCRIPTION("The ability to spawn sandbots in"),

    COMMAND_SET_BASE_REGION_FAILED("&cPlease wait {time} minutes before resetting your base region."),
    COMMAND_SET_BASE_REGION_SUCCESS("&aSaved {claims} connected claims in a 50x50 radius as your faction's base region."),
    COMMAND_SET_BASE_REGION_DESCRIPTION("Set base region for faction"),
    COMMAND_SET_BASE_REGION_RESET("&aYour factions Base Region has been successfully cleared"),
    COMMAND_SET_BASE_REGION_MAX_REGIONS("&c&l[!] &7Your faction already has set their base region &e%1$s&7/&e%2$s times!"),
    COMMAND_SET_BASE_REGION_GRACE("&c&l[!] &7You may not set your base region whilst &cgrace is disabled&7!"),
    COMMAND_SET_BASE_REGION_NOT_CLAIMS("&c&l[!] &7You must be in &eyour own claims &7to use this command!"),

    COMMAND_BOOSTER_NONE_ACTIVE("&c&l[!] &7Your faction does not have any boosters active!"),
    COMMAND_BOOSTER_DESCRIPTION("View All Active Factions Boosters"),

    BOOSTER_CANNOT_USE_WILDERNESS("&c&l[!] &7You may not use a faction booster whilst in wilderness!"),
    BOOSTER_OVER_CAP_LIMIT("&c&l[!] &7You may not use boosters with over a 10x multiplier!"),
    BOOSTER_MULTIPLE_RUNNING("&c&l[!] &7You can only apply the same {multiplier}x while your booster is active."),
    BOOSTER_ALREADY_ACTIVE("&c&l[!] Your faction already has this booster activated by {player} for another {time-left}!"),
    BOOSTER_TITLE_COMMAND("&e&lCurrent Active Faction Boosters&e:"),
    BOOSTER_ACTIVE_PHRASE("&6&l* &e&l{multiplier}x {boosterType} &6started by &e&l{player} &6expires in &e&l{time-left}&6!"),
    BOOSTER_EXPIRED("&c&l[!] &e&l{multiplier}x {boosterType} &ffrom &e&l{player} &fhas &c&lEXPIRED&7!"),
    BOOSTER_REMINDER_EXP("&a&l+ {multiplier}x EXP &a({player}''s Faction Booster &7[{time-left}]&a)"),
    BOOSTER_REMINDER_MCMMO("&a&l+ {multiplier}x mcMMO &a({player}''s Faction Booster &7[{time-left}]&a)"),

    COMMAND_BYPASS_ENABLE("&e&l[!] &eYou have enabled admin bypass mode. You will be able to build or destroy anywhere."),
    COMMAND_BYPASS_ENABLELOG(" has ENABLED admin bypass mode."),
    COMMAND_BYPASS_DISABLE("&c&l[!] &cYou have disabled admin bypass mode."),
    COMMAND_BYPASS_DISABLELOG(" has DISABLED admin bypass mode."),
    COMMAND_BYPASS_DESCRIPTION("Enable admin bypass mode"),

    COMMAND_BANNER_DESCRIPTION("Turn a held banner into a war banner"),
    BANNER_CANNOT_BREAK("&c&l[!] &7You may not break a faction banner!"),
    COMMAND_BANNER_NOBANNER("&c&l[!] &cPlease set a banner using /f setbanner"),
    COMMAND_BANNER_NOTENOUGHMONEY("&c&l[!] &7You do&c not&7 have enough money"),
    COMMAND_BANNER_MONEYTAKE("&c&l[!] $&c{amount} &7has been taken from your account."),
    COMMAND_BANNER_SUCCESS("&c&l[!] &7You have created a &c&lWarBanner!"),
    COMMAND_BANNER_DISABLED("&c&l[!] &7Buying&c warbanners&7 is &cdisabled!"),

    COMMAND_TPBANNER_NOTSET("&c&l[!] &7Your faction &cdoes not &7have a &c&lWarBanner &7placed!"),
    COMMAND_TPBANNER_SUCCESS("&c&l[!] &cTeleporting &7to your factions's &c&lWarBanner"),
    COMMAND_TPBANNER_DESCRIPTION("Teleport to your faction banner"),


    COMMAND_CHAT_DISABLED("&c&l[!] &7The built in chat channels are &cdisabled &7on this server."),
    COMMAND_CHAT_INVALIDMODE("&c&l[!] &cUnrecognised &7chat mode. Please enter either '&da&7','&af&7','&6m&7' or '&fp&7'"),
    COMMAND_CHAT_DESCRIPTION("Change chat mode"),

    COMMAND_CHAT_MODE_PUBLIC("&c&l[!] &fPublic &7chat mode."),
    COMMAND_CHAT_MODE_ALLIANCE("&c&l[!] &dAlliance &7only chat mode."),
    COMMAND_CHAT_MODE_TRUCE("&c&l[!] &5Truce &7only chat mode."),
    COMMAND_CHAT_MODE_FACTION("&c&l[!] &aFaction&7 only chat mode."),
    COMMAND_CHAT_MODE_MOD("&c&l[!] &dMod &7only chat mode."),
    COMMAND_CHAT_MOD_ONLY("&c&l[!] &7Only Mods can talk through this chat mode."),

    COMMAND_CHATSPY_ENABLE("&c&l[!] &7You have &cenabled &7chat spying mode."),
    COMMAND_CHATSPY_ENABLELOG(" has ENABLED chat spying mode."),
    COMMAND_CHATSPY_DISABLE("&c&l[!] &7You have &cdisabled &7chat spying mode."),
    COMMAND_CHATSPY_DISABLELOG(" has DISABLED chat spying mode."),
    COMMAND_CHATSPY_DESCRIPTION("Enable admin chat spy mode"),

    COMMAND_CLAIM_INVALIDRADIUS("&c&l[!]&7 If you specify a &cradius&7, it must be at least &c1&7."),
    COMMAND_CLAIM_RADIUSOVER("&c&l[!] &7The specified radius is too large. &cRadius Claim Limit: %s"),
    COMMAND_CLAIM_DENIED("&c&l[!]&7 You &cdo not &7have &cpermission&7 to &cclaim&7 in a radius."),
    COMMAND_CLAIM_DESCRIPTION("Claim land from where you are standing"),

    COMMAND_CLAIMFILL_DESCRIPTION("Claim land filling in a gap in claims"),
    COMMAND_CLAIMFILL_ABOVEMAX("&cThe maximum limit for claim fill is %s."),
    COMMAND_CLAIMFILL_ALREADYCLAIMED("&cCannot claim fill using already claimed land!"),
    COMMAND_CLAIMFILL_TOOFAR("&cThis fill would exceed the maximum distance of %.2f"),
    COMMAND_CLAIMFILL_PASTLIMIT("&cThis claim would exceed the limit!"),
    COMMAND_CLAIMFILL_NOTENOUGHLANDLEFT("%s &cdoes not have enough land left to make %d claims"),
    COMMAND_CLAIMFILL_TOOMUCHFAIL("&cAborting claim fill after %d failures"),

    COMMAND_CLAIMLINE_INVALIDRADIUS("&c&l[!]&7 If you &cspecify&7 a distance, it must be at least &c1&7."),
    COMMAND_CLAIMLINE_DENIED("&c&l[!]&7 You &cdo not &7have&c permission&7 to claim in a line."),
    COMMAND_CLAIMLINE_DESCRIPTION("Claim land in a straight line."),
    COMMAND_CLAIMLINE_ABOVEMAX("&c&l[!]&7 The &cmaximum&7 limit for claim line is &c%s&7."),
    COMMAND_CLAIMLINE_NOTVALID("&c&l[!]&7 &c%s&7 is not a &ccardinal &7direction. You may use &cnorth&7, &ceast&7, &csouth &7or &cwest&7."),

    CHEST_ITEM_DENIED_TRANSFER("&c&l[!] &7You may not transfer &b%1$s &7into your factions chest!"),

    COMMAND_CONFIG_NOEXIST("&c&l[!]&7 No configuration setting \"&c%1$s&7\" exists."),
    COMMAND_CONFIG_SET_TRUE("\" option set to true (enabled)."),
    COMMAND_CONFIG_SET_FALSE("\" option set to false (disabled)."),
    COMMAND_CONFIG_OPTIONSET("\" option set to "),
    COMMAND_CONFIG_COLOURSET("\" color option set to \""),
    COMMAND_CONFIG_INTREQUIRED("Cannot set \"%1$s\": An integer (whole number) value required."),
    COMMAND_CONFIG_LONGREQUIRED("Cannot set \"%1$s\": A long integer (whole number) value required."),
    COMMAND_CONFIG_DOUBLEREQUIRED("Cannot set \"%1$s\": A double (numeric) value required."),
    COMMAND_CONFIG_FLOATREQUIRED("Cannot set \"%1$s\": A float (numeric) value required."),
    COMMAND_CONFIG_INVALID_COLOUR("Cannot set \"%1$s\": \"%2$s\" is not a valid color."),
    COMMAND_CONFIG_INVALID_COLLECTION("\"%1$s\" is not a data collection type which can be modified with this command."),
    COMMAND_CONFIG_INVALID_MATERIAL("Cannot change \"%1$s\" set: \"%2$s\" is not a valid material."),
    COMMAND_CONFIG_INVALID_TYPESET("\"%1$s\" is not a data type set which can be modified with this command."),
    COMMAND_CONFIG_MATERIAL_ADDED("\"%1$s\" set: Material \"%2$s\" added."),
    COMMAND_CONFIG_MATERIAL_REMOVED("\"%1$s\" set: Material \"%2$s\" removed."),
    COMMAND_CONFIG_SET_ADDED("\"%1$s\" set: \"%2$s\" added."),
    COMMAND_CONFIG_SET_REMOVED("\"%1$s\" set: \"%2$s\" removed."),
    COMMAND_CONFIG_LOG(" (Command was run by %1$s.)"),
    COMMAND_CONFIG_ERROR_SETTING("Error setting configuration setting \"%1$s\" to \"%2$s\"."),
    COMMAND_CONFIG_ERROR_MATCHING("Configuration setting \"%1$s\" couldn't be matched, though it should be... please report this error."),
    COMMAND_CONFIG_ERROR_TYPE("'%1$s' is of type '%2$s', which cannot be modified with this command."),
    COMMAND_CONFIG_DESCRIPTION("Change a conf.json setting"),

    COMMAND_CONVERT_BACKEND_RUNNING("&c&l[!]&7 Already running that backend."),
    COMMAND_CONVERT_BACKEND_INVALID("&c&l[!]&7 Invalid backend"),
    COMMAND_CONVERT_DESCRIPTION("Convert the plugin backend"),

    COMMAND_COORDS_MESSAGE("&c&l[!] &7{player}&7's coords are &c{x}&7,&c{y}&7,&c{z}&7 in &c{world}"),
    COMMAND_COORDS_DESCRIPTION("broadcast your coords to your faction"),

    COMMAND_CHECKPOINT_DISABLED("&c&l[!]&7 You &ccannot&7 use checkpoint while its&c disabled&7!"),
    COMMAND_CHECKPOINT_SET("&c&l[!]&7 You have &cset&7 the &cfaction checkpoint&7 at your &cLocation&7."),
    COMMAND_CHECKPOINT_GO("&c&l[!]&7 &cTeleporting&7 to &cfaction checkpoint"),
    COMMAND_CHECKPOINT_INVALIDLOCATION("&c&l[!]&7 &cInvalid Location!&7 You can &cset&7 checkpoints in &cyour claims&7 or &2wilderness&7."),
    COMMAND_CHECKPOINT_NOT_SET("&c&l[!]&7 You have to &cset &7the &cfaction checkpoint&7 first."),
    COMMAND_CHECKPOINT_CLAIMED("&c&l[!]&7 Your current &cfaction checkpoint&7 is claimed, set a &cnew &7one!"),
    COMMAND_CHECKPOINT_DESCRIPTION("Set or go to your faction checkpoint!"),

    COMMAND_CREATE_ALREADY_RESERVED("&c&l[!] &7This faction tag has already been reserved!"),
    COMMAND_CREATE_MUSTLEAVE("&c&l[!]&7 You must &cleave &7your &ccurrent faction &7first."),
    COMMAND_CREATE_INUSE("&c&l[!]&7 That tag is &calready &7in use."),
    COMMAND_CREATE_OVER_LIMIT("&c&l[!]&7 &cThe amount of factions allowed on the server has already been reached!"),

    COMMAND_CREATE_TOCREATE("to create a new faction"),
    COMMAND_CREATE_FORCREATE("for creating a new faction"),
    COMMAND_CREATE_ERROR("&c&l[!]&7 There was an &cinternal error&7 while trying to create your faction. &cPlease try again&7."),
    COMMAND_CREATE_CREATED("&c&l[!]&7 &c%1$s &7created a new faction named &c&l%2$s&7."),
    COMMAND_CREATE_YOUSHOULD("&c&l[!]&7 You should now: &c%1$s"),
    COMMAND_CREATE_CREATEDLOG(" created a new faction: "),
    COMMAND_CREATE_DESCRIPTION("Create a new faction"),


    COMMAND_DELHOME_SUCCESS("%1$s has deleted your faction home"),
    COMMAND_DELHOME_DESCRIPTION("delete home of your faction"),


    COMMAND_CHECK_DESCRIPTION("manage your factions check system!"),
    CHECK_BUFFERS_CHECK("\n &c&lFaction Walls&7 » &bCheck Your Buffers! \n"),
    CHECK_WALLS_CHECK("\n &c&lFaction Walls&7 » &bCheck Your Walls! \n"),
    CHECK_ALREADY_CHECKED("&c&lFaction Settings&7 » &bWalls have already been checked"),
    CHECK_NO_CHECKS("&c&lFaction Walls&7 » &bNothing to Check!"),
    CHECK_WALLS_MARKED_CHECKED("&c&lFaction Walls&7 » &aMarked walls as checked"),
    CHECK_BUFFERS_MARKED_CHECKED("&c&lFaction Walls&7 » &aMarked buffers as checked"),
    CHECK_HISTORY_GUI_TITLE("&aCheck History"),
    CHECK_SETTINGS_GUI_TITLE("&a&lManage Check Settings"),
    CHECK_WALL_CHECK_GUI_ICON("&a&lWall Check Settings"),
    CHECK_BUFFER_CHECK_GUI_ICON("&a&lBuffer Check Settings"),
    CHECK_CHECK_LORE_LINE("&bCheck: &a%1$s"),
    CHECK_WALLS_CHECKED_GUI_ICON("&aWalls checked"),
    CHECK_BUFFERS_CHECKED_GUI_ICON("&aBuffers checked"),
    CHECK_WALLS_UNCHECKED_GUI_ICON("&cWalls unchecked"),
    CHECK_BUFFERS_UNCHECKED_GUI_ICON("&cBuffers unchecked"),
    CHECK_TIME_LORE_LINE("&bTime: &f%1$s"),
    CHECK_PLAYER_LORE_LINE("&bPlayer: &f%1$s"),
    CHECK_HISTORY_GUI_ICON("&bCheck history"),
    CHECK_MUST_BE_ATLEAST_COLEADER("&cYou must be atleast &dCo Leader &cto access &fcheck settings"),
    WEE_WOO_MESSAGE("&c&lFaction WeeWoo&7 » We Are Being Raided!"),
    COMMAND_WEEWOO_STARTED("&c&lFaction WeeWoo&7 » &aWeewoo started by %1$s"),
    COMMAND_WEEWOO_STOPPED("&c&lFaction WeeWoo&7 » &aWeewoo stopped by %1$s"),
    COMMAND_WEEWOO_ALREADY_STARTED("&cWeewoo already started"),
    COMMAND_WEEWOO_ALREADY_STOPPED("&cWeewoo already stopped"),
    COMMAND_WEEWOO_DESCRIPTION("notifies all faction members you are being raided"),
    CHECK_LEADERBOARD_HEADER("&8---- Check Leaderboard ----"),
    CHECK_LEADERBOARD_LINE("&f%1$s. &d%2$s: &f%3$s (%4$s Buffer, %5$s Walls)"),
    CHECK_LEADERBOARD_NO_DATA("&8No data"),
    COMMAND_DISCORD_DESCRIPTION("Link your Discord account"),
    COMMAND_DEBUG_DESCRIPTION("Print debugging info to console"),
    COMMAND_DEBUG_PRINTED("&c&l[!]&7 Debug info has been printed to console"),

    //DISCORD
    COMMAND_INVITE_BOT("&c&l[!] Click Here To Invite The Discord Bot To Your Factions Discord"),
    WEEWOO_ALREADY_STARTED_DISCORD("Weewoo already started"),
    WEEWOO_ALREADY_STARTED_INGAME("&cWeewoo already started"),
    WEEWOO_STARTED_DISCORD("Weewoo started by %1$s"),
    WEEWOO_ALREADY_STOPPED_DISCORD("Weewoo already stopped"),
    WEEWOO_STOPPED_DISCORD("Weewoo stopped by %1$s"),
    INVITE_BOT_USAGE("Gets the invite for the Discord bot"),
    SET_GUILD_ID_USAGE("Sets the guild id for the faction"),
    SET_GUILD_ID_SUCCESS("&aSuccesfully set guild id"),
    SET_GUILD_ID_INVALID_ID("&cInvalid guild id (is the bot in the guild?)"),
    SET_GUILD_ID_UNABLE_TO_MESSAGE_GUILD_OWNER("&cUnable to message guild owner"),
    SET_GUILD_ID_TIMED_OUT_MINECRAFT("&cTimed out"),
    SET_GUILD_ID_TIMED_OUT_DISCORD("Timed out"),
    SET_GUILD_ID_GUILD_ALREADY_LINKED("&cThat guild is already linked to a faction"),
    SET_GUILD_ID_RESET_ID("&c&l[!] &7You have reset your guild id!"),
    SET_GUILD_ID_PMING_OWNER("&aNow Direct messaging the Discord server owner to approve the link, times out in 15 seconds."),
    CANT_FORCE_SET_GUILD_ID("&cYou cannot forcefully set guild ids for other guilds."),
    DISCORD_LINK_REQUIRED("&c&l[!] You cannot do this while your Discord is not linked! Link your account by using /f discord link"),
    DISCORD_LINK_SUCCESS("Your account has been linked!"),
    DISCORD_CODE_SENT("&c&l[!]&7 Your code is &f%1$s &7please send this to the SaberFactions bot on Discord"),
    DISCORD_ALREADY_LINKED("&c&l[!]&7 Your account is already linked to &f%1$s"),
    DISCORD_CODE_INVALID_KEY("That code is invalid, verify the code is correct."),
    DISCORD_CODE_INVALID_FORMAT("If you are submitting a code please only type the code. Example message: 0000"),

    GOD_APPLE_COOLDOWN("&c&l[!] &cYou may eat another god apple again in &b{seconds} seconds&c!"),
    ENDER_PEARL_COOLDOWN("&c&l[!] &cYou may enderpearl again in &b{seconds} seconds&c!"),
    VEHICLE_TELEPORT_BLOCK("&c&l[!] You cannot teleport while you are inside a vehicle"),


    COMMAND_DEINVITE_CANDEINVITE("&c&l[!]&7 Players you can &cdeinvite: "),
    COMMAND_DEINVITE_CLICKTODEINVITE("&c&l[!]&7 Click to &crevoke&7 invite for &c%1$s"),
    COMMAND_DEINVITE_ALREADYMEMBER("&c&l[!]&7 &c%1$s&7 is already a member of &c%2$s"),
    COMMAND_DEINVITE_MIGHTWANT("&c&l[!]&7 You might want to: &c%1$s"),
    COMMAND_DEINVITE_REVOKED("&c&l[!]&7 &7%1$s &crevoked&7 your invitation to &c%2$s&7."),
    COMMAND_DEINVITE_REVOKES("&c&l[!]&7 %1$s&c revoked &7%2$s's&c invitation."),
    COMMAND_DEINVITE_DESCRIPTION("Remove a pending invitation"),

    COMMAND_DELFWARP_DELETED("&c&l[!]&7 Deleted warp &c%1$s"),
    COMMAND_DELFWARP_INVALID("&c&l[!]&7 Couldn't &cfind&7 warp &c%1$s"),
    COMMAND_DELFWARP_TODELETE("to delete warp"),
    COMMAND_DELFWARP_FORDELETE("for deleting warp"),
    COMMAND_DELFWARP_DESCRIPTION("Delete a faction warp"),

    COMMAND_DESCRIPTION_CHANGES("&c&l[!]&7 You have &cchanged&7 the &cdescription&7 for &c%1$s&7 to:"),
    COMMAND_DESCRIPTION_CHANGED("&c&l[!]&7 The faction&c %1$s&7 changed their &cdescription &7to:"),
    COMMAND_DESCRIPTION_TOCHANGE("to change faction description"),
    COMMAND_DESCRIPTION_FORCHANGE("for changing faction description"),
    COMMAND_DESCRIPTION_DESCRIPTION("Change the faction description"),

    COMMAND_DISBAND_IMMUTABLE("&c&l[!]&7 &7You &ccannot&7 disband &2Wilderness&7,&e SafeZone&7, or &4WarZone."),
    COMMAND_DISBAND_TOO_YOUNG("&c&l[!] &7Your Faction is too young to withdraw money like this!"),
    COMMAND_DISBAND_MARKEDPERMANENT("&c&l[!]&7 This faction is designated as&c permanent&7, so you cannot disband it."),
    COMMAND_DISBAND_BROADCAST_YOURS("&c&l[!]&7 &c%1$s&7 disbanded your &cfaction."),
    COMMAND_DISBAND_BROADCAST_GENERIC("&c&l[!]&7 The Faction &c%1$s&7 was disbanded."),
    COMMAND_DISBAND_BROADCAST_NOTYOURS("&c&l[!]&7 &c%1$s &7disbanded the faction &c%2$s&7."),
    COMMAND_DISBAND_HOLDINGS("&c&l[!]&7 &7You have been given the disbanded &cfaction's bank&7, totaling &c%1$s."),
    COMMAND_DISBAND_PLAYER("&c&l[!] &7You have disbanded your &cfaction"),
    COMMAND_DISBAND_CONFIRM("&c&l[!]&7 Your Faction has&c {tnt} &7tnt left in the bank, it will be &clost&7 if the faction is &cdisbanded&7. Type&c /f disband &7again within &c10&7 seconds to&c disband&7."),
    COMMAND_DISBAND_DESCRIPTION("Disband a faction"),

    COMMAND_FLY_DISABLED("&c&l[!]&7 Sorry, Faction flight is &cdisabled &7on this server."),
    COMMAND_FLY_DESCRIPTION("Enter or leave Faction flight mode"),
    COMMAND_FLY_CHANGE("&c&l[!]&7 Faction flight has been &c%1$s&7."),
    COMMAND_FLY_COOLDOWN("&c&l[!]&7 You will &cnot&7 take fall damage for &c{amount}&7 seconds."),
    COMMAND_FLY_DAMAGE("&c&l[!]&7 Faction flight &cdisabled&7 due to entering combat."),
    COMMAND_FLY_NO_ACCESS("&c&l[!]&7 &cCannot fly &7in territory of %1$s"),
    COMMAND_FLY_ENEMY_NEAR("&c&l[!]&7 Flight has been&c disabled&7 an enemy is nearby."),
    COMMAND_FLY_CHECK_ENEMY("&c&l[!]&7 Cannot fly here, an enemy is &cnearby&7."),
    COMMAND_FLY_NO_EPEARL("&c&l[!] &7You &ccannot&7 throw enderpearls while flying!"),
    COMMAND_FLY_AUTO("&eFaction auto flight &d%1$s"),

    COMMAND_FOCUS_SAMEFACTION("&c[!] You may not focus players in your faction!"),
    COMMAND_FOCUS_FOCUSING("&c&l[!] &7Your faction is now focusing &c%s"),
    COMMAND_FOCUS_NO_LONGER("&c&l[!] &7Your faction is no longer focusing &c%s"),
    COMMAND_FOCUS_DESCRIPTION("Focus a Specific Player"),

    COMMAND_FRIENDLY_FIRE_DESCRIPTION("Toggle friendly fire for yourself."),
    COMMAND_FRIENDLY_FIRE_TOGGLE_OFF("&c[!] &7You have toggled friendly fire &4off&7!"),
    COMMAND_FRIENDLY_FIRE_TOGGLE_ON("&c[!] &7You have toggled friendly fire &aon&7!"),
    FRIENDLY_FIRE_OFF_ATTACKER("&b%1$s &7has friendly fire toggle &4off&7!"),
    FRIENDLY_FIRE_YOU_MUST("&c[!] &7You must have friendly fire active to attack this person!"),

    COMMAND_ALLYFWARP_INVALID_FACTION("&c&l[!] &cThat faction does not exist!"),
    COMMAND_ALLYFWARP_DESCRIPTION("Teleport to a ally/truces faction warps"),

    COMMAND_ALLYFWARP_USAGE("&c&l[!]&7 /f warp <faction> <warpname> &c[password]"),
    COMMAND_ALLYFWARP_MUSTBE("&c&l[!] &cYou must be at least truced with this faction to access their warps!"),

    COMMAND_FWARP_CLICKTOWARP("&c&l[!]&7 Click to &cwarp!"),
    COMMAND_FWARP_COMMANDFORMAT("&c&l[!]&7 /f warp <warpname> &c[password]"),
    COMMAND_FWARP_WARPED("&c&l[!]&7 Warped to &c%1$s"),
    COMMAND_FWARP_INVALID_WARP("&c&l[!]&7 Couldn't find warp &c%1$s"),
    COMMAND_FWARP_TOWARP("to warp"),
    COMMAND_FWARP_FORWARPING("for warping"),
    COMMAND_FWARP_WARPS("Warps: "),
    COMMAND_FWARP_DESCRIPTION("Teleport to a faction warp"),
    COMMAND_FWARP_INVALID_PASSWORD("&c&l[!]&7 &cInvalid password!"),
    COMMAND_FWARP_PASSWORD_REQUIRED("&c&l[!]&c Warp Password:"),
    COMMAND_FWARP_PASSWORD_TIMEOUT("&c&l[!]&7 Warp password &ccanceled"),

    COMMAND_GRACE_TIME_REMAINING("&c&lGracePeriod &8» &cTime Remaining: &b%1$s"),
    COMMAND_GRACE_DISABLED_NO_FORMAT("&c[!] &7Grace is disabled! Explosions are allowed!"),
    COMMAND_GRACE_ENABLED_FORMAT("&c&lGracePeriod &8» &7Grace Period Has Now &aStarted&7! &cTime Remaining: &b%1$s"),
    COMMAND_GRACE_DISABLED_FORMAT("&c&lGracePeriod &8» &7Grace Period Has Now &cEnded&7! &cExplosions are now enabled!"),
    COMMAND_GRACE_DESCRIPTION("Toggles Grace Period on/off"),
    COMMAND_GRACE_ENABLED_PLACEMENT("&cYou cannot place &e%s &cwhile grace period is active!"),

    COMMAND_HINT_PERMISSION("&aYou can manage your factions permissions using &7/f perms"),

    COMMAND_SPAWNERCHUNK_CLAIM_SUCCESSFUL("&a[!] &7You have successfully claimed a &espawner chunk &7for your faction."),
    COMMAND_SPAWNERCHUNK_ALREADY_CHUNK("&c&l[!] &7This chunk is already a spawnerchunk!"),
    COMMAND_SPAWNERCHUNK_PAST_LIMIT("&c&l[!] &cYou have exceeded your max spawnerchunk limit! &7Limit: &f%1$s"),
    SPAWNER_CHUNK_UNCLAIMED("&aYou have unclaimed a spawnerchunk!"),
    COMMAND_SPAWNERCHUNK_DESCRIPTION("Claim a spawnerchunk"),

    COMMAND_HOME_DISABLED("&c&l[!]&7 Sorry, Faction homes are &cdisabled on this server."),
    COMMAND_HOME_TELEPORTDISABLED("&c&l[!]&7 Sorry, the ability to &cteleport &7to Faction homes is &cdisabled &7on this server."),
    COMMAND_HOME_NOHOME("&c&l[!]&7 Your faction does &cnot &7have a home. "),
    COMMAND_HOME_UNSET("&c&l[!]&7 Sorry, your faction home has been &cun-set &7since it is no longer in your territory."),
    COMMAND_HOME_INENEMY("&c&l[!]&7 You &ccannot teleport &7to your &cfaction home&7 while in the territory of an &cenemy faction&7."),
    COMMAND_HOME_WRONGWORLD("&c&l[!]&7 You &ccannot &7teleport to your &cfaction home&7 while in a different world."),
    COMMAND_HOME_ENEMYNEAR("&c&l[!]&7 You &ccannot teleport&7 to your faction home while an enemy is within &c%s&7 blocks of you."),
    COMMAND_HOME_TOTELEPORT("to teleport to your faction home"),
    COMMAND_HOME_FORTELEPORT("for teleporting to your faction home"),
    COMMAND_HOME_DESCRIPTION("Teleport to the faction home"),
    COMMAND_HOME_BLOCKED("&c&l[!] You may not teleport to a home that is claimed by &b%1$s"),

    COMMAND_INVENTORYSEE_DESCRIPTION("View a faction members inventory"),

    COMMAND_INSPECT_DISABLED_MSG("&c&l[!]&7 Inspect mode is now &cdisabled."),
    COMMAND_INSPECT_DISABLED_NOFAC("&c&l[!]&7 Inspect mode is now &cdisabled,&7 because you &cdo not have a faction!"),
    COMMAND_INSPECT_ENABLED("&c&l[!]&7 Inspect mode is now &aEnabled."),
    COMMAND_INSPECT_HEADER("&c&m---&7Inspect Data&c&m---&c//&7x:{x},y:{y},z:{z}"),
    COMMAND_INSPECT_ROW("&c{time} &7// &c{action} &7// &c{player} &7// &c{block-type}"),
    COMMAND_INSPECT_NODATA("&c&l[!]&7 &7No Data was found!"),
    COMMAND_INSPECT_NOTINCLAIM("&c&l[!]&7 &7You can &conly&7 inspect in &cyour &7claims!"),
    COMMAND_INSPECT_BYPASS("&c&l[!]&7 Inspecting in &cbypass&7 mode"),
    COMMAND_INSPECT_DESCRIPTION("Inspect blocks!"),

    COMMAND_INVITE_TOINVITE("to invite someone"),
    COMMAND_INVITE_FORINVITE("for inviting someone"),
    COMMAND_INVITE_CLICKTOJOIN("Click to join!"),
    COMMAND_INVITE_INVITEDYOU("&c&l[!]&7 &c%1$s&7 has invited you to join &c%2$s&7"),
    COMMAND_INVITE_INVITED("&c&l[!]&7 &c%1$s&7 invited &c%2$s&7 to your faction."),
    COMMAND_ALTINVITE_INVITED_ALT("&c&l[!]&7 &c%1$s&7 invited &c%2$s&7 to your faction as an alt."),

    COMMAND_INVITE_ALREADYMEMBER("&c&l[!]&7 &c%1$s&7 is already a member of&c %2$s"),
    COMMAND_INVITE_ALREADYINVITED("&c&l[!]&7 &c%1$s&7 has already been invited"),
    COMMAND_INVITE_DESCRIPTION("Invite a player to your faction"),
    COMMAND_INVITE_BANNED("&c&l[!]&7 &7%1$s &cis banned &7from your Faction. &cNot &7sending an invite."),
    COMMAND_INVITE_NOT_IN_ROSTER("&c&l[!] &b%s &7is not in your factions roster!"),

    COMMAND_JOIN_CANNOTFORCE("&c&l[!]&7 You&c do not&7 have permission to &cmove other players&7 into a faction."),
    COMMAND_JOIN_SYSTEMFACTION("&c&l[!]&7 Players may only join &cnormal factions&7. This is a &c&lsystem faction&7."),
    COMMAND_JOIN_ALREADYMEMBER("&c&l[!]&7 &c%1$s %2$s already a member of&c %3$s"),
    COMMAND_JOIN_ATLIMIT_MEMBERS(" &c&l[!]&7 The faction &c%1$s &7is at the limit of&c %2$d&7 members, so&c %3$s&7 cannot currently join."),
    COMMAND_JOIN_ATLIMIT_ALTS(" &c&l[!]&7 The faction &c{faction} &7is at the limit of&c {limit}&7 alts, so&c {at}&7 cannot currently join."),
    COMMAND_JOIN_INOTHERFACTION("&c&l[!]&7 &c%1$s &7must leave&c %2$s &7current faction first."),
    COMMAND_JOIN_NEGATIVEPOWER("&c&l[!]&7 &c%1$s &7cannot join a faction with a &cnegative power&7 level."),
    COMMAND_JOIN_REQUIRESINVITATION("&c&l[!]&7 This faction &crequires&7 an invitation."),
    COMMAND_JOIN_ATTEMPTEDJOIN("&c&l[!]&7 &c%1$s&7 tried to join your faction."),
    COMMAND_JOIN_TOJOIN("to join a faction"),
    COMMAND_JOIN_FORJOIN("for joining a faction"),
    COMMAND_JOIN_SUCCESS("&c&l[!]&7 &c%1$s &7successfully joined &c%2$s."),
    COMMAND_JOIN_MOVED("&c&l[!]&7 &c%1$s &7moved you into the faction&c %2$s."),
    COMMAND_JOIN_JOINED("&c&l[!]&7 &c%1$s &7joined your faction."),
    COMMAND_JOIN_JOINEDLOG("&c&l[!]&7 &c%1$s&7 joined the faction&c %2$s."),
    COMMAND_JOIN_MOVEDLOG("&c&l[!]&7 &c%1$s &7moved the player&c %2$s &7into the faction&c %3$s&7."),
    COMMAND_JOIN_DESCRIPTION("&a&l» &7Join a faction"),
    COMMAND_JOIN_BANNED("&c&l[!]&7 You are &cbanned &7from &c%1$s."),
    COMMAND_JOIN_NOT_IN_ROSTER("&c&l[!] &7You cannot join this faction, you're not on their roster!"),

    COMMAND_KICK_CANDIDATES("&c&l[!]&7 Players you can kick: "),
    COMMAND_KICK_CLICKTOKICK("Click to kick "),
    COMMAND_KICK_SELF("&c&l[!]&7 You &ccannot &7kick&c yourself&7."),
    COMMAND_KICK_NONE("&c&l[!]&7 That player&c is not&7 in a faction."),
    COMMAND_KICK_NOTMEMBER("&c&l[!]&7 &c%1$s is not a member of %2$s"),
    COMMAND_KICK_INSUFFICIENTRANK("&c&l[!]&7 Your rank is &ctoo low &7to kick this player."),
    COMMAND_KICK_NEGATIVEPOWER("&c&l[!]&7 You &ccannot &7kick that member until their power is &apositive&7."),
    COMMAND_KICK_TOKICK("to kick someone from the faction"),
    COMMAND_KICK_FORKICK("for kicking someone from the faction"),
    COMMAND_KICK_FACTION("&c&l[!]&7 %1$s&7 kicked %2$s&c from the faction!"), //message given to faction members
    COMMAND_KICK_KICKS("&c&l[!]&7 You kicked &c%1$s&7 from the faction&c %2$s&7!"), //kicker perspective
    COMMAND_KICK_KICKED("&c&l[!]&7 &c%1$s &7kicked you from&c %2$s&7!"), //kicked player perspective
    COMMAND_KICK_DESCRIPTION("Kick a player from the faction"),

    COMMAND_LIST_FACTIONLIST("&c&l[!]&7 Faction List "),
    COMMAND_LIST_TOLIST("to list the factions"),
    COMMAND_LIST_FORLIST("for listing the factions"),
    COMMAND_LIST_ONLINEFACTIONLESS("Online factionless: "),
    COMMAND_LIST_DESCRIPTION("&a&l» &7See a list of the factions"),


    COMMAND_SPAM_ENABLED("&c&l[!]&7 Factions Anti-Spam is now&a enabled"),
    COMMAND_SPAM_DISABLED("&c&l[!]&7 Factions Anti-Spam in now&c disabled"),
    COMMAND_SPAM_DESCRIPTION("enable antispam system"),

    COMMAND_LOCK_LOCKED("&c&l[!]&7 Factions is now&c locked"),
    COMMAND_LOCK_UNLOCKED("&c&l[!]&7 Factions in now&a unlocked"),
    COMMAND_LOCK_DESCRIPTION("Lock all write stuff. Apparently."),

    COMMAND_LOGINS_TOGGLE("&c&l[!]&7 Set login / logout notifications for Faction members to: &c%s"),
    COMMAND_LOGINS_DESCRIPTION("Toggle(?) login / logout notifications for Faction members"),

    COMMAND_LOWPOWER_HEADER("&8&m--------&8<Players with power under {maxpower}&8>&8&m---------"),
    COMMAND_LOWPOWER_FORMAT("&c{player} &8(&c{player_power}&8/&c{maxpower}&8)"),
    COMMAND_LOWPOWER_DESCRIPTION("Shows a list of players in your faction with lower power levels"),

    COMMAND_LOOKUP_INVALID("&c&l[!] &cInvalid Faction Found!"),
    COMMAND_LOOKUP_FACTION_HOME("&c&l[!] &cFaction Home: &f%1$dx %2$sy %3$sz"),
    COMMAND_LOOKUP_CLAIM_COUNT("&c&l[!] &cFound &c&n%1$s &cClaimed Chunk(s) for &f%2$s"),
    COMMAND_LOOKUP_CLAIM_LIST("&f%1$s &7(%2$sx, %2$sz)"),
    COMMAND_LOOKUP_ONLY_NORMAL("&cYou can only enter normal factions."),
    COMMAND_LOOKUP_DESCRIPTION("Lookup claim & home stats for faction"),


    COMMAND_MAP_TOSHOW("to show the map"),
    COMMAND_MAP_FORSHOW("for showing the map"),
    COMMAND_MAP_UPDATE_ENABLED("&c&l[!]&7 Map auto update &aENABLED."),
    COMMAND_MAP_UPDATE_DISABLED("&c&l[!]&7 Map auto update &cDISABLED."),
    COMMAND_MAP_DESCRIPTION("Show the territory map, and set optional auto update"),

    COMMAND_MAPHEIGHT_DESCRIPTION("&eUpdate the lines that /f map sends"),
    COMMAND_MAPHEIGHT_SET("&c&l[!]&7 Set /f map lines to &c&a%1$d"),
    COMMAND_MAPHEIGHT_CURRENT("&c&l[!]&7 Current &cmapheight: &a%1$d"),

    COMMAND_MOD_CANDIDATES("&c&l[!]&7 Players you can promote: "),
    COMMAND_MOD_CLICKTOPROMOTE("Click to promote "),
    COMMAND_MOD_NOTMEMBER("&c&l[!]&7 &c%1$s7 is not a member in your faction."),
    COMMAND_MOD_NOTADMIN("&c&l[!]&7 You &care not&7 the faction admin."),
    COMMAND_MOD_SELF("&c&l[!]&7 The target player&c musn't&7 be yourself."),
    COMMAND_MOD_TARGETISADMIN("&c&l[!]&7 The target player is a &cfaction admin.&7 Demote them first."),
    COMMAND_MOD_REVOKES("&c&l[!]&7 &7You have &cremoved&7 moderator status from &c%1$s."),
    COMMAND_MOD_REVOKED("&c&l[!]&7 &c%1$s&7 is &cno longer&7 moderator in your faction."),
    COMMAND_MOD_PROMOTES("&c&l[!]&7 &c%1$s&7 was &cpromoted&7 to moderator in your faction."),
    COMMAND_MOD_PROMOTED("&c&l[!]&7 You have promoted&c %1$s&7 to moderator."),
    COMMAND_MOD_DESCRIPTION("Give or revoke moderator rights"),

    COMMAND_COLEADER_CANDIDATES("&c&l[!]&7 Players you can promote: "),
    COMMAND_COLEADER_CLICKTOPROMOTE("Click to promote "),
    COMMAND_COLEADER_NOTMEMBER("&c&l[!]&7 &c%1$s&7 is &cnot a member&7 in your faction."),
    COMMAND_COLEADER_NOTADMIN("&c&l[!]&7 You are&c not&7 the faction admin."),
    COMMAND_COLEADER_SELF("&c&l[!]&7 The target player&c musn't&7 be yourself."),
    COMMAND_COLEADER_TARGETISADMIN("&c&l[!]&7 The target player is a &cfaction admin&7. Demote them first."),
    COMMAND_COLEADER_REVOKES("&c&l[!]&7 You have removed &ccoleader &7status from&c %1$s&7."),
    COMMAND_COLEADER_REVOKED("&c&l[!]&7 &c%1$s&7 is no longer&c coleader &7in your faction."),
    COMMAND_COLEADER_PROMOTES("&c&l[!]&7 &c%1$s&7 was promoted to &ccoleader &7in your faction."),
    COMMAND_COLEADER_PROMOTED("&c&l[!]&7 You have &cpromoted &7%1$s to &ccoleader."),
    COMMAND_COLEADER_DESCRIPTION("Give or revoke coleader rights"),

    COMMAND_CONVERTCONFIG_DESCRIPTION("Convert your SavageFactions config to SaberFactions"),
    COMMAND_CONVERTCONFIG_SUCCESS("&cConfiguration successfully converted"),
    COMMAND_CONVERTCONFIG_FAIL("&cConfiguration conversion failed!"),
    COMMAND_CONVERTCONFIG_FAILCONFIGMISSING("&cPlease ensure you have placed SavageFactions files in a folder called SavageFactions"),

    COMMAND_MODIFYPOWER_ADDED("&c&l[!]&7 Added &c%1$f &7power to &c%2$s. &7New total rounded power: &c%3$d"),
    COMMAND_MODIFYPOWER_DESCRIPTION("Modify the power of a faction/player"),

    COMMAND_MONEY_LONG("&c&l[!]&7 The faction money commands."),
    COMMAND_MONEY_DESCRIPTION("Faction money commands"),

    COMMAND_MONEY_CANTAFFORD("&c&l[!]&7 &c%1$s&7 can't afford &c%2$s&7 %3$s"),
    COMMAND_MONEY_GAINED("&c&l[!]&7 &c%1$s&7 gained &c%2$s %2%6"),

    COMMAND_MONEYBALANCE_SHORT("show faction balance"),
    COMMAND_MONEYBALANCE_DESCRIPTION("Show your factions current money balance"),

    COMMAND_MONEYDEPOSIT_DESCRIPTION("Deposit money"),
    COMMAND_MONEYDEPOSIT_DEPOSITED("&c&l[!]&7 &c%1$s &7deposited&c %2$s&7 in the faction bank:&c %3$s"),

    COMMAND_MONEYTRANSFERFF_DESCRIPTION("Transfer f -> f"),
    COMMAND_MONEYTRANSFERFF_TRANSFER("&c&l[!]&7 &c%1$s&7 transferred&c %2$s &7from the faction &c\"%3$s\"&7 to the faction&c \"%4$s\"&7"),
    COMMAND_MONEYTRANSFERFF_TRANSFERCANTAFFORD("&c&l[!]&7 &c%1$s&7 can't afford to transfer &c%2$s &7to %3$s"),


    COMMAND_MONEYTRANSFERFP_DESCRIPTION("Transfer f -> plugin"),
    COMMAND_MONEYTRANSFERFP_TRANSFER("&c&l[!]&7 &c%1$s &7transferred&c %2$s &7from the faction&c \"%3$s\" &7to the player &c\"%4$s\""),

    COMMAND_MONEYTRANSFERPF_DESCRIPTION("Transfer plugin -> f"),
    COMMAND_MONEYTRANSFERPF_TRANSFER("&c&l[!]&7 &c%1$s&7 transferred &c%2$s&7 from the player &c\"%3$s\" &7to the faction&c \"%4$s\""),

    COMMAND_MONEYWITHDRAW_DESCRIPTION("Withdraw money"),
    COMMAND_MONEYWITHDRAW_WITHDRAW("&c&l[!]&7 &c%1$s&7 withdrew&c %2$s &7from the faction bank:&c %3$s"),


    COMMAND_COOLDOWN("&c&l[!] &7You are currently on cooldown for this command!"),
    COMMAND_OPEN_TOOPEN("to open or close the faction"),
    COMMAND_OPEN_FOROPEN("for opening or closing the faction"),
    COMMAND_OPEN_OPEN("open"),
    COMMAND_OPEN_CLOSED("closed"),
    COMMAND_OPEN_CHANGES("&c&l[!]&7 &c%1$s&7 changed the faction to &c%2$s&7."),
    COMMAND_OPEN_CHANGED("&c&l[!]&7 The faction &c%1$s&7 is now &c%2$s&7."),
    COMMAND_OPEN_DESCRIPTION("Switch if invitation is required to join"),

    COMMAND_OWNER_DISABLED("&c&l[!]&7 Sorry, but &cowned areas &7are &cdisabled &7on this server."),
    COMMAND_OWNER_LIMIT("&c&l[!]&7 Sorry, but you have reached the server's &climit&7 of &c%1$d&7 owned areas per faction."),
    COMMAND_OWNER_WRONGFACTION("&c&l[!]&7 &7This land is &cnot claimed &7by your faction, so you &ccan't set&7 ownership of it."),
    COMMAND_OWNER_NOTCLAIMED("&c&l[!]&7 This land&c is not &7claimed by a faction. Ownership &cis not &7possible."),
    COMMAND_OWNER_NOTMEMBER("&c&l[!]&7 &c%1$s&7 is &cnot a member &7of this faction."),
    COMMAND_OWNER_CLEARED("&c&l[!]&7 You have &ccleared &7ownership for this claimed area."),
    COMMAND_OWNER_REMOVED("&c&l[!]&7 You have&c removed ownership &7of this &cclaimed land&7 from &c%1$s&7."),
    COMMAND_OWNER_TOSET("to set ownership of claimed land"),
    COMMAND_OWNER_FORSET("for setting ownership of claimed land"),
    COMMAND_OWNER_ADDED("&c&l[!]&7 You have added &c%1$s&7 to the &cowner list&7 for this claimed land."),
    COMMAND_OWNER_DESCRIPTION("Set ownership of claimed land"),

    COMMAND_KILLHOLOGRAMS_DESCRIPTION("Kill holograms in a radius, admin command"),

    COMMAND_OWNERLIST_DISABLED("&c&l[!]&7 &cSorry, &7but owned areas are &cdisabled&7 on this server."),//dup->
    COMMAND_OWNERLIST_WRONGFACTION("&c&l[!]&7 This land &cis not&7 claimed by your faction."),//eq
    COMMAND_OWNERLIST_NOTCLAIMED("&c&l[!]&7 This land is not claimed by any faction, thus no owners."),//eq
    COMMAND_OWNERLIST_NONE("&c&l[!]&7 No owners are set here; everyone in the faction has access."),
    COMMAND_OWNERLIST_OWNERS("&c&l[!]&7 Current owner(s) of this land: %1$s"),
    COMMAND_OWNERLIST_DESCRIPTION("List owner(s) of this claimed land"),

    COMMAND_DISCORDSET_ADMIN_SUCCESSFUL("&c&l[!] &7You have set &b%1$s's &7discord to &b%2$s&7."),
    COMMAND_DISCORDSET_ADMIN_FAILED("&c&l[!] &b%1$s &7is not an discord link!"),
    COMMAND_DISCORDSET_NOTEMAIL("&c&l[!] &b%1$s &7is not an discord link!"),
    COMMAND_DISCORDSET_DESCRIPTION("&c&l[!] &7Set the link of your factions discord."),
    COMMAND_DISCORDSET_SUCCESSFUL("&c&l[!] &7Successfully set your factions discord link - &b%1$s&7."),
    DISCORD_PLAYER_DISCORD("&c&l[!] &7You're factions discord link is: &b%1$s&7."),
    COMMAND_DISCORD_NOTSET("&c&l[!] &7Your faction does not have their discord set!"),
    COMMAND_DISCORDSEE_FACTION_NOTSET("&c&l[!] &b%1$s's &7discord has not yet been set!"),
    COMMAND_DISCORDSEE_FACTION_DISCORD("&c&l[!] &b%1$s's &7faction has their discord link set to &b%2$s&7."),
    COMMAND_DISCORDSEE_DESCRIPTION("&c&l[!] &7View a specific factions discord link with &b/f discord <faction>&b."),


    PAYPALSEE_PLAYER_PAYPAL("&c&l[!] &7You're factions paypal is: &b%1$s&7."),
    COMMAND_PAYPAL_NOTSET("&c&l[!] &7Your faction does not have their paypal set!"),
    COMMAND_PAYPALSET_ADMIN_SUCCESSFUL("&c&l[!] &7You have set &b%1$s's &7paypal to &b%2$s&7."),
    COMMAND_PAYPALSET_ADMIN_FAILED("&c&l[!] &b%1$s &7is not an email!"),
    COMMAND_PAYPALSET_NOTEMAIL("&c&l[!] &b%1$s &7is not an email!"),
    COMMAND_PAYPALSET_DESCRIPTION("&c&l[!] &7Set the email of your faction to claim rewards."),
    COMMAND_PAYPALSEE_DESCRIPTION("&c&l[!] &7View a specific factions paypal email with &b/f <seepaypal/getpaypal> <faction>&b."),
    COMMAND_PAYPALSET_CREATED("&c&l[!] &7Make sure to type &b/f <paypal/setpaypal> <email>&7!"),
    COMMAND_PAYPALSET_SUCCESSFUL("&c&l[!] &7Successfully set your factions email - &b%1$s&7."),
    COMMAND_PAYPALSEE_FACTION_PAYPAL("&c&l[!] &b%1$s's &7faction has their paypal set to &b%2$s&7."),
    COMMAND_PAYPALSEE_FACTION_NOTSET("&c&l[!] &b%1$s's &7paypal has not yet been set!"),
    COMMAND_PAYPALSEE_FACTION_NOFACTION("&c&l[!] &b%1$s &7does not have a faction!"),

    COMMAND_PEACEFUL_DESCRIPTION("&c&l[!]&7Set a faction to peaceful"),
    COMMAND_PEACEFUL_YOURS("&c&l[!]&7%1$s has %2$s your faction"),
    COMMAND_PEACEFUL_OTHER("&c&l[!]&7%s has %s the faction '%s'."),
    COMMAND_PEACEFUL_GRANT("&c&l[!]&7 granted peaceful status to"),
    COMMAND_PEACEFUL_REVOKE("removed peaceful status from"),

    COMMAND_PERM_DESCRIPTION("&c&l[!]&7&6Edit or list your Faction's permissions."),
    COMMAND_PERM_INVALID_RELATION("&c&l[!]&7Invalid relation defined. Try something like&c 'ally'"),
    COMMAND_PERM_INVALID_ACCESS("&c&l[!]&7 Invalid access defined. Try something like &c'allow'"),
    COMMAND_PERM_INVALID_ACTION("&c&l[!]&7 Invalid action defined. Try something like &c'build'"),
    COMMAND_PERM_SET("&c&l[!]&7 Set permission&c %1$s &7to &c%2$s &7for relation&c %3$s"),
    COMMAND_PERM_TOP("RCT MEM OFF ALLY TRUCE NEUT ENEMY"),
    COMMAND_PERM_LOCKED("&cThis permission has been locked by the server"),
    COMMAND_PERM_EDIT_ONLY_MENU("&c&l[!] &cYou mat not edit permissions via command!"),


    COMMAND_POINTS_SHOW_DESCRIPTION("See the point balance of factions"),
    COMMAND_POINTS_SHOW_WILDERNESS("&c&l[!] &7You may not check the point balance of wilderness!"),
    COMMAND_POINTS_SHOW_OWN("&c&l[!] &7Your faction has &e%1$s &7points."),
    COMMAND_POINTS_SHOW_OTHER("&c&l[!] &e{faction} &7has a point balance of &b{points}&7."),
    COMMAND_POINTS_FAILURE("&c&l[!] &c{faction} does not exist."),
    COMMAND_POINTS_SUCCESSFUL("&c&l[!] &7You have added &e%1$s &7points to &b%2$s&7. &b%2$s's &7New Point Balance: &e%3$s"),
    COMMAND_POINTS_INSUFFICIENT("&c&l[!] &7You may not add/set/remove a negative number of points to a faction!"),
    COMMAND_POINTS_DESCRIPTION("General Command For Faction Points"),

    COMMAND_ADDPOINTS_DESCRIPTION("Add Points to Faction"),


    COMMAND_REMOVEPOINTS_SUCCESSFUL("&c&l[!] &7You have taken &e%1$s &7points from &b%2$s&7. &b%2$s's &7New Point Balance: &e%3$s"),
    COMMAND_REMOVEPOINTS_DESCRIPTION("Remove Points from a Faction"),

    COMMAND_SETPOINTS_SUCCESSFUL("&c&l[!] &7You have set &e%1$s &7points to &b%2$s&7. &b%2$s's &7New Point Balance: &e%3$s"),
    COMMAND_SETPOINTS_DESCRIPTION("Set Points of a Faction"),

    COMMAND_SET_RELATION_SUCCESS("&c&l[!] &7You have set a relation of &e%1$s &7between &b%2$s and &b%3$s&7."),
    COMMAND_SET_RELATION_DESCRIPTION("Set the relation of 2 factions."),

    COMMAND_PERMANENT_DESCRIPTION("Toggles a permanent faction option"),
    COMMAND_PERMANENT_GRANT("&c&l[!]&7 added permanent status to"),
    COMMAND_PERMANENT_REVOKE("&c&l[!]&7 removed permanent status from"),
    COMMAND_PERMANENT_YOURS("&c&l[!]&7 &c%1$s&7 has &c%2$s&7 your faction"),
    COMMAND_PERMANENT_OTHER("&c&l[!]&7 &c%s &7has &c%s &7the faction '&c%s&7'."),
    COMMAND_PROMOTE_TARGET("&c&l[!]&7 You've been &c%1$s&7 to &c%2$s&7."),
    COMMAND_PROMOTE_SUCCESS("&c&l[!]&7 You successfully &c%1$s %2$s &cto&7 %3$s&7."),
    COMMAND_PROMOTE_PROMOTED("promoted"),
    COMMAND_PROMOTE_DEMOTED("demoted"),
    COMMAND_PROMOTE_LOWEST_RANK("&c&l[!]&7 &c%1$s&7 already has the lowest rank in the faction."),
    COMMAND_PROMOTE_HIGHEST_RANK("&c&l[!]&7 &c%1$s&7 already has the highest rank in the faction."),
    COMMAND_PROMOTE_HIGHER_RANK("&c&l[!]&7 &c%1$s&7 has a higher rank than yours. You &4can not modify&7 his rank."),
    COMMAND_PROMOTE_COLEADER_ADMIN("&c&l[!]&7 &cColeaders cant promote players to Admin!"),

    COMMAND_PERMANENTPOWER_DESCRIPTION("Toggle permanent faction power option"),
    COMMAND_PERMANENTPOWER_GRANT("added permanentpower status to"),
    COMMAND_PERMANENTPOWER_REVOKE("removed permanentpower status from"),
    COMMAND_PERMANENTPOWER_SUCCESS("&c&l[!]&7 You&c %s &7%s."),
    COMMAND_PERMANENTPOWER_FACTION("&c&l[!]&7 &c%s %s &7your faction"),

    COMMAND_PROMOTE_DESCRIPTION("/f promote <name>"),
    COMMAND_PROMOTE_WRONGFACTION("&c&l[!]&7 &c%1$s&7 is &cnot&7 part of your faction."),
    COMMAND_NOACCESS("&c&l[!]&7 You don't have access to that."),
    COMMAND_PROMOTE_NOT_ALLOWED("&c&l[!]&7 You cannot promote to the same rank as yourself!"),
    COMMAND_PROMOTE_NOTSELF("&c&l[!]&7 You cannot manage your own rank."),
    COMMAND_PROMOTE_NOT_SAME("&c&l[!]&7 You cannot promote to the same rank as yourself!"),


    COMMAND_POWER_TOSHOW("to show player power info"),
    COMMAND_POWER_FORSHOW("for showing player power info"),
    COMMAND_POWER_POWER("&c&l[!]&7 &c%1$s » &cPower &7/ &cMaxpower&a » &c%2$d &7/ &c%3$d %4$s"),
    COMMAND_POWER_BONUS(" (bonus: "),
    COMMAND_POWER_PENALTY(" (penalty: "),
    COMMAND_POWER_DESCRIPTION("&a&l» &7Show player &apower &7info"),

    COMMAND_POWERBOOST_HELP_1("&c&l[!]&7 You must specify \"plugin\" or \"player\" to target a player or \"f\" or \"faction\" to target a faction."),
    COMMAND_POWERBOOST_HELP_2("&c&l[!]&7 ex. /f powerboost plugin SomePlayer 0.5  -or-  /f powerboost f SomeFaction -5"),
    COMMAND_POWERBOOST_INVALIDNUM("You must specify a valid numeric value for the power bonus/penalty amount."),
    COMMAND_POWERBOOST_PLAYER("Player \"%1$s\""),
    COMMAND_POWERBOOST_FACTION("Faction \"%1$s\""),
    COMMAND_POWERBOOST_BOOST("%1$s now has a power bonus/penalty of %2$d to min and max power levels."),
    COMMAND_POWERBOOST_BOOSTLOG("%1$s has set the power bonus/penalty for %2$s to %3$d."),
    COMMAND_POWERBOOST_DESCRIPTION("Apply permanent power bonus/penalty to specified player or faction"),

    FACTION_RALLY_MESSAGE("&e&l[!] &7%1$s's coords are &e%2$s, %3$s, %4$s"),
    COMMAND_RALLY_DESCRIPTION("Send a Rally Message/Waypoint To Faction Members"),

    COMMAND_RELATIONS_ALLTHENOPE("&c&l[!]&7 &cNope!You can't."),
    COMMAND_RELATIONS_MORENOPE("&c&l[!]&7 &cNope!&7You can't declare a relation to &cyourself"),
    COMMAND_RELATIONS_ALREADYINRELATIONSHIP("&c&l[!]&7 You &calready&7 have that relation wish set with&c %1$s."),
    COMMAND_RELATIONS_TOMARRY("to change a relation wish"),
    COMMAND_RELATIONS_FORMARRY("for changing a relation wish"),
    COMMAND_RELATIONS_MUTUAL("&c&l[!]&7 Your faction is now %1$s&7 to &c%2$s&7."),
    COMMAND_RELATIONS_PEACEFUL("&c&l[!]&7 This will have no effect while your faction is peaceful."),
    COMMAND_RELATIONS_PEACEFULOTHER("&c&l[!]&7 This will have &cno effect&7 while their faction is peaceful."),
    COMMAND_RELATIONS_DESCRIPTION("Set relation wish to another faction"),
    COMMAND_RELATIONS_EXCEEDS_ME("&c&l[!]&7 Failed to set relation wish. You can only have %1$s %2$s."),
    COMMAND_RELATIONS_EXCEEDS_THEY("&c&l[!]&7 Failed to set relation wish. They can only have %1$s %2$s."),

    COMMAND_RELATIONS_PROPOSAL_1("&c&l[!]&7 &c%1$s &7wishes to be your &c%2$s&7."),
    COMMAND_RELATIONS_PROPOSAL_2("&c&l[!]&7 Type &c/%1$s %2$s %3$s&7 to accept."),
    COMMAND_RELATIONS_PROPOSAL_SENT("&c&l[!]&7 &c%1$s&7 were informed that you wish to be &c%2$s&7."),

    COMMAND_RELOAD_TIME("&c&l[!]&7 Reloaded &call &7configuration files from disk, took &c%1$d ms."),
    COMMAND_RELOAD_NOTICE("&c&l[!] NOTE: &fFiles That Require Stopping Server: conf.json, any file in the configuration directory."),
    COMMAND_RELOAD_DESCRIPTION("Reload data file(s) from disk"),

    COMMAND_RESERVE_DESCRIPTION("Reserve any faction name for any player"),
    COMMAND_RESERVE_SUCCESS("&a&l[!] &7You have reserved the faction &a%1$s &7for player &a%2$s"),
    COMMAND_RESERVE_ALREADYRESERVED("&c&l[!] &7The faction &b%1$s &7has already been reserved!"),


    COMMAND_SAFEUNCLAIMALL_DESCRIPTION("Unclaim all safezone land"),
    COMMAND_SAFEUNCLAIMALL_UNCLAIMED("&c&l[!]&7 You unclaimed&c ALL&7 safe zone land."),
    COMMAND_SAFEUNCLAIMALL_UNCLAIMEDLOG("&c&l[!]&7 &c%1$s&7 unclaimed all safe zones."),

    COMMAND_SAVEALL_SUCCESS("&c&l[!]&7 &cFactions saved to disk!"),
    COMMAND_SAVEALL_DESCRIPTION("Save all data to disk"),

    COMMAND_SCOREBOARD_DESCRIPTION("Scoreboardy things"),

    COMMAND_SETBANNER_SUCCESS("&c&l[!] &7Banner Pattern Set!"),
    COMMAND_SETBANNER_NOTBANNER("&c&l[!] &7The item is &cnot&7 a banner!"),
    COMMAND_SETBANNER_DESCRIPTION("set banner pattern for your faction"),


    COMMAND_SETDEFAULTROLE_DESCRIPTION("/f defaultrole <role> - set your Faction's default role."),
    COMMAND_SETDEFAULTROLE_NOTTHATROLE("&c&l[!]&7 You cannot set the default to admin."),
    COMMAND_SETDEFAULTROLE_SUCCESS("Set default role of your faction to %1$s"),
    COMMAND_SETDEFAULTROLE_INVALIDROLE("Couldn't find matching role for %1$s"),

    COMMAND_SETFWARP_NOTCLAIMED("&c&l[!]&7 You can &conly&7 set warps in your faction territory."),
    COMMAND_SETFWARP_LIMIT("&c&l[!]&7 Your Faction already has the &cmax amount&7 of warps set &c(%1$d)."),
    COMMAND_SETFWARP_SET("&c&l[!]&7 Set warp &c%1$s&7 and password &c'%2$s' &7to your location."),
    COMMAND_SETFWARP_TOSET("to set warp"),
    COMMAND_SETFWARP_FORSET("for setting warp"),
    COMMAND_SETFWARP_DESCRIPTION("Set a faction warp"),

    COMMAND_SETHOME_DISABLED("&c&l[!]&7 &cSorry&7, Faction homes are disabled on this server."),
    COMMAND_SETHOME_NOTCLAIMED("&c&l[!]&c Sorry&7, your faction home can only be set inside your &cown &7claimed territory."),
    COMMAND_SETHOME_TOSET("to set the faction home"),
    COMMAND_SETHOME_FORSET("for setting the faction home"),
    COMMAND_SETHOME_SET("&c&l[!]&c %1$s&7 set the home for your faction. You can now use:"),
    COMMAND_SETHOME_SETOTHER("&c&l[!]&7 You have set the home for the &c%1$s&7 faction."),
    COMMAND_SETHOME_DESCRIPTION("Set the faction home"),

    COMMAND_SETTNT_SUCCESS("&aSet tnt for faction &e%s &ato &b%d"),
    COMMAND_SETTNT_DESCRIPTION("set the amount of tnt for a faction"),


    COMMAND_SETPOWER_SUCCESS("&aSet power for &e%s &ato &b%d"),
    COMMAND_SETPOWER_DESCRIPTION("set current playing power for player"),

    COMMAND_SETMAXVAULTS_DESCRIPTION("Set max vaults for a Faction."),
    COMMAND_SETMAXVAULTS_SUCCESS("&aSet max vaults for &e%s &ato &b%d"),
    COMMAND_ONCOOOLDOWN("&c&l[!] &7You cannot use this command for another &b%1$s &7seconds."),

    COMMAND_SHIELD_DESCRIPTION("Use of shield selection guis"),

    COMMAND_SPAWNER_LOCK_TOGGLED("&c&l[!] &7You have set placement of spawners to %1$s"),
    COMMAND_SPAWNER_LOCK_DESCRIPTION("enable/disable placement of spawners"),
    COMMAND_SPAWNER_LOCK_CANNOT_PLACE("&c&l[!] &7Placement of spawners has been temporarily disabled!"),

    COMMAND_STRIKES_CHANGED("&c&l[!] &7You have set &c%1$s's &7strikes to &c%2$s"),
    COMMAND_STRIKES_INFO("&c&l[!] &7%1$s has %2$s strikes"),
    COMMAND_STRIKES_TARGET_INVALID("&c&l[!] &7The faction %1$s is invalid."),
    COMMAND_STRIKES_STRUCK("&c&l[!] &7Your faction strikes have changed by &c%1$s &7strike(s)! Your faction now has &c%2$s/%3$s"),
    COMMAND_STRIKES_DESCRIPTION("Set strikes on factions to warn them"),
    COMMAND_STRIKESGIVE_DESCRIPTION("Give a faction 1 strike"),
    COMMAND_STRIKETAKE_DESCRIPTION("Take a strike from a faction"),
    COMMAND_STRIKESET_DESCRIPTION("Set a faction's strikes explicitly."),
    COMMAND_STRIKESINFO_DESCRIPTION("Get a faction's strikes"),

    SHOP_NOT_ENOUGH_POINTS("&c&l[!] &7Your faction does not have enough points to purchase this!"),
    SHOP_ERROR_DURING_PURCHASE("&c&l[!] &7There was an error while trying to give items please check your inventory! Purchase was not completed!"),
    SHOP_BOUGHT_BROADCAST_FACTION("\n&c&l[!] &e&lFactionShop » &b{player} &7bought &b{item}&7 for &b{cost} &7points!\n"),


    COMMAND_VIEWCHEST_DESCRIPTION("view a factions chest/pv"),

    COMMAND_VAULT_DESCRIPTION("Open your placed faction vault!"),
    COMMAND_VAULT_INVALID("&c&l[!]&7 Your vault was either&c claimed&7, &cbroken&7, or has&c not been&7 placed yet."),
    COMMAND_VAULT_OPENING("&c&l[!]&7 Opening faction vault."),
    COMMAND_VAULT_NO_HOPPER("&c&l[!] &7You cannot place a hopper near a vault!"),

    COMMAND_GETVAULT_ALREADYSET("&c&l[!]&7 Vault has already been set!"),
    COMMAND_GETVAULT_ALREADYHAVE("&c&l[!]&7 You already have a vault in your inventory!"),
    COMMAND_GETVAULT_CHESTNEAR("&c&l[!]&7 &7There is a chest or hopper &cnearby"),
    COMMAND_GETVAULT_SUCCESS("&cSucessfully set vault."),
    COMMAND_GETVAULT_INVALIDLOCATION("&cVault can only be placed in faction land!"),
    COMMAND_GETVAULT_DESCRIPTION("Get the faction vault item!"),
    COMMAND_GETVAULT_RECEIVE("&cYou have recieved a faction vault!"),
    COMMAND_GETVAULT_NOMONEY("&cYou do not have enough money"),
    COMMAND_GETVAULT_MONEYTAKE("&c{amount} has been taken from your account"),

    COMMAND_LOGOUT_KICK_MESSAGE("&2You have safely logged out!"),
    COMMAND_LOGOUT_ACTIVE("&c&l[!] &7You are already logging out!"),
    COMMAND_LOGOUT_LOGGING("&c&l[!] &7You are logging out. Please wait &b%1$s &7seconds."),
    COMMAND_LOGOUT_DESCRIPTION("logout safely from the server"),
    COMMAND_LOGOUT_MOVED("&c&l[!] &7Your logout was cancelled because you moved!"),
    COMMAND_LOGOUT_DAMAGE_TAKEN("&c&l[!] &7Your logout was cancelled because you were damaged!"),
    COMMAND_LOGOUT_TELEPORTED("&c&l[!] &7Your logout was cancelled because you teleported!"),

    COMMAND_NOTIFICATIONS_TOGGLED_ON("&c&l[!] &7You will &anow see &7claimed land notifications!"),
    COMMAND_NOTIFICATIONS_TOGGLED_OFF("&c&l[!] &7You will &cno longer see &7claimed land notifications!"),
    COMMAND_NOTIFICATIONS_DESCRIPTION("Toggle notifications for land claiming"),

    COMMAND_SHOW_NOFACTION_SELF("You are not in a faction"),
    COMMAND_SHOW_NOFACTION_OTHER("That's not a faction"),
    COMMAND_SHOW_TOSHOW("to show faction information"),
    COMMAND_SHOW_FORSHOW("for showing faction information"),
    COMMAND_SHOW_DESCRIPTION("Description: %1$s"),
    COMMAND_SHOW_PEACEFUL("This faction is Peaceful"),
    COMMAND_SHOW_PERMANENT("This faction is permanent, remaining even with no members."),
    COMMAND_SHOW_JOINING("Joining: %1$s "),
    COMMAND_SHOW_INVITATION("invitation is required"),
    COMMAND_SHOW_UNINVITED("no invitation is needed"),
    COMMAND_SHOW_NOHOME("n/a"),
    COMMAND_SHOW_POWER("Land / Power / Maxpower:  %1$d/%2$d/%3$d %4$s."),
    COMMAND_SHOW_BONUS(" (bonus: "),
    COMMAND_SHOW_PENALTY(" (penalty: "),
    COMMAND_SHOW_DEPRECIATED("(%1$s depreciated)"), //This is spelled correctly.
    COMMAND_SHOW_LANDVALUE("Total land value: %1$s %2$s"),
    COMMAND_SHOW_BANKCONTAINS("Bank contains: %1$s"),
    COMMAND_SHOW_ALLIES("Allies: "),
    COMMAND_SHOW_ENEMIES("Enemies: "),
    COMMAND_SHOW_MEMBERSONLINE("Members online: "),
    COMMAND_SHOW_MEMBERSOFFLINE("Members offline: "),
    COMMAND_SHOW_COMMANDDESCRIPTION("Show faction information"),
    COMMAND_SHOW_DEATHS_TIL_RAIDABLE("DTR: %1$d"),
    COMMAND_SHOW_EXEMPT("This faction is exempt and cannot be seen."),
    COMMAND_SHOW_NEEDFACTION("&cYou need to join a faction to view your own!"),

    COMMAND_SHOWCLAIMS_HEADER("&8&m-------------&8<{faction}'s claims&8>&8&m-------------"),
    COMMAND_SHOWCLAIMS_FORMAT("&8[{world}]:"),
    COMMAND_SHOWCLAIMS_CHUNKSFORMAT("&8(&c{x}&8,&c{z}&8)"),
    COMMAND_SHOWCLAIMS_DESCRIPTION("show your factions claims!"),

    COMMAND_SHOWINVITES_PENDING("Players with pending invites: "),
    COMMAND_SHOWINVITES_CLICKTOREVOKE("Click to revoke invite for %1$s"),
    COMMAND_SHOWINVITES_DESCRIPTION("Show pending faction invites"),

    COMMAND_ALTS_LIST_FORMAT("%1$s Power: %2$s Last Seen: %3$s"),
    COMMAND_ALTS_DEINVITE_DESCRIPTION("Base command for revoking alt invitations"),

    COMMAND_SEECHUNK_DESCRIPTION("Show chunk boundaries"),
    COMMAND_SEECHUNK_TOGGLE("&eSeechunk &d%1$s"),

    COMMAND_STATUS_FORMAT("%1$s Power: %2$s Last Seen: %3$s"),
    COMMAND_STATUS_ONLINE("Online"),
    COMMAND_STATUS_AGOSUFFIX(" ago."),
    COMMAND_STATUS_DESCRIPTION("Show the status of a player"),

    COMMAND_STEALTH_DESCRIPTION("Enable and Disable Stealth Mode"),
    COMMAND_STEALTH_ENABLE("&cStealth &7» &7You will no longer disable nearby players in /f fly."),
    COMMAND_STEALTH_DISABLE("&cStealth &8» &7You will now disable other nearby players in /f fly."),
    COMMAND_STEALTH_MUSTBEMEMBER("&cStealth &8» &4You must be in a faction to use this command"),

    COMMAND_STUCK_TIMEFORMAT("m 'minutes', s 'seconds.'"),
    COMMAND_STUCK_CANCELLED("Teleport cancelled because you were damaged"),
    COMMAND_STUCK_OUTSIDE("Teleport cancelled because you left %1$d block radius"),
    COMMAND_STUCK_EXISTS("You are already teleporting, you must wait %1$s"),
    COMMAND_STUCK_START("Teleport will commence in %s. Don't take or deal damage. "),
    COMMAND_STUCK_TELEPORT("Teleported safely to %1$d, %2$d, %3$d."),
    COMMAND_STUCK_TOSTUCK("to safely teleport %1$s out"),
    COMMAND_STUCK_FORSTUCK("for %1$s initiating a safe teleport out"),
    COMMAND_STUCK_DESCRIPTION("Safely teleports you out of enemy faction"),

    COMMAND_SEECHUNK_ENABLED("&cSeechunk enabled!"),
    COMMAND_SEECHUNK_DISABLED("&cSeechunk disabled!"),


    COMMAND_TAG_TAKEN("That tag is already taken"),
    COMMAND_TAG_TOCHANGE("to change the faction tag"),
    COMMAND_TAG_FORCHANGE("for changing the faction tag"),
    COMMAND_TAG_FACTION("%1$s changed your faction tag to %2$s"),
    COMMAND_TAG_CHANGED("The faction %1$s changed their name to %2$s."),
    COMMAND_TAG_DESCRIPTION("Change the faction tag"),

    COMMAND_TITLE_TOCHANGE("to change a players title"),
    COMMAND_TITLE_FORCHANGE("for changing a players title"),
    COMMAND_TITLE_CHANGED("%1$s changed a title: %2$s"),
    COMMAND_TITLE_DESCRIPTION("Set or remove a players title"),

    COMMAND_TITLETOGGLE_TOGGLED("You have changed your title setting to &c%1$s"),
    COMMAND_TITLETOGGLE_DESCRIPTION("Toggle titles to be served to you"),


    COMMAND_TOGGLEALLIANCECHAT_DESCRIPTION("Toggles whether or not you will see alliance chat"),
    COMMAND_TOGGLEALLIANCECHAT_IGNORE("Alliance chat is now ignored"),
    COMMAND_TOGGLEALLIANCECHAT_UNIGNORE("Alliance chat is no longer ignored"),

    COMMAND_TOGGLESB_DISABLED("You can't toggle scoreboards while they are disabled."),


    COMMAND_TOP_DESCRIPTION("Sort Factions to see the top of some criteria."),
    COMMAND_TOP_TOP("Top Factions by %s. Page %d/%d"),
    COMMAND_TOP_LINE("%d. &6%s: &c%s"), // Rank. Faction: Value
    COMMAND_TOP_INVALID("Could not sort by %s. Try money, online, members, power or land."),

    COMMAND_TNT_DISABLED_MSG("&cThis command is disabled!"),
    COMMAND_TNT_INVALID_NUM("The amount needs to be a number!"),
    COMMAND_TNT_WIDTHDRAW_NOTENOUGH_TNT("&cNot enough tnt in bank."),
    COMMAND_TNTFILL_NODISPENSERS("&c&l[!] &7No dispensers were found in a radius of {radius} blocks."),
    COMMAND_TNT_DEPOSIT_SUCCESS("&cSuccessfully deposited tnt."),
    COMMAND_TNT_EXCEEDLIMIT("&cThis exceeds the bank limit!"),
    COMMAND_TNT_WIDTHDRAW_SUCCESS("&cSuccessfully withdrew tnt."),
    COMMAND_TNT_WIDTHDRAW_NOTENOUGH("&cNot enough tnt in bank."),
    COMMAND_TNT_DEPOSIT_NOTENOUGH("&cNot enough tnt in tnt inventory."),
    COMMAND_TNT_AMOUNT("&cYour faction has {amount}/{maxAmount} tnt in the tnt bank."),
    COMMAND_TNT_POSITIVE("&cPlease use positive numbers!"),
    COMMAND_TNT_DESCRIPTION("add/widthraw from faction's tnt bank"),
    COMMAND_TNT_WIDTHDRAW_NOTENOUGH_SPACE("&cNot enough space in your inventory."),
    COMMAND_TNT_ADD_DESCRIPTION("&b/f tnt add&3 <amount>"),
    COMMAND_TNT_TAKE_DESCRIPTION("&b/f tnt take&3 <amount>"),

    COMMAND_TNTFILL_HEADER("&c&l[!] &7Filling tnt in dispensers..."),
    COMMAND_TNTFILL_SUCCESS("&c&l[!] &7Filled &c{amount}&7 Tnt in &c{dispensers} &7dispensers"),
    COMMAND_TNTFILL_NOTENOUGH("&c&l[!] &7You do not have enough tnt in your tnt bank to fill that amount."),
    COMMAND_TNTFILL_RADIUSMAX("&c&l[!] &7The max radius is {max}"),
    COMMAND_TNTFILL_AMOUNTMAX("&c&l[!] &7The max amount is {max}"),
    COMMAND_TNTFILL_MOD("&c&l[!] &7Tnt will be used from the faction bank because you dont have the specified amount in your inventory and you are a {role}"),
    COMMAND_TNTFILL_DESCRIPTION("Fill tnt into dispensers around you"),

    COMMAND_UNBAN_DESCRIPTION("Unban someone from your Faction"),
    COMMAND_UNBAN_NOTBANNED("&7%s &cisn't banned. Not doing anything."),
    COMMAND_UNBAN_TARGET_IN_OTHER_FACTION("&c%1$s is not in your faction!"),
    COMMAND_UNBAN_UNBANNED("&e%1$s &cunbanned &7%2$s"),
    COMMAND_UNBAN_TARGETUNBANNED("&aYou were unbanned from &r%s"),

    COMMAND_UNCLAIM_SAFEZONE_SUCCESS("Safe zone was unclaimed."),
    COMMAND_UNCLAIM_WRONGFACTIONOTHER("&cAttempted to unclaim land for incorrect faction"),
    COMMAND_UNCLAIM_SAFEZONE_NOPERM("This is a safe zone. You lack permissions to unclaim."),
    COMMAND_UNCLAIM_WARZONE_SUCCESS("War zone was unclaimed."),
    COMMAND_UNCLAIM_WARZONE_NOPERM("This is a war zone. You lack permissions to unclaim."),
    COMMAND_UNCLAIM_UNCLAIMED("%1$s unclaimed some of your land."),
    COMMAND_UNCLAIM_UNCLAIMS("You unclaimed this land."),
    COMMAND_UNCLAIM_LOG("%1$s unclaimed land at (%2$s) from the faction: %3$s"),
    COMMAND_UNCLAIM_WRONGFACTION("You don't own this land."),
    COMMAND_UNCLAIM_TOUNCLAIM("to unclaim this land"),
    COMMAND_UNCLAIM_FORUNCLAIM("for unclaiming this land"),
    COMMAND_UNCLAIM_FACTIONUNCLAIMED("%1$s unclaimed some land."),
    COMMAND_UNCLAIM_DESCRIPTION("Unclaim the land where you are standing"),
    COMMAND_UNCLAIM_SPAWNERCHUNK_SPAWNERS("&c&l[!] &7You may not unclaim a spawnerchunk whilst there are still spawners in it! &eSpawner Count: %1$s"),

    COMMAND_UNCLAIMALL_TOUNCLAIM("to unclaim all faction land"),
    COMMAND_UNCLAIMALL_FORUNCLAIM("for unclaiming all faction land"),
    COMMAND_UNCLAIMALL_UNCLAIMED("%1$s unclaimed ALL of your faction's land."),
    COMMAND_UNCLAIMALL_LOG("%1$s unclaimed everything for the faction: %2$s"),
    COMMAND_UNCLAIMALL_DESCRIPTION("Unclaim all of your factions land"),
    COMMAND_UNCLAIM_CLICKTOUNCLAIM("Click to unclaim &2(%1$d, %2$d)"),

    COMMAND_UNCLAIMFILL_DESCRIPTION("Unclaim contiguous land"),
    COMMAND_UNCLAIMFILL_ABOVEMAX("&cThe maximum limit for unclaim fill is %s."),
    COMMAND_UNCLAIMFILL_NOTCLAIMED("&cCannot unclaim fill using non-claimed land!"),
    COMMAND_UNCLAIMFILL_TOOFAR("&cThis unclaim would exceed the maximum distance of %.2f"),
    COMMAND_UNCLAIMFILL_PASTLIMIT("&cThis unclaim would exceed the limit!"),
    COMMAND_UNCLAIMFILL_TOOMUCHFAIL("&cAborting unclaim fill after %d failures"),
    COMMAND_UNCLAIMFILL_UNCLAIMED("%1$s&e unclaimed %d claims of your faction's land around %s."),
    COMMAND_UNCLAIMFILL_BYPASSCOMPLETE("&eUnclaimed %d claims."),

    COMMAND_VERSION_NAME("&c&l[!]&7 &c&k||| &r&4SaberFactions&7 &c&k|||&r &c» &7By Driftay"),
    COMMAND_VERSION_VERSION("&7Version &c» &7%1$s"),
    COMMAND_VERSION_DESCRIPTION("Show plugin and translation version information"),

    COMMAND_WARUNCLAIMALL_DESCRIPTION("Unclaim all warzone land"),
    COMMAND_WARUNCLAIMALL_SUCCESS("You unclaimed ALL war zone land."),
    COMMAND_WARUNCLAIMALL_LOG("%1$s unclaimed all war zones."),


    COMMAND_DRAIN_DESCRIPTION("The ability to obtain all the money in faction members balances."),
    COMMAND_DRAIN_ROLE_DRAINED_AMOUNT("&c&l[!] &fYou have drained members with %1$s Role for &b%2$s."),
    COMMAND_DRAIN_NO_PLAYERS("&c&l[!] &cYou cannot drain a faction with no other members!"),
    COMMAND_DRAIN_RECIEVED_AMOUNT("&c&l[!] &fYou have drained all of your faction members for &b%1$s."),
    COMMAND_DRAIN_INVALID_AMOUNT("&c&l[!] &fYou cannot drain a faction with no worth."),
    COMMAND_DRAIN_COOLDOWN("&c&l[!] &cYou may use /f drain again in &b{seconds} seconds&c!"),

    COMMAND_WILD_DESCRIPTION("Teleport to a random location"),
    COMMAND_WILD_SUCCESS("&c&l[!] &7Teleporting..."),
    COMMAND_WILD_WORLD_NOT_ALLOWED("&c&l[!] &7You may not use &e/f wild &7in this world!"),
    COMMAND_WILD_FAILED("&c&l[!] &7No Location Found... Please Try Again!"),

    COMMAND_RULES_DISABLED_MSG("&cThis command is disabled!"),
    COMMAND_RULES_DESCRIPTION("set/remove/add rules!"),
    COMMAND_RULES_ADD_INVALIDARGS("Please include a rule!"),
    COMMAND_RULES_SET_INVALIDARGS("Please include a line number & rule!"),
    COMMAND_RULES_REMOVE_INVALIDARGS("Please include a line number!"),
    COMMAND_RULES_ADD_SUCCESS("&cRule added successfully!"),
    COMMAND_RULES_REMOVE_SUCCESS("&cRule removed successfully!"),
    COMMAND_RULES_SET_SUCCESS("&cRule set successfully!"),
    COMMAND_RULES_CLEAR_SUCCESS("&cRule cleared successfully!"),

    // F Global \\
    COMMAND_F_GLOBAL_TOGGLE("&c&l[!] &7You have &b%1$s &7Global Chat"),
    COMMAND_F_GLOBAL_DESCRIPTION("Toggle global chat and only allow factions based chats"),

    /**
     * Leaving - This is accessed through a command, and so it MAY need a COMMAND_* slug :s
     */
    LEAVE_PASSADMIN("&c&l[!] &7You must give the admin role to someone else first."),
    LEAVE_NEGATIVEPOWER("&c&l[!] &7You cannot leave until your power is positive."),
    LEAVE_TOLEAVE("to leave your faction."),
    LEAVE_FORLEAVE("for leaving your faction."),
    LEAVE_LEFT("&c&l[!] &c%s&7 left faction &c%s&7."),
    LEAVE_DISBANDED("&c&l[!] &c%s&7 was disbanded."),
    LEAVE_DISBANDEDLOG("The faction %s (%s) was disbanded due to the last player (%s) leaving."),
    LEAVE_DESCRIPTION("\\n  &a&l» &7Leave your faction"),
    AUTOLEAVE_ADMIN_PROMOTED("&e&l[!] &7Faction admin &c%s&7 has been removed. &c%s&7 has been promoted as the new faction admin."),

    /**
     * Claiming - Same as above basically. No COMMAND_* because it's not in a command class, but...
     */
    CLAIM_PROTECTED("This land is protected"),
    CLAIM_DISABLED("Sorry, this world has land claiming disabled."),
    CLAIM_CANTCLAIM("You can't claim land for %s."),
    CLAIM_CANTUNCLAIM("&cYou can't unclaim land for &d%s&c."),
    CLAIM_ALREADYOWN("%s already own this land."),
    CLAIM_MUSTBE("You must be %s to claim land."),
    CLAIM_MEMBERS("Factions must have at least %s members to claim land."),
    CLAIM_SAFEZONE("You can not claim a Safe Zone."),
    CLAIM_WARZONE("You can not claim a War Zone."),
    CLAIM_POWER("You can't claim more land! You need more power!"),
    CLAIM_LIMIT("Limit reached. You can't claim more land!"),
    CLAIM_ALLY("You can't claim the land of your allies."),
    CLAIM_CONTIGIOUS("You can only claim additional land which is connected to your first claim or controlled by another faction!"),
    CLAIM_FACTIONCONTIGUOUS("You can only claim additional land which is connected to your first claim!"),
    CLAIM_PEACEFUL("%s owns this land. Your faction is peaceful, so you cannot claim land from other factions."),
    CLAIM_PEACEFULTARGET("%s owns this land, and is a peaceful faction. You cannot claim land from them."),
    CLAIM_THISISSPARTA("%s owns this land and is strong enough to keep it."),
    CLAIM_BORDER("You must start claiming land at the border of the territory."),
    CLAIM_TOCLAIM("to claim this land"),
    CLAIM_FORCLAIM("for claiming this land"),
    CLAIM_TOOVERCLAIM("to overclaim this land"),
    CLAIM_FOROVERCLAIM("for over claiming this land"),
    CLAIM_RADIUS_CLAIM("%1$s &eclaimed %2$s chunks &astarting from &e(X: %3$s, Z: %4$s)"),
    CLAIM_CLAIMED("%s claimed land for %s from %s."),
    CLAIM_CLAIMEDLOG("%s claimed land at (%s) for the faction: %s"),
    CLAIM_OVERCLAIM_DISABLED("Over claiming is disabled on this server."),
    CLAIM_TOOCLOSETOOTHERFACTION("Your claim is too close to another Faction. Buffer required is %d"),
    CLAIM_OUTSIDEWORLDBORDER("Your claim is outside the border."),
    CLAIM_OUTSIDEBORDERBUFFER("Your claim is outside the border. %d chunks away world edge required."),
    CLAIM_CLICK_TO_CLAIM("Click to try to claim &2(%1$d, %2$d)"),
    CLAIM_MAP_OUTSIDEBORDER("&cThis claim is outside the worldborder!"),
    CLAIM_YOUAREHERE("You are here"),
    CLAIM_NO_TERRITORY_PERM("You do not have permission from your faction leader to do this!"),


    FACTION_BANNER_CANNOT_DESTROY_1("&c&l[!] &cYou cannot destroy %1$s's banner!"),
    FACTION_BANNER_CANNOT_DESTROY_2("&7It will despawn in: %1$ss!"),
    FACTION_BANNER_CANNOT_PLACE("&c&l[!] &cYou cannot place Faction Banners ion this world!"),
    FACTION_BANNER_MUST_PLACE("&c&l[!] &cYou must place Faction Banners directly beneath you"),
    FACTION_BANNER_ALREADY_PLACED_1("&c&l[!] &cYour faction already has an active /f banner placed!"),
    FACTION_BANNER_ALREADY_PLACED_2("&7You can place a new /f banner in: %1$ss!"),

    /**
     * More generic, or less easily categorisable translations, which may apply to more than one class
     */
    GENERIC_YOU("you"),
    GENERIC_YOURFACTION("your faction"),
    GENERAL_ENABLED("Enabled"),
    GENERAL_DISABLED("Disabled"),

    GENERIC_NOPERMISSION("You don't have permission to %1$s."),
    GENERIC_ACTION_NOPERMISSION("You don't have permission to use %1$s"),
    GENERIC_FPERM_NOPERMISSION("&7The faction leader does not allow you to &c%1$s."),
    GENERIC_DOTHAT("do that"),  //Ugh nuke this from high orbit
    GENERIC_NOPLAYERMATCH("No player match found for \"<plugin>%1$s\"."),
    GENERIC_NOPLAYERFOUND("No player \"<plugin>%1$s\" could not be found."),
    GENERIC_ARGS_TOOFEW("Too few arguments. Use like this:"),
    GENERIC_ARGS_TOOMANY("Strange argument \"<plugin>%1$s\". Use the command like this:"),
    GENERIC_DEFAULTDESCRIPTION("Default faction description :("),
    GENERIC_OWNERS("Owner(s): %1$s"),
    GENERIC_PUBLICLAND("Public faction land."),
    GENERIC_FACTIONLESS("factionless"),
    GENERIC_SERVERADMIN("A server admin"),
    GENERIC_SERVER("Server"),
    GENERIC_DISABLED("&c&l[!] &7The feature &b%1$s &7is currently disabled."),
    GENERIC_ENABLED("enabled"),
    GENERIC_INFINITY("âˆž"),
    GENERIC_NULLPLAYER("null player"),
    GENERIC_CONSOLEONLY("This command cannot be run as a player."),
    GENERIC_PLAYERONLY("This command can only be used by ingame players."),
    GENERIC_ASKYOURLEADER(" Ask your leader to:"),
    GENERIC_YOUSHOULD("You should:"),
    GENERIC_YOUMAYWANT("You may want to: "),
    GENERIC_TRANSLATION_VERSION("Translation: %1$s(%2$s,%3$s) State: %4$s"),
    GENERIC_TRANSLATION_CONTRIBUTORS("Translation contributors: %1$s"),
    GENERIC_TRANSLATION_RESPONSIBLE("Responsible for translation: %1$s"),
    GENERIC_FACTIONTAG_BLACKLIST("&cThat faction tag is blacklisted."),
    GENERIC_FACTIONTAG_TOOSHORT("The faction tag can't be shorter than %1$s chars."),
    GENERIC_FACTIONTAG_TOOLONG("The faction tag can't be longer than %s chars."),
    GENERIC_FACTIONTAG_ALPHANUMERIC("Faction tag must be alphanumeric. \"%s\" is not allowed."),
    GENERIC_PLACEHOLDER("<This is a placeholder for a message you should not see>"),
    GENERIC_NOTENOUGHMONEY("&cYou dont have enough money!"),
    GENERIC_MONEYTAKE("&c{amount} has been taken from your account."),
    GENERIC_FPERM_OWNER_NOPERMISSION("&7This land is ownerclaimed, you need to be an owner to %1$s it."),
    GENERIC_NOFACTION_FOUND("&cCouldn't find a faction with that name!"),
    GENERIC_YOUMUSTBE("&cYour must be atleast %1$s to do this!"),
    GENERIC_MEMBERONLY("&cYou must be in a faction to do this!"),
    GENERIC_WORLDGUARD("&cThis area is worldguard protected."),
    GRACE_DISABLED_PLACEHOLDER("Disabled"),
    MACRO_DETECTED("&c&l[!] &cNo sir!"),

    ROLE_LIST("&eTry using &arecruit, normal, moderator, coleader"),


    COMMAND_ROSTER_DESCRIPTION("manage your roster"),
    COMMAND_ROSTER_GRACE("&c&l[!] &7You may not edit your faction roster whilst grace period is disabled!"),
    COMMAND_ROSTER_ADD_LIMIT("&c&l[!] &7You may not add more people to your roster. &b%1$s&7/&b%2$s &7Roster Players"),

    COMMAND_ROSTERADD_NEED_ROLE("&c[!] &7You need to enter a role"),
    COMMAND_ROSTERADD_COLEADER("&c[!] &7You cannot set them as a leader try coleader"),
    COMMAND_ROSTERADD_DESCRIPTION("add users to your roster"),

    COMMAND_ROSTERMANGE_KICKUSAGE("&cSet a factions roster kicks"),

    COMMAND_ROSTERKICK_NOTMEMBER("&c&l[!] &7This player is not in your factions roster"),
    COMMAND_ROSTERKICK_DESCRIPTION("Kick roster members from your faction"),
    COMMAND_ROSTERKICK_NOTALT("&c&l[!] &7Player is not on your roster"),
    COMMAND_ROSTERREMOVE_NOTENOUGH_KICKS("&cYou have no more roster kicks avaiable"),

    COMMAND_ROSTER_GUI_KICK("&a&l[!] &7You have removed &b%1$s &7from your faction roster."),


    // MISSION_CREATED_COOLDOWN("&c&l[!] &7Due to your immediate faction creation, you may not start missions for &b%1$s minutes&7!"),
    MISSION_MISSION_STARTED("&f%1$s &dstarted the %2$s &fmission"),
    MISSION_ALREAD_COMPLETED("&c&l[!] &7You may not restart a mission you have already completed"),
    MISSION_MISSION_ACTIVE("&c&l[!] &7This mission is currently active!"),
    MISSION_MISSION_MAX_ALLOWED("&c&l[!] &7You may not have more than &b%1$s &7missions active at once."),
    MISSION_MISSION_ALL_COMPLETED("&c&l[!] &7Your faction has completed all available missions."),
    MISSION_MISSION_FINISHED("&c&l[!] &7Your faction has successfully completed %1$s &7mission!"),
    MISSION_MISSION_FAILED("&c&l[!] &7Your faction has failed %1$s &cmission!"),
    COMMAND_MISSION_DESCRIPTION("Opens missions gui"),
    MISSION_MISSION_CANCELLED("&c&l[!] &7You have cancelled your factions current mission!"),
    MISSION_TRIBUTE_ITEM_DENIED_TRANSFER("&c&l[!] &7There are no tribute missions that accept &b%1$s&7."),
    MISSION_RANDOM_MODE_DENIED("&c&l[!] &7Please select the %1$s&7 item to have a random mission assigned."),
    MISSION_CANCEL_POINTS_TAKEN("&cFaction Mission cancelled for &e%1$s points! &cNew Point Balance: &e%2$s"),
    MISSION_CANCEL_NOT_ENOUGH_POINTS("&cYour faction does not have enough points to cancel this mission!"),
    MISSION_TOCANCEL("to cancel this mission."),
    MISSION_FORCANCEL("for cancelling this mission."),


    // F Global \\


    PLAYER_NOT_FOUND("&c&l[!] &b%1$s &7is either not online or not in your faction!"),
    INVALID_PLAYER("&c&l[!] &b{player} &7is not online!"),
    INVALID_WORLD("&c&l[!] &b{world} &7is not a world!"),
    PLACEHOLDER_ROLE_NAME("None"),
    PLACEHOLDER_CUSTOM_FACTION("{faction} "),

    SPAWNER_CHUNK_PLACE_DENIED_WILDERNESS("&c&l[!] &7You may not place spawners in wilderness!"),
    SPAWNER_CHUNK_PLACE_DENIED_NOT_SPAWNERCHUNK("&c&l[!] &7You may not place spawners in this chunk. Only Spawner Chunks!"),


    WARBANNER_NOFACTION("&cYou need a faction to use a warbanner!"),
    WARBANNER_COOLDOWN("&cThe warbanner is on cooldown for your faction!"),
    WARBANNER_INVALIDLOC("&cYou can only use warbanners in enemy land or the warzone"),

    /**
     * ASCII compass (for chat map)
     */
    COMPASS_SHORT_NORTH("N"),
    COMPASS_SHORT_EAST("E"),
    COMPASS_SHORT_SOUTH("S"),
    COMPASS_SHORT_WEST("W"),

    /**
     * Chat modes
     */
    CHAT_MOD("mod chat"),
    CHAT_FACTION("faction chat"),
    CHAT_ALLIANCE("alliance chat"),
    CHAT_TRUCE("truce chat"),
    CHAT_PUBLIC("public chat"),

    /**
     * Economy stuff
     */

    ECON_OFF("no %s"), // no balance, no value, no refund, etc
    ECON_FORMAT("###,###.###"),
    ECON_MONEYTRASFERREDFROM("%1$s was transferred from %2$s to %3$s."),
    ECON_PERSONGAVEMONEYTO("%1$s gave %2$s to %3$s."),
    ECON_PERSONTOOKMONEYFROM("%1$s took %2$s from %3$s."),
    ECON_DISABLED("Factions econ is disabled."),
    ECON_OVER_BAL_CAP("&4The amount &e%s &4is over Essentials' balance cap."),
    ECON_MONEYLOST("&c&l[!] %s &7lost &c%s &7%s."),
    ECON_CANTAFFORD("&c%s &7can't afford &c%s&7 %s."),
    ECON_UNABLETOTRANSFER("&7Unable to transfer &c%s&7 to &c%s&7 from &c%s&7."),
    ECON_PLAYERBALANCE("&c%s&7's balance is &c%s&7."),
    ECON_DEPOSITFAILED("&c%s&7 would have gained &c%s&7 %s, but the deposit failed."),
    ECON_CANTCONTROLMONEY("&c%s&7 lacks permission to control &c%s&7's money."),
    ECON_MONEYTRASFERREDFROMPERSONTOPERSON("%1$s transferred %2$s from %3$s to %4$s."),


    /**
     * Relations
     */
    RELATION_MEMBER_SINGULAR("member"),
    RELATION_MEMBER_PLURAL("members"),
    RELATION_ALLY_SINGULAR("ally"),
    RELATION_ALLY_PLURAL("allies"),
    RELATION_TRUCE_SINGULAR("truce"),
    RELATION_TRUCE_PLURAL("truces"),
    RELATION_NEUTRAL_SINGULAR("neutral"),
    RELATION_NEUTRAL_PLURAL("neutrals"),
    RELATION_ENEMY_SINGULAR("enemy"),
    RELATION_ENEMY_PLURAL("enemies"),

    /**
     * Roles
     */
    ROLE_LEADER("leader"),
    ROLE_COLEADER("coleader"),
    ROLE_MODERATOR("moderator"),
    ROLE_NORMAL("normal member"),
    ROLE_RECRUIT("recruit"),

    /**
     * Region types.
     */
    REGION_SAFEZONE("safezone"),
    REGION_WARZONE("warzone"),
    REGION_WILDERNESS("wilderness"),

    REGION_PEACEFUL("peaceful territory"),
    /**
     * In the player and entity listeners
     */
    PLAYER_CANTHURT("You may not harm other players in %s"),
    PLAYER_SAFEAUTO("This land is now a safe zone."),
    PLAYER_WARAUTO("This land is now a war zone."),
    PLAYER_OUCH("Ouch, that is starting to hurt. You should give it a rest."),
    PLAYER_USE_WILDERNESS("You can't use %s in the wilderness."),
    PLAYER_USE_SAFEZONE("You can't use %s in a safe zone."),
    PLAYER_USE_WARZONE("You can't use %s in a war zone."),
    PLAYER_USE_TERRITORY("You can't %s in the territory of %s."),
    PLAYER_USE_OWNED("You can't use %s in this territory, it is owned by: %s."),
    PLAYER_COMMAND_WARZONE("You can't use the command '%s' in warzone."),
    PLAYER_COMMAND_NEUTRAL("You can't use the command '%s' in neutral territory."),
    PLAYER_COMMAND_ENEMY("You can't use the command '%s' in enemy territory."),
    PLAYER_COMMAND_PERMANENT("You can't use the command '%s' because you are in a permanent faction."),
    PLAYER_COMMAND_ALLY("You can't use the command '%s' in ally territory."),
    PLAYER_COMMAND_WILDERNESS("You can't use the command '%s' in the wilderness."),

    PLAYER_POWER_NOLOSS_PEACEFUL("You didn't lose any power since you are in a peaceful faction."),
    PLAYER_POWER_NOLOSS_WORLD("You didn't lose any power due to the world you died in."),
    PLAYER_POWER_NOLOSS_WILDERNESS("You didn't lose any power since you were in the wilderness."),
    PLAYER_POWER_NOLOSS_WARZONE("You didn't lose any power since you were in a war zone."),
    PLAYER_POWER_LOSS_WARZONE("The world you are in has power loss normally disabled, but you still lost power since you were in a war zone.\nYour power is now %d / %d"),
    PLAYER_POWER_NOW("Your power is now %d / %d"),

    PLAYER_PVP_LOGIN("You can't hurt other players for %d seconds after logging in."),
    PLAYER_PVP_REQUIREFACTION("You can't hurt other players until you join a faction."),
    PLAYER_PVP_FACTIONLESS("You can't hurt players who are not currently in a faction."),
    PLAYER_PVP_PEACEFUL("Peaceful players cannot participate in combat."),
    PLAYER_PVP_NEUTRAL("You can't hurt neutral factions. Declare them as an enemy."),
    PLAYER_PVP_CANTHURT("You can't hurt %s."),

    PLAYER_PVP_NEUTRALFAIL("You can't hurt %s in their own territory unless you declare them as an enemy."),
    PLAYER_PVP_TRIED("%s tried to hurt you."),

    SHIELD_CURRENTLY_ENABLE("&a&lCurrently Protected"),
    SHIELD_NOT_SET("&c&lNot Set"),
    SHIELD_CURRENTLY_NOT_ENABLED("&c&lCurrently Unprotected"),

    /**
     * Strings lying around in other bits of the plugins
     */
    NOPAGES("Sorry. No Pages available."),
    INVALIDPAGE("Invalid page. Must be between 1 and %1$d"),

    /**
     * The ones here before I started messing around with this
     */
    TITLE("title", "&bFactions &0|&r"),
    WILDERNESS("wilderness", "&2Wilderness"),
    WILDERNESS_DESCRIPTION("wilderness-description", ""),
    WARZONE("warzone", "&4Warzone"),
    WARZONE_DESCRIPTION("warzone-description", "Not the safest place to be."),
    SAFEZONE("safezone", "&6Safezone"),
    SAFEZONE_DESCRIPTION("safezone-description", "Free from pvp and monsters."),
    TOGGLE_SB("toggle-sb", "You now have scoreboards set to {value}"),
    FACTION_LEAVE("faction-leave", "Leaving %1$s&r, Entering %2$s&r"),
    FACTIONS_ANNOUNCEMENT_TOP("faction-announcement-top", "&d--Unread Faction Announcements--"),
    FACTIONS_ANNOUNCEMENT_BOTTOM("faction-announcement-bottom", "&d--Unread Faction Announcements--"),
    DEFAULT_PREFIX("default-prefix", "{relationcolor}[{faction}]"),
    FACTION_LOGIN("faction-login", "&e%1$s &9logged in."),
    FACTION_LOGOUT("faction-logout", "&e%1$s &9logged out.."),
    NOFACTION_PREFIX("nofactions-prefix", "&6[&ano-faction&6]&r"),
    DATE_FORMAT("date-format", "MM/d/yy h:ma"), // 3/31/15 07:49AM

    /**
     * Raidable is used in multiple places. Allow more than just true/false.
     */
    RAIDABLE_TRUE("raidable-true", "true"),
    RAIDABLE_FALSE("raidable-false", "false"),
    /**
     * Warmups
     */
    WARMUPS_NOTIFY_FLIGHT("&eFlight will enable in &d%2$d &eseconds."),
    WARMUPS_NOTIFY_TELEPORT("&eYou will teleport to &d%1$s &ein &d%2$d &eseconds."),
    WARMUPS_ALREADY("&cYou are already warming up."),
    WARMUPS_CANCELLED("&cYou have cancelled your warmup."),

    PLACEHOLDERAPI_NULL("");

    public static SimpleDateFormat sdf;
    private static YamlConfiguration LANG;
    private String path;
    private String def;

    public static final TL[] VALUES = values();

    /**
     * Lang enum constructor.
     *
     * @param path  The string path.
     * @param start The default string.
     */
    TL(String path, String start) {
        this.path = path;
        this.def = start;
    }

    /**
     * Lang enum constructor. Use this when your desired path simply exchanges '_' for '.'
     *
     * @param start The default string.
     */
    TL(String start) {
        this.path = this.name().replace('_', '.');
        if (this.path.startsWith(".")) {
            path = "root" + path;
        }
        this.def = start;
    }

    /**
     * Set the {@code YamlConfiguration} to use.
     *
     * @param config The config to set.
     */
    public static void setFile(YamlConfiguration config) {
        LANG = config;
        sdf = new SimpleDateFormat(DATE_FORMAT.toString());
    }

    public Component toComponent() {
        return TextUtil.parseFancy(toString()).build();
    }

    public TextComponent toFormattedComponent(Object... args) {
        return TextUtil.parseFancy(format(args)).build();
    }

    @Override
    public String toString() {
        return CC.translate(LANG.getString(this.path, def)) + (this == TITLE ? " " : "");
    }

    public String format(Object... args) {
        return String.format(toString(), args);
    }

    /**
     * Get the default value of the path.
     *
     * @return The default value of the path.
     */
    public String getDefault() {
        return this.def;
    }

    /**
     * Get the path to the string.
     *
     * @return The path to the string.
     */
    public String getPath() {
        return this.path;
    }
}
