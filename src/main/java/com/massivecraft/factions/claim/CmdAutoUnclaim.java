package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdAutoUnclaim extends FCommand {

    public CmdAutoUnclaim() {
        super();
        this.getAliases().add("autounclaim");

        //this.requiredArgs.add("");
        this.getOptionalArgs().put("faction", "your");

        this.setRequirements(new CommandRequirements.Builder(Permission.AUTOCLAIM)
                .playerOnly()
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        Faction forFaction = context.argAsFaction(0, context.faction);
        if (forFaction == null || forFaction == context.fPlayer.getAutoUnclaimFor()) {
            context.fPlayer.setAutoUnclaimFor(null);
            context.msg(TL.COMMAND_AUTOUNCLAIM_DISABLED);
            return;
        }

        if (!context.fPlayer.canClaimForFaction(forFaction)) {
            if (context.faction == forFaction) {
                context.msg(TL.CLAIM_CANTUNCLAIM, forFaction.describeTo(context.fPlayer));
            } else {
                context.msg(TL.COMMAND_AUTOUNCLAIM_OTHERFACTION, forFaction.describeTo(context.fPlayer));
            }

            return;
        }

        context.fPlayer.setAutoUnclaimFor(forFaction);

        context.msg(TL.COMMAND_AUTOUNCLAIM_ENABLED, forFaction.describeTo(context.fPlayer));
        context.fPlayer.attemptUnclaim(forFaction, FLocation.wrap(context.player.getLocation()), true);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_AUTOUNCLAIM_DESCRIPTION;
    }

}
