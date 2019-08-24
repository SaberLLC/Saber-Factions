package com.massivecraft.factions.cmd.alts;

import com.google.common.base.Joiner;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

import java.util.stream.Collectors;

public class CmdAltsList extends FCommand {


    public CmdAltsList() {
        super();
        this.aliases.add("list");
        this.aliases.add("l");
        this.optionalArgs.put("faction", "yours");


        this.permission = Permission.LIST.node;
        this.disableOnLock = false;
        this.disableOnSpam = false;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

    }

    @Override
    public void perform() {
        Faction faction = myFaction;
        if (argIsSet(0)) {
            faction = argAsFaction(0);
        }
        if (faction == null)
            return;

        if (faction != myFaction && !fme.isAdminBypassing()) {
            return;
        }

        if (faction.getAltPlayers().size() == 0) {
            msg(TL.COMMAND_ALTS_LIST_NOALTS, faction.getTag());
            return;
        }

        msg("There are " + faction.getAltPlayers().size() + " alts in " + faction.getTag() + ":");
        msg(Joiner.on(", ").join(faction.getAltPlayers().stream().map(FPlayer::getName).collect(Collectors.toList())));
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALTS_LIST_DESCRIPTION;
    }
}
