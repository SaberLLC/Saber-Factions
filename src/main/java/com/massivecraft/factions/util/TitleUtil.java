package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TagUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleUtil {

    public static void sendFactionChangeTitle(FPlayer me, Faction faction) {
        String title = FactionsPlugin.getInstance().getConfig().getString("Title.Format.Title");
        title = title.replace("{Faction}", faction.getColorTo(me) + faction.getTag());
        title = parseAllPlaceholders(title, faction, me.getPlayer());
        String subTitle = FactionsPlugin.getInstance().getConfig().getString("Title.Format.Subtitle").replace("{Description}", faction.getDescription()).replace("{Faction}", faction.getColorTo(me) + faction.getTag());
        subTitle = parseAllPlaceholders(subTitle, faction, me.getPlayer());
        String finalTitle = title;
        String finalsubTitle = subTitle;
        if (FactionsPlugin.getInstance().version != 7) {
            Bukkit.getScheduler().runTaskLater(FactionsPlugin.getInstance(), () -> {
                if (FactionsPlugin.getInstance().version != 8) {
                    me.getPlayer().sendTitle(CC.translate(finalTitle), CC.translate(finalsubTitle), FactionsPlugin.getInstance().getConfig().getInt("Title.Options.FadeInTime"),
                            FactionsPlugin.getInstance().getConfig().getInt("Title.Options.ShowTime"),
                            FactionsPlugin.getInstance().getConfig().getInt("Title.Options.FadeOutTime"));
                } else {
                    me.getPlayer().sendTitle(CC.translate(finalTitle), CC.translate(finalsubTitle));
                }
            }, 5);
            me.getPlayer().removeMetadata("showFactionTitle", FactionsPlugin.getInstance());
        }
    }


    public static String parseAllPlaceholders(String string, Faction faction, Player player) {
        string = TagUtil.parsePlaceholders(player, string);
        string = string.replace("{Faction}", faction.getTag())
                .replace("{online}", faction.getOnlinePlayers().size() + "")
                .replace("{offline}", faction.getFPlayers().size() - faction.getOnlinePlayers().size() + "")
                .replace("{chunks}", faction.getAllClaims().size() + "")
                .replace("{power}", faction.getPower() + "")
                .replace("{leader}", faction.getFPlayerAdmin() + "");
        return string;
    }
}
