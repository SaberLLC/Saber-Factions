package com.massivecraft.factions.zcore.fperms;

import com.massivecraft.factions.FactionsPlugin;

public enum Access {
    ALLOW("Allow"),
    UNDEFINED("Undefined"),
    DENY("Deny");

    private final String name;

    Access(String name) {
        this.name = name;
    }

    /**
     * Case insensitive check for access.
     *
     * @param check
     * @return
     */
    public static Access fromString(String check) {
        for (Access access : values())
            if (access.name().equalsIgnoreCase(check))
                return access;
        return null;
    }

    public String getName() {
        return this.name.toLowerCase();
    }

    @Override
    public String toString() {
        return name();
    }

    public String getColor() {
        return FactionsPlugin.getInstance().getConfig().getString("fperm-gui.action.Access-Colors." + this.name);
    }
}
