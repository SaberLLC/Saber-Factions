/*
 * Copyright (C) 2018 ProSavage
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

import org.bukkit.ChatColor;
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
     * Command translations
     */

    /**
     * Messsges for /f help
     */
    COMMAND_HELP_NEXTCREATE("<i>Learn how to create a faction on the next page."),
    COMMAND_HELP_INVITATIONS("command.help.invitations", "<i>You might want to close it and use invitations:"),
    COMMAND_HELP_HOME("<i>And don't forget to set your home:"),
    COMMAND_HELP_404("&c&l» &7This page does &cnot &7exist"),
    COMMAND_HELP_BANK_1("<i>Your faction has a bank which is used to pay for certain"), //Move to last /f help page
    COMMAND_HELP_BANK_2("<i>things, so it will need to have money deposited into it."), //Move to last /f help page
    COMMAND_HELP_BANK_3("<i>To learn more, use the money command."), //Move to last /f help page
    COMMAND_HELP_PLAYERTITLES("<i>Player titles are just for fun. No rules connected to them."), //Move to last /f help page
    COMMAND_HELP_OWNERSHIP_1("<i>Claimed land with ownership set is further protected so"), //Move to last /f help page
    COMMAND_HELP_OWNERSHIP_2("<i>that only the owner(s), faction admin, and possibly the"), //Move to last /f help page
    COMMAND_HELP_OWNERSHIP_3("<i>faction moderators have full access."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_1("<i>Set the relation you WISH to have with another faction."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_2("<i>Your default relation with other factions will be neutral."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_3("<i>If BOTH factions choose \"ally\" you will be allies."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_4("<i>If ONE faction chooses \"enemy\" you will be enemies."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_5("<i>You can never hurt members or allies."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_6("<i>You can not hurt neutrals in their own territory."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_7("<i>You can always hurt enemies and players without faction."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_8(""),
    COMMAND_HELP_RELATIONS_9("<i>Damage from enemies is reduced in your own territory."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_10("<i>When you die you lose power. It is restored over time."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_11("<i>The power of a faction is the sum of all member power."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_12("<i>The power of a faction determines how much land it can hold."), //Move to last /f help page
    COMMAND_HELP_RELATIONS_13("<i>You can claim land from factions with too little power."), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_1("<i>Only faction members can build and destroy in their own"), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_2("<i>territory. Usage of the following items is also restricted:"), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_3("<i>Door, Chest, Furnace, Dispenser, Diode."), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_4(""),
    COMMAND_HELP_PERMISSIONS_5("<i>Make sure to put pressure plates in front of doors for your"), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_6("<i>guest visitors. Otherwise they can't get through. You can"), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_7("<i>also use this to create member only areas."), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_8("<i>As dispensers are protected, you can create traps without"), //Move to last /f help page
    COMMAND_HELP_PERMISSIONS_9("<i>worrying about those arrows getting stolen."), //Move to last /f help page
    COMMAND_HELP_ADMIN_1("&a&l» &a/f claim safezone \n   &7claim land for the Safe Zone"),
    COMMAND_HELP_ADMIN_2("&a&l» &a/f claim warzone \n   &7claim land for the War Zone"),
    COMMAND_HELP_ADMIN_3("&a&l» &a/f autoclaim [safezone|warzone] \n   &7take a guess"),
    COMMAND_HELP_MOAR_1("Finally some commands for the server admins:"),
    COMMAND_HELP_MOAR_2("<i>More commands for server admins:"),
    COMMAND_HELP_MOAR_3("<i>Even more commands for server admins:"),
    COMMAND_HELP_DESCRIPTION("\n  &a&l» &7Display a &ahelp &7page"),

    COMMAND_NEAR_DESCRIPTION("Get nearby faction players in a radius."),
    COMMAND_NEAR_DISABLED_MSG("&cThis command is disabled!"),
    COMMAND_NEAR_FORMAT("{playername} &c({distance}m)"),
    COMMAND_NEAR_USE_MSG("&cFaction members nearby"),

    /**
     * Messsges for Faction Admins/Mods
     */

    COMMAND_UPGRADES_DESCRIPTION("&cOpen the Upgrades Menu"),
    COMMAND_UPGRADES_MONEYTAKE("&c{amount} has been taken from your account."),
    COMMAND_UPGRADES_NOTENOUGHMONEY("&cYou dont have enough money!"),


    COMMAND_ADMIN_NOTMEMBER("&c&l[!] &7%1$s &cis not a member in your faction."),
    COMMAND_ADMIN_NOTADMIN("&c&l[!] &cYou are not the faction admin."),
    COMMAND_ADMIN_TARGETSELF("'&c&l[!] &cThe target player musn''t be yourself."),
    COMMAND_ADMIN_DEMOTES("&c&l[!] &cYou have demoted &7%1$s &cfrom the position of faction admin."),
    COMMAND_ADMIN_DEMOTED("&c&l[!] &cYou have been demoted from the position of faction admin by &7%1$s&c"),
    COMMAND_ADMIN_PROMOTES("&e&l[!] &eYou have promoted &6%1$s &eto the position of faction admin."),
    COMMAND_ADMIN_PROMOTED("&e&l[!] &6%1$s &egave &6%2$s &ethe leadership of &6%3$s&e."),
    COMMAND_ADMIN_DESCRIPTION("Hand over your admin rights"),
    COMMAND_ADMIN_NOMEMBERS("&e&l[!] &cNo one else to promote, please disband faction."),

    COMMAND_AHOME_DESCRIPTION("Send a player to their f home no matter what."),
    COMMAND_AHOME_NOHOME("%1$s doesn't have an f home."),
    COMMAND_AHOME_SUCCESS("$1%s was sent to their f home."),
    COMMAND_AHOME_OFFLINE("%1$s is offline."),
    COMMAND_AHOME_TARGET("You were sent to your f home."),

    COMMAND_ANNOUNCE_DESCRIPTION("Announce a message to players in faction."),

    COMMAND_FREECAM_ENEMYINRADIUS("Freecam disabled, An enemy is closeby!"),
    COMMAND_FREECAM_OUTSIDEFLIGHT("Please dont leave the flight radius!"),
    COMMAND_FREECAM_ENABLED("Freecam is now enabled!"),
    COMMAND_FREECAM_DISABLED("Freecam is now disabled"),
    COMMAND_FREECAM_DESCRIPTION("Go into spectator mode"),


    COMMAND_AUTOCLAIM_ENABLED("&c&l[!] &7Now &cauto-claiming&7 land for <h>%1$s<i>."),
    COMMAND_AUTOCLAIM_DISABLED("&c&l[!] Auto-claiming&7 of land is now &cdisabled."),
    COMMAND_AUTOCLAIM_REQUIREDRANK("&c&l[!] &7You must be &c%1$s&7 to claim land."),
    COMMAND_AUTOCLAIM_OTHERFACTION("&c&l[!]&7 You &ccan't &7claim land for &c%1$s&7."),
    COMMAND_AUTOCLAIM_DESCRIPTION("Auto-claim land as you walk around"),

    COMMAND_AUTOHELP_HELPFOR("Help for command \""),

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


    COMMAND_BYPASS_ENABLE("&e&l[!] &eYou have enabled admin bypass mode. You will be able to build or destroy anywhere."),
    COMMAND_BYPASS_ENABLELOG(" has ENABLED admin bypass mode."),
    COMMAND_BYPASS_DISABLE("&c&l[!] &cYou have disabled admin bypass mode."),
    COMMAND_BYPASS_DISABLELOG(" has DISABLED admin bypass mode."),
    COMMAND_BYPASS_DESCRIPTION("Enable admin bypass mode"),

    COMMAND_BANNER_DESCRIPTION("Turn a held banner into a war banner"),
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

    COMMAND_CHATSPY_ENABLE("&c&l[!] &7You have &cenabled &7chat spying mode."),
    COMMAND_CHATSPY_ENABLELOG(" has ENABLED chat spying mode."),
    COMMAND_CHATSPY_DISABLE("&c&l[!] &7You have &cdisabled &7chat spying mode."),
    COMMAND_CHATSPY_DISABLELOG(" has DISABLED chat spying mode."),
    COMMAND_CHATSPY_DESCRIPTION("Enable admin chat spy mode"),

    COMMAND_CLAIM_INVALIDRADIUS("&c&l[!]&7 If you specify a &cradius&7, it must be at least &c1&7."),
    COMMAND_CLAIM_DENIED("&c&l[!]&7 You &cdo not &7have &cpermission&7 to &cclaim&7 in a radius."),
    COMMAND_CLAIM_DESCRIPTION("Claim land from where you are standing"),

    COMMAND_CLAIMLINE_INVALIDRADIUS("&c&l[!]&7 If you &cspecify&7 a distance, it must be at least &c1&7."),
    COMMAND_CLAIMLINE_DENIED("&c&l[!]&7 You &cdo not &7have&c permission&7 to claim in a line."),
    COMMAND_CLAIMLINE_DESCRIPTION("Claim land in a straight line."),
    COMMAND_CLAIMLINE_ABOVEMAX("&c&l[!]&7 The &cmaximum&7 limit for claim line is &c%s&7."),
    COMMAND_CLAIMLINE_NOTVALID("&c&l[!]&7 &c%s&7 is not a &ccardinal &7direction. You may use &cnorth&7, &ceast&7, &csouth &7or &cwest&7."),

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

    COMMAND_CREATE_MUSTLEAVE("&c&l[!]&7 You must &cleave &7your &ccurrent faction &7first."),
    COMMAND_CREATE_INUSE("&c&l[!]&7 That tag is &calready &7in use."),
    COMMAND_CREATE_TOCREATE("to create a new faction"),
    COMMAND_CREATE_FORCREATE("for creating a new faction"),
    COMMAND_CREATE_ERROR("&c&l[!]&7 There was an &cinternal error&7 while trying to create your faction. &cPlease try again&7."),
    COMMAND_CREATE_CREATED("&c&l[!]&7 &c%1$s<i> &7created a new faction &c&l%2$s"),
    COMMAND_CREATE_YOUSHOULD("&c&l[!]&7 You should now: &c%1$s"),
    COMMAND_CREATE_CREATEDLOG(" created a new faction: "),
    COMMAND_CREATE_DESCRIPTION("Create a new faction"),

    COMMAND_DEINVITE_CANDEINVITE("&c&l[!]&7 Players you can &cdeinvite: "),
    COMMAND_DEINVITE_CLICKTODEINVITE("&c&l[!]&7 Click to &crevoke&7 invite for &c%1$s"),
    COMMAND_DEINVITE_ALREADYMEMBER("&c&l[!]&7 &c%1$s<i>&7 is already a member of &c%2$s"),
    COMMAND_DEINVITE_MIGHTWANT("&c&l[!]&7 You might want to: &c%1$s"),
    COMMAND_DEINVITE_REVOKED("&c&l[!]&7 &7%1$s<i> &crevoked&7 your invitation to &c%2$s&7."),
    COMMAND_DEINVITE_REVOKES("&c&l[!]&7 %1$s&c revoked &7%2$s's&c invitation."),
    COMMAND_DEINVITE_DESCRIPTION("Remove a pending invitation"),

    COMMAND_DELFWARP_DELETED("&c&l[!]&7 Deleted warp &c%1$s"),
    COMMAND_DELFWARP_INVALID("&c&l[!]&7 Couldn't &cfind&7 warp &c%1$s"),
    COMMAND_DELFWARP_TODELETE("to delete warp"),
    COMMAND_DELFWARP_FORDELETE("for deleting warp"),
    COMMAND_DELFWARP_DESCRIPTION("Delete a faction warp"),

    COMMAND_DESCRIPTION_CHANGES("&c&l[!]&7 You have &cchanged&7 the &cdescription&7 for &c%1$s&7 to:"),
    COMMAND_DESCRIPTION_CHANGED("&c&l[!]&7 The faction&c %1$s<i>&7 changed their &cdescription &7to:"),
    COMMAND_DESCRIPTION_TOCHANGE("to change faction description"),
    COMMAND_DESCRIPTION_FORCHANGE("for changing faction description"),
    COMMAND_DESCRIPTION_DESCRIPTION("Change the faction description"),

    COMMAND_DISBAND_IMMUTABLE("&c&l[!]&7 &7You &ccannot&7 disband &2Wilderness&7,&e SafeZone&7, or &4WarZone."),
    COMMAND_DISBAND_MARKEDPERMANENT("&c&l[!]&7 This faction is designated as&c permanent&7, so you cannot disband it."),
    COMMAND_DISBAND_BROADCAST_YOURS("&c&l[!]&7 &c%1$s&7 disbanded your &cfaction."),
    COMMAND_DISBAND_BROADCAST_NOTYOURS("&c&l[!]&7 &c%1$s<i> &7disbanded the faction &c%2$s."),
    COMMAND_DISBAND_HOLDINGS("&c&l[!]&7 &7You have been given the disbanded &cfaction's bank&7, totaling &c%1$s."),
    COMMAND_DISBAND_DESCRIPTION("Disband a faction"),

    COMMAND_FLY_DISABLED("&c&l[!]&7 Sorry, Faction flight is &cdisabled &7on this server"),
    COMMAND_FLY_DESCRIPTION("Enter or leave Faction flight mode"),
    COMMAND_FLY_CHANGE("&c&l[!]&7 Faction flight &c%1$s"),
    COMMAND_FLY_COOLDOWN("&c&l[!]&7 You will &cnot&7 take fall damage for &c{amount}&7 seconds"),
    COMMAND_FLY_DAMAGE("&c&l[!]&7 Faction flight &cdisabled&7 due to entering combat"),
    COMMAND_FLY_NO_ACCESS("&c&l[!]&7 &cCannot fly &7in territory of %1$s"),
    COMMAND_FLY_ENEMY_NEAR("&c&l[!]&7 Flight has been&c disabled&7 an enemy is nearby"),
    COMMAND_FLY_CHECK_ENEMY("&c&l[!]&7 Cannot fly here, an enemy is &cnearby"),
    COMMAND_FLY_NO_EPEARL("&c&l[!] &7You &ccannot&7 throw enderpearls while flying!"),

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

    COMMAND_HOME_DISABLED("&c&l[!]&7 Sorry, Faction homes are &cdisabled on this server."),
    COMMAND_HOME_TELEPORTDISABLED("&c&l[!]&7 Sorry, the ability to &cteleport &7to Faction homes is &cdisabled &7on this server."),
    COMMAND_HOME_NOHOME("&c&l[!]&7 Your faction does &cnot &7have a home. "),
    COMMAND_HOME_INENEMY("&c&l[!]&7 You &ccannot teleport &7to your &cfaction home&7 while in the territory of an &cenemy faction&7."),
    COMMAND_HOME_WRONGWORLD("&c&l[!]&7 You &ccannot &7teleport to your &cfaction home&7 while in a different world."),
    COMMAND_HOME_ENEMYNEAR("&c&l[!]&7 You &ccannot teleport&7 to your faction home while an enemy is within &c%s&7 blocks of you."),
    COMMAND_HOME_TOTELEPORT("to teleport to your faction home"),
    COMMAND_HOME_FORTELEPORT("for teleporting to your faction home"),
    COMMAND_HOME_DESCRIPTION("Teleport to the faction home"),

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
    COMMAND_INVITE_INVITEDYOU("&chas invited you to join "),
    COMMAND_INVITE_INVITED("&c&l[!]&7 &c%1$s&7 invited &c%2$s&7 to your faction."),
    COMMAND_INVITE_ALREADYMEMBER("&c&l[!]&7 &c%1$s&7 is already a member of&c %2$s"),
    COMMAND_INVITE_DESCRIPTION("Invite a player to your faction"),
    COMMAND_INVITE_BANNED("&c&l[!]&7 &7%1$s &cis banned &7from your Faction. &cNot &7sending an invite."),

    COMMAND_JOIN_CANNOTFORCE("&c&l[!]&7 You&c do not&7 have permission to &cmove other players&7 into a faction."),
    COMMAND_JOIN_SYSTEMFACTION("&c&l[!]&7 Players may nly join &cnormal factions&7. This is a &c&lsystem faction&7."),
    COMMAND_JOIN_ALREADYMEMBER("&c&l[!]&7 &c%1$s %2$s already a member of&c %3$s"),
    COMMAND_JOIN_ATLIMIT(" &c&l[!]&7 The faction &c%1$s &7is at the limit of&c %2$d&7 members, so&c %3$s&7 cannot currently join."),
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

    COMMAND_KICK_CANDIDATES("&c&l[!]&7 Players you can kick: "),
    COMMAND_KICK_CLICKTOKICK("Click to kick "),
    COMMAND_KICK_SELF("&c&l[!]&7 You &ccannot &7kick&c yourself&7."),
    COMMAND_KICK_NONE("&c&l[!]&7 That player&c is not&7 in a faction."),
    COMMAND_KICK_NOTMEMBER("&c&l[!]&7 &c%1$s<b> is not a member of %2$s"),
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

    COMMAND_LOCK_LOCKED("&c&l[!]&7 Factions is now&c locked"),
    COMMAND_LOCK_UNLOCKED("&c&l[!]&7 Factions in now&a unlocked"),
    COMMAND_LOCK_DESCRIPTION("Lock all write stuff. Apparently."),

    COMMAND_LOGINS_TOGGLE("&c&l[!]&7 Set login / logout notifications for Faction members to: &c%s"),
    COMMAND_LOGINS_DESCRIPTION("Toggle(?) login / logout notifications for Faction members"),

    COMMAND_LOWPOWER_HEADER("&8&m--------&8<Players with power under {maxpower}&8>&8&m---------"),
    COMMAND_LOWPOWER_FORMAT("&c{player} &8(&c{player_power}&8/&c{maxpower}&8)"),
    COMMAND_LOWPOWER_DESCRIPTION("Shows a list of players in your faction with lower power levels"),

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

    COMMAND_MODIFYPOWER_ADDED("&c&l[!]&7 Added &c%1$f &7power to &c%2$s. &7New total rounded power: &c%3$d"),
    COMMAND_MODIFYPOWER_DESCRIPTION("Modify the power of a faction/player"),

    COMMAND_MONEY_LONG("&c&l[!]&7 The faction money commands."),
    COMMAND_MONEY_DESCRIPTION("Faction money commands"),

    COMMAND_MONEYBALANCE_SHORT("show faction balance"),
    COMMAND_MONEYBALANCE_DESCRIPTION("Show your factions current money balance"),

    COMMAND_MONEYDEPOSIT_DESCRIPTION("Deposit money"),
    COMMAND_MONEYDEPOSIT_DEPOSITED("&c&l[!]&7 &c%1$s &7deposited&c %2$s&7 in the faction bank:&c %3$s"),

    COMMAND_MONEYTRANSFERFF_DESCRIPTION("Transfer f -> f"),
    COMMAND_MONEYTRANSFERFF_TRANSFER("&c&l[!]&7 &c%1$s&7 transferred&c %2$s &7from the faction &c\"%3$s\"&7 to the faction&c \"%4$s\"&7"),

    COMMAND_MONEYTRANSFERFP_DESCRIPTION("Transfer f -> p"),
    COMMAND_MONEYTRANSFERFP_TRANSFER("&c&l[!]&7 &c%1$s &7transferred&c %2$s &7from the faction&c \"%3$s\" &7to the player &c\"%4$s\""),

    COMMAND_MONEYTRANSFERPF_DESCRIPTION("Transfer p -> f"),
    COMMAND_MONEYTRANSFERPF_TRANSFER("&c&l[!]&7 &c%1$s&7 transferred &c%2$s&7 from the player &c\"%3$s\" &7to the faction&c \"%4$s\""),

    COMMAND_MONEYWITHDRAW_DESCRIPTION("Withdraw money"),
    COMMAND_MONEYWITHDRAW_WITHDRAW("&c&l[!]&7 &c%1$s&7 withdrew&c %2$s &7from the faction bank:&c %3$s"),

    COMMAND_OPEN_TOOPEN("to open or close the faction"),
    COMMAND_OPEN_FOROPEN("for opening or closing the faction"),
    COMMAND_OPEN_OPEN("open"),
    COMMAND_OPEN_CLOSED("closed"),
    COMMAND_OPEN_CHANGES("&c&l[!]&7 &c%1$s&7 changed the faction to &c%2$s&7."),
    COMMAND_OPEN_CHANGED("&c&l[!]&7 The faction &c%1$s&7 is now &c%2$s"),
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

    COMMAND_PEACEFUL_DESCRIPTION("&c&l[!]&7Set a faction to peaceful"),
    COMMAND_PEACEFUL_YOURS("&c&l[!]&7%1$s has %2$s your faction"),
    COMMAND_PEACEFUL_OTHER("&c&l[!]&7%s has %s the faction '%s<i>'."),
    COMMAND_PEACEFUL_GRANT("&c&l[!]&7 granted peaceful status to"),
    COMMAND_PEACEFUL_REVOKE("removed peaceful status from"),

    COMMAND_PERM_DESCRIPTION("&c&l[!]&7&6Edit or list your Faction's permissions."),
    COMMAND_PERM_INVALID_RELATION("&c&l[!]&7Invalid relation defined. Try something like&c 'ally'"),
    COMMAND_PERM_INVALID_ACCESS("&c&l[!]&7 Invalid access defined. Try something like &c'allow'"),
    COMMAND_PERM_INVALID_ACTION("&c&l[!]&7 Invalid action defined. Try something like &c'build'"),
    COMMAND_PERM_SET("&c&l[!]&7 Set permission&c %1$s &7to &c%2$s &7for relation&c %3$s"),
    COMMAND_PERM_TOP("RCT MEM OFF ALLY TRUCE NEUT ENEMY"),

    COMMAND_PERMANENT_DESCRIPTION("Toggles a faction's permanence"), //TODO: Real word?
    COMMAND_PERMANENT_GRANT("&c&l[!]&7 added permanent status to"),
    COMMAND_PERMANENT_REVOKE("&c&l[!]&7 removed permanent status from"),
    COMMAND_PERMANENT_YOURS("&c&l[!]&7 &c%1$s&7 has &c%2$s&7 your faction"),
    COMMAND_PERMANENT_OTHER("&c&l[!]&7 &c%s &7has &c%s &7the faction &c'%s'."),
    COMMAND_PROMOTE_TARGET("&c&l[!]&7 You've been &c%1$s&7 to &c%2$s"),
    COMMAND_PROMOTE_SUCCESS("&c&l[!]&7 You successfully&c %1$s %2$s &cto&7 %3$s"),
    COMMAND_PROMOTE_PROMOTED("promoted"),
    COMMAND_PROMOTE_DEMOTED("demoted"),
    COMMAND_PROMOTE_COLEADER_ADMIN("&c&l[!]&7 &cColeaders cant promote players to Admin!"),

    COMMAND_PERMANENTPOWER_DESCRIPTION("Toggle faction power permanence"), //TODO: This a real word?
    COMMAND_PERMANENTPOWER_GRANT("added permanentpower status to"),
    COMMAND_PERMANENTPOWER_REVOKE("removed permanentpower status from"),
    COMMAND_PERMANENTPOWER_SUCCESS("&c&l[!]&7 You&c %s &7%s."),
    COMMAND_PERMANENTPOWER_FACTION("&c&l[!]&7 &c%s %s &7your faction"),

    COMMAND_PROMOTE_DESCRIPTION("/f promote <name>"),
    COMMAND_PROMOTE_WRONGFACTION("&c&l[!]&7 &c%1$s&7 is &cnot&7 part of your faction."),
    COMMAND_NOACCESS("&c&l[!]&7 You don't have access to that."),
    COMMAND_PROMOTE_NOTTHATPLAYER("&c&l[!]&7 That player &ccannot&7 be promoted."),
    COMMAND_PROMOTE_NOT_ALLOWED("&c&l[!]&7 You cannot promote to the same rank as yourself!"),


    COMMAND_POWER_TOSHOW("to show player power info"),
    COMMAND_POWER_FORSHOW("for showing player power info"),
    COMMAND_POWER_POWER("&c&l[!]&7 &c%1$s » &cPower &7/ &cMaxpower&a » &c%2$d &7/&c%3$d %4$s"),
    COMMAND_POWER_BONUS(" (bonus: "),
    COMMAND_POWER_PENALTY(" (penalty: "),
    COMMAND_POWER_DESCRIPTION("&a&l» &7Show player &apower &7info"),

    COMMAND_POWERBOOST_HELP_1("&c&l[!]&7 You must specify \"p\" or \"player\" to target a player or \"f\" or \"faction\" to target a faction."),
    COMMAND_POWERBOOST_HELP_2("&c&l[!]&7 ex. /f powerboost p SomePlayer 0.5  -or-  /f powerboost f SomeFaction -5"),
    COMMAND_POWERBOOST_INVALIDNUM("<b>You must specify a valid numeric value for the power bonus/penalty amount."),
    COMMAND_POWERBOOST_PLAYER("Player \"%1$s\""),
    COMMAND_POWERBOOST_FACTION("Faction \"%1$s\""),
    COMMAND_POWERBOOST_BOOST("<i>%1$s now has a power bonus/penalty of %2$d to min and max power levels."),
    COMMAND_POWERBOOST_BOOSTLOG("%1$s has set the power bonus/penalty for %2$s to %3$d."),
    COMMAND_POWERBOOST_DESCRIPTION("Apply permanent power bonus/penalty to specified player or faction"),

    COMMAND_RELATIONS_ALLTHENOPE("&c&l[!]&7 &cNope! You can't."),
    COMMAND_RELATIONS_MORENOPE("&c&l[!]&7 &cNope! &7You can't declare a relation to &cyourself"),
    COMMAND_RELATIONS_ALREADYINRELATIONSHIP("&c&l[!]&7 You &calready&7 have that relation wish set with&c %1$s."),
    COMMAND_RELATIONS_TOMARRY("to change a relation wish"),
    COMMAND_RELATIONS_FORMARRY("for changing a relation wish"),
    COMMAND_RELATIONS_MUTUAL("&c&l[!]&7 Your faction is now %1$s<i> to %2$s"),
    COMMAND_RELATIONS_PEACEFUL("&c&l[!]&7 This will have no effect while your faction is peaceful."),
    COMMAND_RELATIONS_PEACEFULOTHER("&c&l[!]&7 This will have &cno effect&7 while their faction is peaceful."),
    COMMAND_RELATIONS_DESCRIPTION("Set relation wish to another faction"),
    COMMAND_RELATIONS_EXCEEDS_ME("&c&l[!]&7 Failed to set relation wish. You can only have %1$s %2$s."),
    COMMAND_RELATIONS_EXCEEDS_THEY("&c&l[!]&7 Failed to set relation wish. They can only have %1$s %2$s."),

    COMMAND_RELATIONS_PROPOSAL_1("&c&l[!]&7&c %1$s &7wishes to be your&c %2$s"),
    COMMAND_RELATIONS_PROPOSAL_2("&c&l[!]&7 Type &c/%1$s %2$s %3$s&7 to accept."),
    COMMAND_RELATIONS_PROPOSAL_SENT("&c&l[!]&7 &c%1$s&7 were informed that you wish to be &c%2$s"),

    COMMAND_RELOAD_TIME("&c&l[!]&7 Reloaded &call &7configuration files <i>from disk, took &c%1$d ms<i>."),
    COMMAND_RELOAD_DESCRIPTION("Reload data file(s) from disk"),

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

    COMMAND_SETMAXVAULTS_DESCRIPTION("Set max vaults for a Faction."),
    COMMAND_SETMAXVAULTS_SUCCESS("&aSet max vaults for &e%s &ato &b%d"),

    COMMAND_VAULT_DESCRIPTION("Open your placed faction vault!"),
    COMMAND_VAULT_INVALID("&c&l[!]&7 Your vault was either&c claimed&7, &cbroken&7, or has&c not been&7 placed yet."),
    COMMAND_VAULT_OPENING("&c&l[!]&7 Opening faction vault."),

    COMMAND_GETVAULT_ALREADYSET("&c&l[!]&7 Vault has already been set!"),
    COMMAND_GETVAULT_ALREADYHAVE("&c&l[!]&7 You already have a vault in your inventory!"),
    COMMAND_GETVAULT_CHESTNEAR("&c&l[!]&7 &7There is a chest &cnearby"),
    COMMAND_GETVAULT_SUCCESS("&cSucessfully set vault."),
    COMMAND_GETVAULT_INVALIDLOCATION("&cVault can only be placed in faction land!"),
    COMMAND_GETVAULT_DESCRIPTION("Get the faction vault item!"),
    COMMAND_GETVAULT_RECEIVE("&cYou have recieved a faction vault!"),
    COMMAND_GETVAULT_NOMONEY("&cYou do not have enough money"),
    COMMAND_GETVAULT_MONEYTAKE("&c{amount} has been taken from your account"),

    COMMAND_SHOW_NOFACTION_SELF("You are not in a faction"),
    COMMAND_SHOW_NOFACTION_OTHER("That's not a faction"),
    COMMAND_SHOW_TOSHOW("to show faction information"),
    COMMAND_SHOW_FORSHOW("for showing faction information"),
    COMMAND_SHOW_DESCRIPTION("<a>Description: <i>%1$s"),
    COMMAND_SHOW_PEACEFUL("This faction is Peaceful"),
    COMMAND_SHOW_PERMANENT("<a>This faction is permanent, remaining even with no members."),
    COMMAND_SHOW_JOINING("<a>Joining: <i>%1$s "),
    COMMAND_SHOW_INVITATION("invitation is required"),
    COMMAND_SHOW_UNINVITED("no invitation is needed"),
    COMMAND_SHOW_NOHOME("n/a"),
    COMMAND_SHOW_POWER("<a>Land / Power / Maxpower: <i> %1$d/%2$d/%3$d %4$s."),
    COMMAND_SHOW_BONUS(" (bonus: "),
    COMMAND_SHOW_PENALTY(" (penalty: "),
    COMMAND_SHOW_DEPRECIATED("(%1$s depreciated)"), //This is spelled correctly.
    COMMAND_SHOW_LANDVALUE("<a>Total land value: <i>%1$s %2$s"),
    COMMAND_SHOW_BANKCONTAINS("<a>Bank contains: <i>%1$s"),
    COMMAND_SHOW_ALLIES("Allies: "),
    COMMAND_SHOW_ENEMIES("Enemies: "),
    COMMAND_SHOW_MEMBERSONLINE("Members online: "),
    COMMAND_SHOW_MEMBERSOFFLINE("Members offline: "),
    COMMAND_SHOW_COMMANDDESCRIPTION("Show faction information"),
    COMMAND_SHOW_DEATHS_TIL_RAIDABLE("<i>DTR: %1$d"),
    COMMAND_SHOW_EXEMPT("<b>This faction is exempt and cannot be seen."),
    COMMAND_SHOW_NEEDFACTION("&cYou need to join a faction to view your own!"),

    COMMAND_SHOWCLAIMS_HEADER("&8&m-------------&8<{faction}'s claims&8>&8&m-------------"),
    COMMAND_SHOWCLAIMS_FORMAT("&8[{world}]:"),
    COMMAND_SHOWCLAIMS_CHUNKSFORMAT("&8(&c{x}&8,&c{z}&8)"),
    COMMAND_SHOWCLAIMS_DESCRIPTION("show your factions claims!"),

    COMMAND_SHOWINVITES_PENDING("Players with pending invites: "),
    COMMAND_SHOWINVITES_CLICKTOREVOKE("Click to revoke invite for %1$s"),
    COMMAND_SHOWINVITES_DESCRIPTION("Show pending faction invites"),

    COMMAND_STATUS_FORMAT("%1$s Power: %2$s Last Seen: %3$s"),
    COMMAND_STATUS_ONLINE("Online"),
    COMMAND_STATUS_AGOSUFFIX(" ago."),
    COMMAND_STATUS_DESCRIPTION("Show the status of a player"),

    COMMAND_STUCK_TIMEFORMAT("m 'minutes', s 'seconds.'"),
    COMMAND_STUCK_CANCELLED("<a>Teleport cancelled because you were damaged"),
    COMMAND_STUCK_OUTSIDE("<a>Teleport cancelled because you left <i>%1$d <a>block radius"),
    COMMAND_STUCK_EXISTS("<a>You are already teleporting, you must wait <i>%1$s"),
    COMMAND_STUCK_START("<a>Teleport will commence in <i>%s<a>. Don't take or deal damage. "),
    COMMAND_STUCK_TELEPORT("<a>Teleported safely to %1$d, %2$d, %3$d."),
    COMMAND_STUCK_TOSTUCK("to safely teleport %1$s out"),
    COMMAND_STUCK_FORSTUCK("for %1$s initiating a safe teleport out"),
    COMMAND_STUCK_DESCRIPTION("Safely teleports you out of enemy faction"),

    COMMAND_SEECHUNK_ENABLED("&cSeechunk enabled!"),
    COMMAND_SEECHUNK_DISABLED("&cSeechunk disabled!"),


    COMMAND_TAG_TAKEN("<b>That tag is already taken"),
    COMMAND_TAG_TOCHANGE("to change the faction tag"),
    COMMAND_TAG_FORCHANGE("for changing the faction tag"),
    COMMAND_TAG_FACTION("%1$s<i> changed your faction tag to %2$s"),
    COMMAND_TAG_CHANGED("<i>The faction %1$s<i> changed their name to %2$s."),
    COMMAND_TAG_DESCRIPTION("Change the faction tag"),

    COMMAND_TITLE_TOCHANGE("to change a players title"),
    COMMAND_TITLE_FORCHANGE("for changing a players title"),
    COMMAND_TITLE_CHANGED("%1$s<i> changed a title: %2$s"),
    COMMAND_TITLE_DESCRIPTION("Set or remove a players title"),

    COMMAND_TOGGLEALLIANCECHAT_DESCRIPTION("Toggles whether or not you will see alliance chat"),
    COMMAND_TOGGLEALLIANCECHAT_IGNORE("Alliance chat is now ignored"),
    COMMAND_TOGGLEALLIANCECHAT_UNIGNORE("Alliance chat is no longer ignored"),

    COMMAND_TOGGLESB_DISABLED("You can't toggle scoreboards while they are disabled."),

    COMMAND_TOP_DESCRIPTION("Sort Factions to see the top of some criteria."),
    COMMAND_TOP_TOP("Top Factions by %s. Page %d/%d"),
    COMMAND_TOP_LINE("%d. &6%s: &c%s"), // Rank. Faction: Value
    COMMAND_TOP_INVALID("Could not sort by %s. Try balance, online, members, power or land."),

    COMMAND_TNT_DISABLED_MSG("&cThis command is disabled!"),
    COMMAND_TNT_INVALID_NUM("The amount needs to be a number!"),
    COMMAND_TNT_DEPOSIT_SUCCESS("&cSuccessfully deposited tnt."),
    COMMAND_TNT_EXCEEDLIMIT("&cThis exceeds the bank limit!"),
    COMMAND_TNT_WIDTHDRAW_SUCCESS("&cSuccessfully withdrew tnt."),
    COMMAND_TNT_WIDTHDRAW_NOTENOUGH("&cNot enough tnt in bank."),
    COMMAND_TNT_DEPOSIT_NOTENOUGH("&cNot enough tnt in tnt inventory."),
    COMMAND_TNT_AMOUNT("&cYour faction has {amount} tnt in the tnt bank."),
    COMMAND_TNT_POSITIVE("&cPlease use positive numbers!"),
    COMMAND_TNT_DESCRIPTION("add/widthraw from faction's tnt bank"),

    COMMAND_TNTFILL_HEADER("&c&l[!] &7Filling tnt in dispensers..."),
    COMMAND_TNTFILL_SUCCESS("&c&l[!] &7Filled &c{amount}&7 Tnt in &c{dispensers} &7dispensers"),
    COMMAND_TNTFILL_NOTENOUGH("&c&l[!] &7Not enough tnt in inventory."),
    COMMAND_TNTFILL_RADIUSMAX("&c&l[!] &7The max radius is {max}"),
    COMMAND_TNTFILL_AMOUNTMAX("&c&l[!] &7The max amount is {max}"),
    COMMAND_TNTFILL_MOD("&c&l[!] &7Tnt will be used from the faction bank because you dont have the specified amount in your inventory and you are a {role}"),
    COMMAND_TNTFILL_DESCRIPTION("Fill tnt into dispensers around you"),

    COMMAND_UNBAN_DESCRIPTION("Unban someone from your Faction"),
    COMMAND_UNBAN_NOTBANNED("&7%s &cisn't banned. Not doing anything."),
    COMMAND_UNBAN_UNBANNED("&e%1$s &cunbanned &7%2$s"),
    COMMAND_UNBAN_TARGET("&aYou were unbanned from &r%s"),

    COMMAND_UNCLAIM_SAFEZONE_SUCCESS("<i>Safe zone was unclaimed."),
    COMMAND_UNCLAIM_SAFEZONE_NOPERM("<b>This is a safe zone. You lack permissions to unclaim."),
    COMMAND_UNCLAIM_WARZONE_SUCCESS("<i>War zone was unclaimed."),
    COMMAND_UNCLAIM_WARZONE_NOPERM("<b>This is a war zone. You lack permissions to unclaim."),
    COMMAND_UNCLAIM_UNCLAIMED("%1$s<i> unclaimed some of your land."),
    COMMAND_UNCLAIM_UNCLAIMS("<i>You unclaimed this land."),
    COMMAND_UNCLAIM_LOG("%1$s unclaimed land at (%2$s) from the faction: %3$s"),
    COMMAND_UNCLAIM_WRONGFACTION("<b>You don't own this land."),
    COMMAND_UNCLAIM_TOUNCLAIM("to unclaim this land"),
    COMMAND_UNCLAIM_FORUNCLAIM("for unclaiming this land"),
    COMMAND_UNCLAIM_FACTIONUNCLAIMED("%1$s<i> unclaimed some land."),
    COMMAND_UNCLAIM_DESCRIPTION("Unclaim the land where you are standing"),

    COMMAND_UNCLAIMALL_TOUNCLAIM("to unclaim all faction land"),
    COMMAND_UNCLAIMALL_FORUNCLAIM("for unclaiming all faction land"),
    COMMAND_UNCLAIMALL_UNCLAIMED("%1$s<i> unclaimed ALL of your faction's land."),
    COMMAND_UNCLAIMALL_LOG("%1$s unclaimed everything for the faction: %2$s"),
    COMMAND_UNCLAIMALL_DESCRIPTION("Unclaim all of your factions land"),
    COMMAND_UNCLAIM_CLICKTOUNCLAIM("Click to unclaim &2(%1$d, %2$d)"),

    COMMAND_VERSION_NAME("&c&l[!]&7 &c&k||| &r&4SavageFactions&7 &c&k|||&r &c» &7By ProSavage"),
    COMMAND_VERSION_VERSION("&7Version &c» &7%1$s"),
    COMMAND_VERSION_DESCRIPTION("Show plugin and translation version information"),

    COMMAND_WARUNCLAIMALL_DESCRIPTION("Unclaim all warzone land"),
    COMMAND_WARUNCLAIMALL_SUCCESS("<i>You unclaimed ALL war zone land."),
    COMMAND_WARUNCLAIMALL_LOG("%1$s unclaimed all war zones."),

    COMMAND_RULES_DISABLED_MSG("&cThis command is disabled!"),
    COMMAND_RULES_DESCRIPTION("set/remove/add rules!"),
    COMMAND_RULES_ADD_INVALIDARGS("Please include a rule!"),
    COMMAND_RULES_SET_INVALIDARGS("Please include a line number & rule!"),
    COMMAND_RULES_REMOVE_INVALIDARGS("Please include a line number!"),
    COMMAND_RULES_ADD_SUCCESS("&cRule added successfully!"),
    COMMAND_RULES_REMOVE_SUCCESS("&cRule removed successfully!"),
    COMMAND_RULES_SET_SUCCESS("&cRule set successfully!"),
    COMMAND_RULES_CLEAR_SUCCESS("&cRule cleared successfully!"),



    /**
     * Leaving - This is accessed through a command, and so it MAY need a COMMAND_* slug :s
     */
    LEAVE_PASSADMIN("<b>You must give the admin role to someone else first."),
    LEAVE_NEGATIVEPOWER("<b>You cannot leave until your power is positive."),
    LEAVE_TOLEAVE("to leave your faction."),
    LEAVE_FORLEAVE("for leaving your faction."),
    LEAVE_LEFT("%s<i> left faction %s<i>."),
    LEAVE_DISBANDED("<i>%s<i> was disbanded."),
    LEAVE_DISBANDEDLOG("The faction %s (%s) was disbanded due to the last player (%s) leaving."),
    LEAVE_DESCRIPTION("\\n  &a&l» &7Leave your faction"),

    /**
     * Claiming - Same as above basically. No COMMAND_* because it's not in a command class, but...
     */
    CLAIM_PROTECTED("<b>This land is protected"),
    CLAIM_DISABLED("<b>Sorry, this world has land claiming disabled."),
    CLAIM_CANTCLAIM("<b>You can't claim land for <h>%s<b>."),
    CLAIM_ALREADYOWN("%s<i> already own this land."),
    CLAIM_MUSTBE("<b>You must be <h>%s<b> to claim land."),
    CLAIM_MEMBERS("Factions must have at least <h>%s<b> members to claim land."),
    CLAIM_SAFEZONE("<b>You can not claim a Safe Zone."),
    CLAIM_WARZONE("<b>You can not claim a War Zone."),
    CLAIM_POWER("<b>You can't claim more land! You need more power!"),
    CLAIM_LIMIT("<b>Limit reached. You can't claim more land!"),
    CLAIM_ALLY("<b>You can't claim the land of your allies."),
    CLAIM_CONTIGIOUS("<b>You can only claim additional land which is connected to your first claim or controlled by another faction!"),
    CLAIM_FACTIONCONTIGUOUS("<b>You can only claim additional land which is connected to your first claim!"),
    CLAIM_PEACEFUL("%s<i> owns this land. Your faction is peaceful, so you cannot claim land from other factions."),
    CLAIM_PEACEFULTARGET("%s<i> owns this land, and is a peaceful faction. You cannot claim land from them."),
    CLAIM_THISISSPARTA("%s<i> owns this land and is strong enough to keep it."),
    CLAIM_BORDER("<b>You must start claiming land at the border of the territory."),
    CLAIM_TOCLAIM("to claim this land"),
    CLAIM_FORCLAIM("for claiming this land"),
    CLAIM_TOOVERCLAIM("to overclaim this land"),
    CLAIM_FOROVERCLAIM("for over claiming this land"),
    CLAIM_CLAIMED("<h>%s<i> claimed land for <h>%s<i> from <h>%s<i>."),
    CLAIM_CLAIMEDLOG("%s claimed land at (%s) for the faction: %s"),
    CLAIM_OVERCLAIM_DISABLED("<i>Over claiming is disabled on this server."),
    CLAIM_TOOCLOSETOOTHERFACTION("<i>Your claim is too close to another Faction. Buffer required is %d"),
    CLAIM_OUTSIDEWORLDBORDER("<i>Your claim is outside the border."),
    CLAIM_OUTSIDEBORDERBUFFER("<i>Your claim is outside the border. %d chunks away world edge required."),
    CLAIM_CLICK_TO_CLAIM("Click to try to claim &2(%1$d, %2$d)"),
    CLAIM_MAP_OUTSIDEBORDER("&cThis claim is outside the worldborder!"),
    CLAIM_YOUAREHERE("You are here"),

    /**
     * More generic, or less easily categorisable translations, which may apply to more than one class
     */
    GENERIC_YOU("you"),
    GENERIC_YOURFACTION("your faction"),
    GENERIC_NOPERMISSION("<b>You don't have permission to %1$s."),
    GENERIC_FPERM_NOPERMISSION("&7Your faction leader does not allow you to %1$s."),
    GENERIC_DOTHAT("do that"),  //Ugh nuke this from high orbit
    GENERIC_NOPLAYERMATCH("<b>No player match found for \"<p>%1$s<b>\"."),
    GENERIC_NOPLAYERFOUND("<b>No player \"<p>%1$s<b>\" could not be found."),
    GENERIC_ARGS_TOOFEW("<b>Too few arguments. <i>Use like this:"),
    GENERIC_ARGS_TOOMANY("<b>Strange argument \"<p>%1$s<b>\". <i>Use the command like this:"),
    GENERIC_DEFAULTDESCRIPTION("Default faction description :("),
    GENERIC_OWNERS("Owner(s): %1$s"),
    GENERIC_PUBLICLAND("Public faction land."),
    GENERIC_FACTIONLESS("factionless"),
    GENERIC_SERVERADMIN("A server admin"),
    GENERIC_DISABLED("disabled"),
    GENERIC_ENABLED("enabled"),
    GENERIC_INFINITY("âˆž"),
    GENERIC_CONSOLEONLY("This command cannot be run as a player."),
    GENERIC_PLAYERONLY("<b>This command can only be used by ingame players."),
    GENERIC_ASKYOURLEADER("<i> Ask your leader to:"),
    GENERIC_YOUSHOULD("<i>You should:"),
    GENERIC_YOUMAYWANT("<i>You may want to: "),
    GENERIC_TRANSLATION_VERSION("Translation: %1$s(%2$s,%3$s) State: %4$s"),
    GENERIC_TRANSLATION_CONTRIBUTORS("Translation contributors: %1$s"),
    GENERIC_TRANSLATION_RESPONSIBLE("Responsible for translation: %1$s"),
    GENERIC_FACTIONTAG_TOOSHORT("<i>The faction tag can't be shorter than <h>%1$s<i> chars."),
    GENERIC_FACTIONTAG_TOOLONG("<i>The faction tag can't be longer than <h>%s<i> chars."),
    GENERIC_FACTIONTAG_ALPHANUMERIC("<i>Faction tag must be alphanumeric. \"<h>%s<i>\" is not allowed."),
    GENERIC_PLACEHOLDER("<This is a placeholder for a message you should not see>"),



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
    ROLE_ADMIN("admin"),
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
    PLAYER_CANTHURT("<i>You may not harm other players in %s"),
    PLAYER_SAFEAUTO("<i>This land is now a safe zone."),
    PLAYER_WARAUTO("<i>This land is now a war zone."),
    PLAYER_OUCH("<b>Ouch, that is starting to hurt. You should give it a rest."),
    PLAYER_USE_WILDERNESS("<b>You can't use <h>%s<b> in the wilderness."),
    PLAYER_USE_SAFEZONE("<b>You can't use <h>%s<b> in a safe zone."),
    PLAYER_USE_WARZONE("<b>You can't use <h>%s<b> in a war zone."),
    PLAYER_USE_TERRITORY("<b>You can't <h>%s<b> in the territory of <h>%s<b>."),
    PLAYER_USE_OWNED("<b>You can't use <h>%s<b> in this territory, it is owned by: %s<b>."),
    PLAYER_COMMAND_WARZONE("<b>You can't use the command '%s' in warzone."),
    PLAYER_COMMAND_NEUTRAL("<b>You can't use the command '%s' in neutral territory."),
    PLAYER_COMMAND_ENEMY("<b>You can't use the command '%s' in enemy territory."),
    PLAYER_COMMAND_PERMANENT("<b>You can't use the command '%s' because you are in a permanent faction."),
    PLAYER_COMMAND_ALLY("<b>You can't use the command '%s' in ally territory."),
    PLAYER_COMMAND_WILDERNESS("<b>You can't use the command '%s' in the wilderness."),

    PLAYER_POWER_NOLOSS_PEACEFUL("<i>You didn't lose any power since you are in a peaceful faction."),
    PLAYER_POWER_NOLOSS_WORLD("<i>You didn't lose any power due to the world you died in."),
    PLAYER_POWER_NOLOSS_WILDERNESS("<i>You didn't lose any power since you were in the wilderness."),
    PLAYER_POWER_NOLOSS_WARZONE("<i>You didn't lose any power since you were in a war zone."),
    PLAYER_POWER_LOSS_WARZONE("<b>The world you are in has power loss normally disabled, but you still lost power since you were in a war zone.\n<i>Your power is now <h>%d / %d"),
    PLAYER_POWER_NOW("<i>Your power is now <h>%d / %d"),

    PLAYER_PVP_LOGIN("<i>You can't hurt other players for %d seconds after logging in."),
    PLAYER_PVP_REQUIREFACTION("<i>You can't hurt other players until you join a faction."),
    PLAYER_PVP_FACTIONLESS("<i>You can't hurt players who are not currently in a faction."),
    PLAYER_PVP_PEACEFUL("<i>Peaceful players cannot participate in combat."),
    PLAYER_PVP_NEUTRAL("<i>You can't hurt neutral factions. Declare them as an enemy."),
    PLAYER_PVP_CANTHURT("<i>You can't hurt %s<i>."),

    PLAYER_PVP_NEUTRALFAIL("<i>You can't hurt %s<i> in their own territory unless you declare them as an enemy."),
    PLAYER_PVP_TRIED("%s<i> tried to hurt you."),

    /**
     * Strings lying around in other bits of the plugins
     */
    NOPAGES("<i>Sorry. No Pages available."),
    INVALIDPAGE("<i>Invalid page. Must be between 1 and %1$d"),

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
    FACTION_LEAVE("faction-leave", "<a>Leaving %1$s, <a>Entering %2$s"),
    FACTIONS_ANNOUNCEMENT_TOP("faction-announcement-top", "&d--Unread Faction Announcements--"),
    FACTIONS_ANNOUNCEMENT_BOTTOM("faction-announcement-bottom", "&d--Unread Faction Announcements--"),
    DEFAULT_PREFIX("default-prefix", "{relationcolor}[{faction}]"),
    FACTION_LOGIN("faction-login", "&e%1$s &9logged in."),
    FACTION_LOGOUT("faction-logout", "&e%1$s &9logged out.."),
    NOFACTION_PREFIX("nofactions-prefix", "&6[&a4-&6]&r"),
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
    WARMUPS_CANCELLED("&cYou have cancelled your warmup.");

    private String path;
    private String def;
    private static YamlConfiguration LANG;
    public static SimpleDateFormat sdf;

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

    @Override
    public String toString() {
        return this == TITLE ? ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def)) + " " : ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
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
