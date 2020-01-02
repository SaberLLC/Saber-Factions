package com.massivecraft.factions.cmd.chest;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdChest extends FCommand {

    /**
     * @author Illyria Team
     */

    public CmdChest() {
        this.aliases.addAll(Aliases.chest);

        this.requirements = new CommandRequirements.Builder(Permission.CHEST)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.CHEST)
                .build();
    }

    @Override
    public void perform(CommandContext context) {


        if (!FactionsPlugin.getInstance().getConfig().getBoolean("fchest.Enabled")) {
            context.msg(TL.GENERIC_DISABLED, "Faction Chests");
            return;
        }
        // This permission check is way too explicit but it's clean
        context.fPlayer.setInFactionsChest(true);
        context.player.openInventory(context.faction.getChestInventory());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_VAULT_DESCRIPTION;
    }
}
