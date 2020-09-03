package com.massivecraft.factions.discord;

import com.massivecraft.factions.*;
import com.massivecraft.factions.discord.json.JSONGuilds;
import com.massivecraft.factions.zcore.util.TL;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class DiscordListener extends ListenerAdapter {

    /**
     * @author Vankka
     */

    private static File file = new File(FactionsPlugin.getInstance().getDataFolder(), "discord_guilds.json");
    public static JSONGuilds guilds = loadGuilds();
    private final DecimalFormat decimalFormat;
    private FactionsPlugin plugin;

    public DiscordListener(FactionsPlugin plugin) {
        this.decimalFormat = new DecimalFormat("$#,###.##");
        this.plugin = plugin;
        int minute = 3600;
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, DiscordListener::saveGuilds, minute * 15, minute * 15);
    }

    private static JSONGuilds loadGuilds() {
        try {
            if (file.exists())
                return FactionsPlugin.getInstance().gson.fromJson(String.join("\n", Files.readAllLines(file.toPath())), JSONGuilds.class);
            Files.createFile(file.toPath());
            Files.write(file.toPath(), "{}".getBytes());
            return FactionsPlugin.getInstance().gson.fromJson(String.join("\n", Files.readAllLines(file.toPath())), JSONGuilds.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new NullPointerException();
        }
    }


    public static void saveGuilds() {
        try {
            String content = FactionsPlugin.getInstance().gson.toJson(guilds);
            Files.write(file.toPath(), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new NullPointerException();
        }
    }

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        Integer i;
        if (e.getAuthor().isBot()) return;
        try {
            i = Integer.valueOf(e.getMessage().getContentDisplay());
        } catch (NumberFormatException ex) {
            e.getChannel().sendMessage(TL.DISCORD_CODE_INVALID_FORMAT.toString()).queue();
            return;
        }
        if (Discord.waitingLink.containsKey(i)) {
            FPlayer f = Discord.waitingLink.get(i);
            f.setDiscordSetup(true);
            f.setDiscordUserID(e.getAuthor().getId());
            e.getChannel().sendMessage(TL.DISCORD_LINK_SUCCESS.toString()).queue();
            Discord.waitingLink.remove(i);
            Discord.waitingLinkk.remove(f);
        } else {
            e.getChannel().sendMessage(TL.DISCORD_CODE_INVALID_KEY.toString()).queue();
        }
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        try {
            if (event.getMessage().isWebhookMessage() || event.getAuthor().isBot()) return;
            String prefix = DiscordListener.guilds.getGuildById(event.getGuild().getId()).getPrefix();
            if (prefix == null || prefix.isEmpty()) {
                prefix = ".";
            }
            String content = event.getMessage().getContentRaw();
            if (!content.startsWith(prefix) && !content.startsWith(event.getGuild().getSelfMember().getAsMention()))
                return;
            if (content.startsWith(prefix + "help") || content.startsWith(event.getGuild().getSelfMember().getAsMention() + " help")) {
                this.help(event, content, prefix);
            } else if (content.startsWith(prefix + "stats")) {
                this.stats(event, content, prefix);
            } else if (content.startsWith(prefix + "fstats")) {
                this.fstats(event, content, prefix);
            } else if (content.startsWith(event.getGuild().getSelfMember().getAsMention() + " setprefix")) {
                this.setPrefix(event, content);
            } else if (content.startsWith(prefix + "setfchatchannel")) {
                this.setFChatChannel(event);
            } else if (content.startsWith(prefix + "setwallnotifychannel") || content.startsWith(prefix + "swnc")) {
                this.setWallNotifyChannel(event);
            } else if (content.startsWith(prefix + "setbuffernotifychannel") || content.startsWith(prefix + "sbnf")) {
                this.setBufferNotifyChannel(event);
            } else if (content.startsWith(prefix + "setweewoochannel")) {
                this.setWeewooChannel(event);
            } else if (content.startsWith(prefix + "setnotifyformat")) {
                this.setNotifyFormat(event, content, prefix);
            } else if (content.startsWith(prefix + "setweewooformat")) {
                this.setWeewooFormat(event, content, prefix);
            } else if (content.startsWith(prefix + "setmemberrole")) {
                this.setMemberRole(event, content, prefix);
            } else if (content.startsWith(prefix + "checkleaderboard") || content.startsWith(prefix + "cl")) {
                this.checkLeaderboard(event);
            } else if (content.startsWith(prefix + "weewoo")) {
                this.weewoo(event, content, prefix);
            } else if (content.startsWith(prefix + "settings")) {
                this.settings(event);
            }
        } catch (PermissionException exception) {
            if (!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_READ, Permission.MESSAGE_WRITE))
                return;
            event.getChannel().sendMessage((":x: Missing permission, `" + exception.getPermission().toString() + "`")).queue();
        }
    }

    private Faction getFaction(Guild guild) {
        return Factions.getInstance().getAllFactions().stream().filter(faction -> guild.getId().equals(faction.getGuildId())).findAny().orElse(null);
    }

    private Faction getFactionWithWarning(TextChannel textChannel) {
        Faction faction = this.getFaction(textChannel.getGuild());
        if (faction == null)
            textChannel.sendMessage((":x: This guild isn't linked to a faction, use `/f setguild " + textChannel.getGuild().getId() + "` in game")).queue();
        return faction;
    }

    private boolean cantAccessPermissionWithWarning(TextChannel textChannel, Member member) {
        boolean can = member.hasPermission(Permission.MANAGE_SERVER);
        if (!can) textChannel.sendMessage(":x: You need to have the Manage Server permission to do that").queue();
        return !can;
    }

    private boolean canAccessRole(Faction faction, Member member) {
        if (member.hasPermission(Permission.MANAGE_SERVER)) return true;
        Role role = member.getGuild().getRoleById(faction.getMemberRoleId());
        return role != null && member.getRoles().stream().anyMatch(r -> r.getPosition() >= role.getPosition());
    }

    private boolean cantAccessRoleWithWarning(TextChannel textChannel, Faction faction, Member member) {
        boolean can = this.canAccessRole(faction, member);
        if (!can) textChannel.sendMessage(":x: You don't have a faction member role").queue();
        return !can;
    }

    private void help(GuildMessageReceivedEvent event, String content, String prefix) {
        if (content.contains("help ")) {
            content = content.substring(content.indexOf("help") + 5).trim();
            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.MAGENTA);
            if (content.equalsIgnoreCase("help")) {
                embedBuilder.setTitle("Help | Help command").setDescription("Provides a list of commands & docs about commands").addField("Usage", "`" + prefix + "help [command]`", false);
            } else if (content.equalsIgnoreCase("stats")) {
                embedBuilder.setTitle("Help | Stats command").setDescription("Provides stats about the given player").addField("Usage", "`" + prefix + "stats <player name>`", false);
            } else if (content.equalsIgnoreCase("fstats")) {
                embedBuilder.setTitle("Help | Fstats command").setDescription("Provides stats about the give faction").addField("Usage", "`" + prefix + "fstats <faction name>`", false);
            } else if (content.equalsIgnoreCase("setprefix")) {
                embedBuilder.setTitle("Help | Setprefix command").setDescription("Changes the bot's prefix for all commands in the current guild").addField("Usage", "`@" + event.getGuild().getSelfMember().getEffectiveName() + " setprefix <new prefix>`", false);
            } else if (content.equalsIgnoreCase("setfchatchannel")) {
                embedBuilder.setTitle("Help | Setfchatchannel").setDescription("Sets or removes the channel for Faction chat \"mirroring\", where messages sent in in-game faction chat are sent in the channel & messages sent in the channel are sent to in-game faction chat").addField("Usage", "`" + prefix + "setfchatchannel [#channel]`", false);
            } else if (content.equalsIgnoreCase("setwallnotifychannel")) {
                embedBuilder.setTitle("Help | Setwallnotifychanel").setDescription("Sets or removes the wall check notification channel").addField("Usage", "`" + prefix + "setwallnotifychannel [#channel]`", false);
            } else if (content.equalsIgnoreCase("setbuffernotifychannel")) {
                embedBuilder.setTitle("Help | Setbuffernotifychannel").setDescription("Sets or removes the buffer check notification channel").addField("Usage", "`" + prefix + "setbuffernotifychannel [#channel]`", false);
            } else if (content.equalsIgnoreCase("setweewoochannel")) {
                embedBuilder.setTitle("Help | Setweewoochannel").setDescription("Sets or removes the weewoo (raid alert) channel").addField("Usage", "`" + prefix + "setweewoochannel [#channel>`", false);
            } else if (content.equalsIgnoreCase("setnotifyformat")) {
                embedBuilder.setTitle("Help | Setnotifyformat").setDescription("Sets the wall & buffer notification format, where `%type%` will be replaced with `walls` or `buffers`").addField("Usage", "`" + prefix + "setnotifyformat <format>`", false).addField("Default", "`@everyone, check %type%`", false);
            } else if (content.equalsIgnoreCase("setweewooformat")) {
                embedBuilder.setTitle("Help | Setweewooformat").setDescription("Sets the weewoo (raid alert) format").addField("Usage", "`" + prefix + "setweewooformat <fomat>`", false).addField("Default", "`@everyone, we're being raided! Get online!`", false);
            } else if (content.equalsIgnoreCase("setmemberrole")) {
                embedBuilder.setTitle("Help | Setmemberrole").setDescription("Sets the __lowest__ member role, where the specified role & any roles above it will be counted as members").addField("Usage", "`" + prefix + "setmemberrole <@role/role name/role id>`", false);
            } else if (content.equalsIgnoreCase("checkleaderboard")) {
                embedBuilder.setTitle("Help | Checkleaderboard").setDescription("Gets the leaderboard for wall & buffer checks").addField("Usage", "`" + prefix + "checkleaderboard`", false);
            } else if (content.equalsIgnoreCase("weewoo")) {
                embedBuilder.setTitle("Help | Weewoo").setDescription("Starts/stops the weewoo (raid alert").addField("Usage", "`" + prefix + "weewoo <start/stop>`", false);
            } else if (content.equalsIgnoreCase("settings")) {
                embedBuilder.setTitle("Help | Settings").setDescription("Gets the current settings").addField("Usage", "`" + prefix + "settings`", false);
            } else {
                embedBuilder.setColor(Color.RED).setTitle("Command not found");
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        } else {
            event.getChannel().sendMessage(("`" + prefix + "help [command]` This list or documentation of a command\n"
                    + prefix + "stats <player name>` Get stats for a player\n`"
                    + prefix + "fstats <faction name>` Get stats for a faction`\n__Requires Manage Server permission__\n@" + event.getGuild().getSelfMember().getEffectiveName() + " setprefix <prefix>` Change the bot's prefix for this guild`\n" +
                    "\n**Requires a linked faction**\n__Requires Manage Server permission__\n`" + prefix + "setfchatchannel [#channel]` Set or reset the fchat channel\n`" +
                    prefix + "setwallnotifychannel [#channel]` Set or reset the wall check notification channel\n`" + prefix + "setbuffernotifychannel [#channel]` Set or reset the buffer check notification channel\n`" +
                    prefix + "setweewoochannel [#channel]` Set or reset the weewoo channel\n`" + prefix + "setnotifyformat <format>` Changes the notification format (`%type%` -> `walls/buffers`)\n`" + prefix + "setweewooformat <format>` Changes the weewoo format\n`" +
                    prefix + "setmemberrole <@role/role name/role id>` Changes the *lowest* member role\n__Member role only__\n`" +
                    prefix + "checkleaderboard` Wall & buffer check leaderboard\n`" +
                    prefix + "weewoo <start/stop>` Starts/stops the weewoo\n`" +
                    prefix + "settings` Displays the current settings")).queue();
        }
    }

    private void fstats(GuildMessageReceivedEvent event, String content, String prefix) {
        if (!content.contains(" ")) {
            event.getChannel().sendMessage((":x: Usage, `" + prefix + "fstats <faction name>`")).queue();
            return;
        }
        String[] args = content.split(" ");
        Faction faction = Factions.getInstance().getByTag(args[1]);
        if (faction == null) {
            event.getChannel().sendMessage(":x: Faction not found").queue();
            return;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.MAGENTA).setTitle("Faction Stats").setAuthor(ChatColor.stripColor(faction.getTag())).addField("Description", faction.getDescription(), false).addField("Players Online", String.valueOf(faction.getOnlinePlayers().size()), true).addField("Total players", String.valueOf(faction.getFPlayers().size()), true).addField("Alts", String.valueOf(faction.getAltPlayers().size()), true).addField("Land", String.valueOf(faction.getLandRounded()), true).addField("Power", faction.getPowerRounded() + "/" + faction.getPowerMaxRounded(), true);
        Faction guildFaction = this.getFaction(event.getGuild());
        if (guildFaction != null && guildFaction.getId().equals(faction.getId()) && this.canAccessRole(guildFaction, event.getMember())) {
            embedBuilder.addField("Kills", String.valueOf(faction.getKills()), true).addField("Points", String.valueOf(faction.getPoints()), true).addField("Deaths", String.valueOf(faction.getDeaths()), true);
        }
        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void stats(GuildMessageReceivedEvent event, String content, String prefix) {
        if (!content.contains(" ")) {
            event.getChannel().sendMessage((":x: Usage, `" + prefix + "stats <player name>`")).queue();
            return;
        }
        String[] args = content.split(" ");
        OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(args[1]);
        FPlayer fPlayer = FPlayers.getInstance().getByOfflinePlayer(offlinePlayer);
        String role = fPlayer.getRole().toString();
        role = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.MAGENTA).setTitle("Player Stats").setAuthor(offlinePlayer.getName(), null, FactionsPlugin.getInstance().getFileManager().getDiscord().fetchString("Discord.Bot.avatarUrl").replace("%uuid%", offlinePlayer.getUniqueId().toString())).addField("Balance", this.decimalFormat.format(this.plugin.getEcon().getBalance(offlinePlayer)), false).addField("Faction", ChatColor.stripColor(fPlayer.getFaction().getTag()), true).addField("Faction Role", role, true).addField("Power", fPlayer.getPower() + "/" + fPlayer.getPowerMax(), true).addField("Online", offlinePlayer.isOnline() ? "Yes" : "No", true);
        Faction faction = this.getFaction(event.getGuild());
        if (faction != null && fPlayer.getFactionId().equals(faction.getId())) {
            embedBuilder.addField("Wall checks", String.valueOf(faction.getPlayerWallCheckCount().getOrDefault(offlinePlayer.getUniqueId(), 0)), true).addField("Buffer checks", String.valueOf(faction.getPlayerBufferCheckCount().getOrDefault(offlinePlayer.getUniqueId(), 0)), true).addField("Kills", String.valueOf(fPlayer.getKills()), true).addField("Deaths", String.valueOf(fPlayer.getDeaths()), true);
        }
        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    private void setPrefix(GuildMessageReceivedEvent event, String content) {
        if (cantAccessPermissionWithWarning(event.getChannel(), event.getMember())) return;
        String[] split = content.split(" ");
        if (split.length != 3) {
            event.getChannel().sendMessage((":x: Usage, `@" + event.getGuild().getSelfMember().getEffectiveName() + " setprefix <prefix>`")).queue();
            return;
        }
        String newPrefix = split[2];
        if (newPrefix.length() > 16) {
            event.getChannel().sendMessage(":x: Prefix may not be longer than 16 characters").queue();
            return;
        }
        DiscordListener.guilds.getGuildById(event.getGuild().getId()).setPrefix(newPrefix);
        event.getMessage().addReaction("\u2705").queue();
    }


    private void setFChatChannel(GuildMessageReceivedEvent event) {
        Faction faction = this.getFactionWithWarning(event.getChannel());
        if (faction == null) return;
        if (cantAccessPermissionWithWarning(event.getChannel(), event.getMember())) return;
        List<TextChannel> mentionedChannels = event.getMessage().getMentionedChannels();
        if (mentionedChannels.isEmpty()) {
            faction.setFactionChatChannelId(null);
            event.getChannel().sendMessage("Cleared fchat channel").queue();
        } else {
            TextChannel textChannel = mentionedChannels.get(0);
            if (!event.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)) {
                event.getChannel().sendMessage((":x: Missing read/write permission in " + textChannel.getAsMention())).queue();
                return;
            }
            faction.setFactionChatChannelId(textChannel.getId());
            event.getChannel().sendMessage(("New fchat channel set to " + textChannel.getAsMention())).queue();
        }
    }

    private void setWallNotifyChannel(GuildMessageReceivedEvent event) {
        Faction faction = this.getFactionWithWarning(event.getChannel());
        if (faction == null) return;
        if (cantAccessPermissionWithWarning(event.getChannel(), event.getMember())) return;
        List<TextChannel> mentionedChannels = event.getMessage().getMentionedChannels();
        if (mentionedChannels.isEmpty()) {
            faction.setWallNotifyChannelId(null);
            event.getChannel().sendMessage("Cleared wall notify channel").queue();
        } else {
            TextChannel textChannel = mentionedChannels.get(0);
            if (!event.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)) {
                event.getChannel().sendMessage((":x: Missing read/write permission in " + textChannel.getAsMention())).queue();
                return;
            }
            faction.setWallNotifyChannelId(textChannel.getId());
            event.getChannel().sendMessage(("New wall notify channel set to " + textChannel.getAsMention())).queue();
        }
    }

    private void setBufferNotifyChannel(GuildMessageReceivedEvent event) {
        Faction faction = this.getFactionWithWarning(event.getChannel());
        if (faction == null) return;
        if (cantAccessPermissionWithWarning(event.getChannel(), event.getMember())) return;
        List<TextChannel> mentionedChannels = event.getMessage().getMentionedChannels();
        if (mentionedChannels.isEmpty()) {
            faction.setBufferNotifyChannelId(null);
            event.getChannel().sendMessage("Cleared buffer notify channel").queue();
        } else {
            TextChannel textChannel = mentionedChannels.get(0);
            if (!event.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)) {
                event.getChannel().sendMessage((":x: Missing read/write permission in " + textChannel.getAsMention())).queue();
                return;
            }
            faction.setBufferNotifyChannelId(textChannel.getId());
            event.getChannel().sendMessage(("New buffer notify channel set to " + textChannel.getAsMention())).queue();
        }
    }

    private void setWeewooChannel(GuildMessageReceivedEvent event) {
        Faction faction = this.getFactionWithWarning(event.getChannel());
        if (faction == null) return;
        if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            event.getChannel().sendMessage(":x: You need to have the Manage Server permission to do that").queue();
            return;
        }
        List<TextChannel> mentionedChannels = event.getMessage().getMentionedChannels();
        if (mentionedChannels.isEmpty()) {
            faction.setWeeWooChannelId(null);
            event.getChannel().sendMessage("Cleared weewoo channel").queue();
        } else {
            TextChannel textChannel = mentionedChannels.get(0);
            if (!event.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)) {
                event.getChannel().sendMessage((":x: Missing read/write permission in " + textChannel.getAsMention())).queue();
                return;
            }
            faction.setWeeWooChannelId(textChannel.getId());
            event.getChannel().sendMessage(("New weewoo channel set to " + textChannel.getAsMention())).queue();
        }
    }

    private void setNotifyFormat(GuildMessageReceivedEvent event, String content, String prefix) {
        Faction faction = this.getFactionWithWarning(event.getChannel());
        if (faction == null) return;
        if (cantAccessPermissionWithWarning(event.getChannel(), event.getMember())) return;
        if (!content.contains(" ")) {
            event.getChannel().sendMessage((":x: Usage, `" + prefix + "setnotifyformat <format>` (%type%)")).queue();
            return;
        }
        List<String> arguments = new ArrayList<>(Arrays.asList(content.split(" ")));
        arguments.remove(0);
        String format = String.join(" ", arguments);
        if (format.length() > 1000) {
            event.getChannel().sendMessage(":x: The notify format may not be longer than 1000 characters").queue();
            return;
        }
        faction.setNotifyFormat(format);
        event.getChannel().sendMessage(("New notify format set to `" + format + "`")).queue();
    }

    private void setWeewooFormat(GuildMessageReceivedEvent event, String content, String prefix) {
        Faction faction = this.getFactionWithWarning(event.getChannel());
        if (faction == null) return;
        if (cantAccessPermissionWithWarning(event.getChannel(), event.getMember())) return;
        if (!content.contains(" ")) {
            event.getChannel().sendMessage((":x: Usage, `" + prefix + "setweewooformat <format>`")).queue();
            return;
        }
        List<String> arguments = new ArrayList<>(Arrays.asList(content.split(" ")));
        arguments.remove(0);
        String format = String.join(" ", arguments);
        if (format.length() > 1000) {
            event.getChannel().sendMessage(":x: The weewoo format may not be longer than 1000 characters").queue();
            return;
        }
        faction.setWeeWooFormat(format);
        event.getChannel().sendMessage(("New weewoo format set to `" + format + "`")).queue();
    }

    private void setMemberRole(GuildMessageReceivedEvent event, String content, String prefix) {
        Faction faction = this.getFactionWithWarning(event.getChannel());
        if (faction == null) return;
        if (cantAccessPermissionWithWarning(event.getChannel(), event.getMember())) return;
        List<String> split = new ArrayList<>(Arrays.asList(content.split(" ")));
        if (split.size() < 2) {
            event.getChannel().sendMessage((":x: Usage, `" + prefix + "setmemberrole <@role/role name/role id>`")).queue();
            return;
        }
        split.remove(0);
        Role role = event.getMessage().getMentionedRoles().stream().findFirst().orElse(null);
        if (role == null) {
            role = event.getGuild().getRolesByName(String.join(" ", split), true).stream().findAny().orElse(null);
        }
        if (role == null) {
            try {
                role = event.getGuild().getRoleById(split.get(0));
            } catch (NumberFormatException ex) {
            }
        }
        if (role == null) {
            event.getChannel().sendMessage(":x: Role not found").queue();
            return;
        }
        faction.setMemberRoleId(role.getId());
        event.getChannel().sendMessage(("New *lowest* member role set to `" + role.getName() + "`")).queue();
    }

    private void checkLeaderboard(GuildMessageReceivedEvent event) {
        Faction faction = this.getFactionWithWarning(event.getChannel());
        if (faction == null) return;
        if (cantAccessRoleWithWarning(event.getChannel(), faction, event.getMember())) return;

        Map<UUID, Integer> players = new HashMap<>();
        for (Map.Entry<UUID, Integer> entry : faction.getPlayerWallCheckCount().entrySet()) {
            players.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : faction.getPlayerBufferCheckCount().entrySet()) {
            if (players.containsKey(entry.getKey())) {
                players.replace(entry.getKey(), players.get(entry.getKey()) + entry.getValue());
            } else {
                players.put(entry.getKey(), entry.getValue());
            }
        }
        List<Map.Entry<UUID, Integer>> entryList = players.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());
        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Check Leaderboard").setColor(Color.MAGENTA);
        StringBuilder stringBuilder = new StringBuilder();
        for (int max = Math.min(entryList.size(), 10), current = 0; current < max; ++current) {
            Map.Entry<UUID, Integer> entry2 = entryList.get(current);
            OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(entry2.getKey());
            stringBuilder.append("**").append(current + 1).append(".** ").append(offlinePlayer.getName()).append(" __").append(entry2.getValue()).append(" Total (").append(faction.getPlayerBufferCheckCount().getOrDefault(entry2.getKey(), 0)).append(" Buffer, ").append(faction.getPlayerWallCheckCount().getOrDefault(entry2.getKey(), 0)).append(" Wall)__\n");
        }
        if (entryList.isEmpty()) {
            stringBuilder.append("_No data_");
        }
        event.getChannel().sendMessage(embedBuilder.setDescription(stringBuilder.toString()).build()).queue();
    }

    private void weewoo(GuildMessageReceivedEvent event, String content, String prefix) {
        Faction faction = this.getFactionWithWarning(event.getChannel());
        if (faction == null) return;
        if (cantAccessRoleWithWarning(event.getChannel(), faction, event.getMember())) return;
        if (!content.contains(" ")) {
            event.getChannel().sendMessage((":x: Usage, `" + prefix + "weewoo <start/stop>`")).queue();
            return;
        }
        List<String> arguments = new ArrayList<>(Arrays.asList(content.split(" ")));
        boolean weeWoo = faction.isWeeWoo();
        if (arguments.get(1).equalsIgnoreCase("start")) {
            if (weeWoo) {
                event.getChannel().sendMessage(TL.WEEWOO_ALREADY_STARTED_DISCORD.toString()).queue();
                return;
            }
            faction.setWeeWoo(true);
            event.getMessage().addReaction("\u2705").queue();
            faction.msg(TL.COMMAND_WEEWOO_STARTED, event.getAuthor().getAsTag());
            String discordChannelId = faction.getWeeWooChannelId();
            if (discordChannelId != null && !discordChannelId.isEmpty()) {
                TextChannel textChannel = event.getJDA().getTextChannelById(discordChannelId);
                if (textChannel == null) return;
                textChannel.sendMessage(TL.WEEWOO_STARTED_DISCORD.format(event.getAuthor().getAsTag())).queue();
            }
        } else if (arguments.get(1).equalsIgnoreCase("stop")) {
            if (!weeWoo) {
                event.getChannel().sendMessage(TL.WEEWOO_ALREADY_STOPPED_DISCORD.toString()).queue();
                return;
            }
            faction.setWeeWoo(false);
            event.getMessage().addReaction("\u2705").queue();
            faction.msg(TL.COMMAND_WEEWOO_STARTED, event.getAuthor().getAsTag());
            String discordChannelId = faction.getWeeWooChannelId();
            if (discordChannelId != null && !discordChannelId.isEmpty()) {
                TextChannel textChannel = event.getJDA().getTextChannelById(discordChannelId);
                if (textChannel == null) return;
                textChannel.sendMessage(TL.WEEWOO_STOPPED_DISCORD.format(event.getAuthor().getAsTag())).queue();
            }
        } else {
            event.getChannel().sendMessage(":x: Usage, `.weewoo <start/stop>`").queue();
        }
    }

    private void settings(GuildMessageReceivedEvent event) {
        Faction faction = this.getFactionWithWarning(event.getChannel());
        if (faction == null) return;
        if (cantAccessRoleWithWarning(event.getChannel(), faction, event.getMember())) return;
        int wallCheck = faction.getWallCheckMinutes();
        int bufferCheck = faction.getBufferCheckMinutes();
        String wallChannel = faction.getWallNotifyChannelId();
        String bufferChannel = faction.getBufferNotifyChannelId();
        String weeWooChannel = faction.getWeeWooChannelId();
        String fChatChannel = faction.getFactionChatChannelId();
        MessageEmbed embed = new EmbedBuilder().setTitle("Settings").setColor(Color.MAGENTA).addField("WeeWoo channel", (weeWooChannel != null && !weeWooChannel.isEmpty()) ? ("<#" + weeWooChannel + ">") : "None", true).addField("Wall check channel", (wallChannel != null && !wallChannel.isEmpty()) ? ("<#" + wallChannel + ">") : "None", true).addField("Buffer check channel", (bufferChannel != null && !bufferChannel.isEmpty()) ? ("<#" + bufferChannel + ">") : "None", true).addField("Faction Chat channel", (fChatChannel != null && !fChatChannel.isEmpty()) ? ("<#" + fChatChannel + ">") : "None", true).addField("Wall check notifications", (wallCheck <= 0) ? "Offline" : (wallCheck + " Minutes"), true).addField("Buffer check notifications", (bufferCheck <= 0) ? "Offline" : (bufferCheck + " Minutes"), true).addField("Notify format", "`" + faction.getNotifyFormat() + "`", false).addField("WeeWoo format", "`" + faction.getWeeWooFormat() + "`", false).build();
        event.getChannel().sendMessage(embed).queue();
    }
}

