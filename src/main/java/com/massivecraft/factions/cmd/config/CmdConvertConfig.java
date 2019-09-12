package com.massivecraft.factions.cmd.config;

import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdConvertConfig extends FCommand {
    public CmdConvertConfig() {

        super();
        this.aliases.add("convertconfig");
        this.permission = Permission.CONVERTCONFIG.node;
        this.disableOnLock = false;
        senderMustBePlayer = true;
    }

    @Override
    public void perform() {
        ConvertConfigHandler.convertconfig(fme.getPlayer());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CONVERTCONFIG_DESCRIPTION;
    }

}
