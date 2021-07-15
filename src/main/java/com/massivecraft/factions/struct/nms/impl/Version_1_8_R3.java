package com.massivecraft.factions.struct.nms.impl;

import com.massivecraft.factions.struct.nms.NMSManager;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class Version_1_8_R3 implements NMSManager {

    @Override
    public void setBlock(World world, int x, int y, int z, int id, byte data) {
        if (y > 255) return;

        net.minecraft.server.v1_8_R3.World w = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_8_R3.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
        BlockPosition bp = new BlockPosition(x, y, z);

        IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(id + (data << 12));

        ChunkSection chunksection = chunk.getSections()[bp.getY() >> 4];

        if (chunksection == null)
            chunksection = chunk.getSections()[bp.getY() >> 4] = new ChunkSection(bp.getY() >> 4 << 4, !chunk.getWorld().worldProvider.o());

        chunksection.setType(bp.getX() & 15, bp.getY() & 15, bp.getZ() & 15, ibd);

        w.notify(bp);
    }
}
