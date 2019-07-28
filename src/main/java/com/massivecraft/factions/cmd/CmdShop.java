package com.massivecraft.factions.cmd;

import com.massivecraft.factions.shop.ShopGUI;
import com.massivecraft.factions.zcore.util.TL;

public class CmdShop extends FCommand {


    public CmdShop() {
        this.aliases.add("shop");
        this.senderMustBePlayer = true;
        this.senderMustBeColeader = true;
    }

    @Override
    public void perform() {
        ShopGUI shopGUI = new ShopGUI(p, fme);
        shopGUI.build();
        fme.getPlayer().openInventory(shopGUI.getInventory());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHOP_DESCRIPTION;
    }
}