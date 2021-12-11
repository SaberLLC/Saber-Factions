package com.massivecraft.factions.util;

import com.massivecraft.factions.FLocation;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.util.Objects;

public class FastChunk {

    private String world;
    private int x, z;

    public FastChunk(String world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public FastChunk(String world, FLocation floc) {
        this.world = world;
        this.x = (int) floc.getX();
        this.z = (int) floc.getZ();
    }

    public FastChunk(FLocation floc) {
        this.world = floc.getWorld().getName();
        this.x = (int) floc.getX();
        this.z = (int) floc.getZ();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastChunk fastChunk = (FastChunk) o;
        return x == fastChunk.x &&
                z == fastChunk.z &&
                world.equals(fastChunk.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, z);
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public Chunk getChunk() {
        return Bukkit.getWorld(world).getChunkAt(x, z);
    }
}
