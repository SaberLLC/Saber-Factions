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
import com.massivecraft.factions.zcore.file.CustomFile;
import com.massivecraft.factions.zcore.util.TL;

import java.util.UUID;

/**
 * @Author: Driftay
 * @Date: 7/22/2024 11:53 AM
 */
public class CmdRosterAdd extends FCommand {

    public CmdRosterAdd() {
        super();
        this.getAliases().addAll(Aliases.roster_invite);

        this.getRequiredArgs().add("player name");
        this.getOptionalArgs().put("role", "faction role");

        this.setRequirements(new CommandRequirements.Builder(Permission.ROSTER)
                .playerOnly()
                .memberOnly()
                .withRole(Role.COLEADER)
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        FPlayer target = context.argAsBestFPlayerMatch(0);
        CustomFile rosterFile = FactionsPlugin.getInstance().getFileManager().getRoster();
        if (target == null) {
            context.msg(TL.COMMAND_ROSTERADD_NOT_FOUND);
            return;
        }

        if(target == context.fPlayer) {
            context.msg(TL.COMMAND_ROSTERADD_CANNOT_ADD_SELF);
            return;
        }

        RosterPlayer rp = RosterPlayerManager.getRosterPlayerFromUUID(UUID.fromString(target.getId()), context.faction);
        if(rp != null) {
            context.msg(TL.COMMAND_ROSTERADD_ALREADY_MEMBER);
            return;
        }

        Role role = null;
        if(context.args.size() == 2) {
            role = Role.fromString(context.argAsString(1));
        }

        if(role == null) {
            if(rosterFile.fetchBoolean("use-default-role-for-roster")) {
                role = Role.fromString(rosterFile.fetchString("default-role-roster-add"));
            } else {
                context.msg(TL.COMMAND_ROSTERADD_INVALIDROLE);
                return;
            }
        }

        int limit = rosterFile.fetchInt("roster-limit");

        if(context.faction.getRoster().size() + 1 >= limit) {
            context.msg(TL.COMMAND_ROSTERADD_FULL);
            return;
        }

        rp = new RosterPlayer(UUID.fromString(target.getId()), role, 0);
        context.faction.getRoster().add(rp);
        context.msg(TL.COMMAND_ROSTERADD_PLAYER_ADDED, target.getName(), role.getRoleCapitalized());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ROSTERADD_DESCRIPTION;
    }
}
