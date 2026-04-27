package com.massivecraft.factions.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.io.Serializable;

/*
 * This class provides a lazy-load Location, so that World doesn't need to be initialized
 * yet when an object of this class is created, only when the Location is first accessed.
 */

public final class LazyLocation implements Serializable {
    private static final long serialVersionUID = -6049901271320963314L;
    private transient Location location = null;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public LazyLocation(Location loc) {
        this(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public LazyLocation(String worldName, double x, double y, double z) {
        this(worldName, x, y, z, 0, 0);
    }

    public LazyLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    @Nullable
    public Location getLocation() {
        if (this.location == null) {
            World world = Bukkit.getWorld(this.worldName);
            if (world == null) {
                return null;
            }
            this.location = new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
        }
        return this.location;
    }

    // change the Location
    public void setLocation(Location loc) {
        this.location = loc;
        this.worldName = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    public String getWorldName() {
        return this.worldName;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getPitch() {
        return this.pitch;
    }

    public double getYaw() {
        return this.yaw;
    }
}
