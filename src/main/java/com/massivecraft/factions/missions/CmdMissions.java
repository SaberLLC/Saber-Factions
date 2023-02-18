package com.massivecraft.factions.missions;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdMissions extends FCommand {

    /**
     * @author Driftay
     */

    public CmdMissions() {
        this.aliases.addAll(Aliases.missions_missions);

        this.requirements = new CommandRequirements.Builder(Permission.MISSIONS)
                .memberOnly()
                .playerOnly()
                .build();

        this.optionalArgs.put("tribute", "tribute");

    }


    @Override
    public void perform(CommandContext context) {
        String tributeString = context.argAsString(0);

        if(tributeString != null){
            if(tributeString.startsWith("t")){
                context.player.openInventory(TributeInventoryHandler.getInventory());
            }
            return;
        }

        final MissionGUI missionsGUI = new MissionGUI(FactionsPlugin.getInstance(), context.fPlayer);
        missionsGUI.build(true);
        context.player.openInventory(missionsGUI.getInventory());
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MISSION_DESCRIPTION;
    }
}
