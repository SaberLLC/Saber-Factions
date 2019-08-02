package com.massivecraft.factions.cmd.alts;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

public class CmdInviteAlt extends FCommand {

    public CmdInviteAlt() {
        super();
        this.aliases.add("invite");

        this.requiredArgs.add("player name");
        // this.optionalArgs.put("", "");

        this.permission = Permission.INVITE.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if(!P.p.getConfig().getBoolean("f-alts.Enabled", false)){
            fme.msg(TL.GENERIC_DISABLED);
            return;
        }

        FPlayer target = this.argAsBestFPlayerMatch(0);
        if (target == null) {
            return;
        }

        if (target.getFaction() == myFaction) {
            msg(TL.COMMAND_INVITE_ALREADYMEMBER, target.getName(), myFaction.getTag());
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this
        // command has a cost set, make 'em pay
        if (!payForCommand(Conf.econCostInvite, TL.COMMAND_INVITE_TOINVITE.toString(), TL.COMMAND_INVITE_FORINVITE.toString())) {
            return;
        }

        if (!fme.isAdminBypassing()) {
            Access access = myFaction.getAccess(fme, PermissableAction.INVITE);
            if (access != Access.ALLOW && fme.getRole() != Role.LEADER) {
                fme.msg(TL.GENERIC_FPERM_NOPERMISSION, "manage invites");
                return;
            }
        }

        if (myFaction.isBanned(target)) {
            fme.msg(TL.COMMAND_INVITE_BANNED, target.getName());
            return;
        }

        myFaction.deinvite(target);
        myFaction.altInvite(target);
        if (!target.isOnline()) {
            return;
        }

        FancyMessage message = new FancyMessage(fme.describeTo(target, true))
                .tooltip(TL.COMMAND_INVITE_CLICKTOJOIN.toString())
                .command("/" + Conf.baseCommandAliases.get(0) + " join " + myFaction.getTag())
                .then(TL.COMMAND_INVITE_INVITEDYOU.toString())
                .color(ChatColor.YELLOW)
                .tooltip(TL.COMMAND_INVITE_CLICKTOJOIN.toString())
                .command("/" + Conf.baseCommandAliases.get(0) + " join " + myFaction.getTag())
                .then(myFaction.describeTo(target)).tooltip(TL.COMMAND_INVITE_CLICKTOJOIN.toString())
                .command("/" + Conf.baseCommandAliases.get(0) + " join " + myFaction.getTag());

        message.send(target.getPlayer());

        myFaction.msg(TL.COMMAND_ALTINVITE_INVITED_ALT, fme.describeTo(myFaction, true), target.describeTo(myFaction));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALTINVITE_DESCRIPTION;
    }
}
