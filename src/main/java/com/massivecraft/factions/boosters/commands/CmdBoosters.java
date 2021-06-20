package com.massivecraft.factions.boosters.commands;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.boosters.struct.FactionBoosters;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdBoosters extends FCommand {

    public CmdBoosters() {
        super();
        this.aliases.addAll(Aliases.boosters);

        this.requirements = new CommandRequirements.Builder(Permission.BOOSTERS)
                .memberOnly()
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        FactionBoosters boosters = FactionsPlugin.getInstance().getBoosterManager().getFactionBooster(context.faction);

        if (boosters == null || boosters.isEmpty()) {
            context.msg(TL.COMMAND_BOOSTER_NONE_ACTIVE);
            return;
        }

        FactionsPlugin.getInstance().getBoosterManager().showActiveBoosters(context.player, boosters);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_BOOSTER_DESCRIPTION;
    }
}
