package com.massivecraft.factions.listeners;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginRegistry implements Listener {

    @EventHandler
    public void onJoinPreStart(PlayerJoinEvent e) {
        if (!FactionsPlugin.canPlayersJoin()) {
            e.getPlayer().kickPlayer(CC.translate(TL.PRE_JOIN_KICK_MESSAGE.toString()));
        }
    }
}
