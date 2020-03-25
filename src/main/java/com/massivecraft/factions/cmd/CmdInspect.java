package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdInspect extends FCommand {

    /**
     * @author Illyria Team
     */

    public CmdInspect() {
        super();
        this.aliases.addAll(Aliases.inspect);
        this.requirements = new CommandRequirements.Builder(Permission.INSPECT)
                .playerOnly()
                .memberOnly()
                .build();
    }


    @Override
    public void perform(CommandContext context) {
        if (!Conf.useInspectSystem) {
            context.fPlayer.msg(TL.GENERIC_DISABLED, "Faction Inspection");
            return;
        }

        if (context.fPlayer.isInspectMode()) {
            context.fPlayer.setInspectMode(false);
            context.msg(TL.COMMAND_INSPECT_DISABLED_MSG);
        } else {
            context.fPlayer.setInspectMode(true);
            context.msg(TL.COMMAND_INSPECT_ENABLED);
        }

    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_INSPECT_DESCRIPTION;
    }
}

