package com.massivecraft.factions.cmd.audit;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.XMaterial;
import org.bukkit.Material;

/**
 * @author Saser
 */
public enum FLogType {

    INVITES("Roster Edits", "&e%s&7 &a%s&7 &e%s", 3),
    BANS("Player Bans", "&e%s&7 &e%s&6 &e%s", 3),
    CHUNK_CLAIMS("Claim Edits", "&e%s&7 %s&7 &e%s&7 near &e%s", 3),
    PERM_EDIT_DEFAULTS("Default Perm Edits", "&e%s&7 %s&7 %s for &e%s", 4),
    BANK_EDIT("Money Edits", "&e%s&7 %s &e&l$&e%s", 3),
    FCHEST_EDIT("Chest Edits", "&e%s&7 %s &f%s", 3),
    RELATION_CHANGE("Relation Edits", "&e%s %s&e'd %s", 3),
    FTAG_EDIT("Tag Edits", "&e%s&7 set to &e'%s'", 2),
    FDESC_EDIT("Desc Edits", "&e%s&7 set to &e'%s'", 2),
    ROLE_PERM_EDIT("Promotional Edits", "&e%s&7&e %s &e%s &7to &e%s", 4),
    SPAWNER_EDIT("Spawner Edits", "&e%s&7 %s &e%s&7 %s", 4),
    RANK_EDIT("Rank Edits", "&e%s&7 set &e%s&7 to %s", 3),
    F_TNT("Tnt Edits", "&e%s&7 %s &e%s", 3);

    private String displayName;
    private String msg;
    private int requiredArgs;

    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Case insensitive check for action.
     *
     * @param check
     * @return - action
     */
    public static FLogType fromString(String check) {
        for (FLogType fLogType : values()) {
            if (fLogType.displayName.equalsIgnoreCase(check)) {
                return fLogType;
            }
        }
        return null;
    }

    @Override
    public String toString() { return name(); }

    public int getSlot() { return FactionsPlugin.getInstance().getConfig().getInt("faudit-gui.slots." + name().toLowerCase()); }

    public Material getMaterial(){
        return XMaterial.matchXMaterial(FactionsPlugin.getInstance().getConfig().getString("faudit-gui.materials." + name().toLowerCase())).parseMaterial();
    }


    public String getMsg() {
        return this.msg;
    }

    public int getRequiredArgs() {
        return this.requiredArgs;
    }

    FLogType(String displayName, String msg, int requiredArgs) {
        this.displayName = displayName;
        this.msg = msg;
        this.requiredArgs = requiredArgs;
    }
}
