package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class WorldUtil {

    private WorldUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    private static boolean isParticipating(String name) {
        return Conf.useWorldConfigurationsAsWhitelist == Conf.worldsNoFactionsPlugin.contains(name);
    }

    public static boolean isParticipating(World world) {
        return isParticipating(world.getName());
    }

    public static boolean isParticipating(CommandSender sender) {
        if (sender instanceof Player) {
            return isParticipating(((Player) sender).getWorld().getName());
        }
        return true;
    }

    public static long encodeChunk(int x, int z) {
        return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
    }

    public static int blockToChunk(int block) {
        return block >> 4;
    }

    public static int chunkToBlock(int chunk) {
        return chunk << 4;
    }
}