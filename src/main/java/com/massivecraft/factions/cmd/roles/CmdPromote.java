package com.massivecraft.factions.cmd.roles;

public class CmdPromote extends FPromoteCommand {

    public CmdPromote() {
        aliases.add("promote");
        aliases.add("promo");
        this.relative = 1;
    }
}
