package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdStrikesSet extends FCommand {

    /**
     * @author Driftay
     */

    public CmdStrikesSet() {
        super();
        this.getAliases().addAll(Aliases.strikes_set);
        this.getRequiredArgs().add(0, "faction");
        this.getRequiredArgs().add(1, "amount");

        this.setRequirements(new CommandRequirements.Builder(Permission.SETSTRIKES)
                .playerOnly()
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        Faction target = context.argAsFaction(0);
        if (target == null || target.isSystemFaction()) {
            context.msg(TL.COMMAND_STRIKES_TARGET_INVALID, context.argAsString(0));
            return;
        }
        target.setStrikes(context.argAsInt(1));
        context.msg(TL.COMMAND_STRIKES_CHANGED, target.getTag(), target.getStrikes());
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STRIKESET_DESCRIPTION;
    }

}
