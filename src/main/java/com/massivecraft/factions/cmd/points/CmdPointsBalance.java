package com.massivecraft.factions.cmd.points;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 3/30/2020
 */
public class CmdPointsBalance extends FCommand {

    public CmdPointsBalance() {
        super();
        this.aliases.addAll(Aliases.points_balance);

        this.optionalArgs.put("faction", "yours");

        this.requirements = new CommandRequirements.Builder(Permission.POINTS)
                .build();
    }


    @Override
    public void perform(CommandContext context) {
        Faction faction;

        if (context.argIsSet(0)) {
            faction = context.argAsFaction(0);
        } else if (context.faction.isNormal()) {
            context.msg(TL.COMMAND_POINTS_SHOW_OWN, context.faction.getPoints());
            return;
        } else {
            context.msg(TL.COMMAND_POINTS_SHOW_WILDERNESS);
            return;
        }

        if (faction == null) return;

        if (faction != context.faction && !context.fPlayer.isAdminBypassing()) return;

        context.msg(TL.COMMAND_POINTS_SHOW_OTHER.toString().replace("{faction}", faction.getTag()).replace("{points}", faction.getPoints() + ""));

    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_POINTS_SHOW_DESCRIPTION;
    }
}
