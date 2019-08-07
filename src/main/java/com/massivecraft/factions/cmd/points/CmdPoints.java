package com.massivecraft.factions.cmd.points;

import com.massivecraft.factions.P;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPoints extends FCommand {

    public CmdPointsRemove cmdPointsRemove = new CmdPointsRemove();
    public CmdPointsSet cmdPointsSet = new CmdPointsSet();
    public CmdPointsAdd cmdPointsAdd = new CmdPointsAdd();

    public CmdPoints(){
        super();
        this.aliases.add("points");

        this.disableOnLock = false;
        this.disableOnSpam = false;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;


        this.addSubCommand(this.cmdPointsAdd);
        this.addSubCommand(this.cmdPointsRemove);
        this.addSubCommand(this.cmdPointsSet);
    }


    @Override
    public void perform() {
        if (!P.p.getConfig().getBoolean("f-points.Enabled", true)) {
            msg(TL.GENERIC_DISABLED);
            return;
        }
        this.commandChain.add(this);
        P.p.cmdAutoHelp.execute(this.sender, this.args, this.commandChain);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_POINTS_DESCRIPTION;
    }


}
