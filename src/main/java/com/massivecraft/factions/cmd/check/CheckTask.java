package com.massivecraft.factions.cmd.check;

import com.google.common.collect.Lists;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.discord.Discord;
import com.massivecraft.factions.zcore.util.TL;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CheckTask implements Runnable {

    /**
     * @author Driftay
     */

    private static List<String> wallChecks = new CopyOnWriteArrayList<>();
    private static List<String> bufferChecks = new CopyOnWriteArrayList<>();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Conf.dateFormat);
    private FactionsPlugin plugin;
    private int minute;

    public CheckTask(FactionsPlugin plugin, int minute) {
        this.plugin = plugin;
        this.minute = minute;
    }

    public static void cleanupTask() {
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (!faction.isNormal()) {
                continue;
            }
            List<Long> remove = new ArrayList<>();
            int i = 0;
            if (faction.getChecks() == null) return;
            for (Long key : Lists.reverse(new ArrayList<>(faction.getChecks().keySet()))) {
                if (key == null) return;
                if (i >= 54) {
                    remove.add(key);
                }
                ++i;
            }
            remove.forEach(r -> faction.getChecks().remove(r));
        }
    }

    public static boolean wallCheck(String factionId) {
        return CheckTask.wallChecks.remove(factionId);
    }

    public static boolean bufferCheck(String factionId) {
        return CheckTask.bufferChecks.remove(factionId);
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (!faction.isNormal()) {
                continue;
            }
            if (faction.getWallCheckMinutes() != minute) {
                continue;
            }
            long CurrentTime = currentTime;
            if (CheckTask.wallChecks.contains(faction.getId())) {
                plugin.getServer().getScheduler().runTask(plugin, () -> faction.getChecks().put(CurrentTime, "J"));
            } else {
                CheckTask.wallChecks.add(faction.getId());
            }
            faction.msg(TL.CHECK_WALLS_CHECK);

            if (!FactionsPlugin.getInstance().getFileManager().getDiscord().fetchBoolean("Discord.useDiscordSystem"))
                return;


            String channelId = faction.getWallNotifyChannelId();
            if (channelId == null) {
                continue;
            }
            if (channelId.isEmpty()) {
                continue;
            }
            TextChannel textChannel = Discord.jda.getTextChannelById(channelId);
            if (textChannel == null) {
                continue;
            }
            if (!textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)) {
                textChannel.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage((":x: Missing read/write in " + textChannel.getAsMention())).queue());
            } else {
                String format = faction.getNotifyFormat();
                if (format == null || format.isEmpty()) {
                    format = "@everyone, check %type%";
                }
                format = format.replace("%type%", "walls");
                MessageEmbed embed = new EmbedBuilder().setColor(Color.WHITE).setFooter(this.simpleDateFormat.format(new Date(currentTime)), null).build();
                textChannel.sendMessage(new MessageBuilder(embed).setContent(format).build()).queue();
            }
        }


        ++currentTime;
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (!faction.isNormal()) {
                continue;
            }
            if (faction.getBufferCheckMinutes() != minute) {
                continue;
            }
            if (CheckTask.bufferChecks.contains(faction.getId())) {
                Faction faction2 = null;
                long CurrentTime2 = 0;
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (faction2 != null) {
                        faction2.getChecks().put(CurrentTime2, "H");
                    }
                });
            } else {
                CheckTask.bufferChecks.add(faction.getId());
            }
            faction.msg(TL.CHECK_BUFFERS_CHECK);

            if (!FactionsPlugin.getInstance().getFileManager().getDiscord().fetchBoolean("Discord.useDiscordSystem"))
                return;


            String channelId = faction.getBufferNotifyChannelId();
            if (channelId == null) {
                continue;
            }
            if (channelId.isEmpty()) {
                continue;
            }
            TextChannel textChannel = Discord.jda.getTextChannelById(channelId);
            if (textChannel == null) {
                continue;
            }
            if (!textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)) {
                textChannel.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage((":x: Missing read/write in " + textChannel.getAsMention())).queue());
            } else {
                String format = faction.getNotifyFormat();
                if (format == null || format.isEmpty()) {
                    format = "@everyone, check %type%";
                }
                format = format.replace("%type%", "buffers");
                MessageEmbed embed = new EmbedBuilder().setColor(Color.WHITE).setFooter(this.simpleDateFormat.format(new Date(currentTime)), null).build();
                textChannel.sendMessage(new MessageBuilder(embed).setContent(format).build()).queue();
            }
        }
    }
}

