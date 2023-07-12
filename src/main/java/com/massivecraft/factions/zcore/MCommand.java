package com.massivecraft.factions.zcore;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.CC;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;


public abstract class MCommand<T extends MPlugin> {

    public T p;

    // The sub-commands to this command
    public List<MCommand<?>> subCommands;
    // The different names this commands will react to
    public List<String> aliases;
    public boolean allowNoSlashAccess;
    // Information on the args
    public List<String> requiredArgs;
    public LinkedHashMap<String, String> optionalArgs;
    public boolean errorOnToManyArgs = true;
    public List<String> helpLong;
    public CommandVisibility visibility;
    // Some information on permissions
    public boolean senderMustBePlayer;
    public boolean senderMustHaveFaction;
    public String permission;
    // Information available on execution of the command
    public CommandSender sender; // Will always be set
    public Player me; // Will only be set when the sender is a player
    public boolean senderIsConsole;
    public List<String> args; // Will contain the arguments, or and empty list if there are none.
    public List<MCommand<?>> commandChain = new ArrayList<>(); // The command chain used to execute this command
    // FIELD: Help Short
    // This field may be left blank and will in such case be loaded from the permissions node instead.
    // Thus make sure the permissions node description is an action description like "eat hamburgers" or "do admin stuff".
    private String helpShort;

    public MCommand(T p) {
        this.p = p;

        this.permission = null;

        this.allowNoSlashAccess = false;

        this.subCommands = new ArrayList<>();
        this.aliases = new ArrayList<>();

        this.requiredArgs = new ArrayList<>();
        this.optionalArgs = new LinkedHashMap<>();

        this.helpShort = null;
        this.helpLong = new ArrayList<>();
        this.visibility = CommandVisibility.VISIBLE;
    }

    public void addSubCommand(MCommand<?> subCommand) {
        subCommand.commandChain.addAll(this.commandChain);
        subCommand.commandChain.add(this);
        this.subCommands.add(subCommand);
    }

    public String getHelpShort() {
        return this.helpShort != null ? this.helpShort : getUsageTranslation().toString();
    }

    public void setHelpShort(String val) {
        this.helpShort = val;
    }

    public abstract TL getUsageTranslation();

    public void setCommandSender(CommandSender sender) {
        this.sender = sender;
        if (sender instanceof Player) {
            this.me = (Player) sender;
            this.senderIsConsole = false;
        } else {
            this.me = null;
            this.senderIsConsole = true;
        }
    }

    // The commandChain is a list of the parent command chain used to get to this command.
    public void execute(CommandSender sender, List<String> args, List<MCommand<?>> commandChain) {
        // Set the execution-time specific variables
        setCommandSender(sender);
        this.args = args;
        this.commandChain = commandChain;

        // Is there a matching sub command?
        if (!args.isEmpty()) {
            for (MCommand<?> subCommand : this.subCommands) {
                if (TextUtil.containsIgnoreCase(subCommand.aliases, args.get(0))) {
                    args.remove(0);
                    commandChain.add(this);
                    subCommand.execute(sender, args, commandChain);
                    return;
                }
            }
        }

        if (!this.isEnabled()) {
            return;
        }

        if (!validCall(this.sender, this.args)) {
            return;
        }

        perform();
    }

    public void execute(CommandSender sender, List<String> args) {
        execute(sender, args, new ArrayList<>());
    }

    // This is where the command action is performed.
    public abstract void perform();


    // -------------------------------------------- //
    // Call Validation
    // -------------------------------------------- //

    /**
     * In this method we validate that all prerequisites to perform this command has been met.
     *
     * @param sender of the command
     * @param args   of the command
     * @return true if valid, false if not.
     */
    // TODO: There should be a boolean for silence
    public boolean validCall(CommandSender sender, List<String> args) {
        return validSenderType(sender, true) && validSenderPermissions(sender, true) && validArgs(args, sender);
    }

    public boolean isEnabled() {
        return true;
    }


    public boolean validSenderType(CommandSender sender, boolean informSenderIfNot) {
        if (this.senderMustBePlayer && !(sender instanceof Player)) {
            if (informSenderIfNot) {
                msg(TL.GENERIC_PLAYERONLY);
            }
            return false;

        }
        return !this.senderMustHaveFaction || !FPlayers.getInstance().getByPlayer((Player) sender).hasFaction();
    }

    public boolean validSenderPermissions(CommandSender sender, boolean informSenderIfNot) {
        return this.permission == null || p.perm.has(sender, this.permission, informSenderIfNot);
    }

