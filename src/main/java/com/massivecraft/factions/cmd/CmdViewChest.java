package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdViewChest extends FCommand {

    /**
     * @author Driftay
     */

    public CmdViewChest() {
        super();
        this.aliases.addAll(Aliases.viewChest);

        this.requiredArgs.add("faction name");

        this.requirements = new CommandRequirements.Builder(Permission.VIEWCHEST)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("fchest.Enabled")) {
            context.msg(TL.GENERIC_DISABLED, "Faction Chests");
            return;
        }

        Faction myFaction = context.fPlayer.getFaction();

        Faction faction = context.argAsFaction(0, context.fPlayer == null ? null : myFaction);
        if (faction == null) {
            return;
        }
        context.player.openInventory(context.faction.getChestInventory());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_VIEWCHEST_DESCRIPTION;
    }
}

