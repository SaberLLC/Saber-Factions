package com.massivecraft.factions.shop;

import com.massivecraft.factions.P;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.logout.CmdLogout;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdShop extends FCommand {

    public CmdShop(){
            super();
            this.aliases.add("shop");
            this.disableOnLock = false;

            senderMustBePlayer = true;
            senderMustBeMember = true;
            senderMustBeModerator = false;
            senderMustBeColeader = false;
            senderMustBeAdmin = false;
        }


    @Override
    public void perform() {
        if(!P.p.getConfig().getBoolean("F-Shop.Enabled")){
            return;
        }
        ShopGUI.openShop(fme);
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
