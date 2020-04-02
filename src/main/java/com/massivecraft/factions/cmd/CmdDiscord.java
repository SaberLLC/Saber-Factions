package com.massivecraft.factions.cmd;

import com.massivecraft.factions.discord.Discord;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

import java.util.Random;

/**
 * @author SaberTeam
 */

public class CmdDiscord extends FCommand {
    public CmdDiscord() {
        super();
        this.aliases.addAll(Aliases.discord_discord);
        this.requirements = new CommandRequirements.Builder(Permission.DISCORD)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (context.fPlayer.discordSetup()) {
            context.fPlayer.msg(TL.DISCORD_ALREADY_LINKED, context.fPlayer.discordUser().getName());
        } else {
            if (Discord.waitingLink.containsValue(context.fPlayer)) {
                context.fPlayer.msg(TL.DISCORD_CODE_SENT, Discord.waitingLinkk.get(context.fPlayer), Discord.mainGuild.getSelfMember().getEffectiveName());
                return;
            }
            Integer random = new Random().nextInt(9999);
            while (Discord.waitingLink.containsValue(random)) {
                random = new Random().nextInt(9999);
            }
            Discord.waitingLink.put(random, context.fPlayer);
            Discord.waitingLinkk.put(context.fPlayer, random);
            context.fPlayer.msg(TL.DISCORD_CODE_SENT, String.valueOf(random));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DISCORD_DESCRIPTION;
    }
}
