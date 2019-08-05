package com.massivecraft.factions.cmd.econ;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;


public class CmdMoneyTransferPf extends FCommand {

	public CmdMoneyTransferPf() {
		this.aliases.add("pf");

		this.requiredArgs.add("amount");
		this.requiredArgs.add("player");
		this.requiredArgs.add("faction");

		//this.optionalArgs.put("", "");

		this.permission = Permission.MONEY_P2F.node;
		this.isMoneyCommand = true;


		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeColeader = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		double amount = this.argAsDouble(0, 0d);
		EconomyParticipator from = this.argAsBestFPlayerMatch(1);
		if (from == null) {
			return;
		}
		EconomyParticipator to = this.argAsFaction(2);
		if (to == null) {
			return;
		}

		if (Conf.econFactionStartingBalance != 0 && (System.currentTimeMillis() - myFaction.getFoundedDate()) <= (Conf.econDenyWithdrawWhenMinutesAgeLessThan * 6000)) {
			msg("Your faction is too young to transfer money like this");
			return;
		}
		boolean success = Econ.transferMoney(fme, from, to, amount);

		if (success && Conf.logMoneyTransactions) {
			P.p.log(ChatColor.stripColor(P.p.txt.parse(TL.COMMAND_MONEYTRANSFERPF_TRANSFER.toString(), fme.getName(), Econ.moneyString(amount), from.describeTo(null), to.describeTo(null))));
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_MONEYTRANSFERPF_DESCRIPTION;
	}
}
