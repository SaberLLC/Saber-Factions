package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdLogins extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdLogins() {
        super();
        this.aliases.addAll(Aliases.logins);

        this.requirements = new CommandRequirements.Builder(Permission.MONITOR_LOGINS)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        boolean monitor = context.fPlayer.isMonitoringJoins();
        context.msg(TL.COMMAND_LOGINS_TOGGLE, String.valueOf(!monitor));
        context.fPlayer.setMonitorJoins(!monitor);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_LOGINS_DESCRIPTION;
    }
}
