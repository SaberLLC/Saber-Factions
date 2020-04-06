package com.massivecraft.factions.integration;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.Associables;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 *  WorldGuard Permission Checking.
 *  https://github.com/elBukkit/MagicPlugin/blob/master/Magic/src/main/java/com/elmakers/mine/bukkit/protection/WorldGuardAPI.java
 *  Original Authors: NathonWolf, killme
 *  Converted & Adapted: Savag3life
 */

public class Worldguard {

    private static Worldguard instance;
    private Object worldGuard;
    private WorldGuardPlugin worldGuardPlugin;
    private Object regionContainer;
    private Method regionContainerGetMethod;
    private Method createQueryMethod;
    private Method regionQueryTestStateMethod;
    private Method locationAdaptMethod;
    private Method worldAdaptMethod;
    private Method regionManagerGetMethod;
    private Constructor<?> vectorConstructor;
    private Method vectorConstructorAsAMethodBecauseWhyNot;
    private StateFlag buildFlag;
    private StateFlag breakFlag;
    private boolean initialized = false;

    public Worldguard() {
        instance = this;

        Plugin p = Bukkit.getPluginManager().getPlugin("WorldGuard");

        if (p == null) {
            FactionsPlugin.getInstance().log("Could not find WorldGuard! Support will not be added.");
            return;
        }
        if (p instanceof WorldGuardPlugin) {
            worldGuardPlugin = (WorldGuardPlugin) p;

            try {
                Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
                Method getInstanceMethod = worldGuardClass.getMethod("getInstance");
                worldGuard = getInstanceMethod.invoke(null);
                FactionsPlugin.getInstance().log("Found WorldGuard 7+");
            } catch (Exception ex) {
                FactionsPlugin.getInstance().log("Found WorldGuard <7");
            }
        }
    }

    public static Worldguard getInstance() {
        return instance;
    }

    public boolean isEnabled() {
        return worldGuardPlugin != null;
    }

    protected RegionAssociable getAssociable(Player player) {
        RegionAssociable associable;
        if (player == null) {
            associable = Associables.constant(Association.NON_MEMBER);
        } else {
            associable = worldGuardPlugin.wrapPlayer(player);
        }

        return associable;
    }

