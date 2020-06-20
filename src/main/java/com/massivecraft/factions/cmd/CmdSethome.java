package com.massivecraft.factions.cmd;

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
        this.aliases.addAll(Aliases.setHome);
        this.optionalArgs.put("faction tag", "mine");

        this.requirements = new CommandRequirements.Builder(Permission.SETHOME)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.SETHOME)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        FactionsPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(FactionsPlugin.instance, () -> {
            if (!Conf.homesEnabled) {
                context.msg(TL.COMMAND_SETHOME_DISABLED);
                return;
            }

            Faction faction = context.argAsFaction(0, context.faction);
            if (faction == null) {
                return;
            }

            // Can the player set the faction home HERE?
            if (!Permission.BYPASS.has(context.player) &&
                    Conf.homesMustBeInClaimedTerritory &&
                    Board.getInstance().getFactionAt(new FLocation(context.player)) != faction) {
                context.msg(TL.COMMAND_SETHOME_NOTCLAIMED);
                return;
            }

            if (!context.args.isEmpty()) {
                Faction target = context.argAsFaction(0);
                if (target == null) return;
                context.faction = target;
                if (target.getAccess(context.fPlayer, PermissableAction.SETHOME) != Access.ALLOW) {
                    context.fPlayer.msg(TL.GENERIC_FPERM_NOPERMISSION, "set faction home");
                    return;
                }
            }

            // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
            if (!context.payForCommand(Conf.econCostSethome, TL.COMMAND_SETHOME_TOSET, TL.COMMAND_SETHOME_FORSET)) {
                return;
            }

            faction.setHome(context.player.getLocation());

            faction.msg(TL.COMMAND_SETHOME_SET, context.fPlayer.describeTo(context.faction, true));
            faction.sendMessage(FactionsPlugin.getInstance().cmdBase.cmdHome.getUsageTemplate(context));
            if (faction != context.faction) {
                context.msg(TL.COMMAND_SETHOME_SETOTHER, faction.getTag(context.fPlayer));
            }
        });
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETHOME_DESCRIPTION;
    }

}