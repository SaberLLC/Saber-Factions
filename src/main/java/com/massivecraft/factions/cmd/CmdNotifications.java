package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/2/2020
 */
public class CmdNotifications extends FCommand {

    public CmdNotifications() {
        super();
        this.aliases.addAll(Aliases.notifications);
        this.requirements = new CommandRequirements.Builder(Permission.NOTIFICATIONS)
                .playerOnly()
                .memberOnly()
                .build();
    }


    @Override
    public void perform(CommandContext context) {
        if (context.fPlayer.hasNotificationsEnabled()) {
            context.fPlayer.setNotificationsEnabled(false);
            context.msg(TL.COMMAND_NOTIFICATIONS_TOGGLED_OFF);
        } else {
            context.fPlayer.setNotificationsEnabled(true);
            context.msg(TL.COMMAND_NOTIFICATIONS_TOGGLED_ON);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_NOTIFICATIONS_DESCRIPTION;
    }
}