    public boolean validArgs(List<String> args, CommandSender sender) {
        if (args.size() < this.requiredArgs.size()) {
            if (sender != null) {
                msg(TL.GENERIC_ARGS_TOOFEW);
                sender.sendMessage(this.getUseageTemplate());
            }
            return false;
        }

        if (this.errorOnToManyArgs && args.size() > this.requiredArgs.size() + this.optionalArgs.size()) {
            if (sender != null) {
                // Get the too much string slice
                List<String> theToMany = args.subList(this.requiredArgs.size() + this.optionalArgs.size(), args.size());
                msg(TL.GENERIC_ARGS_TOOMANY, TextUtil.implode(theToMany, " "));
                sender.sendMessage(this.getUseageTemplate());
            }
            return false;
        }
        return true;
    }

    public boolean validArgs(List<String> args) {
        return this.validArgs(args, null);
    }

    // -------------------------------------------- //
    // Help and Usage information
    // -------------------------------------------- //

    public String getUseageTemplate(List<MCommand<?>> commandChain, boolean addShortHelp) {
        StringBuilder ret = new StringBuilder();
        ret.append(TextUtil.parseTags("<c>"));
        ret.append('/');

        for (MCommand<?> mc : commandChain) {
            ret.append(TextUtil.implode(mc.aliases, ","));
            ret.append(' ');
        }

        ret.append(TextUtil.implode(this.aliases, ","));

        List<String> args = new ArrayList<>();

        for (String requiredArg : this.requiredArgs) {
            args.add("<" + requiredArg + ">");
        }

        for (Entry<String, String> optionalArg : this.optionalArgs.entrySet()) {
            String val = optionalArg.getValue();
            if (val == null) {
                val = "";
            } else {
                val = "=" + val;
            }
            args.add("[" + optionalArg.getKey() + val + "]");
        }

        if (args.size() > 0) {
            ret.append(TextUtil.parseTags("<plugin> "));
            ret.append(TextUtil.implode(args, " "));
        }

        if (addShortHelp) {
            ret.append(TextUtil.parseTags(" <i>"));
            ret.append(this.getHelpShort());
        }

        return ret.toString();
    }

    public String getUseageTemplate(boolean addShortHelp) {
        return getUseageTemplate(this.commandChain, addShortHelp);
    }

    public String getUseageTemplate() {
        return getUseageTemplate(false);
    }

    // -------------------------------------------- //
    // Message Sending Helpers
    // -------------------------------------------- //

    public void msg(String str, Object... args) {
        sender.sendMessage(TextUtil.parse(str, args));
    }

    public void msg(TL translation, Object... args) {
        sender.sendMessage(TextUtil.parse(translation.toString(), args));
    }

    public void sendMessage(String msg) {
        sender.sendMessage(msg);
    }

    public void sendMessage(List<String> msgs) {
        for (String msg : msgs) {
            this.sendMessage(msg);
        }
    }

    public void sendComponent(Component message) {
        TextUtil.AUDIENCES.sender(this.sender).sendMessage(message);
    }

    public void sendComponent(List<Component> messages) {
        for (Component m : messages) {
            sendComponent(m);
        }
    }

    public List<String> getToolTips(FPlayer player) {
        List<String> original = p.getConfig().getStringList("tooltips.show");
        List<String> lines = new ArrayList<>(original.size());
        for (String s : original) {
            lines.add(CC.translate(replaceFPlayerTags(s, player)));
        }
        return lines;
    }

    public List<String> getToolTips(Faction faction) {
        List<String> original = p.getConfig().getStringList("tooltips.list");
        List<String> lines = new ArrayList<>(original.size());
        for (String s : original) {
            lines.add(CC.translate(replaceFactionTags(s, faction)));
        }
        return lines;
    }

