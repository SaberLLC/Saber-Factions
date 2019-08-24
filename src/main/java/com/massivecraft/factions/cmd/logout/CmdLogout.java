package com.massivecraft.factions.cmd.logout;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdLogout extends FCommand {

    public CmdLogout() {
        super();
        this.aliases.add("logout");

        this.permission = Permission.LOGOUT.node;
        this.disableOnLock = true;
        this.disableOnSpam = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        LogoutHandler handler = LogoutHandler.getByName(fme.getPlayer().getName());

        if (handler.isLogoutActive(fme.getPlayer())) {
            fme.msg(TL.COMMAND_LOGOUT_ACTIVE);
            return;
        }

        handler.applyLogoutCooldown(fme.getPlayer());
        fme.msg(TL.COMMAND_LOGOUT_LOGGING, Conf.logoutCooldown);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_LOGOUT_DESCRIPTION;
    }
}
