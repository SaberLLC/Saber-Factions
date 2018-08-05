package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdStealth extends FCommand {
    public CmdStealth() {
        this.aliases.add("ninja");
        this.aliases.add("stealth");
        this.permission = Permission.STEALTH.node;

        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeColeader = false;
        this.senderMustBeAdmin = false;
    }

    public void perform() {
        if (myFaction != null && !myFaction.isWilderness() && !myFaction.isSafeZone() && !myFaction.isWarZone() && myFaction.isNormal()) {
            fme.setStealth(!fme.isStealthEnabled());
            // Sends Enable/Disable Message
            fme.msg(fme.isStealthEnabled() ? TL.COMMAND_STEALTH_ENABLE : TL.COMMAND_STEALTH_DISABLE);
        } else {
            fme.msg(TL.COMMAND_STEALTH_MUSTBEMEMBER);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STEALTH_DESCRIPTION;
    }

}
