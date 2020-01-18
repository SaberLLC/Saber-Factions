package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPaypalSee extends FCommand {

    /**
     * @author Driftay
     */

    public CmdPaypalSee() {
        this.aliases.addAll(Aliases.paypal_see);

        this.optionalArgs.put("faction", "yours");

        this.requirements = new CommandRequirements.Builder(Permission.PAYPAL)
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("fpaypal.Enabled")) {
            context.msg(TL.GENERIC_DISABLED, "Faction Paypals");
            return;
        }


        if (context.args.size() == 0) {
            if (context.fPlayer.getFaction().getPaypal() == null) {
                context.msg(TL.COMMAND_PAYPAL_NOTSET);
            } else {
                context.msg(TL.PAYPALSEE_PLAYER_PAYPAL, context.fPlayer.getFaction().getPaypal());
            }
        } else if (context.args.size() == 1) {
            if (context.fPlayer.isAdminBypassing()) {
                Faction faction = context.argAsFaction(0);
                if (faction != null) {
                    if (faction.getPaypal() == null) {
                        context.msg(TL.COMMAND_PAYPALSEE_FACTION_NOTSET, faction.getTag());
                    } else {
                        context.msg(TL.COMMAND_PAYPALSEE_FACTION_PAYPAL.toString(), faction.getTag(), faction.getPaypal());
                    }
                }
            } else {
                context.msg(TL.GENERIC_NOPERMISSION, "see another factions paypal.");
            }
        } else {
            context.msg(FactionsPlugin.getInstance().cmdBase.cmdPaypalSee.getUsageTemplate(context));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PAYPALSEE_DESCRIPTION;
    }
}


