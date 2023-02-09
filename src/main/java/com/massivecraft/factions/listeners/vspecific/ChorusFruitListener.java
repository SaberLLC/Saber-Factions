package com.massivecraft.factions.listeners.vspecific;

import com.massivecraft.factions.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * @Author: Driftay
 * @Date: 1/23/2022 3:19 PM
 */
public class ChorusFruitListener implements Listener {

    @EventHandler
    public void onChorusTeleport(PlayerTeleportEvent event){
        if(!FactionsPlugin.instance.getConfig().getBoolean("disable-chorus-teleport-in-territory", true))return;
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) {
            if(event.getTo() == null )return;
            Faction fac = Board.getInstance().getFactionAt(FLocation.wrap(event.getTo()));
            FPlayer fplayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
            if(fac != null && !fac.isSystemFaction()){
                if(!fplayer.hasFaction() || fplayer.getFaction() != fac){
                    event.getPlayer().sendMessage("§cYou are not allowed to teleport there.");
                    event.setTo(event.getFrom());
                }
            }
        }
    }
}
