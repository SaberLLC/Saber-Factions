package com.massivecraft.factions.cmd.logout;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.cmd.Aliases;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdLogout extends FCommand {

    public CmdLogout() {
        super();
        this.aliases.addAll(Aliases.logout);

        this.requirements = new CommandRequirements.Builder(Permission.LOGOUT)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        LogoutHandler handler = LogoutHandler.getByName(context.player.getName());

        if (handler.isLogoutActive(context.player)) {
            context.msg(TL.COMMAND_LOGOUT_ACTIVE);
            return;
        }

        handler.applyLogoutCooldown(context.player);
        context.msg(TL.COMMAND_LOGOUT_LOGGING, Conf.logoutCooldown);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_LOGOUT_DESCRIPTION;
    }
}
