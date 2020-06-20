package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

public class CmdUnclaimall extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    //TODO: Add UnclaimAll Confirmation GUI
    public CmdUnclaimall() {
        this.aliases.addAll(Aliases.unclaim_all_unsafe);

        this.optionalArgs.put("faction", "yours");

        this.requirements = new CommandRequirements.Builder(Permission.UNCLAIM_ALL)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.TERRITORY) //TODO: Add Unclaimall PermissableAction
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction target = context.faction;
        if (context.args.size() == 1) {
            target = context.argAsFaction(0);
            if (target == null) {
                context.msg(TL.GENERIC_NOFACTION_FOUND);
                return;
            }

            if (!context.fPlayer.isAdminBypassing()) {
                context.msg(TL.ACTIONS_NOPERMISSION.toString().replace("{faction}", target.getTag()).replace("{action}", "unclaimall land"));
                return;
            }

            Board.getInstance().unclaimAll(target.getId());
            context.faction.msg(TL.COMMAND_UNCLAIMALL_LOG, context.fPlayer.describeTo(target, true), target.getTag());
            if (Conf.logLandUnclaims)
                FactionsPlugin.getInstance().log(TL.COMMAND_UNCLAIMALL_LOG.format(context.fPlayer.getName(), context.faction.getTag()));
            return;

        }
        if (Econ.shouldBeUsed()) {
            double refund = Econ.calculateTotalLandRefund(target.getLandRounded());
            if (!Econ.modifyMoney(target, refund, TL.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIMALL_FORUNCLAIM.toString())) {
                return;
            }
        }

        LandUnclaimAllEvent unclaimAllEvent = new LandUnclaimAllEvent(target, context.fPlayer);
        Bukkit.getScheduler().runTask(FactionsPlugin.getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(unclaimAllEvent));
        if (unclaimAllEvent.isCancelled()) {
            return;
        }
        int unclaimed = target.getAllClaims().size();


        Board.getInstance().unclaimAll(target.getId());
        context.faction.msg(TL.COMMAND_UNCLAIMALL_UNCLAIMED, context.fPlayer.describeTo(context.faction, true));
        FactionsPlugin.instance.logFactionEvent(context.faction, FLogType.CHUNK_CLAIMS, context.fPlayer.getName(), CC.RedB + "UNCLAIMED", String.valueOf(unclaimed), new FLocation(context.fPlayer.getPlayer().getLocation()).formatXAndZ(","));

        if (Conf.logLandUnclaims) {
            FactionsPlugin.getInstance().log(TL.COMMAND_UNCLAIMALL_LOG.format(context.fPlayer.getName(), context.faction.getTag()));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_UNCLAIMALL_DESCRIPTION;
    }

}

