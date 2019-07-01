package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPointsRemove extends FCommand {

    public CmdPointsRemove() {
        super();
        this.aliases.add("remove");

        this.requiredArgs.add("faction");
        this.requiredArgs.add("# of points");


        this.errorOnToManyArgs = false;
        //this.optionalArgs

        this.permission = Permission.REMOVEPOINTS.node;

        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }


    @Override
    public void perform() {
        Faction faction = Factions.getInstance().getByTag(args.get(0));

        if (faction == null) {
            fme.msg(TL.COMMAND_POINTS_FAILURE.toString().replace("{faction}", args.get(0)));
            return;
        }

        if(argAsInt(1) <= 0){
            fme.msg(TL.COMMAND_POINTS_INSUFFICIENT);
            return;
        }

        faction.setPoints(faction.getPoints() - argAsInt(1));
        fme.msg(TL.COMMAND_REMOVEPOINTS_SUCCESSFUL, argAsInt(1), faction.getTag(), faction.getPoints());
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_REMOVEPOINTS_DESCRIPTION;
    }


}
