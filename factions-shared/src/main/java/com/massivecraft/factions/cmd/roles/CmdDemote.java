package com.massivecraft.factions.cmd.roles;

import com.massivecraft.factions.cmd.Aliases;

public class CmdDemote extends FPromoteCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdDemote() {
        this.getAliases().addAll(Aliases.roles_demote);
        this.relative = -1;
    }
}
