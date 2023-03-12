package com.massivecraft.factions.util;

import com.massivecraft.factions.FLocation;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.util.Objects;

public class FastChunk {

    private String world;
    private long key;

    public FastChunk() {
    }

    public FastChunk(String world, int x, int z) {
        this.world = world;
        this.key = WorldUtil.encodeChunk(x, z);
    }

    public FastChunk(String world, FLocation floc) {
        this(world, floc.getIntX(), floc.getIntZ());
    }

    public FastChunk(FLocation floc) {
        this(floc.getWorldName(), floc);
    }

    public FastChunk getRelative(String world, int dx, int dz) {
        return new FastChunk(world, getX() + dx, getZ() + dz);
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return WorldUtil.getChunkX(this.key);
    }

    public int getZ() {
        return WorldUtil.getChunkZ(this.key);
    }

    public Chunk getChunk() {
        return Bukkit.getWorld(world).getChunkAt(getX(), getZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastChunk fastChunk = (FastChunk) o;
        return key == fastChunk.key && world.equals(fastChunk.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, key);
    }
}
