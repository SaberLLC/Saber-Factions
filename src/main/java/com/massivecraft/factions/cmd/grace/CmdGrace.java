package com.massivecraft.factions.cmd.grace;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdGrace extends FCommand {

    public CmdGrace() {
        super();
        this.aliases.add("grace");

        this.requirements = new CommandRequirements.Builder(Permission.GRACE)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        Conf.gracePeriod = !Conf.gracePeriod;
        context.msg(TL.COMMAND_GRACE_TOGGLE, Conf.gracePeriod ? FactionsPlugin.getInstance().color("&aEnabled") : FactionsPlugin.getInstance().color("&4Disabled"));
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_GRACE_DESCRIPTION;
    }

}
