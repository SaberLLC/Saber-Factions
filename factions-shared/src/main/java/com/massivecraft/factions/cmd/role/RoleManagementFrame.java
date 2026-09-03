package com.massivecraft.factions.cmd.role;

import com.cryptomorin.xseries.XMaterial;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CmdRole;
import com.massivecraft.factions.struct.FactionRole;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.ItemBuilder;
import com.massivecraft.factions.util.SaberGUI;
import com.massivecraft.factions.util.serializable.InventoryItem;
import com.massivecraft.factions.zcore.fperms.gui.PermissableActionFrame;
import com.massivecraft.factions.zcore.fperms.gui.PermissableRelationFrame;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleManagementFrame extends SaberGUI {

    private static final int DEFAULT_GUI_SIZE = 54;
    private static final int[] DEFAULT_CONTENT_SLOTS = createContentSlots();
    private static final Map<String, String> EMPTY_PLACEHOLDERS = Collections.emptyMap();

    private final Player player;
    private final Faction faction;
    private final int page;

    public RoleManagementFrame(Player player, Faction faction, int page) {
        super(player,
                title("menus.main", "&8Faction Roles", pagePlaceholders(faction, Math.max(0, page), 0)),
                size("menus.main", DEFAULT_GUI_SIZE / 9));
        this.player = player;
        this.faction = faction;
        this.page = Math.max(0, page);
    }

    @Override
    public void redraw() {
        if (!isCustomRoleSystemEnabled()) {
            this.player.closeInventory();
            this.player.sendMessage(TL.COMMAND_ROLE_CUSTOM_DISABLED.toString());
            return;
        }

        fillBackground(this, this.size);

        List<FactionRole> roles = sortRoles(this.faction.getRoles().values(), true);
        int[] contentSlots = slots("menus.main.slots.role-slots", "menus.main.slots.content", DEFAULT_CONTENT_SLOTS);
        int maxPage = getMaxPage(roles.size(), contentSlots.length);
        int currentPage = Math.min(this.page, maxPage);
        Map<String, String> pagePlaceholders = pagePlaceholders(this.faction, currentPage, maxPage);

        setItem(slot("menus.main.slots.create", 0),
                new InventoryItem(item("menus.main.items.create", material(XMaterial.ANVIL, XMaterial.IRON_BLOCK), merge(pagePlaceholders, roleLimitPlaceholders(this.faction))))
                        .click(() -> {
                            if (hasReachedCustomRoleLimit(this.faction)) {
                                this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_ROLE_LIMIT_REACHED, roleLimitPlaceholders(this.faction)));
                                return;
                            }
                            openCreateRoleFrame(this.player, this.faction, currentPage);
                        }));

        setItem(slot("menus.main.slots.members", 1),
                new InventoryItem(item("menus.main.items.members", material(XMaterial.BOOK, XMaterial.PAPER), pagePlaceholders))
                        .click(() -> openMemberFrame(this.player, this.faction, currentPage)));

        setItem(slot("menus.main.slots.permissions", 2),
                new InventoryItem(item("menus.main.items.permissions", material(XMaterial.REDSTONE, XMaterial.REDSTONE_BLOCK), pagePlaceholders))
                        .click(() -> new PermissableRelationFrame(this.player, this.faction).openGUI(FactionsPlugin.getInstance())));

        setItem(slot("menus.main.slots.info", 4),
                new InventoryItem(item("menus.main.items.info", material(XMaterial.NETHER_STAR, XMaterial.BEACON), pagePlaceholders)));

        setItem(slot("menus.main.slots.close", 49),
                new InventoryItem(item("menus.main.items.close", material(XMaterial.BARRIER, XMaterial.REDSTONE_BLOCK), pagePlaceholders))
                        .click(this.player::closeInventory));

        if (currentPage > 0) {
            setItem(slot("menus.main.slots.previous", 45),
                    new InventoryItem(item("menus.main.items.previous", material(XMaterial.ARROW, XMaterial.PAPER), pagePlaceholders(this.faction, currentPage - 1, maxPage)))
                            .click(() -> openMain(this.player, this.faction, currentPage - 1)));
        }

        if (currentPage < maxPage) {
            setItem(slot("menus.main.slots.next", 53),
                    new InventoryItem(item("menus.main.items.next", material(XMaterial.ARROW, XMaterial.PAPER), pagePlaceholders(this.faction, currentPage + 1, maxPage)))
                            .click(() -> openMain(this.player, this.faction, currentPage + 1)));
        }

        int startIndex = currentPage * contentSlots.length;
        int endIndex = Math.min(startIndex + contentSlots.length, roles.size());
        for (int index = startIndex; index < endIndex; index++) {
            FactionRole role = roles.get(index);
            boolean defaultRole = isDefaultRole(this.faction, role);
            int slot = contentSlots[index - startIndex];
            InventoryItem item = new InventoryItem(buildRoleItem(this.faction, role, defaultRole, "menus.main.items.role", "menus.main.items.role-default"))
                    .click(ClickType.LEFT, () -> openRoleDetail(this.player, this.faction, role.getId(), currentPage))
                    .click(ClickType.RIGHT, () -> {
                        if (role.isLeaderTier()) {
                            this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_LEADER_DEFAULT, rolePlaceholders(this.faction, role, defaultRole)));
                            return;
                        }
                        this.faction.setDefaultRole(role);
                        this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_DEFAULT_SET, rolePlaceholders(this.faction, role, true)));
                        openMain(this.player, this.faction, currentPage);
                    });
            setItem(slot, item);
        }
    }

    private static void fillBackground(SaberGUI gui, int size) {
        if (!bool("common.filler.enabled", true)) {
            for (int slot = 0; slot < size; slot++) {
                gui.removeItem(slot);
            }
            return;
        }

        InventoryItem filler = new InventoryItem(item("common.filler", material(XMaterial.GRAY_STAINED_GLASS_PANE, XMaterial.GLASS_PANE), placeholders()));
        for (int slot = 0; slot < size; slot++) {
            gui.setItem(slot, filler);
        }
    }

    private static void openMain(Player player, Faction faction, int page) {
        new RoleManagementFrame(player, faction, page).openGUI(FactionsPlugin.getInstance());
    }

    private static void openRoleDetail(Player player, Faction faction, String roleId, int returnPage) {
        new RoleDetailFrame(player, faction, roleId, returnPage).openGUI(FactionsPlugin.getInstance());
    }

    private static void openCreateRoleFrame(Player player, Faction faction, int returnPage) {
        new RoleCreateBaseFrame(player, faction, returnPage, 0).openGUI(FactionsPlugin.getInstance());
    }

    private static void openMemberFrame(Player player, Faction faction, int returnPage) {
        new RoleMemberFrame(player, faction, returnPage, 0).openGUI(FactionsPlugin.getInstance());
    }

    private static List<FactionRole> sortRoles(Collection<FactionRole> roles, boolean includeLeader) {
        List<FactionRole> sorted = new ArrayList<>();
        if (roles != null) {
            for (FactionRole role : roles) {
                if (role == null || (!includeLeader && role.isLeaderTier())) {
                    continue;
                }
                sorted.add(role);
            }
        }

        sorted.sort(Comparator.comparingInt((FactionRole role) -> role.getTier().value)
                .reversed()
                .thenComparing(role -> role.isSystem() ? 0 : 1)
                .thenComparing(FactionRole::getDisplayName, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    private static ItemBuilder baseRoleBuilder(Faction faction, FactionRole role, boolean highlighted, String configPath) {
        Material material = resolveRoleMaterial(role);
        Map<String, String> placeholders = rolePlaceholders(faction, role, isDefaultRole(faction, role));
        ItemBuilder builder = new ItemBuilder(item(configPath, material, placeholders));
        if (highlighted) {
            builder.glowing(true);
        }
        return builder;
    }

    private static ItemStack buildRoleItem(Faction faction, FactionRole role, boolean defaultRole, String normalPath, String defaultPath) {
        String path = defaultRole ? defaultPath : normalPath;
        return baseRoleBuilder(faction, role, defaultRole, path).build();
    }

    private static Material resolveRoleMaterial(FactionRole role) {
        if (role != null) {
            org.bukkit.inventory.ItemStack item = role.buildItem();
            if (item != null && item.getType() != null && item.getType() != Material.AIR) {
                return item.getType();
            }
        }
        return material(XMaterial.PAPER, XMaterial.BOOK);
    }

    private static boolean isDefaultRole(Faction faction, FactionRole role) {
        FactionRole defaultRole = faction.getDefaultFactionRole();
        return defaultRole != null && role != null && defaultRole.getId().equals(role.getId());
    }

    private static int countMembers(Faction faction, FactionRole role) {
        int count = 0;
        for (FPlayer member : faction.getFPlayers()) {
            if (member == null || member.isAlt() || member.getFactionRole() == null) {
                continue;
            }
            if (member.getFactionRole().equals(role)) {
                count++;
            }
        }
        return count;
    }

    private static String normalizeRoleNameToId(String roleName) {
        if (roleName == null) {
            return null;
        }

        String visibleName = ChatColor.stripColor(TextUtil.parse(roleName));
        return FactionRole.normalizeId(visibleName);
    }

    private static int getMaxPage(int itemCount, int pageSize) {
        return Math.max(0, (int) Math.ceil(itemCount / (double) Math.max(1, pageSize)) - 1);
    }

    private static int[] createContentSlots() {
        int[] slots = new int[36];
        for (int index = 0; index < slots.length; index++) {
            slots[index] = 9 + index;
        }
        return slots;
    }

    private static int[] createRangeSlots(int start, int end) {
        int length = Math.max(0, end - start + 1);
        int[] slots = new int[length];
        for (int index = 0; index < length; index++) {
            slots[index] = start + index;
        }
        return slots;
    }

    private static Material material(XMaterial primary, XMaterial fallback) {
        Material material = primary == null ? null : primary.parseMaterial();
        if (material == null && fallback != null) {
            material = fallback.parseMaterial();
        }
        return material == null ? Material.STONE : material;
    }

    private static int size(String path, int defaultRows) {
        ConfigurationSection config = getRolesConfig();
        int rows = config == null ? defaultRows : config.getInt(path + ".rows", defaultRows);
        return Math.max(1, rows) * 9;
    }

    private static String title(String path, String fallback, Map<String, String> placeholders) {
        return text(path + ".title", fallback, placeholders);
    }

    private static int slot(String path, int fallback) {
        ConfigurationSection config = getRolesConfig();
        return config == null ? fallback : config.getInt(path, fallback);
    }

    private static int[] slots(String path, int[] fallback) {
        return slots(path, null, fallback);
    }

    private static int[] slots(String path, String legacyPath, int[] fallback) {
        ConfigurationSection config = getRolesConfig();
        if (config == null) {
            return fallback;
        }

        List<Integer> configured = config.getIntegerList(path);
        if ((configured == null || configured.isEmpty()) && legacyPath != null) {
            configured = config.getIntegerList(legacyPath);
        }
        if (configured == null || configured.isEmpty()) {
            return fallback;
        }

        int[] slots = new int[configured.size()];
        for (int index = 0; index < configured.size(); index++) {
            slots[index] = configured.get(index);
        }
        return slots;
    }

    private static boolean bool(String path, boolean fallback) {
        ConfigurationSection config = getRolesConfig();
        return config == null ? fallback : config.getBoolean(path, fallback);
    }

    private static String text(String path, String fallback) {
        return text(path, fallback, EMPTY_PLACEHOLDERS);
    }

    private static String text(String path, String fallback, Map<String, String> placeholders) {
        ConfigurationSection config = getRolesConfig();
        String value = config == null ? fallback : config.getString(path, fallback);
        return parseText(value, placeholders);
    }

    private static List<String> lines(String path, Map<String, String> placeholders) {
        return lines(path, Collections.emptyList(), placeholders);
    }

    private static List<String> lines(String path, List<String> fallback, Map<String, String> placeholders) {
        ConfigurationSection config = getRolesConfig();
        List<String> raw = config == null ? Collections.emptyList() : config.getStringList(path);
        if (raw == null || raw.isEmpty()) {
            raw = fallback == null ? Collections.emptyList() : fallback;
        }
        if (raw.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> parsed = new ArrayList<>(raw.size());
        for (String line : raw) {
            parsed.add(parseText(line, placeholders));
        }
        return parsed;
    }

    private static ItemStack item(String path, Material defaultMaterial, Map<String, String> placeholders) {
        ConfigurationSection section = getSection(path);
        Material material = defaultMaterial == null ? Material.STONE : defaultMaterial;
        if (section != null) {
            String configuredMaterial = section.getString("Material");
            if (configuredMaterial != null && !configuredMaterial.trim().isEmpty()) {
                Material configured = XMaterial.matchXMaterial(configuredMaterial)
                        .orElse(XMaterial.STONE)
                        .parseMaterial();
                if (configured != null) {
                    material = configured;
                }
            }
        }

        ItemBuilder builder = new ItemBuilder(material == null ? Material.STONE : material)
                .name(text(path + ".Name", " ", placeholders))
                .lore(lines(path + ".Lore", placeholders));

        if (bool(path + ".Glow", false)) {
            builder.glowing(true);
        }

        return builder.build();
    }

    private static ItemStack renameRoleItem(Map<String, String> placeholders) {
        ItemBuilder builder = new ItemBuilder(material(XMaterial.NAME_TAG, XMaterial.PAPER))
                .name(text("menus.detail.items.display-name.Name", "&eRename Role", placeholders))
                .lore(lines("menus.detail.items.display-name.Lore", Arrays.asList(
                        "&7Current name: &f{role-name}",
                        "&7Change the name players see",
                        "&7for this role.",
                        "",
                        "&eLeft-Click &7Rename this role"
                ), placeholders));

        if (bool("menus.detail.items.display-name.Glow", false)) {
            builder.glowing(true);
        }

        return builder.build();
    }

    private static String parseText(String value, Map<String, String> placeholders) {
        if (value == null) {
            return "";
        }

        String replaced = value;
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                replaced = replaced.replace(entry.getKey(), entry.getValue() == null ? "" : entry.getValue());
            }
        }
        return TextUtil.parse(replaced);
    }

    private static ConfigurationSection getSection(String path) {
        ConfigurationSection config = getRolesConfig();
        return config == null ? null : config.getConfigurationSection(path);
    }

    private static ConfigurationSection getRolesConfig() {
        FactionsPlugin plugin = FactionsPlugin.getInstance();
        if (plugin == null || plugin.getFileManager() == null || plugin.getFileManager().getRoles() == null) {
            return null;
        }
        return plugin.getFileManager().getRoles().getConfig();
    }

    private static boolean isCustomRoleSystemEnabled() {
        ConfigurationSection config = getRolesConfig();
        return config == null || config.getBoolean("settings.custom-role-system", true);
    }

    private static int getMaxCustomRoles() {
        ConfigurationSection config = getRolesConfig();
        return config == null ? -1 : config.getInt("settings.max-custom-roles", -1);
    }

    private static boolean hasReachedCustomRoleLimit(Faction faction) {
        int maxCustomRoles = getMaxCustomRoles();
        return faction != null && maxCustomRoles >= 0 && faction.getCustomRoles().size() >= maxCustomRoles;
    }

    private static Map<String, String> roleLimitPlaceholders(Faction faction) {
        int maxCustomRoles = getMaxCustomRoles();
        return placeholders(
                "{faction}", faction == null ? "" : faction.getTag(),
                "{role-limit}", maxCustomRoles < 0 ? "unlimited" : String.valueOf(maxCustomRoles),
                "{role-count}", faction == null ? "0" : String.valueOf(faction.getCustomRoles().size())
        );
    }

    private static Map<String, String> placeholders(String... values) {
        Map<String, String> placeholders = new HashMap<>();
        for (int index = 0; index + 1 < values.length; index += 2) {
            placeholders.put(values[index], values[index + 1]);
        }
        return placeholders;
    }

    private static Map<String, String> pagePlaceholders(Faction faction, int currentPage, int maxPage) {
        return placeholders(
                "{faction}", faction == null ? "" : faction.getTag(),
                "{page}", String.valueOf(currentPage + 1),
                "{max-page}", String.valueOf(maxPage + 1)
        );
    }

    private static Map<String, String> rolePlaceholders(Faction faction, FactionRole role, boolean defaultRole) {
        String prefix = role == null ? noneText() : role.getPrefix();
        if (prefix == null || prefix.isEmpty()) {
            prefix = noneText();
        }

        return placeholders(
                "{faction}", faction == null ? "" : faction.getTag(),
                "{role-name}", role == null ? fallbackRoleName() : role.getDisplayName(),
                "{role-id}", role == null || role.getId() == null ? "" : role.getId(),
                "{role-tier}", role == null ? Role.NORMAL.getRoleCapitalized() : role.getTier().getRoleCapitalized(),
                "{role-prefix}", prefix,
                "{role-type}", role != null && role.isSystem() ? text("common.values.system", "&7system") : text("common.values.custom", "&acustom"),
                "{role-members}", role == null || faction == null ? "0" : String.valueOf(countMembers(faction, role)),
                "{role-default}", defaultRole ? text("common.values.default", "&aDefault join role") : "",
                "{role-default-state}", defaultRole ? text("common.values.default-state", "&atrue") : text("common.values.non-default-state", "&7false")
        );
    }

    private static Map<String, String> roleEditorPlaceholders(Faction faction, String roleId) {
        FactionRole role = faction == null ? null : faction.getRole(roleId);
        if (role == null) {
            return placeholders(
                    "{faction}", faction == null ? "" : faction.getTag(),
                    "{role-id}", roleId == null ? "" : roleId,
                    "{role-name}", roleId == null ? fallbackRoleName() : prettifyRoleId(roleId)
            );
        }
        return rolePlaceholders(faction, role, isDefaultRole(faction, role));
    }

    private static Map<String, String> memberEditorPlaceholders(Faction faction, String memberId) {
        FPlayer member = memberId == null ? null : FPlayers.getInstance().getById(memberId);
        if (member == null) {
            return placeholders(
                    "{faction}", faction == null ? "" : faction.getTag(),
                    "{member-name}", "",
                    "{member-role}", fallbackRoleName()
            );
        }
        return memberPlaceholders(faction, member);
    }

    private static String message(TL translation, Map<String, String> placeholders) {
        return replacePlaceholders(translation.toString(), placeholders);
    }

    private static List<String> messageLines(TL translation, Map<String, String> placeholders) {
        String message = message(translation, placeholders);
        if (message.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.asList(message.split("\\r?\\n"));
    }

    private static String replacePlaceholders(String value, Map<String, String> placeholders) {
        if (value == null) {
            return "";
        }

        String replaced = value;
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                replaced = replaced.replace(entry.getKey(), entry.getValue() == null ? "" : entry.getValue());
            }
        }
        return replaced;
    }

    private static String fallbackRoleName() {
        return text("common.values.role-fallback", "Role");
    }

    private static String noneText() {
        return text("common.values.none", "&7(none)");
    }

    private static String prettifyRoleId(String roleId) {
        if (roleId == null || roleId.isEmpty()) {
            return fallbackRoleName();
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

        return builder.length() == 0 ? fallbackRoleName() : builder.toString();
    }

    private static Map<String, String> memberPlaceholders(Faction faction, FPlayer member) {
        FactionRole role = member == null ? null : member.getFactionRole();
        String prefix = role == null ? noneText() : role.getPrefix();
        if (prefix == null || prefix.isEmpty()) {
            prefix = noneText();
        }

        return placeholders(
                "{faction}", faction == null ? "" : faction.getTag(),
                "{member-name}", member == null || member.getName() == null ? "" : member.getName(),
                "{member-role}", role == null ? fallbackRoleName() : role.getDisplayName(),
                "{member-prefix}", prefix,
                "{member-status}", member != null && member.isOnline() ? text("common.values.online", "&aonline") : text("common.values.offline", "&7offline")
        );
    }

    private static Map<String, String> merge(Map<String, String> left, Map<String, String> right) {
        Map<String, String> merged = new HashMap<>();
        if (left != null) {
            merged.putAll(left);
        }
        if (right != null) {
            merged.putAll(right);
        }
        return merged;
    }

    private static final class RoleDetailFrame extends SaberGUI {

        private final Player player;
        private final Faction faction;
        private final String roleId;
        private final int returnPage;

        private RoleDetailFrame(Player player, Faction faction, String roleId, int returnPage) {
            super(player,
                    title("menus.detail", "&8Role: {role-name}", roleEditorPlaceholders(faction, roleId)),
                    size("menus.detail", 5));
            this.player = player;
            this.faction = faction;
            this.roleId = roleId;
            this.returnPage = Math.max(0, returnPage);
        }

        @Override
        public void redraw() {
            fillBackground(this, this.size);

            FactionRole role = this.faction.getRole(this.roleId);
            if (role == null) {
                this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_ROLE_MISSING, placeholders("{faction}", this.faction.getTag())));
                openMain(this.player, this.faction, this.returnPage);
                return;
            }

            boolean defaultRole = isDefaultRole(this.faction, role);
            Map<String, String> placeholders = rolePlaceholders(this.faction, role, defaultRole);

            setItem(slot("menus.detail.slots.summary", 4),
                    new InventoryItem(item(defaultRole ? "menus.detail.items.summary-default" : "menus.detail.items.summary", resolveRoleMaterial(role), placeholders)));

            setItem(slot("menus.detail.slots.display-name", 12),
                    new InventoryItem(renameRoleItem(placeholders))
                            .click(() -> promptDisplayName(role)));

            if (!role.isLeaderTier()) {
                String defaultPath = defaultRole ? "menus.detail.items.default-current" : "menus.detail.items.default";
                Material defaultMaterial = defaultRole ? material(XMaterial.EMERALD_BLOCK, XMaterial.NETHER_STAR) : material(XMaterial.REDSTONE_BLOCK, XMaterial.NETHER_STAR);
                setItem(slot("menus.detail.slots.default", 14),
                        new InventoryItem(item(defaultPath, defaultMaterial, placeholders)).click(() -> {
                    this.faction.setDefaultRole(role);
                    this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_DEFAULT_SET, placeholders));
                    openRoleDetail(this.player, this.faction, role.getId(), this.returnPage);
                }));
            } else {
                setItem(slot("menus.detail.slots.default", 14),
                        new InventoryItem(item("menus.detail.items.default-locked", material(XMaterial.BARRIER, XMaterial.REDSTONE_BLOCK), placeholders)));
            }

            setItem(slot("menus.detail.slots.permissions", 16),
                    new InventoryItem(item("menus.detail.items.permissions", material(XMaterial.REDSTONE, XMaterial.REDSTONE_BLOCK), placeholders))
                            .click(() -> new PermissableActionFrame(this.player, this.faction, role)
                                    .setParentGUI(this)
                                    .openGUI(FactionsPlugin.getInstance())));

            setItem(slot("menus.detail.slots.members", 20),
                    new InventoryItem(item("menus.detail.items.members", material(XMaterial.BOOK, XMaterial.PAPER), placeholders))
                            .click(() -> openMemberFrame(this.player, this.faction, this.returnPage)));

            if (!role.isSystem()) {
                setItem(slot("menus.detail.slots.delete", 24),
                        new InventoryItem(item("menus.detail.items.delete", material(XMaterial.BARRIER, XMaterial.REDSTONE_BLOCK), placeholders)).click(() -> {
                    if (this.faction.deleteRole(role)) {
                        this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_ROLE_DELETED, placeholders));
                        openMain(this.player, this.faction, this.returnPage);
                        return;
                    }

                    this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_ROLE_DELETE_FAILED, placeholders));
                    openRoleDetail(this.player, this.faction, role.getId(), this.returnPage);
                }));
            } else {
                setItem(slot("menus.detail.slots.delete", 24),
                        new InventoryItem(item("menus.detail.items.system", material(XMaterial.IRON_BARS, XMaterial.BARRIER), placeholders)));
            }

            setItem(slot("menus.detail.slots.back", 36),
                    new InventoryItem(item("menus.detail.items.back", material(XMaterial.ARROW, XMaterial.PAPER), placeholders))
                            .click(() -> openMain(this.player, this.faction, this.returnPage)));

            setItem(slot("menus.detail.slots.close", 44),
                    new InventoryItem(item("menus.detail.items.close", material(XMaterial.BARRIER, XMaterial.REDSTONE_BLOCK), placeholders))
                            .click(this.player::closeInventory));
        }

        private void promptDisplayName(FactionRole role) {
            if (role == null) {
                openMain(this.player, this.faction, this.returnPage);
                return;
            }

            RoleTextInput.request(this.player,
                    messageLines(TL.COMMAND_ROLE_GUI_PROMPT_DISPLAY_NAME, rolePlaceholders(this.faction, role, isDefaultRole(this.faction, role))),
                    () -> openRoleDetail(this.player, this.faction, role.getId(), this.returnPage),
                    input -> {
                        FactionRole current = this.faction.getRole(role.getId());
                        if (current == null) {
                            this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_ROLE_MISSING, placeholders("{faction}", this.faction.getTag())));
                            openMain(this.player, this.faction, this.returnPage);
                            return;
                        }

                        String trimmed = input == null ? "" : input.trim();
                        if (trimmed.equalsIgnoreCase("clear")) {
                            current.setDisplayName(null);
                            this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_DISPLAY_NAME_RESET,
                                    rolePlaceholders(this.faction, current, isDefaultRole(this.faction, current))));
                            openRoleDetail(this.player, this.faction, current.getId(), this.returnPage);
                            return;
                        }

                        if (trimmed.isEmpty()) {
                            promptDisplayName(current);
                            return;
                        }

                        current.setDisplayName(TextUtil.parse(trimmed));
                        this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_DISPLAY_NAME_UPDATED,
                                rolePlaceholders(this.faction, current, isDefaultRole(this.faction, current))));
                        openRoleDetail(this.player, this.faction, current.getId(), this.returnPage);
                    });
        }
    }

    private static final class RoleCreateBaseFrame extends SaberGUI {

        private final Player player;
        private final Faction faction;
        private final int returnPage;
        private final int page;

        private RoleCreateBaseFrame(Player player, Faction faction, int returnPage, int page) {
            super(player,
                    title("menus.create", "&8Choose Base Role", pagePlaceholders(faction, Math.max(0, page), 0)),
                    size("menus.create", DEFAULT_GUI_SIZE / 9));
            this.player = player;
            this.faction = faction;
            this.returnPage = Math.max(0, returnPage);
            this.page = Math.max(0, page);
        }

        @Override
        public void redraw() {
            fillBackground(this, this.size);

            List<FactionRole> baseRoles = sortRoles(this.faction.getRoles().values(), false);
            int[] contentSlots = slots("menus.create.slots.template-slots", "menus.create.slots.content", DEFAULT_CONTENT_SLOTS);
            int maxPage = getMaxPage(baseRoles.size(), contentSlots.length);
            int currentPage = Math.min(this.page, maxPage);
            Map<String, String> pagePlaceholders = pagePlaceholders(this.faction, currentPage, maxPage);

            setItem(slot("menus.create.slots.header", 4),
                    new InventoryItem(item("menus.create.items.header", material(XMaterial.ANVIL, XMaterial.IRON_BLOCK), pagePlaceholders)));

            setItem(slot("menus.create.slots.back", 45),
                    new InventoryItem(item("menus.create.items.back", material(XMaterial.ARROW, XMaterial.PAPER), pagePlaceholders))
                            .click(() -> openMain(this.player, this.faction, this.returnPage)));

            setItem(slot("menus.create.slots.close", 49),
                    new InventoryItem(item("menus.create.items.close", material(XMaterial.BARRIER, XMaterial.REDSTONE_BLOCK), pagePlaceholders))
                            .click(this.player::closeInventory));

            if (currentPage > 0) {
                setItem(slot("menus.create.slots.previous", 46),
                        new InventoryItem(item("menus.create.items.previous", material(XMaterial.ARROW, XMaterial.PAPER), pagePlaceholders(this.faction, currentPage - 1, maxPage)))
                                .click(() -> new RoleCreateBaseFrame(this.player, this.faction, this.returnPage, currentPage - 1).openGUI(FactionsPlugin.getInstance())));
            }

            if (currentPage < maxPage) {
                setItem(slot("menus.create.slots.next", 52),
                        new InventoryItem(item("menus.create.items.next", material(XMaterial.ARROW, XMaterial.PAPER), pagePlaceholders(this.faction, currentPage + 1, maxPage)))
                                .click(() -> new RoleCreateBaseFrame(this.player, this.faction, this.returnPage, currentPage + 1).openGUI(FactionsPlugin.getInstance())));
            }

            int startIndex = currentPage * contentSlots.length;
            int endIndex = Math.min(startIndex + contentSlots.length, baseRoles.size());
            for (int index = startIndex; index < endIndex; index++) {
                FactionRole baseRole = baseRoles.get(index);
                int slot = contentSlots[index - startIndex];
                setItem(slot, new InventoryItem(baseRoleBuilder(this.faction, baseRole, false, "menus.create.items.role")
                        .build()).click(() -> promptForRoleId(baseRole)));
            }
        }

        private void promptForRoleId(FactionRole baseRole) {
            RoleTextInput.request(this.player,
                    messageLines(TL.COMMAND_ROLE_GUI_PROMPT_CREATE_ROLE, rolePlaceholders(this.faction, baseRole, false)),
                    () -> new RoleCreateBaseFrame(this.player, this.faction, this.returnPage, this.page).openGUI(FactionsPlugin.getInstance()),
                    input -> {
                        if (hasReachedCustomRoleLimit(this.faction)) {
                            this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_ROLE_LIMIT_REACHED, roleLimitPlaceholders(this.faction)));
                            new RoleCreateBaseFrame(this.player, this.faction, this.returnPage, this.page).openGUI(FactionsPlugin.getInstance());
                            return;
                        }

                        String displayName = TextUtil.parse(input == null ? "" : input.trim());
                        String normalizedId = normalizeRoleNameToId(displayName);
                        if (normalizedId == null) {
                            this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_INVALID_ROLE_ID, rolePlaceholders(this.faction, baseRole, false)));
                            promptForRoleId(baseRole);
                            return;
                        }
                        if (CmdRole.isReservedRoleId(normalizedId)) {
                            this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_RESERVED_ROLE_ID, placeholders("{role-id}", normalizedId, "{faction}", this.faction.getTag())));
                            promptForRoleId(baseRole);
                            return;
                        }
                        if (this.faction.getRole(normalizedId) != null) {
                            this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_ROLE_EXISTS, placeholders("{role-id}", normalizedId, "{faction}", this.faction.getTag())));
                            promptForRoleId(baseRole);
                            return;
                        }
                        if (baseRole.isLeaderTier()) {
                            this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_LEADER_TIER_FORBIDDEN, rolePlaceholders(this.faction, baseRole, false)));
                            new RoleCreateBaseFrame(this.player, this.faction, this.returnPage, this.page).openGUI(FactionsPlugin.getInstance());
                            return;
                        }

                        FactionRole created = new FactionRole(normalizedId, displayName, null, baseRole.getTier(), false);
                        if (!this.faction.addRole(created, baseRole)) {
                            this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_ROLE_CREATE_FAILED, placeholders("{role-id}", normalizedId, "{faction}", this.faction.getTag())));
                            new RoleCreateBaseFrame(this.player, this.faction, this.returnPage, this.page).openGUI(FactionsPlugin.getInstance());
                            return;
                        }

                        this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_ROLE_CREATED, merge(
                                rolePlaceholders(this.faction, created, false),
                                placeholders("{base-role-name}", baseRole.getDisplayName())
                        )));
                        openRoleDetail(this.player, this.faction, created.getId(), this.returnPage);
                    });
        }
    }

    private static final class RoleMemberFrame extends SaberGUI {

        private final Player player;
        private final Faction faction;
        private final int returnPage;
        private final int page;

        private RoleMemberFrame(Player player, Faction faction, int returnPage, int page) {
            super(player,
                    title("menus.members", "&8Assign Member Roles", pagePlaceholders(faction, Math.max(0, page), 0)),
                    size("menus.members", DEFAULT_GUI_SIZE / 9));
            this.player = player;
            this.faction = faction;
            this.returnPage = Math.max(0, returnPage);
            this.page = Math.max(0, page);
        }

        @Override
        public void redraw() {
            fillBackground(this, this.size);

            List<FPlayer> members = getMembers(this.faction);
            int[] contentSlots = slots("menus.members.slots.member-slots", "menus.members.slots.content", DEFAULT_CONTENT_SLOTS);
            int maxPage = getMaxPage(members.size(), contentSlots.length);
            int currentPage = Math.min(this.page, maxPage);
            Map<String, String> pagePlaceholders = pagePlaceholders(this.faction, currentPage, maxPage);

            setItem(slot("menus.members.slots.header", 4),
                    new InventoryItem(item("menus.members.items.header", material(XMaterial.BOOK, XMaterial.PAPER), pagePlaceholders)));

            setItem(slot("menus.members.slots.back", 45),
                    new InventoryItem(item("menus.members.items.back", material(XMaterial.ARROW, XMaterial.PAPER), pagePlaceholders))
                            .click(() -> openMain(this.player, this.faction, this.returnPage)));

            setItem(slot("menus.members.slots.close", 49),
                    new InventoryItem(item("menus.members.items.close", material(XMaterial.BARRIER, XMaterial.REDSTONE_BLOCK), pagePlaceholders))
                            .click(this.player::closeInventory));

            if (currentPage > 0) {
                setItem(slot("menus.members.slots.previous", 46),
                        new InventoryItem(item("menus.members.items.previous", material(XMaterial.ARROW, XMaterial.PAPER), pagePlaceholders(this.faction, currentPage - 1, maxPage)))
                                .click(() -> new RoleMemberFrame(this.player, this.faction, this.returnPage, currentPage - 1).openGUI(FactionsPlugin.getInstance())));
            }

            if (currentPage < maxPage) {
                setItem(slot("menus.members.slots.next", 52),
                        new InventoryItem(item("menus.members.items.next", material(XMaterial.ARROW, XMaterial.PAPER), pagePlaceholders(this.faction, currentPage + 1, maxPage)))
                                .click(() -> new RoleMemberFrame(this.player, this.faction, this.returnPage, currentPage + 1).openGUI(FactionsPlugin.getInstance())));
            }

            int startIndex = currentPage * contentSlots.length;
            int endIndex = Math.min(startIndex + contentSlots.length, members.size());
            for (int index = startIndex; index < endIndex; index++) {
                FPlayer member = members.get(index);
                int slot = contentSlots[index - startIndex];
                FactionRole role = member.getFactionRole();
                Map<String, String> memberPlaceholders = memberPlaceholders(this.faction, member);
                ItemBuilder builder = new ItemBuilder(item(member.getRole() == Role.LEADER ? "menus.members.items.member-leader" : "menus.members.items.member", resolveRoleMaterial(role), memberPlaceholders));

                if (member.getRole() == Role.LEADER) {
                    setItem(slot, new InventoryItem(builder.build()).click(() ->
                            this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_USE_ADMIN_TRANSFER, memberPlaceholders))));
                    continue;
                }

                setItem(slot, new InventoryItem(builder.build()).click(() ->
                        new PlayerRolePickerFrame(this.player, this.faction, member.getId(), this.returnPage, currentPage).openGUI(FactionsPlugin.getInstance())));
            }
        }

        private List<FPlayer> getMembers(Faction faction) {
            List<FPlayer> members = new ArrayList<>();
            for (FPlayer member : faction.getFPlayers()) {
                if (member == null || member.isAlt()) {
                    continue;
                }
                members.add(member);
            }

            members.sort(Comparator.comparingInt((FPlayer member) -> member.getRole().value)
                    .reversed()
                    .thenComparing(member -> member.getName() == null ? "" : member.getName(), String.CASE_INSENSITIVE_ORDER));
            return members;
        }
    }

    private static final class PlayerRolePickerFrame extends SaberGUI {

        private final Player player;
        private final Faction faction;
        private final String memberId;
        private final int returnPage;
        private final int memberPage;

        private PlayerRolePickerFrame(Player player, Faction faction, String memberId, int returnPage, int memberPage) {
            super(player,
                    title("menus.member-picker", "&8Assign Role: {member-name}", memberEditorPlaceholders(faction, memberId)),
                    size("menus.member-picker", 4));
            this.player = player;
            this.faction = faction;
            this.memberId = memberId;
            this.returnPage = Math.max(0, returnPage);
            this.memberPage = Math.max(0, memberPage);
        }

        @Override
        public void redraw() {
            fillBackground(this, this.size);

            FPlayer member = FPlayers.getInstance().getById(this.memberId);
            if (member == null || member.getFaction() != this.faction || member.isAlt()) {
                this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_MEMBER_MISSING, placeholders("{faction}", this.faction.getTag())));
                new RoleMemberFrame(this.player, this.faction, this.returnPage, this.memberPage).openGUI(FactionsPlugin.getInstance());
                return;
            }

            Map<String, String> memberPlaceholders = memberPlaceholders(this.faction, member);
            setItem(slot("menus.member-picker.slots.header", 4),
                    new InventoryItem(item("menus.member-picker.items.header", material(XMaterial.BOOK, XMaterial.PAPER), memberPlaceholders)));

            List<FactionRole> roles = sortRoles(this.faction.getRoles().values(), false);
            int[] roleSlots = slots("menus.member-picker.slots.roles", createRangeSlots(10, 25));
            for (int index = 0; index < roles.size() && index < roleSlots.length; index++) {
                FactionRole role = roles.get(index);
                int slotIndex = roleSlots[index];

                boolean current = member.getFactionRole().equals(role);
                Map<String, String> rolePlaceholders = rolePlaceholders(this.faction, role, isDefaultRole(this.faction, role));
                setItem(slotIndex, new InventoryItem(baseRoleBuilder(this.faction, role, current, current ? "menus.member-picker.items.role-current" : "menus.member-picker.items.role")
                        .build()).click(() -> {
                    if (member.getFactionRole().equals(role)) {
                        this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_MEMBER_ROLE_UNCHANGED, memberPlaceholders(this.faction, member)));
                        new PlayerRolePickerFrame(this.player, this.faction, this.memberId, this.returnPage, this.memberPage).openGUI(FactionsPlugin.getInstance());
                        return;
                    }

                    member.setFactionRole(role);
                    this.player.sendMessage(message(TL.COMMAND_ROLE_GUI_MEMBER_ROLE_SET,
                            merge(memberPlaceholders(this.faction, member), rolePlaceholders)));
                    if (member.isOnline() && !member.getPlayer().getUniqueId().equals(this.player.getUniqueId())) {
                        member.msg(TL.COMMAND_ROLE_ASSIGNED_TARGET, role.getDisplayName());
                    }
                    new RoleMemberFrame(this.player, this.faction, this.returnPage, this.memberPage).openGUI(FactionsPlugin.getInstance());
                }));
            }

            setItem(slot("menus.member-picker.slots.back", 27),
                    new InventoryItem(item("menus.member-picker.items.back", material(XMaterial.ARROW, XMaterial.PAPER), memberPlaceholders))
                            .click(() -> new RoleMemberFrame(this.player, this.faction, this.returnPage, this.memberPage).openGUI(FactionsPlugin.getInstance())));

            setItem(slot("menus.member-picker.slots.close", 35),
                    new InventoryItem(item("menus.member-picker.items.close", material(XMaterial.BARRIER, XMaterial.REDSTONE_BLOCK), memberPlaceholders))
                            .click(this.player::closeInventory));
        }
    }
}
