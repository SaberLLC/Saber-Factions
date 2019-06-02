package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSpam extends FCommand {

    public CmdSpam(){
        this.aliases.add("spam");

        this.optionalArgs.put("on/off", "flip");

        this.permission = Permission.SPAM.node;
        this.disableOnLock = false;
        this.disableOnSpam = false;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        p.setSpam(this.argAsBool(0, !p.getSpam()));
        msg(p.getSpam() ? TL.COMMAND_SPAM_ENABLED : TL.COMMAND_SPAM_DISABLED);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SPAM_DESCRIPTION;
    }
}
