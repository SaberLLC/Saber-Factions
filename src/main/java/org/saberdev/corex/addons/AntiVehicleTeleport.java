package org.saberdev.corex.addons;

import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.saberdev.corex.CoreAddon;

@CoreAddon(configVariable = "Anti-Vehicle-Teleport")
public class AntiVehicleTeleport implements Listener {

    @EventHandler
    public void onVehicleExit(PlayerTeleportEvent e){
        Player player = e.getPlayer();
        if(player.getVehicle() == null) return;
        if (player.isInsideVehicle()) {
            player.sendMessage(TextUtil.parse(TL.VEHICLE_TELEPORT_BLOCK.toString()));
            e.setCancelled(true);
        }
    }
}
