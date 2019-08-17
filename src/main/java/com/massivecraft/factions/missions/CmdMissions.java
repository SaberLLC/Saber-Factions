package com.massivecraft.factions.missions;

import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdMissions extends FCommand {

    public CmdMissions() {
        this.aliases.add("missions");
        this.aliases.add("mission");
        this.aliases.add("objectives");
        this.aliases.add("objective");

        this.permission = Permission.MISSIONS.node;

        this.disableOnLock = true;
        this.disableOnSpam = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }


    @Override
    public void perform() {
        if (myFaction == null) {
            return;
        }
        final MissionGUI missionsGUI = new MissionGUI(p, fme);
        missionsGUI.build();
        fme.getPlayer().openInventory(missionsGUI.getInventory());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MISSION_DESCRIPTION;
    }
}
