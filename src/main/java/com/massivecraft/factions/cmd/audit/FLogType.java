package com.massivecraft.factions.cmd.audit;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.CC;
import org.bukkit.Material;

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

    private String msg;
    private int requiredArgs;

    FLogType(String msg, int requiredArgs) {
        this.msg = msg;
        this.requiredArgs = requiredArgs;
    }

    public String getDisplayName() {
        return CC.translate(FactionsPlugin.getInstance().getConfig().getString("faudit-gui.names." + name().toLowerCase()));
    }

    @Override
    public String toString() {
        return name();
    }

    public int getSlot() {
        return FactionsPlugin.getInstance().getConfig().getInt("faudit-gui.slots." + name().toLowerCase());
    }

    public Material getMaterial() {
        return XMaterial.matchXMaterial(FactionsPlugin.getInstance().getConfig().getString("faudit-gui.materials." + name().toLowerCase())).get().parseMaterial();
    }

    public String getMsg() {
        return this.msg;
    }

    public int getRequiredArgs() {
        return this.requiredArgs;
    }
}
