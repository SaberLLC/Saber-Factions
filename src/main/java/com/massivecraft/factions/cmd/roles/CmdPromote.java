package com.massivecraft.factions.cmd.roles;

import com.massivecraft.factions.cmd.Aliases;

public class CmdPromote extends FPromoteCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdPromote() {
        aliases.addAll(Aliases.roles_promote);
        this.relative = 1;
    }
}
