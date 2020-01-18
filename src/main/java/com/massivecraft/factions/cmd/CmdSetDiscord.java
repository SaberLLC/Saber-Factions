package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetDiscord extends FCommand {

    /**
     * @author Driftay
     */

    public CmdSetDiscord() {
        super();
        this.aliases.addAll(Aliases.discord_set);

        this.optionalArgs.put("faction", "yours");

        this.requiredArgs.add("link");
        this.requirements = new CommandRequirements.Builder(Permission.SETDISCORD)
                .playerOnly()
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!FactionsPlugin.getInstance().getConfig().getBoolean("fdiscord.Enabled")) {
            context.fPlayer.msg(TL.GENERIC_DISABLED, "discord");
            return;
        }
        if (context.fPlayer.getRole() != Role.LEADER && !context.fPlayer.isAdminBypassing()) {
            //TODO: Create f perm for this
            context.msg(TL.GENERIC_NOPERMISSION, "set your factions Discord!");
            return;
        }

        if (context.args.size() == 1) {
            if (isDiscordInvite(context.argAsString(0))) {
                context.fPlayer.getFaction().setDiscord(context.argAsString(0));
                context.msg(TL.COMMAND_DISCORDSET_SUCCESSFUL, context.argAsString(0));
            } else {
                context.msg(TL.COMMAND_DISCORDSET_NOTEMAIL, context.argAsString(0));
            }
        } else if (context.args.size() == 2) {
            if (context.fPlayer.isAdminBypassing()) {
                Faction faction = context.argAsFaction(1);
                if (faction != null) {
                    if (isDiscordInvite(context.argAsString(0))) {
                        context.fPlayer.getFaction().setDiscord(context.argAsString(0));
                        context.msg(TL.COMMAND_DISCORDSET_ADMIN_SUCCESSFUL, faction.getTag(), context.argAsString(0));
                    } else {
                        context.msg(TL.COMMAND_DISCORDSET_ADMIN_FAILED, context.argAsString(0));
                    }
                }
            } else {
                context.msg(TL.GENERIC_NOPERMISSION, "set another factions discord link!");
            }
        } else {
            context.msg(FactionsPlugin.getInstance().cmdBase.cmdSetDiscord.getUsageTemplate(context));

        }
    }

    private boolean isDiscordInvite(String invite) {
        return invite.contains("discord.gg") || invite.contains("discord.me");
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DISCORDSET_DESCRIPTION;
    }
}
