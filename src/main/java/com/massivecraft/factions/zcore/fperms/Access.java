package com.massivecraft.factions.zcore.fperms;

import com.massivecraft.factions.FactionsPlugin;

public enum Access {

    /**
     * @author Illyria Team
     */

    ALLOW("Allow"),
    DENY("Deny"),
    UNDEFINED("Undefined");

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

    public static Access booleanToAccess(boolean access) {
        if (access) return Access.ALLOW;
        else return Access.DENY;
    }

    public String getInlinedName(Access access) {
        if(access == Access.ALLOW) {
            return "Granted";
        } else if(access == Access.UNDEFINED) {
            return "Undefined";
        } else if(access == Access.DENY) {
            return "Denied";
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
