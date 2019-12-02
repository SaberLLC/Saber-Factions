package com.massivecraft.factions.shop;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdShop extends FCommand {

    /**
     * @author Driftay
     */

    public CmdShop() {
        super();
        this.aliases.add("shop");
        this.requirements = new CommandRequirements.Builder(Permission.SHOP)
                .memberOnly()
                .playerOnly()
                .build();
    }


    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("F-Shop.Enabled")) {
            return;
        }
        new ShopGUIFrame(context.faction).buildGUI(context.fPlayer);
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
