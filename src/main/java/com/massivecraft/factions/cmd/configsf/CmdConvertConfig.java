package com.massivecraft.factions.cmd.configsf;

import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdConvertConfig extends FCommand {

    public CmdConvertConfig() {
        super();
        this.aliases.add("convertconfig");

        this.requirements = new CommandRequirements.Builder(Permission.CONVERTCONFIG)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        ConvertConfigHandler.convertconfig(context.player);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CONVERTCONFIG_DESCRIPTION;
    }
}
