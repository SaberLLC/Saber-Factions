package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdStealth extends FCommand {
	public CmdStealth() {
		this.aliases.add("ninja");
		this.aliases.add("stealth");
		this.permission = Permission.STEALTH.node;

		this.disableOnLock = true;

		this.senderMustBePlayer = true;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;

	}

	public void perform() {
		Faction faction = fme.getFaction();
		if (faction != null && !faction.getId().equalsIgnoreCase("0") && !faction.getId().equalsIgnoreCase("none") && !faction.getId().equalsIgnoreCase("safezone") && !faction.getId().equalsIgnoreCase("warzone")) {
			fme.setStealth(!fme.isStealthEnabled());
			fme.msg(fme.isStealthEnabled() ? TL.COMMAND_STEALTH_ENABLE : TL.COMMAND_STEALTH_DISABLE);
		} else {
			fme.msg(TL.COMMAND_STEALTH_MUSTBEMEMBER);
		}
	}

	public TL getUsageTranslation() {
		return TL.COMMAND_STEALTH_DESCRIPTION;
	}
}
