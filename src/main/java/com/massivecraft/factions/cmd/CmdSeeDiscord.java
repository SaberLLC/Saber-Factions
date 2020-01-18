package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSeeDiscord extends FCommand {

    /**
     * @author Driftay
     */

    public CmdSeeDiscord() {
        this.aliases.addAll(Aliases.discord_see);

        this.optionalArgs.put("faction", "yours");

        this.requirements = new CommandRequirements.Builder(Permission.DISCORD)
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("fdiscord.Enabled")) {
            context.msg(TL.GENERIC_DISABLED, "Faction Discords");
            return;
        }


        if (context.args.size() == 0) {
            if (context.fPlayer.getFaction().getDiscord() == null) {
                context.msg(TL.COMMAND_DISCORD_NOTSET);
            } else {
                context.msg(TL.DISCORD_PLAYER_DISCORD, context.fPlayer.getFaction().getDiscord());
            }
        } else if (context.args.size() == 1) {
            if (context.fPlayer.isAdminBypassing()) {
                Faction faction = context.argAsFaction(0);
                if (faction != null) {
                    if (faction.getDiscord() == null) {
                        context.msg(TL.COMMAND_DISCORDSEE_FACTION_NOTSET, faction.getTag());
                    } else {
                        context.msg(TL.COMMAND_DISCORDSEE_FACTION_DISCORD.toString(), faction.getTag(), faction.getDiscord());
                    }
                }
            } else {
                context.msg(TL.GENERIC_NOPERMISSION, "see another factions discord.");
            }
        } else {
            context.msg(FactionsPlugin.getInstance().cmdBase.cmdSeeDiscord.getUsageTemplate(context));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DISCORDSEE_DESCRIPTION;
    }
}

