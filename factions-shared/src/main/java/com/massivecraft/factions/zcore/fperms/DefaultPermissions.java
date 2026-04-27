package com.massivecraft.factions.zcore.fperms;

import java.util.HashMap;
import java.util.Map;

public class DefaultPermissions {

    private final Map<String, Boolean> permissions = new HashMap<>();
    private Boolean defaultValue = null;
    private boolean legacyMigrated = false;

    // Legacy fields for backward compatibility with existing configs.
    private Boolean ban;
    private Boolean build;
    private Boolean destroy;
    private Boolean frostwalk;
    private Boolean painbuild;
    private Boolean door;
    private Boolean button;
    private Boolean lever;
    private Boolean container;
    private Boolean invite;
    private Boolean kick;
    private Boolean items;
    private Boolean sethome;
    private Boolean territory;
    private Boolean home;
    private Boolean disband;
    private Boolean promote;
    private Boolean setwarp;
    private Boolean warp;
    private Boolean fly;
    private Boolean tntbank;
    private Boolean tntfill;
    private Boolean withdraw;
    private Boolean chest;
    private Boolean audit;
    private Boolean check;
    private Boolean drain;
    private Boolean spawner;

    public DefaultPermissions() {
    }

    public DefaultPermissions(boolean def) {
        this.defaultValue = def;
    }

    public void setDefaultValue(Boolean value) {
        this.defaultValue = value;
    }

    public Boolean getDefaultValue() {
        return defaultValue;
    }

    public void set(String actionId, boolean value) {
        migrateLegacyIfNeeded();
        String id = FPerms.normalizeId(actionId);
        if (id != null) {
            permissions.put(id, value);
        }
    }

    public Boolean get(String actionId) {
        migrateLegacyIfNeeded();
        String id = FPerms.normalizeId(actionId);
        return id != null ? permissions.get(id) : null;
    }

    public boolean getById(String actionId, boolean fallback) {
        migrateLegacyIfNeeded();
        Boolean value = get(actionId);
        if (value != null) {
            return value;
        }
        if (defaultValue != null) {
            return defaultValue;
        }
        return fallback;
    }

    public Map<String, Boolean> getPermissions() {
        migrateLegacyIfNeeded();
        return permissions;
    }

    private void migrateLegacyIfNeeded() {
        if (legacyMigrated) return;
        legacyMigrated = true;

        migrateLegacy("ban", ban);
        migrateLegacy("build", build);
        migrateLegacy("destroy", destroy);
        migrateLegacy("frostwalk", frostwalk);
        migrateLegacy("painbuild", painbuild);
        migrateLegacy("door", door);
        migrateLegacy("button", button);
        migrateLegacy("lever", lever);
        migrateLegacy("container", container);
        migrateLegacy("invite", invite);
        migrateLegacy("kick", kick);
        migrateLegacy("items", items);
        migrateLegacy("sethome", sethome);
        migrateLegacy("territory", territory);
        migrateLegacy("home", home);
        migrateLegacy("disband", disband);
        migrateLegacy("promote", promote);
        migrateLegacy("setwarp", setwarp);
        migrateLegacy("warp", warp);
        migrateLegacy("fly", fly);
        migrateLegacy("tntbank", tntbank);
        migrateLegacy("tntfill", tntfill);
        migrateLegacy("withdraw", withdraw);
        migrateLegacy("chest", chest);
        migrateLegacy("audit", audit);
        migrateLegacy("check", check);
        migrateLegacy("drain", drain);
        migrateLegacy("spawner", spawner);
    }

    private void migrateLegacy(String id, Boolean value) {
        if (value != null) {
            permissions.put(FPerms.normalizeId(id), value);
        }
    }
}
