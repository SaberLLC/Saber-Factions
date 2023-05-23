package com.massivecraft.factions.zcore.util;


import com.google.gson.Gson;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.MiscUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.massivecraft.factions.zcore.util.TagReplacer.TagType;

public class TagUtil {

    public static final Gson SERIALIZER = GsonComponentSerializer.colorDownsamplingGson().serializer();

    private static final int ARBITRARY_LIMIT = 25000;

    /**
     * Replaces all variables in a plain raw line for a faction
     *
     * @param faction for faction
     * @param line    raw line from config with variables to replace for
     * @return clean line
     */
    public static String parsePlain(Faction faction, String line) {
        for (TagReplacer tagReplacer : TagReplacer.getByType(TagType.FACTION)) {
            if (tagReplacer.contains(line)) {
                line = tagReplacer.replace(line, tagReplacer.getValue(faction, null));
            }
        }
        return line;
    }

    /**
     * Replaces all variables in a plain raw line for a player
     *
     * @param fplayer for player
     * @param line    raw line from config with variables to replace for
     * @return clean line
     */
    public static String parsePlain(FPlayer fplayer, String line) {
        for (TagReplacer tagReplacer : TagReplacer.getByType(TagType.PLAYER)) {
            if (tagReplacer.contains(line)) {
                String rep = tagReplacer.getValue(fplayer.getFaction(), fplayer);
                if (rep == null) {
                    rep = ""; // this should work, but it's not a good way to handle whatever is going wrong
                }
                line = tagReplacer.replace(line, rep);
            }
        }
        return line;
    }

    /**
     * Replaces all variables in a plain raw line for a faction, using relations from fplayer
     *
     * @param faction for faction
     * @param fplayer from player
     * @param line    raw line from config with variables to replace for
     * @return clean line
     */
    public static String parsePlain(Faction faction, FPlayer fplayer, String line) {
        for (TagReplacer tagReplacer : TagReplacer.getByType(TagType.PLAYER)) {
            if (tagReplacer.contains(line)) {
                String value = tagReplacer.getValue(faction, fplayer);
                if (value != null) {
                    line = tagReplacer.replace(line, value);
                } else {
                    return null; // minimal show, entire line to be ignored
                }
            }
        }
        return line;
    }

    /**
     * Scan a line and parse the fancy variable into a fancy list
     *
     * @param faction for faction (viewers faction)
     * @param fme     for player (viewer)
     * @param line    fancy message prefix
     * @return list of fancy msgs
     */
    public static List<Component> parseFancy(Faction faction, FPlayer fme, String line) {
        for (TagReplacer tagReplacer : TagReplacer.getByType(TagType.FANCY)) {
            if (tagReplacer.contains(line)) {
                String clean = line.replace(tagReplacer.getTag(), ""); // remove tag
                return getFancy(faction, fme, tagReplacer, clean);
            }
        }
        return null;
    }

