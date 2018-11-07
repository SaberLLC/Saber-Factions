package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.SavageFactions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

public class CmdVault extends FCommand {

    public CmdVault() {
        this.aliases.add("vault");

        //this.requiredArgs.add("");


        this.permission = Permission.VAULT.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {

        if (!SavageFactions.plugin.getConfig().getBoolean("fvault.Enabled")) {
            fme.sendMessage("This command is disabled!");
            return;
        }
        Access access = fme.getFaction().getAccess(fme, PermissableAction.VAULT);
        if (access.equals(Access.DENY)) {
            fme.msg(TL.GENERIC_NOPERMISSION, "vault");
            return;
        }

        if (fme.isInVault()) {
            me.closeInventory();
            return;
        }
        fme.setInVault(true);
        Location vaultLocation = fme.getFaction().getVault();
        if (vaultLocation == null) {
            fme.msg(TL.COMMAND_VAULT_INVALID);
            return;
        }
        FLocation vaultFLocation = new FLocation(vaultLocation);
        if (Board.getInstance().getFactionAt(vaultFLocation) != fme.getFaction()) {
            fme.getFaction().setVault(null);
            fme.msg(TL.COMMAND_VAULT_INVALID);
            return;
        }
        if (vaultLocation.getBlock().getType() != Material.CHEST) {
            fme.getFaction().setVault(null);
            fme.msg(TL.COMMAND_VAULT_INVALID);
            return;
        }
        Chest chest = (Chest) vaultLocation.getBlock().getState();
        Inventory chestInv = chest.getBlockInventory();
        fme.msg(TL.COMMAND_VAULT_OPENING);
        me.openInventory(chestInv);


    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_VAULT_DESCRIPTION;
    }
}
