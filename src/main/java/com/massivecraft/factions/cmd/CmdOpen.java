package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdOpen extends FCommand {

	public CmdOpen() {
		super();
		this.aliases.add("open");

		//this.requiredArgs.add("");
		this.optionalArgs.put("yes/no", "flip");

		this.permission = Permission.OPEN.node;
		this.disableOnLock = false;
		this.disableOnSpam = true;


		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeColeader = true;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostOpen, TL.COMMAND_OPEN_TOOPEN, TL.COMMAND_OPEN_FOROPEN)) {
			return;
		}

		if (!fme.isCooldownEnded("open")) {
			fme.msg(TL.COMMAND_ONCOOOLDOWN, fme.getCooldown("open"));
			return;
		}

		myFaction.setOpen(this.argAsBool(0, !myFaction.getOpen()));

		String open = myFaction.getOpen() ? TL.COMMAND_OPEN_OPEN.toString() : TL.COMMAND_OPEN_CLOSED.toString();

		// Inform
		for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
			if (fplayer.getFactionId().equals(myFaction.getId())) {
				fplayer.msg(TL.COMMAND_OPEN_CHANGES, fme.getName(), open);
				continue;
			}
			fplayer.msg(TL.COMMAND_OPEN_CHANGED, myFaction.getTag(fplayer.getFaction()), open);
		}
		fme.setCooldown("open", System.currentTimeMillis() + (P.p.getConfig().getInt("fcooldowns.f-open") * 1000));
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_OPEN_DESCRIPTION;
	}

}
