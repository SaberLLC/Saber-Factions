package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.spiral.ChunkProcessingContext;
import com.massivecraft.factions.util.spiral.FoliaSpiralTask;
import com.massivecraft.factions.util.spiral.SpiralTask;
import com.massivecraft.factions.util.spiral.generator.SquareSpiralGenerator;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import java.util.concurrent.atomic.AtomicInteger;


public class CmdClaim extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdClaim() {
        super();
        this.getAliases().addAll(Aliases.claim_claim);

        //this.requiredArgs.add("");
        this.getOptionalArgs().put("radius", "1");
        this.getOptionalArgs().put("faction", "your");

        this.setRequirements(new CommandRequirements.Builder(Permission.CLAIM)
                .playerOnly()
                .build());
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

        if(radius > Conf.claimRadiusLimit && Conf.claimRadiusLimit != -1 && !context.fPlayer.isAdminBypassing()) {
            context.msg(TL.COMMAND_CLAIM_RADIUSOVER, Conf.claimRadiusLimit);
            return;
        }

        if (radius < 2) {
            FLocation claimLocation = FLocation.wrap(context.player.getLocation());
            if (context.fPlayer.attemptClaim(forFaction, claimLocation, true)) {
                ClaimCommandUtil.logClaim(forFaction, context.fPlayer, claimLocation);
            }
        } else {
            // radius claim
            if (!Permission.CLAIM_RADIUS.has(context.sender, true)) {
                return;
            }

            boolean batchMessages = ClaimCommandUtil.shouldBatchSuccessMessages();
            int startChunkX = context.player.getLocation().getChunk().getX();
            int startChunkZ = context.player.getLocation().getChunk().getZ();

            if (FactionsPlugin.isFolia()) {
                new FoliaSpiralTask(SpiralTask.buildFLocationQueue(FLocation.wrap(context.player), radius, new SquareSpiralGenerator())) {
                    private final int limit = Conf.radiusClaimFailureLimit - 1;
                    private final AtomicInteger failCount = new AtomicInteger(0);
                    private final AtomicInteger successfulClaims = new AtomicInteger(0);

                    @Override
                    protected void work(FLocation loc) {
                        boolean success = ClaimCommandUtil.attemptClaim(context, forFaction, loc, true, batchMessages);
                        if (success) {
                            failCount.set(0);
                            successfulClaims.incrementAndGet();
                            ClaimCommandUtil.logClaim(forFaction, context.fPlayer, loc);
                        } else if (failCount.getAndIncrement() >= limit) {
                            this.stop();
                        }
                    }

                    @Override
                    protected void finish() {
                        if (batchMessages) {
                            ClaimCommandUtil.broadcastClaimSummary(context, forFaction, successfulClaims.get(), startChunkX, startChunkZ);
                        }
                    }
                }.start();
            } else {
                new SpiralTask(FLocation.wrap(context.player), radius, new SquareSpiralGenerator()) {
                    private final int limit = Conf.radiusClaimFailureLimit - 1;
                    private int failCount = 0;
                    private int successfulClaims = 0;

                    @Override
                    public boolean work(ChunkProcessingContext ctx) {
                        FLocation fLocation = ctx.getFLocation();

                        boolean success = ClaimCommandUtil.attemptClaim(context, forFaction, fLocation, true, batchMessages);
                        if (success) {
                            failCount = 0;
                            successfulClaims++;
                            ClaimCommandUtil.logClaim(forFaction, context.fPlayer, fLocation);
                        } else if (failCount++ >= limit) {
                            this.stop();
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public void finish() {
                        if (batchMessages) {
                            ClaimCommandUtil.broadcastClaimSummary(context, forFaction, successfulClaims, startChunkX, startChunkZ);
                        }
                        super.finish();
                    }
                };
            }
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CLAIM_DESCRIPTION;
    }

}
