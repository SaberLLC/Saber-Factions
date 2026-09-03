package com.massivecraft.factions.struct;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FactionRole implements Permissable {

    private static transient final String FALLBACK_ROLE_NAME = "Role";
    private static transient final String RELATION_CONFIG_PATH = "fperm-gui.relation";
    private static transient final String PLACEHOLDER_ITEM_NAME = "Placeholder-Item.Name";
    private static transient final String LEGACY_PLACEHOLDER_ITEM_NAME = "placeholder-item.name";
    private static transient final String PLACEHOLDER_ITEM_LORE = "Placeholder-Item.Lore";
    private static transient final String LEGACY_PLACEHOLDER_ITEM_LORE = "placeholder-item.lore";
    private static transient final String DEFAULT_MATERIAL = "TERRACOTTA";

    private String id;
    private String displayName;
    private transient String prefix;
    private Role tier;
    private boolean system;

    public FactionRole() {
    }

    public FactionRole(String id) {
        this(id, null, null, Role.NORMAL, false);
    }

    public FactionRole(String id, String displayName, String prefix, Role tier, boolean system) {
        this.id = normalizeId(id);
        this.displayName = normalizeOptionalText(displayName);
        this.prefix = normalizeOptionalText(prefix);
        this.tier = tier == null ? Role.NORMAL : tier;
        this.system = system;
    }

    public static FactionRole fromRole(Role role) {
        return new FactionRole(getSystemRoleId(role), null, null, role, true);
    }

    public static String getSystemRoleId(Role role) {
        return role == null ? null : role.name().toLowerCase(Locale.ROOT);
    }

    public static String normalizeId(String rawId) {
        if (rawId == null) {
            return null;
        }
        String trimmed = rawId.trim().toLowerCase(Locale.ROOT);
        if (trimmed.isEmpty()) {
            return null;
        }

        StringBuilder normalized = new StringBuilder(trimmed.length());
        for (int index = 0; index < trimmed.length(); index++) {
            char character = trimmed.charAt(index);
            if (character == ' ') {
                normalized.append('_');
                continue;
            }

            if ((character >= 'a' && character <= 'z')
                    || (character >= '0' && character <= '9')
                    || character == '_'
                    || character == '-') {
                normalized.append(character);
            }
        }

        return normalized.length() == 0 ? null : normalized.toString();
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        if (displayName != null) {
            return displayName;
        }
        if (system && tier != null) {
            return tier.getRoleCapitalized();
        }
        return prettifyId(id);
    }

    public String getStoredDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = normalizeOptionalText(displayName);
    }

    public String getPrefix() {
        if (prefix != null) {
            return prefix;
        }
        return tier != null ? tier.getPrefix() : "";
    }

    public String getStoredPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = normalizeOptionalText(prefix);
    }

    public Role getTier() {
        return tier == null ? Role.NORMAL : tier;
    }

    public void setTier(Role tier) {
        this.tier = tier == null ? Role.NORMAL : tier;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public boolean isLeaderTier() {
        return getTier() == Role.LEADER;
    }

    @Override
    public String name() {
        return getId();
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public ItemStack buildItem() {
        FactionsPlugin plugin = FactionsPlugin.getInstance();
        if (plugin == null || plugin.getFileManager() == null || plugin.getFileManager().getFperms() == null) {
            return null;
        }

        final ConfigurationSection relationConfig = plugin.getFileManager().getFperms().getConfig().getConfigurationSection(RELATION_CONFIG_PATH);
        if (relationConfig == null) {
            return null;
        }

        String display = replacePlaceholders(getConfigString(relationConfig, PLACEHOLDER_ITEM_NAME, LEGACY_PLACEHOLDER_ITEM_NAME));

        String materialKey = "materials." + getTier().name().toLowerCase(Locale.ROOT);
        Material material = XMaterial.matchXMaterial(relationConfig.getString(materialKey, DEFAULT_MATERIAL))
                .orElse(XMaterial.TERRACOTTA)
                .parseMaterial();
        if (material == null) {
            return null;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return item;
        }

        itemMeta.setDisplayName(display);
        itemMeta.setLore(buildLore(relationConfig));
        if (plugin.version != 7) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public String replacePlaceholders(String string) {
        if (string == null) {
            return "";
        }
        string = TextUtil.parse(string);
        string = string.replace("{relation-color}", ChatColor.GREEN.toString());
        string = string.replace("{relation}", getDisplayName());
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FactionRole)) return false;
        FactionRole that = (FactionRole) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private List<String> buildLore(ConfigurationSection relationConfig) {
        List<String> configuredLore = relationConfig.getStringList(PLACEHOLDER_ITEM_LORE);
        if (configuredLore.isEmpty()) {
            configuredLore = relationConfig.getStringList(LEGACY_PLACEHOLDER_ITEM_LORE);
        }

        List<String> lore = new ArrayList<>(configuredLore.size());
        for (String loreLine : configuredLore) {
            lore.add(replacePlaceholders(loreLine));
        }
        return lore;
    }

    private static String getConfigString(ConfigurationSection config, String path, String legacyPath) {
        return config.getString(path, config.getString(legacyPath, ""));
    }

    private static String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String prettifyId(String roleId) {
        if (roleId == null || roleId.isEmpty()) {
            return FALLBACK_ROLE_NAME;
        }

        StringBuilder builder = new StringBuilder(roleId.length());
        boolean nextUppercase = true;
        for (int index = 0; index < roleId.length(); index++) {
            char character = roleId.charAt(index);
            if (character == '_' || character == '-') {
                nextUppercase = true;
                continue;
            }

            if (builder.length() > 0 && nextUppercase) {
                builder.append(' ');
            }
            builder.append(nextUppercase ? Character.toUpperCase(character) : character);
            nextUppercase = false;
        }

        return builder.length() == 0 ? FALLBACK_ROLE_NAME : builder.toString();
    }
}
