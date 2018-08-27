package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

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
        if (myFaction != null && !myFaction.isWilderness() && !myFaction.isSafeZone() && !myFaction.isWarZone() && myFaction.isNormal()) {


            // Sends Enable/Disable Message
            if (fme.isStealthEnabled()) {
                fme.setStealth(false);
            } else {
                /* The FPlayer#takeMoney method calls the FPlayer#hasMoney method beforehand to check if the amount
                * can be withdrawn successfully.
                * The FPlayer#hasMoney method already sends a deny message so there isn't a need to send another.
                * Basically the takeMoney is an all in one solution for taking money :)
                */
                fme.takeMoney(P.p.getConfig().getInt("stealth-cost"));
                fme.setStealth(true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, new Runnable() {
                    @Override
                    public void run() {
                        if (fme.isStealthEnabled()) {
                            fme.setStealth(false);
                            fme.msg(TL.COMMAND_STEALTH_DISABLE);
                        }
                    }
                    // We multiplied by 20 here because the value is in ticks.
                }, P.p.getConfig().getInt("stealth-timeout") * 20);
            }

            fme.sendMessage(fme.isStealthEnabled() ? TL.COMMAND_STEALTH_ENABLE.toString().replace("{timeout}", P.p.getConfig().getInt("stealth-timeout") + "") : TL.COMMAND_STEALTH_DISABLE.toString());
        } else {
            fme.msg(TL.COMMAND_STEALTH_MUSTBEMEMBER);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STEALTH_DESCRIPTION;
    }

}
