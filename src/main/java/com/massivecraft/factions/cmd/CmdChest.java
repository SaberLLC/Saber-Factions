package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdChest extends FCommand {

    public CmdChest() {
        this.aliases.add("chest");

        //this.requiredArgs.add("");


        this.permission = Permission.CHEST.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {

        if (!P.p.getConfig().getBoolean("fchest.Enabled")) {
            fme.sendMessage("This command is disabled!");
            return;
        }
        Access access = fme.getFaction().getAccess(fme, PermissableAction.CHEST);
        if (access.equals(Access.DENY)) {
            fme.msg(TL.GENERIC_NOPERMISSION, "chest");
        }
        //debug Bukkit.broadcastMessage(fme.getFaction().getUpgrade("Chest") + "");

        me.openInventory(fme.getFaction().getChest());


    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_VAULT_DESCRIPTION;
    }
}
