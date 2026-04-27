package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CmdAdminFaction extends FCommand {

    public CmdAdminFaction() {
        super();
        this.getAliases().addAll(Aliases.adminFaction);
        this.getRequiredArgs().add("action");

        this.setRequirements(new CommandRequirements.Builder(Permission.CREATE_ADMIN)
                .playerOnly()
                .noErrorOnManyArgs()
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        String action = context.argAsString(0, "").toLowerCase();

        switch (action) {
            case "tag":
            case "rename":
                setTag(context);
                return;
            case "desc":
            case "description":
                setDescription(context);
                return;
            case "perm":
            case "perms":
            case "permission":
            case "permissions":
                setPermission(context);
                return;
            case "sethome":
                setHome(context);
                return;
            default:
                context.sendMessage(getUsageTemplate(context));
        }
    }

    private void setTag(CommandContext context) {
        if (context.args.size() < 3) {
            context.sendMessage(getUsageTemplate(context));
            return;
        }

        Faction faction = requireAdminFaction(context, 1);
        if (faction == null) {
            return;
        }

        String tag = context.argAsString(2);
        if (Factions.getInstance().isTagTaken(tag) && !MiscUtil.getComparisonString(tag).equals(faction.getComparisonTag())) {
            context.msg(TL.COMMAND_TAG_TAKEN);
            return;
        }

        ArrayList<String> errors = MiscUtil.validateTag(tag);
        if (!errors.isEmpty()) {
            context.sendMessage(errors);
            return;
        }

        String oldTag = faction.getTag();
        faction.setTag(tag);
        FactionsPlugin.instance.logFactionEvent(faction, FLogType.FTAG_EDIT, context.fPlayer.getName(), tag);
        FTeamWrapper.updatePrefixes(faction);
        context.msg("&c&l[!] &7Updated admin faction tag from &c%1$s &7to &c%2$s&7.", oldTag, faction.getTag());
    }

    private void setDescription(CommandContext context) {
        if (context.args.size() < 3) {
            context.sendMessage(getUsageTemplate(context));
            return;
        }

        Faction faction = requireAdminFaction(context, 1);
        if (faction == null) {
            return;
        }

        List<String> parts = context.args.subList(2, context.args.size());
        String desc = TextUtil.implode(parts, " ").replaceAll("%", "").replaceAll("(&([a-f0-9klmnor]))", "& $2");
        faction.setDescription(desc);
        FactionsPlugin.instance.logFactionEvent(faction, FLogType.FDESC_EDIT, context.fPlayer.getName(), desc);
        context.msg("&c&l[!] &7Updated admin faction description for &c%1$s&7.", faction.getTag(context.fPlayer));
        context.sendMessage(faction.getDescription());
    }

    private void setPermission(CommandContext context) {
        if (context.args.size() != 5) {
            context.sendMessage(getUsageTemplate(context));
            return;
        }

        Faction faction = requireAdminFaction(context, 1);
        if (faction == null) {
            return;
        }

        Set<Permissable> permissables = new HashSet<>();
        Set<PermissableAction> permissableActions = new HashSet<>();

        boolean allRelations = context.argAsString(2).equalsIgnoreCase("all");
        boolean allActions = context.argAsString(3).equalsIgnoreCase("all");

        if (allRelations) {
            permissables.addAll(faction.getPermissions().keySet());
        } else {
            Permissable permissable = getPermissable(context.argAsString(2));
            if (permissable == null) {
                context.msg(TL.COMMAND_PERM_INVALID_RELATION);
                return;
            }
            permissables.add(permissable);
        }

        if (allActions) {
            permissableActions.addAll(Arrays.asList(PermissableAction.values()));
        } else {
            PermissableAction permissableAction = PermissableAction.fromString(context.argAsString(3));
            if (permissableAction == null) {
                context.msg(TL.COMMAND_PERM_INVALID_ACTION);
                return;
            }
            permissableActions.add(permissableAction);
        }

        Access access = Access.fromString(context.argAsString(4));
        if (access == null) {
            context.msg(TL.COMMAND_PERM_INVALID_ACCESS);
            return;
        }

        boolean success = false;
        for (Permissable permissable : permissables) {
            for (PermissableAction permissableAction : permissableActions) {
                success = faction.setPermission(permissable, permissableAction, access, context.fPlayer);
            }
        }

        if (success) {
            context.msg(TL.COMMAND_PERM_SET, context.argAsString(3), access.name(), context.argAsString(2));
            Logger.print(String.format(TL.COMMAND_PERM_SET.toString(), context.argAsString(3), access.name(), context.argAsString(2)) + " for admin faction " + faction.getTag(), Logger.PrefixType.DEFAULT);
        }
    }

    private void setHome(CommandContext context) {
        if (context.args.size() != 2) {
            context.sendMessage(getUsageTemplate(context));
            return;
        }

        Faction faction = requireAdminFaction(context, 1);
        if (faction == null) {
            return;
        }

        faction.setHome(context.player.getLocation());
        context.msg(TL.COMMAND_SETHOME_SETOTHER, faction.getTag(context.fPlayer));
    }

    private Faction requireAdminFaction(CommandContext context, int argIndex) {
        Faction faction = context.argAsFaction(argIndex);
        if (faction == null) {
            return null;
        }
        if (!faction.isAdminFaction()) {
            context.msg("&c&l[!] &7That faction is not a server-managed admin faction.");
            return null;
        }
        return faction;
    }

    private Permissable getPermissable(String name) {
        if (Role.fromString(name.toUpperCase()) != null) {
            return Role.fromString(name.toUpperCase());
        } else if (Relation.fromString(name.toUpperCase()) != null) {
            return Relation.fromString(name.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ADMINFACTION_DESCRIPTION;
    }
}
