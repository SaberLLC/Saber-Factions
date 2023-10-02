package com.massivecraft.factions.cmd.audit;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;

/**
 * @author Saser
 */
public enum FLogType {

    INVITES("&e%s&7 &a%s&7 &e%s", 3),
    BANS("&e%s&7 &e%s&6 &e%s", 3),
    CHUNK_CLAIMS("&e%s&7 %s&7 &e%s&7 near &e%s", 3),
    PERM_EDIT_DEFAULTS("&e%s&7 %s&7 %s for &e%s", 4),
    BANK_EDIT("&e%s&7 %s &e&l$&e%s", 3),
    FCHEST_EDIT("&e%s&7 %s &f%s", 3),
    RELATION_CHANGE("&e%s %s&e'd %s", 3),
    FTAG_EDIT("&e%s&7 set to &e'%s'", 2),
    FDESC_EDIT("&e%s&7 set to &e'%s'", 2),
    ROLE_PERM_EDIT("&e%s&7&e %s &e%s &7to &e%s", 4),
    SPAWNER_EDIT("&e%s&7 %s &e%s&7 %s", 4),
    RANK_EDIT("&e%s&7 set &e%s&7 to %s", 3),
    F_TNT("&e%s&7 %s &e%s", 3);

    private final String msg;
    private final int requiredArgs;

    // Cached for better performance
    private static final Configuration CONFIG = FactionsPlugin.getInstance().getConfig();

    FLogType(String msg, int requiredArgs) {
        this.msg = msg;
        this.requiredArgs = requiredArgs;
    }

    private String getConfigString(String pathSuffix) {
        return CONFIG.getString("faudit-gui." + pathSuffix + "." + name().toLowerCase());
    }

    public String getDisplayName() {
        return CC.translate(getConfigString("names"));
    }

    @Override
    public String toString() {
        return name();
    }

    public int getSlot() {
        return CONFIG.getInt("faudit-gui.slots." + name().toLowerCase());
    }

    public Material getMaterial() {
        return XMaterial.matchXMaterial(getConfigString("materials")).get().parseMaterial();
    }

    public String getMsg() {
        return this.msg;
    }

    public int getRequiredArgs() {
        return this.requiredArgs;
    }
}