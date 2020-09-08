package com.massivecraft.factions.cmd.check;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.discord.Discord;
import com.massivecraft.factions.zcore.util.TL;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

public class WeeWooTask implements Runnable {

    /**
     * @author Driftay
     */

    private FactionsPlugin plugin;

    public WeeWooTask(FactionsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (!faction.isWeeWoo()) {
                continue;
            }
            faction.msg(TL.WEE_WOO_MESSAGE);

            if (!FactionsPlugin.getInstance().getFileManager().getDiscord().fetchBoolean("Discord.useDiscordSystem"))
                return;

            String discordChannelId = faction.getWeeWooChannelId();
            if (discordChannelId == null || discordChannelId.isEmpty()) {
                continue;
            }
            TextChannel textChannel = Discord.jda.getTextChannelById(discordChannelId);
            if (textChannel == null) {
                continue;
            }
            if (!textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)) {
                textChannel.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage((":x: Missing read/write in " + textChannel.getAsMention())).queue());
            } else {
                String format = faction.getWeeWooFormat();
                if (format == null || format.isEmpty()) {
                    format = "@everyone, we're being raided! Get online!";
                }
                textChannel.sendMessage(format).queue();
            }
        }
    }
}
