package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.frame.fupgrades.FactionUpgradeFrame;
import com.massivecraft.factions.zcore.util.TL;

public class CmdUpgrades extends FCommand {

    /**
     * @author Illyria Team
     */

    public CmdUpgrades() {
        super();
        this.getAliases().addAll(Aliases.upgrades);

        this.setRequirements(new CommandRequirements.Builder(Permission.UPGRADES)
                .playerOnly()
                .memberOnly()
                .build());
    }

    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getFileManager().getUpgrades().getConfig().getBoolean("fupgrades.Enabled")) {
            context.fPlayer.msg(TL.COMMAND_UPGRADES_DISABLED);
            return;
        }

        new FactionUpgradeFrame(context.player, context.faction).openGUI(FactionsPlugin.getInstance());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_UPGRADES_DESCRIPTION;
    }

}
