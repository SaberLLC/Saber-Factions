package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.FactionRole;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetDefaultRole extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdSetDefaultRole() {
        super();

        this.getAliases().addAll(Aliases.setDefaultRole);
        this.getRequiredArgs().add("role");

        this.setRequirements(new CommandRequirements.Builder(Permission.DEFAULTRANK)
                .playerOnly()
                .memberOnly()
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        FactionRole target = context.faction.getRoleByName(context.argAsString(0));
        if (target == null) {
            context.msg(TL.COMMAND_SETDEFAULTROLE_INVALIDROLE, context.argAsString(0));
            return;
        }

        if (target.isLeaderTier()) {
            context.msg(TL.COMMAND_SETDEFAULTROLE_NOTTHATROLE, context.argAsString(0));
            return;
        }


        context.faction.setDefaultRole(target);
        context.msg(TL.COMMAND_SETDEFAULTROLE_SUCCESS, target.getDisplayName());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETDEFAULTROLE_DESCRIPTION;
    }
}
