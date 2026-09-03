package com.massivecraft.factions.util;

import com.cryptomorin.xseries.messages.Titles;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TagUtil;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TitleUtil {

    public static void sendFactionChangeTitle(FPlayer me, Faction faction) {
        if (me == null) return;
        int version = FactionsPlugin.getInstance().version;
        if (version != 7) {
            FileConfiguration config = FactionsPlugin.getInstance().getConfig();

            String title = parseAllPlaceholders(TextUtil.replace(config.getString("Title.Format.Title"), "{Faction}", faction.getColorTo(me) + faction.getTag()), faction, me.getPlayer());
            String subTitle = parseAllPlaceholders(TextUtil.replace(config.getString("Title.Format.Subtitle"), "{Description}", faction.getDescription()).replace("{Faction}", faction.getColorTo(me) + faction.getTag()), faction, me.getPlayer());

            Player p = me.getPlayer();
            if (p == null) return;
            FactionsPlugin.getScheduler().runEntityLater(p, 5L, () -> {
                if (version != 8) {
                    Titles.sendTitle(p, config.getInt("Title.Options.FadeInTime"), config.getInt("Title.Options.ShowTime"), config.getInt("Title.Options.FadeOutTime"), TextUtil.parse(title), TextUtil.parse(subTitle));
                } else {
                    p.sendTitle(TextUtil.parse(title), TextUtil.parse(subTitle));
                }
            }, null);
            p.removeMetadata("showFactionTitle", FactionsPlugin.getInstance());
        }
    }


    public static String parseAllPlaceholders(String string, Faction faction, Player player) {
        string = TagUtil.parsePlaceholders(player, string);

        string = TextUtil.replace(TextUtil.replace(TextUtil.replace(TextUtil.replace(TextUtil.replace(string,
                                                "{faction}", faction.getTag()),
                                        "{online}", Integer.toString(faction.getOnlinePlayers().size())),
                                "{offline}", Integer.toString(faction.getFPlayers().size() - faction.getOnlinePlayers().size())),
                        "{chunks}", Integer.toString(faction.getAllClaims().size())),
                "{power}", Double.toString(faction.getPower()));
        return string;
    }
}
