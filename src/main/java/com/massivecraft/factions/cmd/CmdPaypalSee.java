package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;

public class CmdPaypalSee extends FCommand{
    public CmdPaypalSee() {
        aliases.add("seepaypal");
        aliases.add("getpaypal");
        requiredArgs.add("faction");
        permission = Permission.ADMIN.node;
        disableOnLock = false;
        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }

    public void perform() {
        if (!P.p.getConfig().getBoolean("fpaypal.Enabled")) {
            fme.msg(TL.GENERIC_DISABLED);
        } else {
            Faction faction = argAsFaction(0);
            String paypal = argAsString(1);

            if (faction != null) {
                if (!faction.isWilderness() && !faction.isSafeZone() && !faction.isWarZone()) {
                    if (faction.getPaypal() != null) {
                        fme.msg(TL.COMMAND_PAYPALSEE_FACTION_PAYPAL.toString(), faction.getTag(), faction.getPaypal());
                    } else {
                        fme.msg(TL.COMMAND_PAYPALSEE_FACTION_NOTSET.toString(), faction.getTag(), faction.getPaypal());
                    }

                } else {
                    fme.msg(TL.COMMAND_PAYPALSEE_FACTION_NOFACTION.toString(), me.getName());
                }
            }
        }
    }

    public TL getUsageTranslation() {
        return TL.COMMAND_PAYPALSEE_DESCRIPTION;
    }
}


