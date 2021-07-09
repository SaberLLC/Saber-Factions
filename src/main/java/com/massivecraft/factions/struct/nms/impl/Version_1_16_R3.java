package com.massivecraft.factions.struct.nms.impl;

import com.massivecraft.factions.struct.nms.NMSManager;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.Chunk;
import net.minecraft.server.v1_16_R3.IBlockData;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;

public class Version_1_16_R3 implements NMSManager {
    @Override
    public void setBlock(World world, int x, int y, int z, int id, byte data) {
        if (y > 255) return;
        net.minecraft.server.v1_16_R3.World w = ((CraftWorld) world).getHandle();
        Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
        BlockPosition bp = new BlockPosition(x, y, z);
        IBlockData ibd = CraftMagicNumbers.getBlock(org.bukkit.Material.values()[id], data);
        w.setTypeAndData(bp, ibd, 3);
        chunk.setType(bp, ibd, false);
    }
}
