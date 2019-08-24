package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdCheck extends FCommand {

    public CmdCheck() {
        this.aliases.add("check");

        this.requiredArgs.add("minutes");

        this.permission = Permission.CHECK.node;

        this.disableOnLock = true;
        this.disableOnSpam = false;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBeColeader = false;
        this.senderMustBeAdmin = false;
    }


    @Override
    public void perform() {
        if (!Conf.useCheckSystem) {
            msg(TL.GENERIC_DISABLED);
            return;
        }

        Access access = myFaction.getAccess(fme, PermissableAction.CHECK);
        if ((access == Access.DENY || (access == Access.UNDEFINED && !assertMinRole(Role.LEADER))) && !fme.isAdminBypassing()) {
            fme.msg(TL.GENERIC_NOPERMISSION, "check");
            return;
        }
        int minutes = this.argAsInt(0);
        if (minutes <= 0) {
            msg(TL.COMMAND_CHECK_INVALID_NUMBER);
        } else {
            myFaction.setCheckNotifier(minutes);
            msg(TL.COMMAND_CHECK_SUCCESSFUL.format(minutes));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CHECK_DESCRIPTION;
    }
}

