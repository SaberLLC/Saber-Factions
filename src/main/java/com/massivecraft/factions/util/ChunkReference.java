package com.massivecraft.factions.util;

import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

import java.util.EnumMap;
import java.util.Map;

public class ChunkReference {

    public static Map<EntityType, Integer> getSpawners(Chunk chunk) {
        Map<EntityType, Integer> spawners = new EnumMap<>(EntityType.class);
        for (BlockState state : chunk.getTileEntities()) {
            if (state instanceof CreatureSpawner) {
                CreatureSpawner spawner = (CreatureSpawner) state;
                spawners.put(spawner.getSpawnedType(), spawners.getOrDefault(spawner.getSpawnedType(), 0) + 1);
            }
        }
        return spawners;
    }

    public static int getSpawnerCount(Chunk chunk) {
        int i = 0;
        for (BlockState state : chunk.getTileEntities()) {
            if (state instanceof CreatureSpawner) {
                i++;
            }
        }
        return i;
    }

}
