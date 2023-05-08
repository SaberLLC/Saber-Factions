package com.massivecraft.factions.tag;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.QuadFunction;
import com.massivecraft.factions.zcore.util.FastUUID;
import com.massivecraft.factions.zcore.util.TagUtil;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.*;

public enum FancyTag implements Tag {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    NEUTRAL_LIST("{neutral-list}", (target, fme, prefix, gm) -> processRelation(prefix, target, fme, Relation.NEUTRAL)),
    ALLIES_LIST("{allies-list}", (target, fme, prefix, gm) -> processRelation(prefix, target, fme, Relation.ALLY)),
    ENEMIES_LIST("{enemies-list}", (target, fme, prefix, gm) -> processRelation(prefix, target, fme, Relation.ENEMY)),
    TRUCES_LIST("{truces-list}", (target, fme, prefix, gm) -> processRelation(prefix, target, fme, Relation.TRUCE)),
    ONLINE_LIST("{online-list}", (target, fme, prefix, gm) -> {
        List<Component> Components = new ArrayList<>();
        TextComponent.Builder currentOnline = TextUtil.parseFancy(prefix);
        boolean firstOnline = true;
        for (FPlayer p : MiscUtil.rankOrder(target.getFPlayersWhereOnline(true, fme))) {
            if (fme.getPlayer() != null && !fme.getPlayer().canSee(p.getPlayer())) {
                continue; // skip
            }
            String name = p.getNameAndTitle();
            currentOnline.append(Component.text(firstOnline ? name : ", " + name))
                            .hoverEvent(HoverEvent.showText(Component.text(String.join("\n", tipPlayer(p, gm))))).color(TextUtil.kyoriColor(fme.getColorTo(p)));
            firstOnline = false;
            if (TagUtil.SERIALIZER.toJson(currentOnline.build()).length() > ARBITRARY_LIMIT) {
                Components.add(currentOnline.build());
                currentOnline = TextUtil.toFancy("");
            }
        }
        Components.add(currentOnline.build());
        return firstOnline && Tag.isMinimalShow() ? null : Components;
    }),
    OFFLINE_LIST("{offline-list}", (target, fme, prefix, gm) -> {
        List<Component> Components = new ArrayList<>();
        TextComponent.Builder currentOffline = TextUtil.parseFancy(prefix);
        boolean firstOffline = true;
        for (FPlayer p : MiscUtil.rankOrder(target.getFPlayers())) {
            String name = p.getNameAndTitle();
            // Also make sure to add players that are online BUT can't be seen.
            if (!p.isOnline() || (fme.getPlayer() != null && p.isOnline() && !fme.getPlayer().canSee(p.getPlayer()))) {

                currentOffline.append(Component.text(firstOffline ? name : ", " + name))
                        .hoverEvent(HoverEvent.showText(Component.text(String.join("\n", tipPlayer(p, gm))))).color(TextUtil.kyoriColor(fme.getColorTo(p)));
                firstOffline = false;
                if (TagUtil.SERIALIZER.toJson(currentOffline.build()).length() > ARBITRARY_LIMIT) {
                    Components.add(currentOffline.build());
                    currentOffline = TextUtil.toFancy("");
                }
            }
        }
        Components.add(currentOffline.build());
        return firstOffline && Tag.isMinimalShow() ? null : Components;
    }),
    ;

    private final String tag;
    private final QuadFunction<Faction, FPlayer, String, Map<UUID, String>, List<Component>> function;

    FancyTag(String tag, QuadFunction<Faction, FPlayer, String, Map<UUID, String>, List<Component>> function) {
        this.tag = tag;
        this.function = function;
    }

    private static List<Component> processRelation(String prefix, Faction faction, FPlayer fPlayer, Relation relation) {
        List<Component> Components = new ArrayList<>();
        TextComponent.Builder message = TextUtil.parseFancy(prefix);
        boolean first = true;
        for (Faction otherFaction : Factions.getInstance().getAllFactions()) {
            if (otherFaction == faction) {
                continue;
            }
            String s = otherFaction.getTag(fPlayer);
            if (otherFaction.getRelationTo(faction) == relation) {
                message.append(Component.text(first ? s : ", " + s)).hoverEvent(Component.text(String.join("\n", tipFaction(otherFaction, fPlayer))).color(TextUtil.kyoriColor(fPlayer.getColorTo(otherFaction))));
                first = false;
                if (TagUtil.SERIALIZER.toJson(message.build()).length() > ARBITRARY_LIMIT) {
                    Components.add(message.build());
                    message = TextUtil.toFancy("");
                }
            }
        }
        Components.add(message.build());
        return first && Tag.isMinimalShow() ? null : Components;
    }

    public static List<Component> parse(String text, Faction faction, FPlayer player, Map<UUID, String> groupMap) {
        for (FancyTag tag : VALUES) {
            if (tag.foundInString(text)) {
                return tag.getMessage(text, faction, player, groupMap);
            }
        }
        return Collections.emptyList(); // We really shouldn't be here.
    }

    public static boolean anyMatch(String text) {
        return getMatch(text) != null;
    }

    public static FancyTag getMatch(String text) {
        for (FancyTag tag : VALUES) {
            if (tag.foundInString(text)) {
                return tag;
            }
        }
        return null;
    }

    /**
     * Parses tooltip variables from config <br> Supports variables for factions only (type 2)
     *
     * @param faction faction to tooltip for
     * @return list of tooltips for a fancy message
     */
    private static List<String> tipFaction(Faction faction, FPlayer player) {
        List<String> tooltips = FactionsPlugin.getInstance().getConfig().getStringList("tooltips.list");
        List<String> lines = new ArrayList<>(tooltips.size());
        for (String line : tooltips) {
            String string = Tag.parsePlain(faction, player, line);
            if (string == null) {
                continue;
            }
            lines.add(CC.translate(string));
        }
        return lines;
    }

    /**
     * Parses tooltip variables from config <br> Supports variables for players and factions (types 1 and 2)
     *
     * @param fplayer player to tooltip for
     * @return list of tooltips for a fancy message
     */
    private static List<String> tipPlayer(FPlayer fplayer, Map<UUID, String> groupMap) {
        List<String> tooltips = FactionsPlugin.getInstance().getConfig().getStringList("tooltips.show");
        List<String> lines = new ArrayList<>(tooltips.size());
        for (String line : tooltips) {
            String newLine = line;
            everythingOnYourWayOut:
            if (line.contains("{group}")) {
                if (groupMap != null) {
                    String group = groupMap.get(FastUUID.parseUUID(fplayer.getId()));
                    if (!group.trim().isEmpty()) {
                        newLine = TextUtil.replace(newLine, "{group}", group);
                        break everythingOnYourWayOut;
                    }
                }
                continue;
            }
            String string = Tag.parsePlain(fplayer, newLine);
            if (string == null) {
                continue;
            }
            lines.add(CC.translate(string));
        }
        return lines;
    }

    public static final FancyTag[] VALUES = FancyTag.values();

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public boolean foundInString(String test) {
        return test != null && test.contains(this.tag);
    }

    public List<Component> getMessage(String text, Faction faction, FPlayer player, Map<UUID, String> groupMap) {
        if (!this.foundInString(text)) {
            return Collections.emptyList(); // We really, really shouldn't be here.
        }
        return this.function.apply(faction, player, text.replace(this.getTag(), ""), groupMap);
    }
}
