package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdAutoClaim extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdAutoClaim() {
        super();
        this.aliases.addAll(Aliases.claim_auto);

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction", "your");

        this.requirements = new CommandRequirements.Builder(Permission.AUTOCLAIM)
                .playerOnly()
                .withAction(PermissableAction.TERRITORY)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction forFaction = context.argAsFaction(0, context.faction);

        if (forFaction != context.fPlayer.getFaction()) {
            if (!context.fPlayer.isAdminBypassing()) {
                if (forFaction.getAccess(context.fPlayer, PermissableAction.TERRITORY) != Access.ALLOW) {
                    context.msg(TL.COMMAND_CLAIM_DENIED);
                    return;
                }
            }
        }

        if (forFaction == null || forFaction == context.fPlayer.getAutoClaimFor()) {
            context.fPlayer.setAutoClaimFor(null);
            context.msg(TL.COMMAND_AUTOCLAIM_DISABLED);
            return;
        }

        if (!context.fPlayer.canClaimForFaction(forFaction)) {
            if (context.faction == forFaction) {
                context.msg(TL.COMMAND_AUTOCLAIM_REQUIREDRANK, Role.MODERATOR.getTranslation());
            } else {
                context.msg(TL.COMMAND_AUTOCLAIM_OTHERFACTION, forFaction.describeTo(context.fPlayer));
            }

            return;
        }
        Faction at = Board.getInstance().getFactionAt(new FLocation(context.fPlayer.getPlayer().getLocation()));

        if (forFaction.isWarZone()) {
            context.fPlayer.setIsAutoWarClaimEnabled(true);
            context.msg(TL.COMMAND_AUTOCLAIM_ENABLED, forFaction.describeTo(context.fPlayer));
            if (FactionsPlugin.cachedRadiusClaim && context.fPlayer.attemptClaim(forFaction, context.player.getLocation(), true)) {
                context.fPlayer.getFaction().getFPlayersWhereOnline(true).forEach(f -> f.msg(TL.CLAIM_CLAIMED, context.fPlayer.describeTo(f, true), context.fPlayer.getFaction().describeTo(f), at.describeTo(f)));
            } else {
                context.fPlayer.attemptClaim(forFaction, context.fPlayer.getPlayer().getLocation(), true);
            }
            return;
        } else if (forFaction.isSafeZone()) {
            context.fPlayer.setIsAutoSafeClaimEnabled(true);
            context.msg(TL.COMMAND_AUTOCLAIM_ENABLED, forFaction.describeTo(context.fPlayer));
            if (FactionsPlugin.cachedRadiusClaim && context.fPlayer.attemptClaim(forFaction, context.player.getLocation(), true)) {
                context.fPlayer.getFaction().getFPlayersWhereOnline(true).forEach(f -> f.msg(TL.CLAIM_CLAIMED, context.fPlayer.describeTo(f, true), context.fPlayer.getFaction().describeTo(f), at.describeTo(f)));
            } else {
                context.fPlayer.attemptClaim(forFaction, context.fPlayer.getPlayer().getLocation(), true);
            }
            return;
        }

        context.fPlayer.setAutoClaimFor(forFaction);
        FactionsPlugin.instance.logFactionEvent(forFaction, FLogType.CHUNK_CLAIMS, context.fPlayer.getName(), CC.GreenB + "CLAIMED", "1", new FLocation(context.fPlayer.getPlayer().getLocation()).formatXAndZ(","));
        context.msg(TL.COMMAND_AUTOCLAIM_ENABLED, forFaction.describeTo(context.fPlayer));
        if (FactionsPlugin.cachedRadiusClaim && context.fPlayer.attemptClaim(forFaction, context.player.getLocation(), true)) {
            context.fPlayer.getFaction().getFPlayersWhereOnline(true).forEach(f -> f.msg(TL.CLAIM_CLAIMED, context.fPlayer.describeTo(f, true), context.fPlayer.getFaction().describeTo(f), at.describeTo(f)));
        } else {
            context.fPlayer.attemptClaim(forFaction, context.fPlayer.getPlayer().getLocation(), true);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_AUTOCLAIM_DESCRIPTION;
    }

}