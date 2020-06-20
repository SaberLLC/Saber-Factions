package com.massivecraft.factions.cmd.roles;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class FPromoteCommand extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public int relative = 0;

    public FPromoteCommand() {
        super();
        this.requiredArgs.add("player");

        this.requirements = new CommandRequirements.Builder(Permission.PROMOTE)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.PROMOTE)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        FPlayer target = context.argAsBestFPlayerMatch(0);
        if (target == null) {
            context.msg(TL.GENERIC_NOPLAYERFOUND, context.argAsString(0));
            return;
        }

        if (!target.getFaction().equals(context.faction)) {
            context.msg(TL.COMMAND_PROMOTE_WRONGFACTION, target.getName());
            return;
        }

        Role current = target.getRole();
        Role promotion = Role.getRelative(current, +relative);

        // Now it ain't that messy
        if (!context.fPlayer.isAdminBypassing()) {
            if (target == context.fPlayer) {
                context.msg(TL.COMMAND_PROMOTE_NOTSELF);
                return;
            }
            // Don't allow people to manage role of their same rank
            if (context.fPlayer.getRole() == current) {
                context.msg(TL.COMMAND_PROMOTE_NOT_SAME);
                return;
            }
            // Don't allow people to promote people with same or higher rank than their.
            if (context.fPlayer.getRole().value <= target.getRole().value) {
                context.msg(TL.COMMAND_PROMOTE_HIGHER_RANK, target.getName());
                return;
            }
            // Don't allow people to demote people who already have the lowest rank.
            if (current.value == 0 && relative <= 0) {
                context.msg(TL.COMMAND_PROMOTE_LOWEST_RANK, target.getName());
                return;
            }
            // Don't allow people to promote people to their same or higher rank.
            if (context.fPlayer.getRole().value <= promotion.value) {
                context.msg(TL.COMMAND_PROMOTE_NOT_ALLOWED);
                return;
            }
        }

        if (target.isAlt()) {
            return;
        }

        // Don't allow people to demote people who already have the lowest rank.
        if (current.value == 0 && relative <= 0) {
            context.msg(TL.COMMAND_PROMOTE_LOWEST_RANK, target.getName());
            return;
        }
        // Don't allow people to promote people who already have the highest rank.
        if (current.value == 4 && relative > 0) {
            context.msg(TL.COMMAND_PROMOTE_HIGHEST_RANK, target.getName());
            return;
        }
        // Don't allow people to promote people to their same or higher rnak.
        if (context.fPlayer.getRole().value <= promotion.value) {
            context.msg(TL.COMMAND_PROMOTE_NOT_ALLOWED);
            return;
        }

        String action = relative > 0 ? TL.COMMAND_PROMOTE_PROMOTED.toString() : TL.COMMAND_PROMOTE_DEMOTED.toString();

        // Success!
        target.setRole(promotion);
        if (target.isOnline()) {
            target.msg(TL.COMMAND_PROMOTE_TARGET, action, promotion.nicename);
        }

        context.msg(TL.COMMAND_PROMOTE_SUCCESS, action, target.getName(), promotion.nicename);
        FactionsPlugin.instance.getFlogManager().log(context.faction, FLogType.ROLE_PERM_EDIT, context.fPlayer.getName(), action, target.getName(), promotion.nicename);

    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PROMOTE_DESCRIPTION;
    }

}