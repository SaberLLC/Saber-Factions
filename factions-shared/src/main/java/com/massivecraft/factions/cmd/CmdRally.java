package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdRally extends FCommand {

    public CmdRally() {
        super();
        this.getAliases().addAll(Aliases.rally);

        this.setRequirements(new CommandRequirements.Builder(Permission.RALLY)
                .memberOnly()
                .playerOnly()
                .build());
    }


    @Override
    public void perform(CommandContext context) {
        //LunarAPI.sendRallyPing(context.fPlayer);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_RALLY_DESCRIPTION;
    }
}
