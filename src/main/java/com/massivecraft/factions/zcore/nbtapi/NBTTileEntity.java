package com.massivecraft.factions.zcore.nbtapi;

import org.bukkit.block.BlockState;

public class NBTTileEntity extends NBTCompound {

    private final BlockState tile;

    public NBTTileEntity(BlockState tile) {
        this.tile = tile;
    }

    protected Object getCompound() {
        return NBTReflectionUtil.getTileEntityNBTTagCompound(tile);
    }

    protected void setCompound(Object compound) {
        NBTReflectionUtil.setTileEntityNBTTagCompound(tile, compound);
    }

}
