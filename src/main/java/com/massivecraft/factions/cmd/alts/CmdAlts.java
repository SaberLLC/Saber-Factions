package com.massivecraft.factions.cmd.alts;

import com.massivecraft.factions.SaberFactions;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdAlts extends FCommand {


    public CmdInviteAlt cmdInviteAlt = new CmdInviteAlt();
    public CmdAltsList cmdAltsList = new CmdAltsList();


    public CmdAlts(){
        super();

        this.aliases.add("alts");
        this.aliases.add("alt");

        this.permission = Permission.ALTS.node;
        this.disableOnLock = false;
        this.disableOnSpam = false;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;


        this.addSubCommand(this.cmdInviteAlt);
        this.addSubCommand(this.cmdAltsList);
    }

    @Override
    public void perform() {
        if (!SaberFactions.plugin.getConfig().getBoolean("f-alts.Enabled", false)) {
            fme.msg(TL.GENERIC_DISABLED);
            return;
        }

        this.commandChain.add(this);
        SaberFactions.plugin.cmdAutoHelp.execute(this.sender, this.args, this.commandChain);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALTS_DESCRIPTION;
    }

}
