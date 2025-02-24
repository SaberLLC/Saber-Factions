package com.massivecraft.factions.cmd.roster;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.roster.struct.RosterPlayer;
import com.massivecraft.factions.cmd.roster.struct.RosterPlayerManager;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;

import java.util.UUID;

/**
 * @Author: Driftay
 * @Date: 7/21/2024 4:59 PM
 */
public class CmdRosterRemove extends FCommand {

    public CmdRosterRemove() {
        super();
        this.getAliases().addAll(Aliases.roster_kick);

        this.getRequiredArgs().add("player name");

        this.setRequirements(new CommandRequirements.Builder(Permission.ROSTER)
                .playerOnly()
                .memberOnly()
                .withRole(Role.COLEADER)
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        FPlayer target = context.argAsBestFPlayerMatch(0);

        if (target == null) {
            context.msg(TL.COMMAND_ROSTERREMOVE_NOT_FOUND);
            return;
        }

        if (target == context.fPlayer) {
            context.msg(TL.COMMAND_ROSTERREMOVE_CANNOT_REMOVE_SELF);
            return;
        }

        RosterPlayer rp = RosterPlayerManager.getRosterPlayerFromUUID(UUID.fromString(target.getId()), context.faction);

        if (rp == null) {
            context.msg(TL.COMMAND_ROSTERREMOVE_NOT_IN_ROSTER);
            return;
        }

        if(FactionsPlugin.getInstance().getFileManager().getRoster().fetchBoolean("limit-faction-roster-kicks")) {
            int limit = FactionsPlugin.getInstance().getFileManager().getRoster().fetchInt("max-faction-roster-kicks");
            int kicks = context.faction.getRosterKicks();

            if(kicks > limit) {
                context.msg(TL.COMMAND_ROSTERREMOVE_MAX_KICKS_REACHED, kicks, limit);
                return;
            }

            context.faction.setRosterKicks(kicks + 1);
        }

        context.faction.getRoster().remove(rp);
        context.msg(TL.COMMAND_ROSTEREMOVE_PLAYER_REMOVED, target.getName());

    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ROSTERADD_DESCRIPTION;
    }
}
