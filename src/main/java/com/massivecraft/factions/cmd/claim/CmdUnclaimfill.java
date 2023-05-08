package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class CmdUnclaimfill extends FCommand {

    public CmdUnclaimfill() {

        // Aliases
        this.aliases.add("unclaimfill");
        this.aliases.add("ucf");

        // Args
        this.optionalArgs.put("limit", String.valueOf(Conf.maxFillClaimCount));
        this.optionalArgs.put("faction", "you");

        this.requirements = new CommandRequirements.Builder(Permission.UNCLAIM_FILL)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        // Args
        final int limit = context.argAsInt(0, Conf.maxFillClaimCount);

        if (limit > Conf.maxFillClaimCount) {
            context.msg(TL.COMMAND_UNCLAIMFILL_ABOVEMAX, Conf.maxFillClaimCount);
            return;
        }

        final Faction forFaction = context.argAsFaction(1, context.faction);
        Location location = context.player.getLocation();
        FLocation loc = FLocation.wrap(location);
        final boolean bypass = context.fPlayer.isAdminBypassing();

        Faction currentFaction = Board.getInstance().getFactionAt(loc);

        if (currentFaction != forFaction) {
            context.msg(TL.COMMAND_UNCLAIMFILL_NOTCLAIMED);
            return;
        }

        if (!bypass &&
                ((forFaction.isNormal() && forFaction.getAccess(context.fPlayer, PermissableAction.TERRITORY) != Access.ALLOW)
                                ||
                                (forFaction.isWarZone() && !Permission.MANAGE_WAR_ZONE.has(context.player))
                                ||
                                (forFaction.isSafeZone() && !Permission.MANAGE_SAFE_ZONE.has(context.player)))) {
            context.msg(TL.CLAIM_CANTUNCLAIM, forFaction.describeTo(context.fPlayer));
            return;
        }

        final double distance = Conf.maxFillClaimDistance;
        long startX = loc.getX();
        long startZ = loc.getZ();

        Set<FLocation> toClaim = new LinkedHashSet<>();
        Queue<FLocation> queue = new LinkedList<>();
        FLocation currentHead;
        queue.add(loc);
        toClaim.add(loc);
        while (!queue.isEmpty() && toClaim.size() <= limit) {
            currentHead = queue.poll();

            if (Math.abs(currentHead.getX() - startX) > distance || Math.abs(currentHead.getZ() - startZ) > distance) {
                context.msg(TL.COMMAND_UNCLAIMFILL_TOOFAR, distance);
                return;
            }

            addIf(toClaim, queue, currentHead.getRelative(0, 1), currentFaction);
            addIf(toClaim, queue, currentHead.getRelative(0, -1), currentFaction);
            addIf(toClaim, queue, currentHead.getRelative(1, 0), currentFaction);
            addIf(toClaim, queue, currentHead.getRelative(-1, 0), currentFaction);
        }

        if (toClaim.size() > limit) {
            context.msg(TL.COMMAND_UNCLAIMFILL_PASTLIMIT);
            return;
        }

        final int limFail = Conf.radiusClaimFailureLimit;
        Tracker tracker = new Tracker();
        long x = 0;
        long z = 0;
        for (FLocation currentLocation : toClaim) {
            if (this.attemptUnclaim(context, currentLocation, currentFaction, tracker)) {
                tracker.successes++;
                x += currentLocation.getX();
                z += currentLocation.getZ();
            } else {
                tracker.fails++;
            }
            if (tracker.fails >= limFail) {
                context.msg(TL.COMMAND_UNCLAIMFILL_TOOMUCHFAIL, tracker.fails);
                break;
            }
        }
        if (tracker.successes == 0) {
            context.msg(TL.COMMAND_UNCLAIMFILL_BYPASSCOMPLETE, 0);
            return;
        }
        x = x / ((long) tracker.successes);
        z = z / ((long) tracker.successes);
        if (bypass) {
            context.msg(TL.COMMAND_UNCLAIMFILL_BYPASSCOMPLETE, tracker.count());
        } else {
            if (tracker.refund != 0) {
                if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts) {
                    Econ.modifyMoney(context.faction, tracker.refund, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIM_FORUNCLAIM.toString());
                } else {
                    Econ.modifyMoney(context.fPlayer, tracker.refund, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIM_FORUNCLAIM.toString());
                }
            }
            currentFaction.msg(TL.COMMAND_UNCLAIMFILL_UNCLAIMED, context.fPlayer.describeTo(currentFaction, true), tracker.count(), x + "," + z);
        }
    }

    private static class Tracker {
        private int successes;
        private int fails;
        private double refund;

        private int count() {
            return successes + fails;
        }
    }

    private void addIf(Set<FLocation> toClaim, Queue<FLocation> queue, FLocation examine, Faction replacement) {
        if (Board.getInstance().getFactionAt(examine) == replacement && !toClaim.contains(examine)) {
            toClaim.add(examine);
            queue.add(examine);
        }
    }

    private boolean attemptUnclaim(CommandContext context, FLocation target, Faction targetFaction, Tracker tracker) {
        if (targetFaction.isSafeZone() || targetFaction.isWarZone()) {
            Board.getInstance().removeAt(target);
            if (Conf.logLandUnclaims) {
                Logger.print(TL.COMMAND_UNCLAIM_LOG.format(context.fPlayer.getName(), target.getCoordString(), targetFaction.getTag()), Logger.PrefixType.DEFAULT);
            }
            return true;
        }
        if (!context.fPlayer.isAdminBypassing() && targetFaction.getAccess(context.fPlayer, PermissableAction.TERRITORY) != Access.ALLOW) {
            context.msg(TL.CLAIM_CANTUNCLAIM, targetFaction.describeTo(context.fPlayer));
            return false;
        }
        LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(target, targetFaction, context.fPlayer);
        Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
        if (unclaimEvent.isCancelled()) {
            return false;
        }

        if (!context.fPlayer.isAdminBypassing() && Econ.shouldBeUsed()) {
            tracker.refund += Econ.calculateClaimRefund(context.faction.getLandRounded());
        }

        Board.getInstance().removeAt(target);

        if (Conf.logLandUnclaims) {
            Logger.print(TL.COMMAND_UNCLAIM_LOG.format(context.fPlayer.getName(), target.getCoordString(), targetFaction.getTag()), Logger.PrefixType.DEFAULT);
        }
        return true;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_UNCLAIMFILL_DESCRIPTION;
    }
}
