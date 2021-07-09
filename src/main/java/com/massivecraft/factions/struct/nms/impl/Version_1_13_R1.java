package com.massivecraft.factions.struct.nms.impl;

import com.massivecraft.factions.struct.nms.NMSManager;
import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.IBlockData;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;

public class Version_1_13_R1 implements NMSManager {

    @Override
    public void setBlock(World world, int x, int y, int z, int id, byte data) {
        if (y > 255) return;

        net.minecraft.server.v1_13_R1.World w = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_13_R1.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
        BlockPosition bp = new BlockPosition(x, y, z);
        int combined = id + (data << 12);
        IBlockData ibd = net.minecraft.server.v1_13_R1.Block.getByCombinedId(combined);
        w.setTypeAndData(bp, ibd, 2);
        chunk.a(bp, ibd, false);
    }

}
