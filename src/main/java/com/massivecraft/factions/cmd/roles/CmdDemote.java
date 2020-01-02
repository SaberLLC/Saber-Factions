package com.massivecraft.factions.cmd.roles;

import com.massivecraft.factions.cmd.Aliases;

public class CmdDemote extends FPromoteCommand {

    /**
     * @author FactionsUUID Team
     */

    public CmdDemote() {
        aliases.addAll(Aliases.roles_demote);
        this.relative = -1;
    }
}
