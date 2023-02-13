package com.massivecraft.factions.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class ChunkReference {

    public static Map<EntityType, Integer> getSpawners(Chunk chunk) {
        Map<EntityType, Integer> spawners = new EnumMap<>(EntityType.class);
        for (BlockState state : chunk.getTileEntities()) {
            if (state instanceof CreatureSpawner) {
                CreatureSpawner spawner = (CreatureSpawner) state;
                spawners.merge(spawner.getSpawnedType(), 1, Integer::sum);
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

    public static boolean isSameChunk(PlayerMoveEvent event) {
        return isSameChunk(event.getFrom(), event.getTo());
    }

    public static boolean isSameChunk(Location one, Location two) {
        if (one.getBlockX() >> 4 != two.getBlockX() >> 4)
            return false;
        if (one.getBlockZ() >> 4 != two.getBlockZ() >> 4)
            return false;
        return one.getWorld() == two.getWorld();
    }

    public static boolean isSameBlock(PlayerMoveEvent event) {
        return isSameBlock(event.getFrom(), event.getTo());
    }

    public static boolean isSameBlock(Location one, Location two) {
        if (one.getBlockX() != two.getBlockX())
            return false;
        if (one.getBlockZ() != two.getBlockZ())
            return false;
        if (one.getBlockY() != two.getBlockY())
            return false;
        return Objects.equals(one.getWorld(), two.getWorld());
    }

}
