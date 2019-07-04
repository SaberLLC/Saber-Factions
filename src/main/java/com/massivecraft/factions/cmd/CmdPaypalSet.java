package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.SaberFactions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPaypalSet extends FCommand {

    public CmdPaypalSet() {
        this.aliases.add("setpaypal");

        this.optionalArgs.put("faction", "yours");

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

        if (args.size() == 1) {
            if (isEmail(argAsString(0))) {
                myFaction.paypalSet(argAsString(0));
                msg(TL.COMMAND_PAYPALSET_SUCCESSFUL, argAsString(0));
            } else {
                msg(TL.COMMAND_PAYPALSET_NOTEMAIL, argAsString(0));
            }
        } else if (args.size() == 2) {
            if (fme.isAdminBypassing()) {
                Faction faction = argAsFaction(1);
                if (faction != null) {
                    if (isEmail(argAsString(0))) {
                        myFaction.paypalSet(argAsString(0));
                        msg(TL.COMMAND_PAYPALSET_ADMIN_SUCCESSFUL, faction.getTag(), argAsString(0));
                    } else {
                        msg(TL.COMMAND_PAYPALSET_ADMIN_FAILED, argAsString(0));
                    }
                }
            } else {
                msg(TL.GENERIC_NOPERMISSION, "set another factions paypal!");
            }
        } else {
            msg(SaberFactions.plugin.cmdBase.cmdPaypalSet.getUseageTemplate());
        }
    }

    private boolean isEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PAYPALSET_DESCRIPTION;
    }
}

