package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdViewChest extends FCommand {

    public CmdViewChest() {
        this.aliases.add("viewchest");
        this.aliases.add("viewpv");

        this.requiredArgs.add("faction name");


        this.permission = Permission.VIEWCHEST.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (!P.p.getConfig().getBoolean("fchest.Enabled")) {
            fme.msg(TL.GENERIC_DISABLED);
            return;
        }

        Faction faction = this.argAsFaction(0, fme == null ? null : myFaction);
        if (faction == null) {
            return;
        }
        me.openInventory(faction.getChestInventory());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_VIEWCHEST_DESCRIPTION;
    }
}

