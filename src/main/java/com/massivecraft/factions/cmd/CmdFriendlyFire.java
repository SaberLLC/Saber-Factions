package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/6/2020
 */
public class CmdFriendlyFire extends FCommand {

    public CmdFriendlyFire() {
        super();
        this.aliases.addAll(Aliases.friendlyFire);

        this.requirements = new CommandRequirements.Builder(Permission.FRIENDLYFIRE)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!Conf.friendlyFireFPlayersCommand) {
            context.msg(TL.GENERIC_DISABLED, "friendly fire");
            return;
        }

        if (context.fPlayer.hasFriendlyFire()) {
            context.fPlayer.setFriendlyFire(false);
            context.msg(TL.COMMAND_FRIENDLY_FIRE_TOGGLE_OFF);
        } else {
            context.fPlayer.setFriendlyFire(true);
            context.msg(TL.COMMAND_FRIENDLY_FIRE_TOGGLE_ON);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FRIENDLY_FIRE_DESCRIPTION;
    }
}
