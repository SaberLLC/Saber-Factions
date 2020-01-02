package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdStrikesInfo extends FCommand {

    /**
     * @author Driftay
     */

    public CmdStrikesInfo() {
        super();
        this.aliases.addAll(Aliases.strikes_info);
        this.optionalArgs.put("target", "faction");

        this.requirements = new CommandRequirements.Builder(Permission.SETSTRIKES)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction target = context.argAsFaction(0);
        if (target == null) target = context.faction;
        if (target.isSystemFaction()) {
            context.msg(TL.COMMAND_STRIKES_TARGET_INVALID, context.argAsString(0));
            return;
        }
        context.msg(TL.COMMAND_STRIKES_INFO, target.getTag(), target.getStrikes());
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STRIKESINFO_DESCRIPTION;
    }


}
