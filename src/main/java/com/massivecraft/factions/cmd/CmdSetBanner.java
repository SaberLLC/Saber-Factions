package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetBanner extends FCommand {

	public CmdSetBanner() {
		super();
		aliases.add("setbanner");

		permission = Permission.BANNER.node;

		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = true;


	}

	public void perform() {
		if (!me.getItemInHand().getType().toString().contains("BANNER")) {
			fme.msg(TL.COMMAND_SETBANNER_NOTBANNER);
			return;
		}

		fme.getFaction().setBannerPattern(me.getItemInHand());
		fme.msg(TL.COMMAND_SETBANNER_SUCCESS);


	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_SETBANNER_DESCRIPTION;
	}

}
