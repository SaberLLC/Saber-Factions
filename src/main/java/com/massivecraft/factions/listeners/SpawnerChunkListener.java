package com.massivecraft.factions.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.*;
import com.massivecraft.factions.util.FastChunk;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpawnerChunkListener implements Listener {

    @EventHandler
    public void onSpawnerPlace(BlockPlaceEvent e) {


        if (e.getBlockPlaced().getType() == XMaterial.SPAWNER.parseMaterial()) {
            Location location = e.getBlockPlaced().getLocation();
            FLocation fLoc = new FLocation(location);
            Faction fac = Board.getInstance().getFactionAt(fLoc);
            FastChunk fc = new FastChunk(location.getWorld().getName(), location.getChunk().getX(), location.getChunk().getZ());
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(e.getPlayer());

            if (fPlayer.isAdminBypassing()) {
                return;
            }

            if (!Conf.allowSpawnersPlacedInWilderness) {
                if (fac.isNormal()) {
                    if (!fac.getSpawnerChunks().contains(fc)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(TL.SPAWNER_CHUNK_PLACE_DENIED_NOT_SPAWNERCHUNK.toString());
                    }
                } else if (fac.isWilderness()) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(TL.SPAWNER_CHUNK_PLACE_DENIED_WILDERNESS.toString());
                }
            }
        }
    }
}
