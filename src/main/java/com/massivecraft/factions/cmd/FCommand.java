package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.zcore.CommandVisibility;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class FCommand {

    /**
     * Author: FactionsUUID Team - Modified By CmdrKittens
     */

    private final SimpleDateFormat sdf = new SimpleDateFormat(TL.DATE_FORMAT.toString());

    // Command Aliases
    private final List<String> aliases;

    // Information on the args
    private final List<String> requiredArgs;
    private final LinkedHashMap<String, String> optionalArgs;

    // Requirements to execute this command
    private CommandRequirements requirements;
    /*
        Subcommands
     */
    private final List<FCommand> subCommands;
    /*
        Help
     */
    private final List<String> helpLong;
    private CommandVisibility visibility;
    private String helpShort;

    public FCommand() {
        requirements = new CommandRequirements.Builder(null).build();
        subCommands = new ArrayList<>();
        aliases = new ArrayList<>();
        requiredArgs = new ArrayList<>();
        optionalArgs = new LinkedHashMap<>();
        helpShort = null;
        helpLong = new ArrayList<>();
        visibility = CommandVisibility.VISIBLE;
    }

    public abstract void perform(CommandContext context);

    public void execute(CommandContext context) {
        if (context.args.size() > 0) {
            for (FCommand subCommand : subCommands) {
                if (subCommand.aliases.contains(context.args.get(0).toLowerCase())) {
                    context.args.remove(0);
                    context.commandChain.add(this);
                    subCommand.execute(context);
                    return;
                }
            }
        }

        if (!validCall(context) || !isEnabled(context)) {
            return;
        }

        perform(context);
    }

    public boolean validCall(CommandContext context) {
        return requirements.computeRequirements(context, true) && validArgs(context);
    }

    public boolean isEnabled(CommandContext context) {
        if (FactionsPlugin.getInstance().getLocked() && requirements.isDisableOnLock()) {
            context.msg("<b>Factions was locked by an admin. Please try again later.");
            return false;
        }
        return true;
    }

    public boolean validArgs(CommandContext context) {
        if (context.args.size() < requiredArgs.size()) {
            if (context.sender != null) {
                context.msg(TL.GENERIC_ARGS_TOOFEW);
                context.sender.sendMessage(getUsageTemplate(context));
            }
            return false;
        }

        if (context.args.size() > requiredArgs.size() + optionalArgs.size() && requirements.isErrorOnManyArgs()) {
            if (context.sender != null) {
                List<String> theToMany = context.args.subList(requiredArgs.size() + optionalArgs.size(), context.args.size());
                context.msg(TL.GENERIC_ARGS_TOOMANY, TextUtil.implode(theToMany, " "));
                context.sender.sendMessage(getUsageTemplate(context));
            }
            return false;
        }
        return true;
    }

    public void addSubCommand(FCommand subCommand) {
        subCommands.add(subCommand);
    }

    public String getHelpShort() {
        return (helpShort == null) ? getUsageTranslation().toString() : helpShort;
    }

    public void setHelpShort(String val) {
        helpShort = val;
    }

    public abstract TL getUsageTranslation();

    public List<String> getToolTips(FPlayer player) {
        return FactionsPlugin.getInstance().getConfig().getStringList("tooltips.show").stream()
                .map(s -> TextUtil.parse(replaceFPlayerTags(s, player)))
                .collect(Collectors.toList());
    }

    public List<String> getToolTips(Faction faction) {
        return FactionsPlugin.getInstance().getConfig().getStringList("tooltips.list").stream()
                .map(s -> TextUtil.parse(replaceFactionTags(s, faction)))
                .collect(Collectors.toList());
    }

    public String replaceFPlayerTags(String s, FPlayer player) {
        if (s.contains("{balance}")) {
            String balance = Econ.isSetup() ? Econ.getFriendlyBalance(player) : TL.NO_BALANCE_PLACEHOLDER_PARSED.toString();
            s = TextUtil.replace(s, "{balance}", balance);
        }
        if (s.contains("{lastSeen}")) {
            String humanized = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - player.getLastLoginTime(), true, true) + " ago";
            String lastSeen = player.isOnline() ? ChatColor.GREEN + "Online" : (System.currentTimeMillis() - player.getLastLoginTime() < 432000000 ? ChatColor.YELLOW + humanized : ChatColor.RED + humanized);
            s = TextUtil.replace(s, "{lastSeen}", lastSeen);
        }
        if (s.contains("{power}")) {
            String power = player.getPowerRounded() + "/" + player.getPowerMaxRounded();
            s = TextUtil.replace(s, "{power}", power);
        }
        if (s.contains("{group}")) {
            String group = FactionsPlugin.getInstance().getPrimaryGroup(Bukkit.getOfflinePlayer(UUID.fromString(player.getId())));
            s = TextUtil.replace(s, "{group}", group);
        }
        return s;
    }

    public String replaceFactionTags(String s, Faction faction) {
        if (s.contains("{power}")) {
            s = TextUtil.replace(s, "{power}", String.valueOf(faction.getPowerRounded()));
        }
        if (s.contains("{maxPower}")) {
            s = TextUtil.replace(s, "{maxPower}", String.valueOf(faction.getPowerMaxRounded()));
        }
        if (s.contains("{leader}")) {
            FPlayer fLeader = faction.getFPlayerAdmin();
            String leader = fLeader == null ? "Server" : fLeader.getName().substring(0, Math.min(fLeader.getName().length(), 13));
            s = TextUtil.replace(s, "{leader}", leader);
        }
        if (s.contains("{chunks}")) {
            s = TextUtil.replace(s, "{chunks}", String.valueOf(faction.getLandRounded()));
        }
        if (s.contains("{members}")) {
            s = TextUtil.replace(s, "{members}", String.valueOf(faction.getSize()));
        }
        if (s.contains("{online}")) {
            s = TextUtil.replace(s, "{online}", String.valueOf(faction.getOnlinePlayers().size()));
        }
        return s;
    }

    public String getUsageTemplate(CommandContext context, boolean addShortHelp) {
        StringBuilder ret = new StringBuilder(TextUtil.parse(TL.COMMAND_USEAGE_TEMPLATE_COLOR.toString()));
        ret.append('/');

        context.commandChain.forEach(fc -> {
            ret.append(TextUtil.implode(fc.aliases, ","));
            ret.append(' ');
        });

        ret.append(TextUtil.implode(aliases, ","));

        List<String> args = new ArrayList<>();
        requiredArgs.forEach(requiredArg -> args.add("<" + requiredArg + ">"));
        optionalArgs.forEach((key, value) -> args.add("[" + key + (value == null ? "" : "=" + value) + "]"));

        if (!args.isEmpty()) {
            ret.append(TextUtil.parseTags(" "));
            ret.append(TextUtil.implode(args, " "));
        }

        if (addShortHelp) {
            ret.append(TextUtil.parseTags(" "));
            ret.append(getHelpShort());
        }

        return ret.toString();
    }

    public String getUsageTemplate(CommandContext context) {
        return getUsageTemplate(context, false);
    }

    // Getters and Setters
    public List<String> getAliases() {
        return aliases;
    }

    public List<String> getRequiredArgs() {
        return requiredArgs;
    }

    public LinkedHashMap<String, String> getOptionalArgs() {
        return optionalArgs;
    }

    public CommandRequirements getRequirements() {
        return requirements;
    }

    public void setRequirements(CommandRequirements requirements) {
        this.requirements = requirements;
    }

    public List<FCommand> getSubCommands() {
        return subCommands;
    }

    public List<String> getHelpLong() {
        return helpLong;
    }

    public CommandVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(CommandVisibility visibility) {
        this.visibility = visibility;
    }
}
