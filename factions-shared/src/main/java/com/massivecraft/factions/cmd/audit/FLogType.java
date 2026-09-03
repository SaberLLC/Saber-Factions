package com.massivecraft.factions.cmd.audit;

import org.bukkit.Material;

import java.util.Locale;

public enum FLogType {

    INVITES("Roster Edits", Material.WRITABLE_BOOK, 10, "&e%s&7 &a%s&7 &e%s", 60, 7),
    BANS("Player Bans", Material.ANVIL, 11, "&e%s&7 &e%s&6 &e%s", 60, 7),
    CHUNK_CLAIMS("Claim Edits", Material.WOODEN_AXE, 12, "&e%s&7 %s&7 &e%s&7 near &e%s", 60, 7),
    PERM_EDIT_DEFAULTS("Default Perm Edits", Material.WRITTEN_BOOK, 13, "&e%s&7 %s&7 %s for &e%s", 60, 7),
    BANK_EDIT("Money Edits", Material.GOLD_INGOT, 14, "&e%s&7 %s &e&l$&e%s", 60, 7),
    FCHEST_EDIT("Chest Edits", Material.CHEST, 15, "&e%s&7 %s &f%s", 60, 7),
    RELATION_CHANGE("Relation Edits", Material.GOLDEN_SWORD, 16, "&e%s %s&e'd %s", 60, 7),
    FTAG_EDIT("Tag Edits", Material.NAME_TAG, 19, "&e%s&7 set to &e'%s'", 60, 7),
    FDESC_EDIT("Desc Edits", Material.PAPER, 20, "&e%s&7 set to &e'%s'", 60, 7),
    ROLE_PERM_EDIT("Promotional Edits", Material.WRITTEN_BOOK, 21, "&e%s&7 %s &e%s &7to &e%s", 60, 7),
    SPAWNER_EDIT("Spawner Edits", Material.SPAWNER, 22, "&e%s&7 %s &e%s&7 %s", 60, 7),
    RANK_EDIT("Rank Edits", Material.GOLDEN_HELMET, 23, "&e%s&7 set &e%s&7 to %s", 60, 7),
    F_TNT("TNT Edits", Material.TNT, 24, "&e%s&7 %s &e%s", 200, 7);

    private final String defaultDisplayName;
    private final Material defaultMaterial;
    private final int defaultSlot;
    private final String defaultFormat;
    private final int defaultMaxEntries;
    private final int defaultRetentionDays;

    FLogType(String defaultDisplayName, Material defaultMaterial, int defaultSlot, String defaultFormat, int defaultMaxEntries, int defaultRetentionDays) {
        this.defaultDisplayName = defaultDisplayName;
        this.defaultMaterial = defaultMaterial;
        this.defaultSlot = defaultSlot;
        this.defaultFormat = defaultFormat;
        this.defaultMaxEntries = defaultMaxEntries;
        this.defaultRetentionDays = defaultRetentionDays;
    }

    public String getKey() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public String getDefaultDisplayName() {
        return this.defaultDisplayName;
    }

    public Material getDefaultMaterial() {
        return this.defaultMaterial;
    }

    public String getDefaultMaterialName() {
        return this.defaultMaterial.name();
    }

    public int getDefaultSlot() {
        return this.defaultSlot;
    }

    public String getDefaultFormat() {
        return this.defaultFormat;
    }

    public int getDefaultMaxEntries() {
        return this.defaultMaxEntries;
    }

    public int getDefaultRetentionDays() {
        return this.defaultRetentionDays;
    }
}
