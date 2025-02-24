package com.massivecraft.factions.cmd.roles;

import com.massivecraft.factions.cmd.Aliases;

public class CmdPromote extends FPromoteCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdPromote() {
        getAliases().addAll(Aliases.roles_promote);
        this.relative = 1;
    }
}
