package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPaypalSee extends FCommand {
	public CmdPaypalSee() {
		aliases.add("seepaypal");
		aliases.add("paypal");

		optionalArgs.put("faction", "yours");

		permission = Permission.PAYPAL.node;

		disableOnLock = false;

		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeColeader = false;
		senderMustBeAdmin = true;

	}
   	@Override
	public void perform() {
		if (!P.p.getConfig().getBoolean("fpaypal.Enabled")) {
			fme.msg(TL.GENERIC_DISABLED);
			return;
		}

		if (args.size() == 0) {
			if (myFaction.getPaypal().isEmpty()) {
				msg(TL.COMMAND_PAYPAL_NOTSET);
			} else {
				msg(TL.PAYPALSEE_PLAYER_PAYPAL, myFaction.getPaypal());
			}
		} else if (args.size() == 1) {
			if (fme.isAdminBypassing()) {
				Faction faction = argAsFaction(0);
				if (faction != null) {
					if (faction.getPaypal().isEmpty()) {
						msg(TL.COMMAND_PAYPALSEE_FACTION_NOTSET, faction.getTag());
					} else {
						msg(TL.COMMAND_PAYPALSEE_FACTION_PAYPAL.toString(), faction.getTag(), faction.getPaypal());
					}
				}
			} else {
				msg(TL.GENERIC_NOPERMISSION, "see another factions paypal.");
			}
		} else {
			msg(P.p.cmdBase.cmdPaypalSee.getUseageTemplate());
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_PAYPALSEE_DESCRIPTION;
	}
}


