package com.massivecraft.factions.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface FactionScheduler {

    // Entity-bound — runs on the entity's owning region thread (Folia) / main thread (Bukkit)
    // retired: called if the entity is removed before the task fires; pass null if unused
    FactionTask runEntity(Entity entity, Runnable task, Runnable retired);
    FactionTask runEntityLater(Entity entity, long delayTicks, Runnable task, Runnable retired);

    // Location/chunk-bound — runs on the chunk's owning region thread (Folia) / main thread (Bukkit)
    FactionTask runRegion(Location loc, Runnable task);
    FactionTask runRegionLater(Location loc, long delayTicks, Runnable task);
    FactionTask runRegionRepeating(Location loc, long delayTicks, long periodTicks, Runnable task);

    // Global sync — server-wide, not bound to any world or entity
    FactionTask runGlobal(Runnable task);
    FactionTask runGlobalLater(long delayTicks, Runnable task);
    FactionTask runGlobalRepeating(long delayTicks, long periodTicks, Runnable task);

    // Async — identical on both platforms
    FactionTask runAsync(Runnable task);
    FactionTask runAsyncLater(long delayTicks, Runnable task);
    FactionTask runAsyncRepeating(long delayTicks, long periodTicks, Runnable task);

    // Lifecycle
    void cancelAll();
}
