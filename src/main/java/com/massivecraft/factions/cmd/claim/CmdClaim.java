package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.util.SpiralTask;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;


public class CmdClaim extends FCommand {

    /**
     * @author FactionsUUID Team
     */

    public CmdClaim() {
        super();
        this.aliases.add("claim");

        //this.requiredArgs.add("");
        this.optionalArgs.put("radius", "1");
        this.optionalArgs.put("faction", "your");

        this.requirements = new CommandRequirements.Builder(Permission.CLAIM)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {

        // Read and validate input
        int radius = context.argAsInt(0, 1); // Default to 1
        final Faction forFaction = context.argAsFaction(1, context.faction); // Default to own

        if (!context.fPlayer.isAdminBypassing()) {
            if (!(context.fPlayer.getFaction().equals(forFaction) && context.fPlayer.getRole() == Role.LEADER)) {
                if (forFaction.getAccess(context.fPlayer, PermissableAction.TERRITORY) != Access.ALLOW) {
                    context.msg(TL.COMMAND_CLAIM_DENIED);
                    return;
                }
            }
        }


        if (radius < 1) {
            context.msg(TL.COMMAND_CLAIM_INVALIDRADIUS);
            return;
        }

        if (radius < 2) {
            // single chunk
            context.fPlayer.attemptClaim(forFaction, context.player.getLocation(), true);
            FactionsPlugin.instance.logFactionEvent(forFaction, FLogType.CHUNK_CLAIMS, context.fPlayer.getName(), CC.GreenB + "CLAIMED", "1", (new FLocation(context.fPlayer.getPlayer().getLocation())).formatXAndZ(","));
        } else {
            // radius claim
            if (!Permission.CLAIM_RADIUS.has(context.sender, true)) {
                return;
            }

            new SpiralTask(new FLocation(context.player), radius) {
                private final int limit = Conf.radiusClaimFailureLimit - 1;
                private int failCount = 0;

                @Override
                public boolean work() {
                    boolean success = context.fPlayer.attemptClaim(forFaction, this.currentLocation(), true);
                    if (success) {
                        failCount = 0;
                        FactionsPlugin.instance.logFactionEvent(forFaction, FLogType.CHUNK_CLAIMS, context.fPlayer.getName(), CC.GreenB + "CLAIMED", "1", (new FLocation(context.fPlayer.getPlayer().getLocation())).formatXAndZ(","));
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
        return TL.COMMAND_CLAIM_DESCRIPTION;
    }

}