    public static String parsePlaceholders(Player player, String line) {
        if (FactionsPlugin.getInstance().isClipPlaceholderAPIHooked() && player.isOnline()) {
            line = PlaceholderAPI.setPlaceholders(player, line);
        }

        if (FactionsPlugin.getInstance().isMVdWPlaceholderAPIHooked() && player.isOnline()) {
            line = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, line);
        }
        return line;
    }

    /**
     * Checks if a line has fancy variables
     *
     * @param line raw line from config with variables
     * @return if the line has fancy variables
     */
    public static boolean hasFancy(String line) {
        for (TagReplacer tagReplacer : TagReplacer.getByType(TagType.FANCY)) {
            if (tagReplacer.contains(line)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Lets get fancy.
     *
     * @param target Faction to get relate from
     * @param fme    Player to relate to
     * @param prefix First part of the fancy message
     * @return list of fancy messages to send
     */
    protected static List<Component> getFancy(Faction target, FPlayer fme, TagReplacer type, String prefix) {
        List<Component> lines = new ArrayList<>();
        boolean minimal = FactionsPlugin.getInstance().getConfig().getBoolean("minimal-show", false);

        switch (type) {
            case ALLIES_LIST:
                TextComponent.Builder currentAllies = TextUtil.parseFancy(prefix);
                boolean firstAlly = true;
                for (Faction otherFaction : Factions.getInstance().getAllFactions()) {
                    if (otherFaction == target) {
                        continue;
                    }
                    String s = otherFaction.getTag(fme);
                    if (otherFaction.getRelationTo(target).isAlly()) {
                        currentAllies.append(Component.text(firstAlly ? s : ", " + s).hoverEvent(HoverEvent.showText(Component.text(tipFactionSingular(otherFaction)).color(TextUtil.kyoriColor(fme != null ? fme.getColorTo(otherFaction) : Relation.NEUTRAL.getColor())))));
                        firstAlly = false;
                        if (SERIALIZER.toJson(currentAllies.build()).length() > ARBITRARY_LIMIT) {
                            lines.add(currentAllies.build());
                            currentAllies = TextUtil.toFancy("");
                        }
                    }
                }
                lines.add(currentAllies.build());
                return firstAlly && minimal ? null : lines; // we must return here and not outside the switch
            case ENEMIES_LIST:
                TextComponent.Builder currentEnemies = TextUtil.parseFancy(prefix);
                boolean firstEnemy = true;
                for (Faction otherFaction : Factions.getInstance().getAllFactions()) {
                    if (otherFaction == target) {
                        continue;
                    }
                    String s = otherFaction.getTag(fme);
                    if (otherFaction.getRelationTo(target).isEnemy()) {
                        currentEnemies.append(Component.text(firstEnemy ? s : ", " + s).hoverEvent(HoverEvent.showText(Component.text(tipFactionSingular(otherFaction)).color(TextUtil.kyoriColor(fme != null ? fme.getColorTo(otherFaction) : Relation.NEUTRAL.getColor())))));
                        firstEnemy = false;
                        if (SERIALIZER.toJson(currentEnemies.build()).length() > ARBITRARY_LIMIT) {
                            lines.add(currentEnemies.build());
                            currentEnemies = TextUtil.toFancy("");
                        }
                    }
                }
                lines.add(currentEnemies.build());
                return firstEnemy && minimal ? null : lines; // we must return here and not outside the switch
            case TRUCES_LIST:
                TextComponent.Builder currentTruces = TextUtil.parseFancy(prefix);
                boolean firstTruce = true;
                for (Faction otherFaction : Factions.getInstance().getAllFactions()) {
                    if (otherFaction == target) {
                        continue;
                    }
                    String s = otherFaction.getTag(fme);
                    if (otherFaction.getRelationTo(target).isTruce()) {
                        currentTruces.append(Component.text(firstTruce ? s : ", " + s).hoverEvent(HoverEvent.showText(Component.text(tipFactionSingular(otherFaction)).color(TextUtil.kyoriColor(fme != null ? fme.getColorTo(otherFaction) : Relation.NEUTRAL.getColor())))));
                        firstTruce = false;
                        if (SERIALIZER.toJson(currentTruces.build()).length() > ARBITRARY_LIMIT) {
                            lines.add(currentTruces.build());
                            currentTruces = TextUtil.toFancy("");
                        }
                    }
                }
                lines.add(currentTruces.build());
                return firstTruce && minimal ? null : lines; // we must return here and not outside the switch
            case ONLINE_LIST:
                TextComponent.Builder currentOnline = TextUtil.parseFancy(prefix);
                boolean firstOnline = true;
                for (FPlayer p : MiscUtil.rankOrder(target.getFPlayersWhereOnline(true, fme))) {
                    if (fme != null && fme.getPlayer() != null && !fme.getPlayer().canSee(p.getPlayer())) {
                        continue; // skip
                    }
                    String name = p.getNameAndTitle();
                    currentOnline.append(Component.text(firstOnline ? name : ", " + name).hoverEvent(Component.text(tipPlayerSingular(p))).color(TextUtil.kyoriColor(fme != null ? fme.getColorTo(p) : Relation.NEUTRAL.getColor())));
                    firstOnline = false;
                    if (SERIALIZER.toJson(currentOnline.build()).length() > ARBITRARY_LIMIT) {
                        lines.add(currentOnline.build());
                        currentOnline = TextUtil.toFancy("");
                    }
                }
                lines.add(currentOnline.build());
                return firstOnline && minimal ? null : lines; // we must return here and not outside the switch
            case OFFLINE_LIST:
                TextComponent.Builder currentOffline = TextUtil.parseFancy(prefix);
                boolean firstOffline = true;
                for (FPlayer p : MiscUtil.rankOrder(target.getFPlayers())) {
                    String name = p.getNameAndTitle();
                    // Also make sure to add players that are online BUT can't be seen.
                    if (!p.isOnline() || (fme != null && fme.getPlayer() != null && !fme.getPlayer().canSee(p.getPlayer()))) {
                        currentOffline.append(Component.text(firstOffline ? name : ", " + name).hoverEvent(Component.text(tipPlayerSingular(p))).color(TextUtil.kyoriColor(fme != null ? fme.getColorTo(p) : Relation.NEUTRAL.getColor())));
                        firstOffline = false;
                        if (SERIALIZER.toJson(currentOffline.build()).length() > ARBITRARY_LIMIT) {
                            lines.add(currentOffline.build());
                            currentOffline = TextUtil.toFancy("");
                        }
                    }
                }
                lines.add(currentOffline.build());
                return firstOffline && minimal ? null : lines; // we must return here and not outside the switch
            case ALTS:
                TextComponent.Builder alts = TextUtil.parseFancy(prefix);
                boolean firstAlt = true;
                for (FPlayer p : target.getAltPlayers()) {
                    String name = p.getName();
                    ChatColor color;

                    if (p.isOnline()) {
                        color = ChatColor.GREEN;
                    } else {
                        color = ChatColor.RED;
                    }

                    alts.append(Component.text(firstAlt ? name : ", " + name).hoverEvent(HoverEvent.showText(Component.text(tipPlayerSingular(p)).color(TextUtil.kyoriColor(color)))));
                    firstAlt = false;
                    if (SERIALIZER.toJson(alts.build()).length() > ARBITRARY_LIMIT) {
                        lines.add(alts.build());
                    }
                }
                lines.add(alts.build());
                return firstAlt && minimal ? null : lines;
            default:
                break;
        }
        return null;
    }

    /**
     * Parses tooltip variables from config <br> Supports variables for factions only (type 2)
     *
     * @param faction faction to tooltip for
     * @return list of tooltips for a fancy message
     */
    private static List<String> tipFaction(Faction faction) {
        List<String> lines = new ArrayList<>();
        for (String line : FactionsPlugin.getInstance().getConfig().getStringList("tooltips.list")) {
            lines.add(CC.translate(TagUtil.parsePlain(faction, line)));
        }
        return lines;
    }

    private static String tipFactionSingular(Faction faction) {
        List<String> lines = new ArrayList<>();
        for (String line : FactionsPlugin.getInstance().getConfig().getStringList("tooltips.list")) {
            lines.add(CC.translate(TagUtil.parsePlain(faction, line)));
        }
        return String.join("\n", lines);
    }

    /**
     * Parses tooltip variables from config <br> Supports variables for players and factions (types 1 and 2)
     *
     * @param fplayer player to tooltip for
     * @return list of tooltips for a fancy message
     */
    private static List<String> tipPlayer(FPlayer fplayer) {
        List<String> lines = new ArrayList<>();
        for (String line : FactionsPlugin.getInstance().getConfig().getStringList("tooltips.show")) {
            lines.add(CC.translate(TagUtil.parsePlain(fplayer, line)));
        }
        return lines;
    }

    private static String tipPlayerSingular(FPlayer fplayer) {
        List<String> lines = new ArrayList<>();
        for (String line : FactionsPlugin.getInstance().getConfig().getStringList("tooltips.show")) {
            lines.add(CC.translate(TagUtil.parsePlain(fplayer, line)));
        }
        return String.join("\n", lines);
    }
}
