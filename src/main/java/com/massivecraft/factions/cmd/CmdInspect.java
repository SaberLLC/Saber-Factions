package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdInspect extends FCommand {
    public CmdInspect() {
        super();
        this.aliases.add("inspect");
        this.aliases.add("ins");

        this.permission = Permission.INSPECT.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }


    @Override
    public void perform() {
        // Who can inspect?
        if (fme.isInspectMode()) {
            fme.setInspectMode(false);
            msg(TL.COMMAND_INSPECT_DISABLED_MSG);
        } else {
            fme.setInspectMode(true);
            msg(TL.COMMAND_INSPECT_ENABLED);
        }

    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_INSPECT_DESCRIPTION;
    }
}
