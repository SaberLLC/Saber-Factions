package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdLeave extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdLeave() {
        super();
        this.aliases.addAll(Aliases.leave);

        this.requirements = new CommandRequirements.Builder(Permission.LEAVE)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        context.fPlayer.leave(true);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.LEAVE_DESCRIPTION;
    }

}