package com.massivecraft.factions.integration;


import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class LunarAPI {


    public static boolean isLunarAPIEnabled() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("LunarClient-API");
    }


    public static void sendHomeWaypoint(FPlayer fPlayer) {
        Player player = fPlayer.getPlayer();
        Faction faction = fPlayer.getFaction();
        if(fPlayer.hasFaction() && fPlayer.getFaction().getHome() != null) {
            //FactionsPlugin.getInstance().getLunarClientAPI().registerPlayer(player);
            LCWaypoint waypoint = new LCWaypoint("Faction Home", faction.getHome(), Color.LIME.asRGB(), true);
            FactionsPlugin.getInstance().getLunarClientAPI().sendWaypoint(player, waypoint);
        }
    }

}
