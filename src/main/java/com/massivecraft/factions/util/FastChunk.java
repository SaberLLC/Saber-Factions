package com.massivecraft.factions.util;

import com.massivecraft.factions.FLocation;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Objects;

public class FastChunk {

    private String worldName;
    private int x, z;
    private long timeClaimed;


    public FastChunk(String worldName, int x, int z) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
    }

    public FastChunk(String worldName, FLocation floc) {
        this.worldName = worldName;
        this.x = floc.getChunk().getX();
        this.z = floc.getChunk().getZ();
    }

    public FastChunk(FLocation floc) {
        this.worldName = floc.getWorldName();
        this.x = floc.getChunk().getX();
        this.z = floc.getChunk().getZ();
    }

    public FastChunk(FLocation fLoc, Long timeClaimed) {
        this.worldName = fLoc.getWorldName();
        this.timeClaimed = timeClaimed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastChunk fastChunk = (FastChunk) o;
        return x == fastChunk.x &&
                z == fastChunk.z &&
                worldName.equals(fastChunk.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, x, z);
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public long getTimeClaimed() {
        return timeClaimed;
    }

    public Chunk getChunk() {
        return Bukkit.getWorld(worldName).getChunkAt(x, z);
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }
}
