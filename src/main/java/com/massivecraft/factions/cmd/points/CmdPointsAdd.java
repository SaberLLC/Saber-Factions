package com.massivecraft.factions.cmd.points;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPointsAdd extends FCommand {

    /**
     * @author Driftay
     */

    public CmdPointsAdd() {
        super();
        this.aliases.addAll(Aliases.points_add);

        this.requiredArgs.add("faction/player");
        this.requiredArgs.add("# of points");


        this.requirements = new CommandRequirements.Builder(Permission.ADDPOINTS)
                .build();
    }


    @Override
    public void perform(CommandContext context) {
        Faction faction = Factions.getInstance().getByTag(context.args.get(0));

        if (faction == null) {
            FPlayer fPlayer = context.argAsFPlayer(0);
            if (fPlayer != null) {
                faction = fPlayer.getFaction();
            }
        }

        if (faction == null || faction.isWilderness()) {
            context.msg(TL.COMMAND_POINTS_FAILURE.toString().replace("{faction}", context.args.get(0)));
            return;
        }
        if (context.argAsInt(1) <= 0) {
            context.msg(TL.COMMAND_POINTS_INSUFFICIENT);
            return;
        }

        faction.setPoints(faction.getPoints() + context.argAsInt(1));

        context.msg(TL.COMMAND_POINTS_SUCCESSFUL, context.argAsInt(1), faction.getTag(), faction.getPoints());
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ADDPOINTS_DESCRIPTION;
    }


}
