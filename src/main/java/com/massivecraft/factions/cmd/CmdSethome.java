package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSethome extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdSethome() {
        this.getAliases().addAll(Aliases.setHome);
        this.getOptionalArgs().put("faction tag", "mine");

        this.setRequirements(new CommandRequirements.Builder(Permission.SETHOME)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.SETHOME)
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        // Replace runTaskAsynchronously(...) with an AsyncScheduler call
        Bukkit.getAsyncScheduler().runDelayed(FactionsPlugin.instance, scheduledTask -> {
            if (!Conf.homesEnabled) {
                context.msg(TL.COMMAND_SETHOME_DISABLED);
                return;
            }

            Faction faction = context.argAsFaction(0, context.faction);
            if (faction == null) {
                return;
            }

            // Check territory ownership
            if (!Permission.BYPASS.has(context.player)
                && Conf.homesMustBeInClaimedTerritory
                && Board.getInstance().getFactionAt(FLocation.wrap(context.player)) != faction) {
                context.msg(TL.COMMAND_SETHOME_NOTCLAIMED);
                return;
            }

            // If they typed a faction argument, do additional checks
            if (!context.args.isEmpty()) {
                Faction target = context.argAsFaction(0);
                if (target == null) return;
                context.faction = target;
                if (target.getAccess(context.fPlayer, PermissableAction.SETHOME) != Access.ALLOW) {
                    context.fPlayer.msg(TL.GENERIC_FPERM_NOPERMISSION, "set faction home");
                    return;
                }
            }

            // If economy is enabled, handle any costs
            if (!context.payForCommand(Conf.econCostSethome, TL.COMMAND_SETHOME_TOSET, TL.COMMAND_SETHOME_FORSET)) {
                return;
            }

            // Actually set the home
            faction.setHome(context.player.getLocation());

            faction.msg(TL.COMMAND_SETHOME_SET, context.fPlayer.describeTo(context.faction, true));
            faction.sendMessage(FactionsPlugin.getInstance().cmdBase.cmdHome.getUsageTemplate(context));
            if (faction != context.faction) {
                context.msg(TL.COMMAND_SETHOME_SETOTHER, faction.getTag(context.fPlayer));
            }

        }, 0L, java.util.concurrent.TimeUnit.MILLISECONDS); // zero delay => immediate async
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETHOME_DESCRIPTION;
    }

}