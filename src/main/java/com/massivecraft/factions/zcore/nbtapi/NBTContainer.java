package com.massivecraft.factions.zcore.nbtapi;

public class NBTContainer extends NBTCompound {

    private Object nbt;

    public NBTContainer() {
        this(NBTReflectionUtil.getNewNBTTag());
    }

    protected NBTContainer(Object nbt) {
        this.nbt = nbt;
    }

    public NBTContainer(String nbtString) throws IllegalArgumentException {
        try {
            nbt = NBTReflectionUtil.parseNBT(nbtString);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Malformed Json: " + ex.getMessage());
        }
    }

    protected Object getCompound() {
        return nbt;
    }

    protected void setCompound(Object tag) {
        nbt = tag;
    }

}
