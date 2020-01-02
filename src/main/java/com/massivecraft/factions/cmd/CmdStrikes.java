package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdStrikes extends FCommand {

    /**
     * @author Driftay
     */

    public CmdStrikesGive cmdStrikesGive = new CmdStrikesGive();
    public CmdStrikesInfo cmdStrikesInfo = new CmdStrikesInfo();
    public CmdStrikesSet cmdStrikesSet = new CmdStrikesSet();
    public CmdStrikesTake cmdStrikesTake = new CmdStrikesTake();

    public CmdStrikes() {
        super();

        this.aliases.addAll(Aliases.strikes_strikes);

        this.addSubCommand(cmdStrikesGive);
        this.addSubCommand(cmdStrikesInfo);
        this.addSubCommand(cmdStrikesSet);
        this.addSubCommand(cmdStrikesTake);

        this.requirements = new CommandRequirements.Builder(Permission.SETSTRIKES)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        context.commandChain.add(this);
        FactionsPlugin.getInstance().cmdAutoHelp.execute(context);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STRIKES_DESCRIPTION;
    }

}