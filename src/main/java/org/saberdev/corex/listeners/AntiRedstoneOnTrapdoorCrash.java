package org.saberdev.corex.listeners;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.HashMap;

public class AntiRedstoneOnTrapdoorCrash implements Listener {

    private final HashMap<Location, Long> cooldowns = new HashMap<>();
    private final HashMap<Location, Integer> trapdoorPoweredByRedstoneCounts = new HashMap<>();

    public AntiRedstoneOnTrapdoorCrash() {
        FactionsPlugin plugin = FactionsPlugin.getInstance();
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            // Prevent memory leak
            if (!trapdoorPoweredByRedstoneCounts.isEmpty()) trapdoorPoweredByRedstoneCounts.clear();
            if (!cooldowns.isEmpty()) cooldowns.clear();
        }, 6000L, 6000L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onRedstonePowerTrapdoor(BlockRedstoneEvent event) {
        Block poweredBlock = event.getBlock();
        if (!poweredBlock.getType().name().contains("TRAPDOOR")) return;
        Block blockAbove = poweredBlock.getRelative(BlockFace.UP);
        if (!blockAbove.getType().name().equals("REDSTONE_WIRE") && !blockAbove.getType().name().equals("REDSTONE")) return;

        final Location trapdoorLoc = poweredBlock.getLocation();
        final long currentTime = System.currentTimeMillis();

        if (
                !trapdoorPoweredByRedstoneCounts.containsKey(trapdoorLoc)
                || !cooldowns.containsKey(trapdoorLoc)
        ) {
            trapdoorPoweredByRedstoneCounts.put(trapdoorLoc, 1);
            cooldowns.put(trapdoorLoc, currentTime);
            return;
        }

        int trapdoorOpenByRedstoneCount = trapdoorPoweredByRedstoneCounts.get(trapdoorLoc);

        if (trapdoorOpenByRedstoneCount >= 20) {
            if (currentTime - cooldowns.get(trapdoorLoc) < 3000) {
                blockAbove.breakNaturally();
                poweredBlock.breakNaturally();
                Logger.print("Prevented possible crash using trapdoors and redstone at: " + trapdoorLoc);
                return;
            }

            trapdoorOpenByRedstoneCount = 1;
        }

        trapdoorOpenByRedstoneCount++;

        trapdoorPoweredByRedstoneCounts.put(trapdoorLoc, trapdoorOpenByRedstoneCount);
        cooldowns.put(trapdoorLoc, currentTime);
    }
}
