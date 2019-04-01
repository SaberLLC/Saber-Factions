package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.SavageFactions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPaypalSee extends FCommand {
	public CmdPaypalSee() {
		aliases.add("seepaypal");

		requiredArgs.add("faction");

		permission = Permission.ADMIN.node;

		disableOnLock = false;
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeColeader = false;
		senderMustBeAdmin = false;

	}
   	@Override
	public void perform() {
		if (!SavageFactions.plugin.getConfig().getBoolean("fpaypal.Enabled")) {
			fme.msg(TL.GENERIC_DISABLED);
			return;
		}
			Faction faction = argAsFaction(0);

			if (faction != null)
				return;

				if (!faction.isWilderness() && !faction.isSafeZone() && !faction.isWarZone()) {
					fme.msg(TL.COMMAND_PAYPALSEE_FACTION_NOFACTION.toString(), me.getName());
					return;
				}
					if (faction.getPaypal() != null) {
						fme.msg(TL.COMMAND_PAYPALSEE_FACTION_PAYPAL.toString(), faction.getTag(), faction.getPaypal());
					} else {
						fme.msg(TL.COMMAND_PAYPALSEE_FACTION_NOTSET.toString(), faction.getTag(), faction.getPaypal());
					}
				}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_PAYPALSEE_DESCRIPTION;
	}
}


