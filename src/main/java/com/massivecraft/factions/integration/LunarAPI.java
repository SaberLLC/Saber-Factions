package com.massivecraft.factions.integration;


import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class LunarAPI {

    public static void initLunarUser(FPlayer fPlayer) {
        if(fPlayer.hasFaction()) {
            Player player = fPlayer.getPlayer();
            Faction faction = fPlayer.getFaction();
            if(LunarClientAPI.getInstance().isRunningLunarClient(player)) {
                LunarClientAPI.getInstance().registerPlayer(player);
                LCWaypoint waypoint = new LCWaypoint("Faction Home", faction.getHome(), Color.LIME.asRGB(), true);
                LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
            }
        }
    }

    public static  void exilLunarUser(FPlayer fPlayer) {
        if(LunarClientAPI.getInstance().isRunningLunarClient(fPlayer.getPlayer())) {
            LunarClientAPI.getInstance().unregisterPlayer(fPlayer.getPlayer(), true);
        }
    }

}
