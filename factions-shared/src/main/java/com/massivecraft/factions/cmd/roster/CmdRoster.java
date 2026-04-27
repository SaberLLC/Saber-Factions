package com.massivecraft.factions.cmd.roster;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

/**
 * @Author: Driftay
 * @Date: 7/21/2024 4:59 PM
 */
public class CmdRoster extends FCommand {

    public final CmdRosterAdd cmdRosterAdd = new CmdRosterAdd();
    public final CmdRosterRemove cmdRosterRemove = new CmdRosterRemove();


    public CmdRoster() {
        super();
        this.getAliases().addAll(Aliases.roster_rosters);

        this.addSubCommand(cmdRosterAdd);
        this.addSubCommand(cmdRosterRemove);

        this.setRequirements(new CommandRequirements.Builder(Permission.ROSTER)
                .playerOnly()
                .memberOnly()
                .build());
    }
    @Override
    public void perform(CommandContext context) {
        context.commandChain.add(this);
        FactionsPlugin.getInstance().cmdAutoHelp.execute(context);
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
