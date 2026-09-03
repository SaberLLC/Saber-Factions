package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.role.RoleManagementFrame;
import com.massivecraft.factions.struct.FactionRole;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CmdRole extends FCommand {

    public CmdRole() {
        super();
        this.getAliases().addAll(Aliases.role);
        this.setRequirements(new CommandRequirements.Builder(Permission.PERMISSIONS)
                .playerOnly()
                .memberOnly()
                .withRole(Role.LEADER)
                .noErrorOnManyArgs()
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        if (!isCustomRoleSystemEnabled()) {
            sendLegacyRoleHelp(context);
            return;
        }

        if (context.args.isEmpty()) {
            new RoleManagementFrame(context.player, context.faction, 0).openGUI(FactionsPlugin.getInstance());
            return;
        }

        String action = context.argAsString(0, "list").toLowerCase(Locale.ROOT);

        switch (action) {
            case "gui":
            case "edit":
            case "manage":
                new RoleManagementFrame(context.player, context.faction, 0).openGUI(FactionsPlugin.getInstance());
                return;
            case "list":
            case "ls":
                listRoles(context);
                return;
            case "create":
            case "add":
                createRole(context);
                return;
            case "set":
            case "assign":
                assignRole(context);
                return;
            case "delete":
            case "remove":
                deleteRole(context);
                return;
            case "default":
            case "def":
                setDefaultRole(context);
                return;
            default:
                sendUsage(context);
        }
    }

    private void listRoles(CommandContext context) {
        List<FactionRole> roles = new ArrayList<>(context.faction.getRoles().values());
        roles.sort(Comparator.comparingInt((FactionRole role) -> role.getTier().value)
                .thenComparing(role -> role.isSystem() ? 0 : 1)
                .thenComparing(FactionRole::getDisplayName, String.CASE_INSENSITIVE_ORDER));

        context.msg(TL.COMMAND_ROLE_LIST_HEADER, context.faction.getTag(context.fPlayer));
        for (FactionRole role : roles) {
            StringBuilder flags = new StringBuilder();
            if (context.faction.getDefaultFactionRole().getId().equals(role.getId())) {
                flags.append(TL.COMMAND_ROLE_LIST_DEFAULT_FLAG.toString());
            }

            context.msg(TL.COMMAND_ROLE_LIST_ENTRY,
                    role.getDisplayName(),
                    role.getId(),
                    role.getTier().getRoleCapitalized(),
                    flags.length() == 0 ? "" : ", " + flags.toString());
        }
    }

    private void createRole(CommandContext context) {
        if (context.args.size() != 3) {
            sendUsage(context);
            return;
        }

        int maxCustomRoles = getMaxCustomRoles();
        if (maxCustomRoles >= 0 && context.faction.getCustomRoles().size() >= maxCustomRoles) {
            context.msg(TL.COMMAND_ROLE_LIMIT_REACHED, maxCustomRoles);
            return;
        }

        String requestedId = context.argAsString(1);
        String normalizedId = FactionRole.normalizeId(requestedId);
        if (normalizedId == null) {
            context.msg(TL.COMMAND_ROLE_INVALID_ID);
            return;
        }
        if (isReservedRoleId(normalizedId)) {
            context.msg(TL.COMMAND_ROLE_RESERVED_ID);
            return;
        }
        if (context.faction.getRole(normalizedId) != null) {
            context.msg(TL.COMMAND_ROLE_ALREADY_EXISTS, normalizedId);
            return;
        }

        FactionRole baseRole = context.faction.getRoleByName(context.argAsString(2));
        if (baseRole == null) {
            context.msg(TL.COMMAND_ROLE_UNKNOWN_BASE, context.argAsString(2));
            return;
        }
        if (baseRole.isLeaderTier()) {
            context.msg(TL.COMMAND_ROLE_LEADER_TIER_FORBIDDEN);
            return;
        }

        FactionRole created = new FactionRole(normalizedId, requestedId, null, baseRole.getTier(), false);
        if (!context.faction.addRole(created, baseRole)) {
            context.msg(TL.COMMAND_ROLE_CREATE_FAILED);
            return;
        }

        context.msg(TL.COMMAND_ROLE_CREATED, created.getDisplayName(), baseRole.getDisplayName());
    }

    private void assignRole(CommandContext context) {
        if (context.args.size() != 3) {
            sendUsage(context);
            return;
        }

        FPlayer target = context.argAsBestFPlayerMatch(1);
        if (target == null) {
            return;
        }
        if (target.getFaction() != context.faction) {
            context.msg(TL.COMMAND_ROLE_NOT_IN_FACTION);
            return;
        }
        if (target.isAlt()) {
            context.msg(TL.COMMAND_ROLE_ALT_ASSIGN_DENIED);
            return;
        }

        FactionRole role = context.faction.getRoleByName(context.argAsString(2));
        if (role == null) {
            context.msg(TL.COMMAND_ROLE_UNKNOWN, context.argAsString(2));
            return;
        }
        if (role.isLeaderTier()) {
            context.msg(TL.COMMAND_ROLE_USE_ADMIN_TRANSFER);
            return;
        }
        if (target.getRole() == Role.LEADER) {
            context.msg(TL.COMMAND_ROLE_USE_ADMIN_CURRENT_LEADER);
            return;
        }

        target.setFactionRole(role);
        context.msg(TL.COMMAND_ROLE_ASSIGNED, role.getDisplayName(), target.getName());
        if (target != context.fPlayer) {
            target.msg(TL.COMMAND_ROLE_ASSIGNED_TARGET.toString(), role.getDisplayName());
        }
    }

    private void deleteRole(CommandContext context) {
        if (context.args.size() != 2) {
            sendUsage(context);
            return;
        }

        FactionRole role = context.faction.getRoleByName(context.argAsString(1));
        if (role == null) {
            context.msg(TL.COMMAND_ROLE_UNKNOWN, context.argAsString(1));
            return;
        }
        if (role.isSystem()) {
            context.msg(TL.COMMAND_ROLE_SYSTEM_DELETE_DENIED);
            return;
        }

        if (!context.faction.deleteRole(role)) {
            context.msg(TL.COMMAND_ROLE_DELETE_FAILED, role.getDisplayName());
            return;
        }

        context.msg(TL.COMMAND_ROLE_DELETED, role.getDisplayName());
    }

    private void setDefaultRole(CommandContext context) {
        if (context.args.size() != 2) {
            sendUsage(context);
            return;
        }

        FactionRole role = context.faction.getRoleByName(context.argAsString(1));
        if (role == null) {
            context.msg(TL.COMMAND_ROLE_UNKNOWN, context.argAsString(1));
            return;
        }
        if (role.isLeaderTier()) {
            context.msg(TL.COMMAND_ROLE_LEADER_DEFAULT_DENIED);
            return;
        }

        context.faction.setDefaultRole(role);
        context.msg(TL.COMMAND_ROLE_DEFAULT_SET, role.getDisplayName());
    }

    public static boolean isReservedRoleId(String roleId) {
        if (roleId == null || roleId.equals("all")) {
            return true;
        }
        if (Role.fromString(roleId.toUpperCase(Locale.ROOT)) != null) {
            return true;
        }
        return roleId.equals(Relation.ALLY.name().toLowerCase(Locale.ROOT))
                || roleId.equals(Relation.TRUCE.name().toLowerCase(Locale.ROOT))
                || roleId.equals(Relation.ENEMY.name().toLowerCase(Locale.ROOT))
                || roleId.equals(Relation.NEUTRAL.name().toLowerCase(Locale.ROOT))
                || roleId.equals(Relation.MEMBER.name().toLowerCase(Locale.ROOT));
    }

    private void sendLegacyRoleHelp(CommandContext context) {
        context.msg(TL.COMMAND_ROLE_CUSTOM_DISABLED);
        context.msg(TL.COMMAND_ROLE_CUSTOM_DISABLED_HELP);
    }

    private boolean isCustomRoleSystemEnabled() {
        return FactionsPlugin.getInstance().getFileManager() == null
                || FactionsPlugin.getInstance().getFileManager().getRoles() == null
                || FactionsPlugin.getInstance().getFileManager().getRoles().getConfig() == null
                || FactionsPlugin.getInstance().getFileManager().getRoles().getConfig().getBoolean("settings.custom-role-system", true);
    }

    private int getMaxCustomRoles() {
        return FactionsPlugin.getInstance().getFileManager() == null
                || FactionsPlugin.getInstance().getFileManager().getRoles() == null
                || FactionsPlugin.getInstance().getFileManager().getRoles().getConfig() == null
                ? -1
                : FactionsPlugin.getInstance().getFileManager().getRoles().getConfig().getInt("settings.max-custom-roles", -1);
    }

    private void sendUsage(CommandContext context) {
        context.msg(TL.COMMAND_ROLE_USAGE);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ROLE_DESCRIPTION;
    }
}