    public String replaceFPlayerTags(String s, FPlayer player) {
        if (s.contains("{balance}")) {
            String balance = Econ.isSetup() ? Econ.getFriendlyBalance(player) : "no balance";
            s = s.replace("{balance}", balance);
        }
        if (s.contains("{lastSeen}")) {
            String humanized = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - player.getLastLoginTime(), true, true) + " ago";
            String lastSeen = player.isOnline() ? ChatColor.GREEN + "Online" : (System.currentTimeMillis() - player.getLastLoginTime() < 432000000 ? ChatColor.YELLOW + humanized : ChatColor.RED + humanized);
            s = s.replace("{lastSeen}", lastSeen);
        }
        if (s.contains("{power}")) {
            String power = player.getPowerRounded() + "/" + player.getPowerMaxRounded();
            s = s.replace("{power}", power);
        }
        if (s.contains("{group}")) {
            String group = FactionsPlugin.getInstance().getPrimaryGroup(Bukkit.getOfflinePlayer(UUID.fromString(player.getId())));
            s = s.replace("{group}", group);
        }
        return s;
    }

    public String replaceFactionTags(String s, Faction faction) {
        if (s.contains("{power}")) {
            s = s.replace("{power}", String.valueOf(faction.getPowerRounded()));
        }
        if (s.contains("{maxPower}")) {
            s = s.replace("{maxPower}", String.valueOf(faction.getPowerMaxRounded()));
        }
        if (s.contains("{leader}")) {
            FPlayer fLeader = faction.getFPlayerAdmin();
            String leader = fLeader == null ? "Server" : fLeader.getName().substring(0, fLeader.getName().length() > 14 ? 13 : fLeader.getName().length());
            s = s.replace("{leader}", leader);
        }
        if (s.contains("{chunks}")) {
            s = s.replace("{chunks}", String.valueOf(faction.getLandRounded()));
        }
        if (s.contains("{members}")) {
            s = s.replace("{members}", String.valueOf(faction.getSize()));

        }
        if (s.contains("{online}")) {
            s = s.replace("{online}", String.valueOf(faction.getOnlinePlayers().size()));
        }
        return s;
    }

    // -------------------------------------------- //
    // Argument Readers
    // -------------------------------------------- //

    // Is set? ======================
    public boolean argIsSet(int idx) {
        return this.args.size() >= idx + 1;
    }

    // STRING ======================
    public String argAsString(int idx, String def) {
        if (this.args.size() < idx + 1) {
            return def;
        }
        return this.args.get(idx);
    }

    public String argAsString(int idx) {
        return this.argAsString(idx, null);
    }

    // INT ======================
    public Integer strAsInt(String str, Integer def) {
        if (str == null) {
            return def;
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return def;
        }
    }

    public Integer argAsInt(int idx, Integer def) {
        return strAsInt(this.argAsString(idx), def);
    }

    public Integer argAsInt(int idx) {
        return this.argAsInt(idx, null);
    }

    // Double ======================
    public Double strAsDouble(String str, Double def) {
        if (str == null) {
            return def;
        }
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return def;
        }
    }

    public Double argAsDouble(int idx, Double def) {
        return strAsDouble(this.argAsString(idx), def);
    }

    public Double argAsDouble(int idx) {
        return this.argAsDouble(idx, null);
    }

    // TODO: Go through the str conversion for the other arg-readers as well.
    // Boolean ======================
    public Boolean strAsBool(String str) {
        str = str.toLowerCase();
        return str.startsWith("y") || str.startsWith("t") || str.startsWith("on") || str.startsWith("+") || str.startsWith("1");
    }

    public Boolean argAsBool(int idx, boolean def) {
        String str = this.argAsString(idx);
        if (str == null) {
            return def;
        }

        return strAsBool(str);
    }

    public Boolean argAsBool(int idx) {
        return this.argAsBool(idx, false);
    }

    // PLAYER ======================
    public Player strAsPlayer(String name, Player def, boolean msg) {
        Player ret = def;

        if (name != null) {
            Player player = Bukkit.getServer().getPlayer(name);
            if (player != null) {
                ret = player;
            }
        }

        if (msg && ret == null) {
            this.msg(TL.GENERIC_NOPLAYERFOUND, name);
        }

        return ret;
    }

    public Player argAsPlayer(int idx, Player def, boolean msg) {
        return this.strAsPlayer(this.argAsString(idx), def, msg);
    }

    public Player argAsPlayer(int idx, Player def) {
        return this.argAsPlayer(idx, def, true);
    }

    public Player argAsPlayer(int idx) {
        return this.argAsPlayer(idx, null);
    }

    // BEST PLAYER MATCH ======================
    public Player strAsBestPlayerMatch(String name, Player def, boolean msg) {
        Player ret = def;

        if (name != null) {
            List<Player> players = Bukkit.getServer().matchPlayer(name);
            if (players.size() > 0) {
                ret = players.get(0);
            }
        }

        if (msg && ret == null) {
            this.msg(TL.GENERIC_NOPLAYERMATCH, name);
        }

        return ret;
    }

    public Player argAsBestPlayerMatch(int idx, Player def, boolean msg) {
        return this.strAsBestPlayerMatch(this.argAsString(idx), def, msg);
    }

    public Player argAsBestPlayerMatch(int idx, Player def) {
        return this.argAsBestPlayerMatch(idx, def, true);
    }

    public Player argAsBestPlayerMatch(int idx) {
        return this.argAsPlayer(idx, null);
    }
}