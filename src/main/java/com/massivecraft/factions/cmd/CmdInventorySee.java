package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.SaberFactions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Objects;

public class CmdInventorySee extends FCommand {

    public CmdInventorySee() {
        super();

        this.aliases.add("invsee");
        this.aliases.add("inventorysee");

        this.requiredArgs.add("member name");

        this.permission = Permission.INVSEE.node;
        this.disableOnLock = true;
        this.disableOnSpam = false;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (SaberFactions.plugin.getConfig().getBoolean("f-inventory-see.Enabled")) {
            fme.msg(TL.GENERIC_DISABLED);
        }

        Access use = myFaction.getAccess(fme, PermissableAction.TERRITORY);
        if (use == Access.DENY || (use == Access.UNDEFINED && !assertMinRole(Role.MODERATOR))) {
            fme.msg(TL.GENERIC_NOPERMISSION, "territory");
            return;
        }

        ArrayList<Player> fplayers = myFaction.getOnlinePlayers();

        FPlayer targetInv = argAsFPlayer(0);
        if (targetInv == null || !fplayers.contains(targetInv.getPlayer())) {
            fme.msg(TL.PLAYER_NOT_FOUND, Objects.requireNonNull(targetInv).toString());
            return;
        }

        Inventory inventory = Bukkit.createInventory(me, 36, targetInv.getName() + "'s Inventory");
        for (int i = 0; i < 36; i++)
            if (targetInv.getPlayer().getInventory().getItem(i) != null)
                inventory.setItem(i, targetInv.getPlayer().getInventory().getItem(i));

        me.openInventory(inventory);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_INVENTORYSEE_DESCRIPTION;
    }
}
