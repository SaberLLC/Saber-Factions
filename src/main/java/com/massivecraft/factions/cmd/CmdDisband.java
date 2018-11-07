package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.SavageFactions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

import java.util.HashMap;


public class CmdDisband extends FCommand {


    private static HashMap<String, String> disbandMap = new HashMap<>();


    public CmdDisband() {
        super();
        this.aliases.add("disband");

        //this.requiredArgs.add("");
        this.optionalArgs.put("faction tag", "yours");

        this.permission = Permission.DISBAND.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        // The faction, default to your own.. but null if console sender.
        Faction faction = this.argAsFaction(0, fme == null ? null : myFaction);
        if (faction == null) {
            return;
        }

        boolean isMyFaction = fme != null && faction == myFaction;


        if (!fme.isAdminBypassing()) {
            Access access = faction.getAccess(fme, PermissableAction.DISBAND);
            if (fme.getRole() != Role.LEADER && access != Access.ALLOW) {
                fme.msg(TL.GENERIC_FPERM_NOPERMISSION, "disband " + faction.getTag());
                return;
            }
        }

        if (!faction.isNormal()) {
            msg(TL.COMMAND_DISBAND_IMMUTABLE.toString());
            return;
        }
        if (faction.isPermanent()) {
            msg(TL.COMMAND_DISBAND_MARKEDPERMANENT.toString());
            return;
        }


        // check for tnt before disbanding.

        if (!disbandMap.containsKey(me.getUniqueId().toString()) && faction.getTnt() > 0) {
            msg(TL.COMMAND_DISBAND_CONFIRM.toString().replace("{tnt}", faction.getTnt() + ""));
            disbandMap.put(me.getUniqueId().toString(), faction.getId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(SavageFactions.plugin, new Runnable() {
                @Override
                public void run() {
                    disbandMap.remove(me.getUniqueId().toString());
                }
            }, 200L);
        } else {
            //Check if the faction we asked confirmation for is the one being disbanded.
            if (faction.getId().equals(disbandMap.get(me.getUniqueId().toString())) || faction.getTnt() == 0) {
                faction.disband(me);
            }
        }


    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DISBAND_DESCRIPTION;
    }
}
