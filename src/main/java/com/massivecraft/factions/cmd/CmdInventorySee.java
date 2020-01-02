package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
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

    /**
     * @author Driftay
     */

    public CmdInventorySee() {
        super();

        this.aliases.addAll(Aliases.invsee);

        this.requiredArgs.add("member name");

        this.requirements = new CommandRequirements.Builder(Permission.INVSEE)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("f-inventory-see.Enabled")) {
            context.msg(TL.GENERIC_DISABLED, "Inventory See");
            return;
        }

        Access use = context.fPlayer.getFaction().getAccess(context.fPlayer, PermissableAction.TERRITORY);
        if (use == Access.DENY || (use == Access.UNDEFINED && !context.assertMinRole(Role.MODERATOR))) {
            context.msg(TL.GENERIC_NOPERMISSION, "see other faction members inventories");
            return;
        }

        ArrayList<Player> fplayers = context.fPlayer.getFaction().getOnlinePlayers();

        FPlayer targetInv = context.argAsFPlayer(0);
        if (targetInv == null || !fplayers.contains(targetInv.getPlayer())) {
            context.msg(TL.PLAYER_NOT_FOUND, Objects.requireNonNull(targetInv.getName()));
            return;
        }

        Inventory inventory = Bukkit.createInventory(context.player, 36, targetInv.getName() + "'s Inventory");
        for (int i = 0; i < 36; i++)
            if (targetInv.getPlayer().getInventory().getItem(i) != null)
                inventory.setItem(i, targetInv.getPlayer().getInventory().getItem(i));

        context.player.openInventory(inventory);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_INVENTORYSEE_DESCRIPTION;
    }
}
