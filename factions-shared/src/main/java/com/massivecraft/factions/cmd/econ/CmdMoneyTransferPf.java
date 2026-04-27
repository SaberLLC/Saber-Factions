package com.massivecraft.factions.cmd.econ;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.util.TL;


public class CmdMoneyTransferPf extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    public CmdMoneyTransferPf() {
        this.getAliases().addAll(Aliases.money_transfer_Pf);

        this.getRequiredArgs().add("amount");
        this.getRequiredArgs().add("player");
        this.getRequiredArgs().add("faction");

        this.setRequirements(new CommandRequirements.Builder(Permission.MONEY_P2F).build());
    }

    @Override
    public void perform(CommandContext context) {
        double amount = context.argAsDouble(0, 0d);

        if (amount <= 0) {
            return;
        }

        EconomyParticipator from = context.argAsBestFPlayerMatch(1);
        if (from == null) {
            return;
        }
        EconomyParticipator to = context.argAsFaction(2);
        if (to == null) {
            return;
        }

        boolean success = Econ.transferMoney(context.fPlayer, from, to, amount);

        if (success && Conf.logMoneyTransactions) {
            Logger.printArgs(TL.COMMAND_MONEYTRANSFERPF_TRANSFER.toString(), Logger.PrefixType.WARNING, context.fPlayer.getName(), Econ.moneyString(amount), from.describeTo(null), to.describeTo(null));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYTRANSFERPF_DESCRIPTION;
    }
}
