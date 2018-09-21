package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPaypalSet  extends FCommand{

    public CmdPaypalSet() {
        this.aliases.add("setpaypal");
        this.aliases.add("paypal");
        this.requiredArgs.add("email");
        this.permission = Permission.PAYPALSET.node;
        this.disableOnLock = false;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeColeader = true;
        this.senderMustBeAdmin = false;
    }

    public void perform() {
        if (!P.p.getConfig().getBoolean("fpaypal.Enabled")) {
            fme.msg(TL.GENERIC_DISABLED);
        } else {
            String paypal = argAsString(0);
            if (paypal != null) {
                myFaction.paypalSet(paypal);
                fme.msg(TL.COMMAND_PAYPALSET_SUCCESSFUL, paypal);
            }
        }
    }

    public TL getUsageTranslation() {
        return TL.COMMAND_PAYPALSET_DESCRIPTION;
    }
}

