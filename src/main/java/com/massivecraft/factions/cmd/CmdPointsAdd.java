package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPointsAdd extends FCommand {

    public CmdPointsAdd() {
        super();
        this.aliases.add("add");

        this.requiredArgs.add("faction");
        this.requiredArgs.add("# of points");


        this.errorOnToManyArgs = false;
        //this.optionalArgs

        this.permission = Permission.ADDPOINTS.node;

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
        }

        assert faction != null;
        faction.setPoints(faction.getPoints() + argAsInt(1));
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ADDPOINTS_DESCRIPTION;
    }


}
