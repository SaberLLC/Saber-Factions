package com.massivecraft.factions.integration;


import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
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
            FactionsPlugin.getInstance().getLunarClientWrapper().getLcAPI().sendWaypoint(player, waypoint);
        }
    }

    public static void sendRallyPing(FPlayer user) {
        Player player = user.getPlayer();
        Location loc = player.getLocation();
        for(FPlayer fPlayer : user.getFaction().getFPlayersWhereOnline(true)) {
            if(fPlayer.getPlayer().getWorld() != user.getPlayer().getWorld()) continue;
            LCWaypoint waypoint = new LCWaypoint(user.getName(), user.getPlayer().getLocation(), Color.LIME.asRGB(), true);
            FactionsPlugin.getInstance().getLunarClientWrapper().getLcAPI().sendWaypoint(player, waypoint);
            fPlayer.msg(TL.FACTION_RALLY_MESSAGE, user.getName(), loc.getX(), loc.getY(), loc.getZ());
        }
    }
}
