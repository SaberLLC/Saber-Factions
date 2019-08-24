package com.massivecraft.factions.cmd.roles;

public class CmdDemote extends FPromoteCommand {

    public CmdDemote() {
        aliases.add("demote");
        this.relative = -1;
    }
}
