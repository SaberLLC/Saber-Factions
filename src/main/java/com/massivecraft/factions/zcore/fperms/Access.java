package com.massivecraft.factions.zcore.fperms;

import com.massivecraft.factions.FactionsPlugin;

public enum Access {

    /**
     * @author Illyria Team
     */

    ALLOW("Allow"),
    DENY("Deny"),
    UNDEFINED("Undefined");

    public static final Access[] VALUES = values();

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
        for (Access access : VALUES)
            if (access.name().equalsIgnoreCase(check))
                return access;
        return null;
    }

    public static Access parse(Boolean value) {
        return value != null ? value ? ALLOW : DENY : UNDEFINED;
    }

    public String getInlinedName(Access access) {
        switch (access) {
            case ALLOW:
                return "Granted";
            case DENY:
                return "Denied";
            case UNDEFINED:
                return "Undefined";
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public String getNameLowercase() {
        return this.name.toLowerCase();
    }

    @Override
    public String toString() {
        return name();
    }

    public String getColor() {
        return FactionsPlugin.getInstance().getFileManager().getFperms().getConfig().getString("fperm-gui.action.Access-Colors." + this.name);
    }
}
