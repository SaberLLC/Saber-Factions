package com.massivecraft.factions.cmd;

import com.massivecraft.factions.SaberFactions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPaypalSet extends FCommand {

	public CmdPaypalSet() {
		this.aliases.add("setpaypal");
		this.requiredArgs.add("email");

		this.permission = Permission.PAYPALSET.node;

		this.disableOnLock = false;
		this.senderMustBePlayer = true;
		this.senderMustBeMember = true;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = true;

	}
	@Override
	public void perform() {
		if (!SaberFactions.plugin.getConfig().getBoolean("fpaypal.Enabled")) {
			fme.msg(TL.GENERIC_DISABLED);
			return;
		}

		String paypal = this.argAsString(0);
		if(paypal == null)
			return;
		myFaction.paypalSet(paypal);
		fme.msg(TL.COMMAND_PAYPALSET_SUCCESSFUL, paypal);
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_PAYPALSET_DESCRIPTION;
	}
}

