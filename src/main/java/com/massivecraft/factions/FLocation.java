package com.massivecraft.factions;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.massivecraft.factions.util.FastChunk;
import com.massivecraft.factions.util.WorldUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public final class FLocation implements Serializable {

    private static final Map<String, LoadingCache<Long, FLocation>> CACHE = new HashMap<>(Bukkit.getWorlds().size());

    private static final long serialVersionUID = -8292915234027387983L;
    private static boolean WORLD_BORDER_SUPPORT;

    private final String world;
    private final int x;
    private final int z;

    private String formatted = null;

    static {
        try {
            Class.forName("org.bukkit.WorldBorder");
            WORLD_BORDER_SUPPORT = true;
        } catch (ClassNotFoundException ignored) {
            WORLD_BORDER_SUPPORT = false;
        }
    }

    public FLocation() {
        this.world = "";
        this.x = 0;
        this.z = 0;
    }

    public static FLocation empty() {
        return new FLocation();
    }

    public static FLocation wrap(String world, int x, int z) {
        try {
            return CACHE.computeIfAbsent(world, key ->
                    CacheBuilder.newBuilder()
                            .maximumSize(1000) //needs experimenting
                            .weakValues()
                            .expireAfterAccess(5, TimeUnit.MINUTES)
                            .build(new CacheLoader<Long, FLocation>() {
                                @ParametersAreNonnullByDefault
                                @Override
                                public @NotNull FLocation load(Long key) {
                                    return new FLocation(world, (int) key.longValue(), (int) (key >> 32));
                                }
                            })
            ).get((long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32);
        } catch (ExecutionException e) {
            return new FLocation(world, x, z);
        }
    }

    public static FLocation wrap(Chunk chunk) {
        return wrap(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public static FLocation wrap(Location location) {
        return wrap(location.getWorld().getName(), WorldUtil.blockToChunk(location.getBlockX()), WorldUtil.blockToChunk(location.getBlockZ()));
    }

    public static FLocation wrap(Block block) {
        return wrap(block.getLocation());
    }

    public static FLocation wrap(Player player) {
        return wrap(player.getLocation());
    }

    public static FLocation wrap(FPlayer fPlayer) {
        return wrap(fPlayer.getPlayer());
    }

    @Deprecated
    public FLocation(String world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    @Deprecated
    public FLocation(Location location) {
        this(location.getWorld().getName(), WorldUtil.blockToChunk(location.getBlockX()), WorldUtil.blockToChunk(location.getBlockZ()));
    }

    @Deprecated
    public FLocation(Player player) {
        this(player.getLocation());
    }

    @Deprecated
    public FLocation(FPlayer fplayer) {
        this(fplayer.getPlayer());
    }

    @Deprecated
    public FLocation(Block block) {
        this(block.getLocation());
    }

    public World getWorld() {
        return Bukkit.getWorld(this.world);
    }

    public String getWorldName() {
        return this.world;
    }

    @Deprecated
    public long getX() {
        return this.x;
    }

    @Deprecated
    public long getZ() {
        return this.z;
    }

    public int getIntX() {
        return this.x;
    }

    public int getIntZ() {
        return this.z;
    }

    public String getCoordString() {
        return this.formatted == null ? this.formatted = this.x + "," + this.z : this.formatted;
    }

    public String formatXAndZ(String splitter) {
        return WorldUtil.chunkToBlock(this.x) + "x" + splitter + " " + WorldUtil.chunkToBlock(this.z) + "z";
    }

    public Chunk getChunk() {
        return getWorld().getChunkAt(this.x, this.z);
    }

    public FastChunk toFastChunk() {
        return new FastChunk(this);
    }

    public int toBlockX() {
        return WorldUtil.chunkToBlock(this.x);
    }

    public int toBlockZ() {
        return WorldUtil.chunkToBlock(this.z);
    }

    public FLocation getRelative(int dx, int dz) {
        return wrap(this.world, this.x + dx, this.z + dz);
    }

    public FLocation getRelativeWorldName(String worldName, int dx, int dz) {
        return wrap(worldName, this.x + dx, this.z + dz);
    }

    public double getDistanceTo(FLocation that) {
        return Math.sqrt(getDistanceSquaredTo(that));
    }

    public double getDistanceSquaredTo(FLocation that) {
        return getDistanceSquaredTo(that.x, that.z);
    }

    public double getDistanceSquaredTo(int thatx, int thatz) {
        double dx = thatx - this.x;
        double dz = thatz - this.z;
        return dx * dx + dz * dz;
    }

    public boolean isInChunk(Location loc) {
        return loc != null && (loc.getWorld().getName().equals(getWorldName()) && WorldUtil.blockToChunk(loc.getBlockX()) == this.x && WorldUtil.blockToChunk(loc.getBlockZ()) == this.z);
    }


    public static boolean isOutsideWorldBorder(World world, int x, int z, int buffer) {
        if (!WORLD_BORDER_SUPPORT || world.getWorldBorder().getSize() == 0) {
            return false;
        }

        WorldBorder border = world.getWorldBorder();
        Location center = border.getCenter();
        double size = border.getSize() / 2.0D;
        int bufferBlocks = buffer << 4;
        double borderMinX = center.getX() - size + bufferBlocks;
        double borderMinZ = center.getZ() - size + bufferBlocks;
        double borderMaxX = center.getX() + size - bufferBlocks;
        double borderMaxZ = center.getZ() + size - bufferBlocks;

        int chunkMinX = WorldUtil.chunkToBlock(x);
        int chunkMinZ = WorldUtil.chunkToBlock(z);
        int chunkMaxX = chunkMinX | 15;
        int chunkMaxZ = chunkMinZ | 15;

        return chunkMinX >= borderMaxX || chunkMinZ >= borderMaxZ || chunkMaxX <= borderMinX || chunkMaxZ <= borderMinZ;
    }

    public boolean isOutsideWorldBorder(World world, int buffer) {
        return isOutsideWorldBorder(world, this.x, this.z, buffer);
    }

    public boolean isOutsideWorldBorder(int buffer) {
        return isOutsideWorldBorder(getWorld(), buffer);
    }

    @Override
    public String toString() {
        return "[" + getWorldName() + "," + getCoordString() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FLocation fLocation = (FLocation) o;
        return x == fLocation.x &&
                z == fLocation.z &&
                Objects.equals(this.world, fLocation.world);
    }

    @Override
    public int hashCode() {
        return (this.x << 9) ^ this.z + (this.world != null ? this.world.hashCode() : 0);
    }

    public boolean is(Location bukkit) {
        return WorldUtil.blockToChunk(bukkit.getBlockX()) == this.x && WorldUtil.blockToChunk(bukkit.getBlockZ()) == this.z && bukkit.getWorld().getName().equals(this.world);
    }

    public long toKey() {
        return WorldUtil.encodeChunk(this.x, this.z);
    }
}