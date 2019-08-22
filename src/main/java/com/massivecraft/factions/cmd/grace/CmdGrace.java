package com.massivecraft.factions.cmd.grace;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdGrace extends FCommand {

    public CmdGrace() {

        super();
        this.aliases.add("grace");

        this.permission = Permission.GRACE.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (!P.p.getConfig().getBoolean("f-grace.Enabled")) {
            fme.msg(TL.GENERIC_DISABLED);
            return;
        }

        boolean gracePeriod = Conf.gracePeriod;

        if (args.size() == 0) {
            if (gracePeriod)
                Conf.gracePeriod = false;
            else
                Conf.gracePeriod = true;
        }
        fme.msg(TL.COMMAND_GRACE_TOGGLE, gracePeriod ? "enabled" : "disabled");
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_GRACE_DESCRIPTION;
    }
}
