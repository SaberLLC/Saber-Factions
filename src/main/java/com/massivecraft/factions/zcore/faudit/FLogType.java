package com.massivecraft.factions.zcore.faudit;

import com.massivecraft.factions.util.XMaterial;
import org.bukkit.Material;

public enum FLogType {

    INVITES("Roster Edits", XMaterial.WRITABLE_BOOK.parseMaterial(), "&e%s&7 &a%s&7 &e%s", 3),
    BANS("Player Bans", XMaterial.ANVIL.parseMaterial(), "&e%s&7 &e%s&6 &e%s", 3),
    CHUNK_CLAIMS("Claim Edits", XMaterial.WOODEN_AXE.parseMaterial(), "&e%s&7 %s&7 &e%s&7 near &e%s", 3),
    PERM_EDIT_DEFAULTS("Default Perm Edits", XMaterial.WRITTEN_BOOK.parseMaterial(), "&e%s&7 %s&7 %s for &e%s", 4),
    BANK_EDIT("/f money Edits", XMaterial.GOLD_INGOT.parseMaterial(), "&e%s&7 %s &e&l$&e%s", 3),
    FCHEST_EDIT("/f chest Edits", XMaterial.CHEST.parseMaterial(), "&e%s&7 %s &f%s", 3),
    RELATION_CHANGE("Relation Edits", XMaterial.GOLDEN_SWORD.parseMaterial(), "&e%s %s&e'd %s", 3),
    FTAG_EDIT("/f tag Edits", XMaterial.NAME_TAG.parseMaterial(), "&e%s&7 set to &e'%s'", 2),
    FDESC_EDIT("/f desc Edits", XMaterial.PAPER.parseMaterial(), "&e%s&7 set to &e'%s'", 2),
    ROLE_PERM_EDIT("/f promote Edits", XMaterial.WRITTEN_BOOK.parseMaterial(), "&e%s&7&e %s &e%s &7to &e%s", 4),
    SPAWNER_EDIT("Spawner Edits", XMaterial.SPAWNER.parseMaterial(), "&e%s&7 %s &e%s&7 %s", 4),
    RANK_EDIT("Rank Edits", XMaterial.GOLDEN_HELMET.parseMaterial(), "&e%s&7 set &e%s&7 to %s", 3),
    F_TNT("/f tnt Edits", XMaterial.TNT.parseMaterial(), "&e%s&7 %s &e%s", 3);

    private String displayName;
    private Material displayMaterial;
    private String msg;
    private int requiredArgs;

    public String getDisplayName() {
        return this.displayName;
    }

    public Material getDisplayMaterial() {
        return this.displayMaterial;
    }

    public String getMsg() {
        return this.msg;
    }

    public int getRequiredArgs() {
        return this.requiredArgs;
    }

    FLogType(String displayName, Material displayMaterial, String msg, int requiredArgs) {
        this.displayName = displayName;
        this.displayMaterial = displayMaterial;
        this.msg = msg;
        this.requiredArgs = requiredArgs;
    }
}
