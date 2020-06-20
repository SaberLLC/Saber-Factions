package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;


public class CmdVersion extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdVersion() {
        this.aliases.add("version");
        this.aliases.add("ver");

        this.requirements = new CommandRequirements.Builder(Permission.VERSION)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        context.msg(TL.COMMAND_VERSION_NAME); // Did this so people can differentiate between SavageFactions and FactionsUUID (( Requested Feature ))
        context.msg(TL.COMMAND_VERSION_VERSION, FactionsPlugin.getInstance().getDescription().getFullName());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_VERSION_DESCRIPTION;
    }
}