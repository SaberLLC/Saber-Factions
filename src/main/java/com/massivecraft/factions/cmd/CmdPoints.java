package com.massivecraft.factions.cmd;

import com.massivecraft.factions.SaberFactions;
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

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;


        this.addSubCommand(this.cmdPointsAdd);
        this.addSubCommand(this.cmdPointsRemove);
        this.addSubCommand(this.cmdPointsSet);
    }


    @Override
    public void perform() {
        if (!SaberFactions.plugin.getConfig().getBoolean("f-points.Enabled", false)) {
            fme.msg(TL.GENERIC_DISABLED);
            return;
        }

        this.commandChain.add(this);
        SaberFactions.plugin.cmdAutoHelp.execute(this.sender, this.args, this.commandChain);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_POINTS_DESCRIPTION;
    }


}
