package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.spiral.ChunkProcessingContext;
import com.massivecraft.factions.util.spiral.SpiralTask;
import com.massivecraft.factions.util.spiral.generator.SquareSpiralGenerator;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdUnclaim extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdUnclaim() {
        this.getAliases().addAll(Aliases.unclaim_unclaim);

        this.getOptionalArgs().put("radius", "1");
        this.getOptionalArgs().put("faction", "yours");

        this.setRequirements(new CommandRequirements.Builder(Permission.UNCLAIM)
                .playerOnly()
                .withAction(PermissableAction.TERRITORY)
                .build());
    }

    @Override
    public void perform(final CommandContext context) {
        // Read and validate input
        int radius = context.argAsInt(0, 1); // Default to 1
        final Faction forFaction = context.argAsFaction(1, context.faction); // Default to own

        if (radius < 1) {
            context.msg(TL.COMMAND_CLAIM_INVALIDRADIUS);
            return;
        }

        if (radius < 2) {
            // single chunk
            context.fPlayer.attemptUnclaim(forFaction, FLocation.wrap(context.fPlayer), true);
        } else {
            // radius claim
            if (!Permission.CLAIM_RADIUS.has(context.sender, false)) {
                context.msg(TL.COMMAND_CLAIM_DENIED);
                return;
            }

            new SpiralTask(FLocation.wrap(context.fPlayer), radius, new SquareSpiralGenerator()) {
                private final int limit = Conf.radiusClaimFailureLimit - 1;
                private int failCount = 0;

                @Override
                public boolean work(ChunkProcessingContext ctx) {
                    FLocation fLocation = ctx.getFLocation();

                    boolean success = context.fPlayer.attemptUnclaim(forFaction, fLocation, true);
                    if (success) {
                        failCount = 0;
                    } else if (failCount++ >= limit) {
                        this.stop();
                        return false;
                    }

                    return true;
                }
            };
        }
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_UNCLAIM_DESCRIPTION;
    }

}
