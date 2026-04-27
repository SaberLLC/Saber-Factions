package org.saberdev.corex.addons;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.saberdev.corex.CoreAddon;


@CoreAddon(configVariable = "Anti-Nether-Portal")
public class AntiNetherPortal implements Listener {

    @EventHandler
    public void onTeleportNether(PlayerPortalEvent e){
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && !e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }
}
