package org.saberdev.corex.addons;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.saberdev.corex.CoreAddon;
import java.util.concurrent.TimeUnit;

import java.util.HashMap;
import java.util.Map;

@CoreAddon(configVariable = "")
public class AntiRedstoneOnTrapdoorCrash implements Listener {

    private final Map<Location, Long> cooldowns = new HashMap<>();
    private final Map<Location, Integer> trapdoorPoweredByRedstoneCounts = new HashMap<>();

    public AntiRedstoneOnTrapdoorCrash() {
        FactionsPlugin plugin = FactionsPlugin.getInstance();
        // 6000 ticks -> 300000 ms
        long delayMs = 6000L * 50L;
        long periodMs = 6000L * 50L;

        Bukkit.getAsyncScheduler().runAtFixedRate(
            plugin,
            scheduledTask -> {
                cooldowns.clear();
                trapdoorPoweredByRedstoneCounts.clear();
            },
            delayMs,      // initial delay in ms
            periodMs,     // repeat period in ms
            TimeUnit.MILLISECONDS
        );
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onRedstonePowerTrapdoor(BlockRedstoneEvent event) {
        Block poweredBlock = event.getBlock();
        if (!poweredBlock.getType().name().contains("TRAPDOOR")) {
            return;
        }

        Block blockAbove = poweredBlock.getRelative(BlockFace.UP);
        if (!blockAbove.getType().name().equals("REDSTONE_WIRE") && !blockAbove.getType().name().equals("REDSTONE")) {
            return;
        }

        Location trapdoorLoc = poweredBlock.getLocation();
        long currentTime = System.currentTimeMillis();

        trapdoorPoweredByRedstoneCounts.putIfAbsent(trapdoorLoc, 1);
        cooldowns.putIfAbsent(trapdoorLoc, currentTime);

        int trapdoorOpenByRedstoneCount = trapdoorPoweredByRedstoneCounts.get(trapdoorLoc);

        if (trapdoorOpenByRedstoneCount >= 20 && currentTime - cooldowns.get(trapdoorLoc) < 3000) {
            blockAbove.breakNaturally();
            poweredBlock.breakNaturally();
            Logger.print("Prevented possible crash using trapdoors and redstone at: " + trapdoorLoc);
            return;
        }

        trapdoorPoweredByRedstoneCounts.put(trapdoorLoc, trapdoorOpenByRedstoneCount + 1);
        cooldowns.put(trapdoorLoc, currentTime);
    }
}