    /**
     * Credits to the people listed above for using reflections to load WorldGuard 6.0 & 7.0 in 1 class!
     */
    private void initialize() {
        if (!initialized) {
            initialized = true;
            if (worldGuard != null) {
                try {
                    Method getPlatFormMethod = worldGuard.getClass().getMethod("getPlatform");
                    Object platform = getPlatFormMethod.invoke(worldGuard);
                    Method getRegionContainerMethod = platform.getClass().getMethod("getRegionContainer");
                    regionContainer = getRegionContainerMethod.invoke(platform);
                    createQueryMethod = regionContainer.getClass().getMethod("createQuery");
                    Class<?> worldEditLocationClass = Class.forName("com.sk89q.worldedit.util.Location");
                    Class<?> worldEditWorldClass = Class.forName("com.sk89q.worldedit.world.World");
                    Class<?> worldEditAdapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
                    worldAdaptMethod = worldEditAdapterClass.getMethod("adapt", World.class);
                    locationAdaptMethod = worldEditAdapterClass.getMethod("adapt", Location.class);
                    regionContainerGetMethod = regionContainer.getClass().getMethod("get", worldEditWorldClass);
                    Class<?> regionQueryClass = Class.forName("com.sk89q.worldguard.protection.regions.RegionQuery");
                    regionQueryTestStateMethod = regionQueryClass.getMethod("testState", worldEditLocationClass, RegionAssociable.class, StateFlag[].class);

                    Class<?> flagsClass = Class.forName("com.sk89q.worldguard.protection.flags.Flags");

                    buildFlag = (StateFlag) flagsClass.getField("BUILD").get(null);
                    breakFlag = (StateFlag) flagsClass.getField("BREAK").get(null);

                } catch (Exception ex) {
                    FactionsPlugin.getInstance().log("We failed to load some part of World Guard. Support will be removed!");
                    FactionsPlugin.getInstance().log("WorldGuard 7.0.0 support is currently in BETA. Please be careful!");
                    regionContainer = null;
                    return;
                }
            } else {
                regionContainer = worldGuardPlugin.getRegionContainer();
                try {
                    createQueryMethod = regionContainer.getClass().getMethod("createQuery");
                    regionContainerGetMethod = regionContainer.getClass().getMethod("get", World.class);
                    Class<?> regionQueryClass = Class.forName("com.sk89q.worldguard.bukkit.RegionQuery");
                    regionQueryTestStateMethod = regionQueryClass.getMethod("testState", Location.class, RegionAssociable.class, StateFlag[].class);

                    Class<?> flagsClass = Class.forName("com.sk89q.worldguard.protection.flags.DefaultFlag");

                    buildFlag = (StateFlag) flagsClass.getField("BUILD").get(null);
                    breakFlag = (StateFlag) flagsClass.getField("BREAK").get(null);

                } catch (Exception ex) {
                    FactionsPlugin.getInstance().log("We failed to load some part of World Guard. Support will be removed!");
                    FactionsPlugin.getInstance().log("WorldGuard 7.0.0 support is currently in BETA. Please be careful!");
                    regionContainer = null;
                    return;
                }
            }

            try {
                Class<?> vectorClass = Class.forName("com.sk89q.worldedit.Vector");
                vectorConstructor = vectorClass.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE);
                regionManagerGetMethod = RegionManager.class.getMethod("getApplicableRegions", vectorClass);
            } catch (Exception ex) {
                try {
                    Class<?> vectorClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
                    vectorConstructorAsAMethodBecauseWhyNot = vectorClass.getMethod("at", Double.TYPE, Double.TYPE, Double.TYPE);
                    regionManagerGetMethod = RegionManager.class.getMethod("getApplicableRegions", vectorClass);
                } catch (Exception sodonewiththis) {
                    FactionsPlugin.getInstance().log("We failed to load Vector Classes from WorldGuard! Support will be removed!");
                    FactionsPlugin.getInstance().log("WorldGuard 7.0.0 support is currently in BETA. Please be careful!");
                    regionContainer = null;
                    return;
                }
            }
        }
    }

    private RegionManager getRegionManager(World world) {
        initialize();
        if (regionContainer == null || regionContainerGetMethod == null) return null;
        RegionManager regionManager = null;
        try {
            if (worldAdaptMethod != null) {
                Object worldEditWorld = worldAdaptMethod.invoke(null, world);
                regionManager = (RegionManager) regionContainerGetMethod.invoke(regionContainer, worldEditWorld);
            } else {
                regionManager = (RegionManager) regionContainerGetMethod.invoke(regionContainer, world);
            }
        } catch (Exception ex) {
            FactionsPlugin.getInstance().log("An error occurred looking up a WorldGuard RegionManager");
        }
        return regionManager;
    }

    private ApplicableRegionSet getRegionSet(Location location) {
        RegionManager regionManager = getRegionManager(location.getWorld());
        if (regionManager == null) return null;
        try {
            Object vector = vectorConstructorAsAMethodBecauseWhyNot == null
                    ? vectorConstructor.newInstance(location.getX(), location.getY(), location.getZ())
                    : vectorConstructorAsAMethodBecauseWhyNot.invoke(null, location.getX(), location.getY(), location.getZ());
            return (ApplicableRegionSet) regionManagerGetMethod.invoke(regionManager, vector);
        } catch (Exception ex) {
            FactionsPlugin.getInstance().log("An error occurred looking up a WorldGuard ApplicableRegionSet");
            FactionsPlugin.getInstance().log("WorldGuard 7.0.0 support is currently in BETA. Please be careful!");
        }
        return null;
    }


    /**
     * Used to check WorldGuard to see if a Player has permission to place a block.
     *
     * @param player   player in question.
     * @param location Location of block placed.
     * @return
     */
    public boolean hasBuildPermission(Player player, Location location) {
        initialize();
        if (createQueryMethod != null && regionContainer != null) {
            try {
                Object query = createQueryMethod.invoke(regionContainer);
                if (locationAdaptMethod != null) {
                    Object loc = locationAdaptMethod.invoke(null, location);
                    return (boolean) regionQueryTestStateMethod.invoke(query, loc, getAssociable(player), new StateFlag[]{buildFlag});
                } else
                    return (boolean) regionQueryTestStateMethod.invoke(query, location, getAssociable(player), new StateFlag[]{buildFlag});
            } catch (Exception ex) {
                FactionsPlugin.getInstance().log("An error occurred querying WorldGuard! Report this issue to SF Developers!");
                FactionsPlugin.getInstance().log("WorldGuard 7.0.0 support is currently in BETA. Please be careful!");
            }
        }
        return true;
    }

    /**
     * Used to check WorldGuard to see if a player has permission to break a block.
     *
     * @param player   player in question.
     * @param location Location of block broken.
     * @return
     */
    public boolean hasBreakPermission(Player player, Location location) {
        initialize();
        if (createQueryMethod != null && regionContainer != null) {
            try {
                Object query = createQueryMethod.invoke(regionContainer);
                if (locationAdaptMethod != null) {
                    Object loc = locationAdaptMethod.invoke(null, location);
                    return (boolean) regionQueryTestStateMethod.invoke(query, loc, getAssociable(player), new StateFlag[]{breakFlag});
                } else
                    return (boolean) regionQueryTestStateMethod.invoke(query, location, getAssociable(player), new StateFlag[]{breakFlag});

            } catch (Exception ex) {
                FactionsPlugin.getInstance().log("An error occurred querying WorldGuard! Report this issue to SF Developers!");
                FactionsPlugin.getInstance().log("WorldGuard 7.0.0 support is currently in BETA. Please be careful!");
            }
        }
        return true;
    }

    public boolean checkForRegionsInChunk(FLocation floc) {
        return checkForRegionsInChunk(floc.getChunk());
    }

    /**
     * Used for checking if regions are located in a chunk
     *
     * @param chunk Chunk in question.
     * @return
     */
    public boolean checkForRegionsInChunk(Chunk chunk) {
        initialize();
        if (createQueryMethod != null && regionContainer != null) {
            try {
                World world = chunk.getWorld();
                int minChunkX = chunk.getX() << 4;
                int minChunkZ = chunk.getZ() << 4;
                int maxChunkX = minChunkX + 15;
                int maxChunkZ = minChunkZ + 15;

                int worldHeight = world.getMaxHeight(); // Allow for heights other than default

                BlockVector minChunk = new BlockVector(minChunkX, 0, minChunkZ);
                BlockVector maxChunk = new BlockVector(maxChunkX, worldHeight, maxChunkZ);

                ProtectedCuboidRegion region = new ProtectedCuboidRegion("wgfactionoverlapcheck", minChunk, maxChunk);

                Collection<ProtectedRegion> allregionslist = new ArrayList<>(getRegionManager(world).getRegions().values());
                List<ProtectedRegion> overlaps = region.getIntersectingRegions(allregionslist);

                return overlaps != null && !overlaps.isEmpty();
            } catch (Exception ex) {
                FactionsPlugin.getInstance().log("An error occurred querying WorldGuard! Report this issue to SF Developers!");
                FactionsPlugin.getInstance().log("WorldGuard 7.0.0 support is currently in BETA. Please be careful!");
            }
        }
        return false;
    }

    /**
     * General check for WorldGuard region @ location.
     *
     * @param player   player in question.
     * @param location Location of block broken.
     * @return
     */
    public boolean playerCanBuild(Player player, Location location) {
        return hasBuildPermission(player, location) && hasBreakPermission(player, location);
    }
}