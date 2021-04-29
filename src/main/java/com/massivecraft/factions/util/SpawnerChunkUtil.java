package com.massivecraft.factions.util;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;

/**
 * @author Saser
 */
public class SpawnerChunkUtil {

    public static boolean isSpawnerChunk(FLocation fLocation) {
        if(Conf.userSpawnerChunkSystem) {
            FastChunk fastChunk = new FastChunk(fLocation);
            return Board.getInstance().getFactionAt(fLocation).getSpawnerChunks().contains(fastChunk);
        }
        return false;
    }

}
