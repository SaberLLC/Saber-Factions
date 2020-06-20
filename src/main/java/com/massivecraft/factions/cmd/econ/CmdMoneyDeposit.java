package com.massivecraft.factions.cmd.econ;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;


public class CmdMoneyDeposit extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdMoneyDeposit() {
        super();
        this.aliases.addAll(Aliases.money_deposit);

        this.requiredArgs.add("amount");
        this.optionalArgs.put("faction", "yours");

        this.requirements = new CommandRequirements.Builder(Permission.MONEY_DEPOSIT)
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        double amount = context.argAsDouble(0, 0d);
        EconomyParticipator faction = context.argAsFaction(1, context.faction);

        if (amount <= 0) {
            return;
        }

        if (faction == null) {
            return;
        }
        boolean success = Econ.transferMoney(context.fPlayer, context.fPlayer, faction, amount);

        if (success && Conf.logMoneyTransactions) {
            FactionsPlugin.getInstance().log(ChatColor.stripColor(FactionsPlugin.getInstance().txt.parse(TL.COMMAND_MONEYDEPOSIT_DEPOSITED.toString(), context.fPlayer.getName(), Econ.moneyString(amount), faction.describeTo(null))));
            FactionsPlugin.instance.logFactionEvent(context.faction, FLogType.BANK_EDIT, context.fPlayer.getName(), ChatColor.GREEN + ChatColor.BOLD.toString() + "DEPOSITED", amount + "");

        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYDEPOSIT_DESCRIPTION;
    }

}

