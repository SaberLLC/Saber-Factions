package com.massivecraft.factions.cmd.reserve;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

/**
 * @author Saser
 */

public class CmdReserve extends FCommand {

    public CmdReserve() {
        this.aliases.addAll(Aliases.reserve);
        this.requiredArgs.add("tag");
        this.requiredArgs.add("player");
        this.requirements = new CommandRequirements.Builder(
                Permission.RESERVE).build();
    }

    @Override
    public void perform(CommandContext context) {
        ReserveObject reserve = FactionsPlugin.getInstance().getFactionReserves().stream().filter(faction -> faction.getFactionName().equalsIgnoreCase(context.argAsString(0))).findFirst().orElse(null);
        if (reserve != null) {
            context.msg(TL.COMMAND_RESERVE_ALREADYRESERVED, context.argAsString(0));
            return;
        }
        context.msg(TL.COMMAND_RESERVE_SUCCESS, context.argAsString(0), context.argAsString(1));
        FactionsPlugin.getInstance().getFactionReserves().add(new ReserveObject(context.argAsString(1), context.argAsString(0)));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_RESERVE_DESCRIPTION;
    }
}
