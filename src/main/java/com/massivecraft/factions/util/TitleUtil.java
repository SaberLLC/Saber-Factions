package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TagUtil;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TitleUtil {

    public static void sendFactionChangeTitle(FPlayer me, Faction faction) {
        if (me == null) return;
        FileConfiguration config = FactionsPlugin.getInstance().getConfig();
        int version = FactionsPlugin.getInstance().version;
        if (version != 7) {
            String title = parseAllPlaceholders(TextUtil.replace(config.getString("Title.Format.Title"), "{Faction}", faction.getColorTo(me) + faction.getTag()), faction, me.getPlayer());
            String subTitle = parseAllPlaceholders(TextUtil.replace(config.getString("Title.Format.Subtitle"), "{Description}", faction.getDescription()).replace("{Faction}", faction.getColorTo(me) + faction.getTag()), faction, me.getPlayer());

            Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> {
                if (version != 8) {
                    me.getPlayer().sendTitle(CC.translate(title), CC.translate(subTitle), config.getInt("Title.Options.FadeInTime"), config.getInt("Title.Options.ShowTime"), config.getInt("Title.Options.FadeOutTime"));
                } else {
                    me.getPlayer().sendTitle(CC.translate(title), CC.translate(subTitle));
                }
            }, 5);
            me.getPlayer().removeMetadata("showFactionTitle", FactionsPlugin.getInstance());
        }
    }


    public static String parseAllPlaceholders(String string, Faction faction, Player player) {
        string = TagUtil.parsePlaceholders(player, string);
        string = TextUtil.replace(TextUtil.replace(TextUtil.replace(TextUtil.replace(TextUtil.replace(TextUtil.replace(string, "{Faction}", faction.getTag()), "{online}", Integer.toString(faction.getOnlinePlayers().size())), "{offline}", Integer.toString(faction.getFPlayers().size() - faction.getOnlinePlayers().size())), "{chunks}", Integer.toString(faction.getAllClaims().size())), "{power}", Double.toString(faction.getPower())), "{leader}", faction.getFPlayerAdmin().toString());
        return string;
    }
}
