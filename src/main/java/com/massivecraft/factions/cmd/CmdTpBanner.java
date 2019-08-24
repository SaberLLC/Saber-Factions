package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;

public class CmdTpBanner extends FCommand {
    public CmdTpBanner() {
        super();

        this.aliases.add("tpbanner");

        this.permission = Permission.TPBANNER.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;

    }

    @Override
    public void perform() {
        if (!P.p.getConfig().getBoolean("fbanners.Enabled")) {
            return;
        }

        if (FactionsBlockListener.bannerLocations.containsKey(fme.getTag())) {
            fme.msg(TL.COMMAND_TPBANNER_SUCCESS);
            this.doWarmUp(WarmUpUtil.Warmup.BANNER, TL.WARMUPS_NOTIFY_TELEPORT, "Banner", () -> me.teleport(FactionsBlockListener.bannerLocations.get(fme.getTag())), this.p.getConfig().getLong("warmups.f-banner", 0));
        } else {
            fme.msg(TL.COMMAND_TPBANNER_NOTSET);
        }

    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TPBANNER_DESCRIPTION;
    }
}
