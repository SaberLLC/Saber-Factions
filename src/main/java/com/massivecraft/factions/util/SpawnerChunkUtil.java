package com.massivecraft.factions.util;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

/**
 * @author Saser
 */
public class SpawnerChunkUtil {

    public static boolean isSpawnerChunk(FLocation fLocation) {
        if (Conf.userSpawnerChunkSystem) {
            FastChunk fastChunk = new FastChunk(fLocation);
            Faction faction = Board.getInstance().getFactionAt(fLocation);
            if (faction.getSpawnerChunks() != null) {
                return faction.getSpawnerChunks().contains(fastChunk);
            }
        }
        return false;
    }

}
