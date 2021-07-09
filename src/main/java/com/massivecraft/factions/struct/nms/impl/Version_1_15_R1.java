package com.massivecraft.factions.struct.nms.impl;

import com.massivecraft.factions.struct.nms.NMSManager;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.IBlockData;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftMagicNumbers;

public class Version_1_15_R1 implements NMSManager {

    @Override
    public void setBlock(World world, int x, int y, int z, int id, byte data) {
        if (y > 255) return;
        net.minecraft.server.v1_15_R1.World w = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_15_R1.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
        BlockPosition bp = new BlockPosition(x, y, z);
        IBlockData ibd = CraftMagicNumbers.getBlock(org.bukkit.Material.values()[id], data);
        w.setTypeAndData(bp, ibd, 3);
        chunk.setType(bp, ibd, false);
    }
}
