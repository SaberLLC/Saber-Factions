package org.saberdev.corex.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class AntiEndPortal implements Listener {

    @EventHandler
    public void onTeleportEnd(PlayerPortalEvent e) {
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL && !e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }
}
