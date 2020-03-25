package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 3/24/2020
 */
public class CmdDelHome extends FCommand {

    public CmdDelHome() {
        this.aliases.addAll(Aliases.delfHome);

        this.requirements = new CommandRequirements.Builder(Permission.DELHOME)
                .memberOnly()
                .withAction(PermissableAction.SETHOME)
                .build();
    }


    @Override
    public void perform(CommandContext context) {
        FactionsPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(FactionsPlugin.instance, () -> {
            //Check if homes are enabled
            if (!Conf.homesEnabled) {
                context.msg(TL.COMMAND_SETHOME_DISABLED);
                return;
            }
            //If They Don't Have Home
            if (!context.faction.hasHome()) {
                context.msg(TL.COMMAND_HOME_NOHOME.toString());
                context.msg(FactionsPlugin.getInstance().cmdBase.cmdSethome.getUsageTemplate(context));
                return;
            }

            context.faction.deleteHome();
            context.faction.msg(TL.COMMAND_DELHOME_SUCCESS, context.fPlayer.describeTo(context.faction, true));

        });
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DELHOME_DESCRIPTION;
    }
}
