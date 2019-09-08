package com.massivecraft.factions.cmd.check;

import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.zcore.util.TL;

public class CmdWeeWoo extends FCommand {
    public CmdWeeWoo() {
        this.aliases.add("weewoo");
        this.requiredArgs.add("start/stop");

        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }

    public void perform() {
        if (myFaction == null || !myFaction.isNormal()) {
            return;
        }
        String argument = argAsString(0);
        boolean weewoo = myFaction.isWeeWoo();
        if (argument.equalsIgnoreCase("start")) {
            if (weewoo) {
                msg(TL.COMMAND_WEEWOO_ALREADY_STARTED);
                return;
            }
            myFaction.setWeeWoo(true);
            msg(TL.COMMAND_WEEWOO_STARTED, fme.getNameAndTag());

        } else if (argument.equalsIgnoreCase("stop")) {
            if (!weewoo) {
                msg(TL.COMMAND_WEEWOO_ALREADY_STOPPED);
                return;
            }
            myFaction.setWeeWoo(false);
            msg(TL.COMMAND_WEEWOO_STOPPED, fme.getNameAndTag());
        } else {
            msg("/f weewoo <start/stop>");
        }
    }

    public TL getUsageTranslation() {
        return TL.COMMAND_WEEWOO_DESCRIPTION;
    }
}
