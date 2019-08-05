package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdClaimAt extends FCommand {

	public CmdClaimAt() {
		super();
		this.aliases.add("claimat");

		this.requiredArgs.add("world");
		this.requiredArgs.add("x");
		this.requiredArgs.add("z");

		this.permission = Permission.CLAIMAT.node;
		this.disableOnLock = true;


		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeColeader = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform() {
		int x = argAsInt(1);
		int z = argAsInt(2);
		FLocation location = new FLocation(argAsString(0), x, z);
		if (!((fme.getPlayer().getLocation().getX() + (x * 16)) > (fme.getPlayer().getLocation().getX() + (Conf.mapWidth * 16))) &&
				!((fme.getPlayer().getLocation().getZ() + (z * 16)) > (fme.getPlayer().getLocation().getZ() + (Conf.mapHeight * 16)))) {
			fme.attemptClaim(myFaction, location, true);
		} else fme.msg(TL.COMMAND_CLAIM_DENIED);
	}

	@Override
	public TL getUsageTranslation() {
		return null;
	}
}
