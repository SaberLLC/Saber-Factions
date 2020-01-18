package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

import java.text.DecimalFormat;

/**
 * @author Saser
 */
public class CmdDrain extends FCommand {
    public CmdDrain() {
        this.aliases.addAll(Aliases.drain);
        this.requirements = new CommandRequirements.Builder(Permission.DRAIN)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.DRAIN)
                .build();
    }


    @Override
    public void perform(CommandContext context) {
        if (!Conf.factionsDrainEnabled) {
            context.fPlayer.msg(TL.GENERIC_DISABLED, "Factions Drain");
            return;
        }

        double totalBalance = 0;

        for (FPlayer fPlayer : context.faction.getFPlayers()) {
            if (context.faction.getFPlayers().size() == 1) {
                context.fPlayer.msg(TL.COMMAND_DRAIN_NO_PLAYERS);
                return;
            }
            if (FPlayers.getInstance().getByPlayer(context.player).equals(fPlayer)) {
                continue; // skip the command executor
            }
            double balance = FactionsPlugin.getInstance().getEcon().getBalance(fPlayer.getPlayer());
            if (balance > 0) {
                FactionsPlugin.getInstance().getEcon().depositPlayer(context.player, balance);
                FactionsPlugin.getInstance().getEcon().withdrawPlayer(fPlayer.getPlayer(), balance);
                totalBalance = (totalBalance + balance);
            }
        }
        context.fPlayer.msg(TL.COMMAND_DRAIN_RECIEVED_AMOUNT, commas(totalBalance));
    }

    public String commas(final double amount) {
        final DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(amount);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DRAIN_DESCRIPTION;
    }
}
