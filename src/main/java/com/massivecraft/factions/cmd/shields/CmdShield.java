package com.massivecraft.factions.cmd.shields;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.shields.struct.frame.ShieldFrame;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;


/**
 * Factions - Developed by ImCarib.
 * All rights reserved 2020.
 * Creation Date: 5/23/2020
 */

public class CmdShield extends FCommand {

    public CmdShield() {
        this.aliases.add("shield");
        this.requirements = new CommandRequirements.Builder(Permission.SHIELD)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.SHIELD)
                .build();
    }

    public void perform(CommandContext context) {
        if (!Conf.useFShieldSystem) {
            context.msg(TL.GENERIC_DISABLED);
            return;
        }

        (new ShieldFrame()).build(context.fPlayer);
    }

    public TL getUsageTranslation() {
        return TL.COMMAND_SHIELD_DESCRIPTION;
    }
}
