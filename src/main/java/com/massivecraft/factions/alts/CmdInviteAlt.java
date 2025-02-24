package com.massivecraft.factions.cmd.alts;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

public class CmdInviteAlt extends FCommand {

    /**
     * @author Driftay
     */

    public CmdInviteAlt() {
        super();
        this.getAliases().addAll(Aliases.alts_invite);
        this.getRequiredArgs().add("player name");

        this.setRequirements(new CommandRequirements.Builder(Permission.INVITE)
                .playerOnly()
                .memberOnly()
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("f-alts.Enabled", false)) {
            context.msg(TL.GENERIC_DISABLED, "Faction Alts");
            return;
        }

        FPlayer target = context.argAsBestFPlayerMatch(0);
        if (target == null) {

            return;
        }

        if (target.getFaction() == context.faction) {
            context.msg(TL.COMMAND_INVITE_ALREADYMEMBER, target.getName(), context.faction.getTag());
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this
        // command has a cost set, make 'em pay
        if (!context.payForCommand(Conf.econCostInvite, TL.COMMAND_INVITE_TOINVITE.toString(), TL.COMMAND_INVITE_FORINVITE.toString())) {
            return;
        }

        if (!context.fPlayer.isAdminBypassing()) {
            Access access = context.faction.getAccess(context.fPlayer, PermissableAction.INVITE);
            if (access != Access.ALLOW && context.fPlayer.getRole() != Role.LEADER) {
                context.msg(TL.GENERIC_FPERM_NOPERMISSION, "manage invites");
                return;
            }
        }

        if (context.faction.isBanned(target)) {
            context.msg(TL.COMMAND_INVITE_BANNED, target.getName());
            return;
        }

        context.faction.deinvite(target);
        context.faction.altInvite(target);
        if (!target.isOnline()) {
            return;
        }

        Component message = TL.COMMAND_INVITE_INVITEDYOU.toFormattedComponent(context.fPlayer.describeTo(target, true), context.faction.getTag())
                .hoverEvent(TL.COMMAND_INVITE_CLICKTOJOIN.toComponent())
                .clickEvent(ClickEvent.runCommand("/" + Conf.baseCommandAliases.get(0) + " join " + context.faction.getTag()));
        TextUtil.AUDIENCES.player(target.getPlayer()).sendMessage(message);
        FactionsPlugin.instance.logFactionEvent(context.faction, FLogType.INVITES, context.fPlayer.getName(), CC.Green + "invited", target.getName());
        context.faction.msg(TL.COMMAND_ALTINVITE_INVITED_ALT, context.fPlayer.describeTo(context.faction, true), target.describeTo(context.faction));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALTINVITE_DESCRIPTION;
    }
}
