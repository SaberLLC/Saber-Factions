package com.massivecraft.factions;

import cc.javajobs.wgbridge.WorldGuardBridge;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.addon.AddonManager;
import com.massivecraft.factions.addon.FactionsAddon;
import com.massivecraft.factions.compat.CompatibilityBootstrap;
import com.massivecraft.factions.compat.CompatibilityContext;
import com.massivecraft.factions.compat.CompatibilityModule;
import com.massivecraft.factions.cmd.CmdAutoHelp;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.audit.FChestListener;
import com.massivecraft.factions.cmd.audit.FLogManager;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.cmd.chest.AntiChestListener;
import com.massivecraft.factions.cmd.reserve.ReserveAdapter;
import com.massivecraft.factions.cmd.reserve.ReserveObject;
import com.massivecraft.factions.data.helpers.FactionDataHelper;
import com.massivecraft.factions.data.listener.FactionDataListener;
import com.massivecraft.factions.listeners.*;
import com.massivecraft.factions.missions.MissionHandler;
import com.massivecraft.factions.missions.TributeInventoryHandler;
import com.massivecraft.factions.struct.FactionRole;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.util.adapters.*;
import com.massivecraft.factions.util.flight.FlightEnhance;
import com.massivecraft.factions.util.flight.stuct.AsyncPlayerMap;
import com.massivecraft.factions.util.timer.TimerManager;
import com.massivecraft.factions.zcore.CommandVisibility;
import com.massivecraft.factions.zcore.MPlugin;
import com.massivecraft.factions.zcore.file.impl.FileManager;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.frame.fupgrades.UpgradesListener;
import com.massivecraft.factions.scheduler.FactionScheduler;
import com.massivecraft.factions.scheduler.FactionTask;
import com.massivecraft.factions.zcore.util.ShutdownParameter;
import com.massivecraft.factions.zcore.util.StartupParameter;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;


public class FactionsPlugin extends MPlugin {

    public static FactionsPlugin instance;
    private static FactionScheduler scheduler;

    public static FactionScheduler getScheduler() {
        return scheduler;
    }

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private final Gson gsonSerializer = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().enableComplexMapKeySerialization().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
            .registerTypeAdapter(new TypeToken<Map<Permissable, Map<String, Access>>>() {
            }.getType(), new PermissionsMapTypeAdapter())
            .registerTypeAdapter(LazyLocation.class, new MyLocationTypeAdapter())
            .registerTypeAdapter(new TypeToken<Map<FLocation, Set<String>>>() {
            }.getType(), new MapFLocToStringSetTypeAdapter())
            .registerTypeAdapter(Inventory.class, new InventoryTypeAdapter())
            .registerTypeAdapter(ReserveObject.class, new ReserveAdapter())
            .registerTypeAdapter(Location.class, new LocationTypeAdapter())
            .registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY)
            .create();

    public static Permission perms = null;
    private FactionDataHelper factionDataHelper;
    private Map<String, FactionsAddon> factionsAddonHashMap;
    private final HashMap<Faction, String> shieldStatMap = new HashMap<>();

    // This plugin sets the boolean true when fully enabled.
    // Plugins can check this boolean while hooking in have
    // a green light to use the api.
    public static boolean startupFinished = false;
    public boolean PlaceholderApi;

    // Commands
    public FCmdRoot cmdBase;
    public CmdAutoHelp cmdAutoHelp;
    public short version;
    public List<String> itemList = getConfig().getStringList("fchest.Items-Not-Allowed");
    public FLogManager fLogManager;
    public List<ReserveObject> reserveObjects;
    public FileManager fileManager;
    public TimerManager timerManager;
    private FactionsPlayerListener factionsPlayerListener;
    private boolean locked = false;
    private FactionTask autoLeaveTask = null;
    private ClipPlaceholderAPIManager clipPlaceholderAPIManager;
    private boolean mvdwPlaceholderAPIManager = false;
    private CompatibilityModule compatibilityModule;

    public FactionsPlugin() {
        instance = this;
    }

    public static FactionsPlugin getInstance() {
        return instance;
    }

    public static boolean canPlayersJoin() {
        return startupFinished;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public boolean getLocked() {
        return this.locked;
    }

    public CompatibilityModule getCompatibilityModule() {
        return compatibilityModule;
    }

    public boolean usesBrigadierCompletions() {
        return compatibilityModule != null && compatibilityModule.supportsBrigadier();
    }

    public void setLocked(boolean val) {
        this.locked = val;
        this.setAutoSave(val);
    }

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            Logger.print("You are missing dependencies!", Logger.PrefixType.FAILED);
            Logger.print("Please verify [Vault] is installed!", Logger.PrefixType.FAILED);
            Conf.save();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        try {
            if (isFolia()) {
                Class<?> clazz = Class.forName(
                        "com.massivecraft.factions.scheduler.FoliaFactionScheduler");
                scheduler = (FactionScheduler) clazz
                        .getDeclaredConstructor(org.bukkit.plugin.Plugin.class)
                        .newInstance(this);
            } else {
                Class<?> clazz = Class.forName(
                        "com.massivecraft.factions.scheduler.BukkitFactionScheduler");
                scheduler = (FactionScheduler) clazz
                        .getDeclaredConstructor(org.bukkit.plugin.Plugin.class)
                        .newInstance(this);
            }
        } catch (Exception e) {
            getLogger().severe("Failed to initialise FactionScheduler: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.version = Short.parseShort(ReflectionUtils.PackageType.getServerVersion().split("_")[1]);

        if (!preEnable()) {
            this.loadSuccessful = false;
            return;
        }

        // Load Conf from disk
        Conf.load();
        normalizeBaseCommandAliases();

        StartupParameter.initData(this, () -> {
            if (getConfig().getBoolean("enable-faction-flight", true)) {
                FactionsPlugin.getScheduler().runGlobalRepeating(30L, 30L, new FlightEnhance());
            }

            VersionProtocol.printVersionInfo();
            this.compatibilityModule = CompatibilityBootstrap.select(this.version);
            // Add Base Commands
            this.cmdBase = new FCmdRoot();
            this.cmdAutoHelp = new CmdAutoHelp();

            setupPermissions();

            if (Conf.worldGuardChecking || Conf.worldGuardBuildPriority) {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
                if (plugin != null) {
                    new WorldGuardBridge().connect(this, true);
                }
            }

            // start up task which runs the autoLeaveAfterDaysOfInactivity routine
            startAutoLeaveTask(false);

            Bukkit.getPluginManager().registerEvents(new SaberGUIListener(), this);
            Bukkit.getPluginManager().registerEvents(factionsPlayerListener = new FactionsPlayerListener(), this);

            if (Conf.userSpawnerChunkSystem) {
                Bukkit.getPluginManager().registerEvents(new SpawnerChunkListener(), this);
            }

            this.factionDataHelper = new FactionDataHelper(this.getDataFolder());
            Bukkit.getPluginManager().registerEvents(new FactionDataListener(this.factionDataHelper), this);
            FactionsPlugin.getScheduler().runGlobalLater(10L, () -> {
                for (Faction faction : Factions.getInstance().getAllNormalFactions()) {
                    this.factionDataHelper.getOrLoadFactionData(faction);
                }
            });
            this.compatibilityModule.onEnable(new CompatibilityContext(this));

            for (Listener eventListener : new Listener[]{
                    new TributeInventoryHandler(),
                    new FactionsChatListener(),
                    new FactionsEntityListener(),
                    new FactionsExploitListener(),
                    new FactionsBlockListener(),
                    new UpgradesListener(),
                    new MissionHandler(this),
                    new FChestListener(),
                    new MenuListener(),
                    new AntiChestListener()
            })
                Bukkit.getPluginManager().registerEvents(eventListener, this);

            if (Conf.useGraceSystem) {
                Bukkit.getPluginManager().registerEvents(timerManager.graceTimer, this);
            }

            new AsyncPlayerMap(this);

            this.setupPlaceholderAPI();
            factionsAddonHashMap = new HashMap<>();
            AddonManager.getAddonManagerInstance().loadAddons();

            FactionsPlugin.getScheduler().runGlobalLater(100L, () -> {
                //To Add Addon Commands Into "Tab Completion Format"
                if (!factionsAddonHashMap.isEmpty()) {
                    FCmdRoot.instance.addVariableCommands();
                    FCmdRoot.instance.rebuild();
                }
            });

            PluginCommand baseCommand = this.getCommand(refCommand);
            if (baseCommand == null) {
                Logger.print("Unable to register base command aliases because command '" + refCommand + "' is missing from plugin.yml.", Logger.PrefixType.FAILED);
                this.loadSuccessful = false;
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            registerBaseCommandAliases(baseCommand);
            baseCommand.setExecutor(cmdBase);
            if (!usesBrigadierCompletions()) baseCommand.setTabCompleter(this);


            this.postEnable();
            this.loadSuccessful = true;
            // Set startup finished to true. to give plugins hooking in a greenlight
            FactionsPlugin.startupFinished = true;
        });
    }

    private void setupPlaceholderAPI() {
        Plugin clip = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (clip != null && clip.isEnabled()) {
            this.clipPlaceholderAPIManager = new ClipPlaceholderAPIManager();
            if (this.clipPlaceholderAPIManager.register()) {
                PlaceholderApi = true;
                Logger.print("Successfully registered placeholders with PlaceholderAPI.", Logger.PrefixType.DEFAULT);
            } else {
                PlaceholderApi = false;
            }
        } else {
            PlaceholderApi = false;
        }

        Plugin mvdw = Bukkit.getPluginManager().getPlugin("MVdWPlaceholderAPI");
        if (mvdw != null && mvdw.isEnabled()) {
            this.mvdwPlaceholderAPIManager = true;
            Logger.print("Found MVdWPlaceholderAPI. Adding hooks.", Logger.PrefixType.DEFAULT);
        }
    }


    public HashMap<Faction, String> getShieldStatMap() {
        return shieldStatMap;
    }

    public Map<String, FactionsAddon> getFactionsAddonHashMap() {
        return factionsAddonHashMap;
    }

    public boolean isClipPlaceholderAPIHooked() {
        return this.clipPlaceholderAPIManager != null;
    }

    public boolean isMVdWPlaceholderAPIHooked() {
        return this.mvdwPlaceholderAPIManager;
    }

    private void setupPermissions() {
        try {
            RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
            if (rsp != null) perms = rsp.getProvider();
        } catch (NoClassDefFoundError ignored) {
        }
    }

    public void reloadBaseCommandAliases() {
        PluginCommand baseCommand = this.getCommand(refCommand);
        if (baseCommand == null) {
            Logger.print("Unable to reload base command aliases because command '" + refCommand + "' is missing from plugin.yml.", Logger.PrefixType.FAILED);
            return;
        }
        registerBaseCommandAliases(baseCommand);
    }

    private void registerBaseCommandAliases(PluginCommand baseCommand) {
        List<String> aliases = normalizeBaseCommandAliases();
        if (this.cmdBase != null) {
            this.cmdBase.getAliases().clear();
            this.cmdBase.getAliases().addAll(aliases);
        }
        baseCommand.setAliases(aliases);

        CommandMap commandMap = getCommandMap();
        if (commandMap == null) {
            Logger.print("Unable to access Bukkit command map. Only plugin.yml base command aliases will work.", Logger.PrefixType.WARNING);
            return;
        }

        try {
            removeKnownCommandEntries(commandMap, baseCommand);
            baseCommand.unregister(commandMap);
            baseCommand.setAliases(aliases);
            commandMap.register(getDescription().getName().toLowerCase(Locale.ROOT), baseCommand);
            Logger.print("Registered faction base command aliases: " + String.join(", ", aliases), Logger.PrefixType.DEFAULT);
        } catch (Exception exception) {
            Logger.print("Failed to register faction base command aliases: " + exception.getMessage(), Logger.PrefixType.FAILED);
            exception.printStackTrace();
        }
    }

    private List<String> normalizeBaseCommandAliases() {
        LinkedHashSet<String> aliases = new LinkedHashSet<>();
        if (Conf.baseCommandAliases != null) {
            for (String rawAlias : Conf.baseCommandAliases) {
                String alias = normalizeBaseCommandAlias(rawAlias);
                if (alias == null || alias.equalsIgnoreCase(refCommand)) {
                    continue;
                }
                aliases.add(alias);
            }
        }

        if (aliases.isEmpty()) {
            aliases.add("f");
        }

        Conf.baseCommandAliases = new ArrayList<>(aliases);
        return Conf.baseCommandAliases;
    }

    private String normalizeBaseCommandAlias(String rawAlias) {
        if (rawAlias == null) {
            return null;
        }

        String alias = rawAlias.trim().toLowerCase(Locale.ROOT);
        while (alias.startsWith("/")) {
            alias = alias.substring(1);
        }

        if (alias.isEmpty()) {
            return null;
        }

        for (int index = 0; index < alias.length(); index++) {
            char character = alias.charAt(index);
            if (Character.isWhitespace(character) || character == ':') {
                return null;
            }
        }
        return alias;
    }

    @SuppressWarnings("unchecked")
    private void removeKnownCommandEntries(CommandMap commandMap, PluginCommand baseCommand) {
        try {
            Field field = commandMap.getClass().getDeclaredField("knownCommands");
            field.setAccessible(true);
            Object result = field.get(commandMap);
            if (!(result instanceof Map)) {
                return;
            }

            Map<String, Command> knownCommands = (Map<String, Command>) result;
            knownCommands.entrySet().removeIf(entry -> entry.getValue() == baseCommand);
        } catch (Exception ignored) {
        }
    }

    private CommandMap getCommandMap() {
        try {
            Method method = getServer().getClass().getMethod("getCommandMap");
            method.setAccessible(true);
            Object result = method.invoke(getServer());
            if (result instanceof CommandMap) {
                return (CommandMap) result;
            }
        } catch (Exception ignored) {
        }

        try {
            Field field = getServer().getPluginManager().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            Object result = field.get(getServer().getPluginManager());
            if (result instanceof CommandMap) {
                return (CommandMap) result;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public Gson getGson() {
        return this.gsonSerializer;
    }

    @Override
    public void onDisable() {


        ShutdownParameter.initShutdown(this);

        if (scheduler != null) {
            scheduler.cancelAll();
            scheduler = null;
        }
        if (TextUtil.AUDIENCES != null) {
            TextUtil.AUDIENCES.close();
        }

        if (this.factionDataHelper != null) {
            this.factionDataHelper.saveAllCachedData();
            this.factionDataHelper.shutdown();
        }

        super.onDisable();
    }

    public void startAutoLeaveTask(boolean restartIfRunning) {
        if (autoLeaveTask != null) {
            if (!restartIfRunning) return;
            autoLeaveTask.cancel();
            autoLeaveTask = null;
        }

        if (Conf.useAutoLeaveAndDisbandSystem) {
            if (Conf.autoLeaveRoutineRunsEveryXMinutes > 0.0) {
                long ticks = (long) (20 * 60 * Conf.autoLeaveRoutineRunsEveryXMinutes);
                autoLeaveTask = FactionsPlugin.getScheduler().runGlobalRepeating(ticks, ticks, new AutoLeaveTask());
            }
        }
    }

    @Override
    public void postAutoSave() {
        Conf.save();
    }


    public Economy getEcon() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        return rsp.getProvider();
    }


    @Override
    public boolean logPlayerCommands() {
        return Conf.logPlayerCommands;
    }

    @Override
    public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly) {
        return sender instanceof Player && FactionsPlayerListener.preventCommand(commandString, (Player) sender) || super.handleCommand(sender, commandString, testOnly);
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        // Must be a LinkedList to prevent UnsupportedOperationException.
        List<String> argsList = new LinkedList<>(Arrays.asList(args));
        CommandContext context = new CommandContext(sender, argsList, alias);
        List<FCommand> commandsList = cmdBase.getSubCommands();
        FCommand commandsEx = cmdBase;
        List<String> completions = new ArrayList<>();

        // Handle empty first arg (spigot bug workaround)
        if (context.args.get(0).isEmpty()) {
            for (FCommand subCommand : commandsEx.getSubCommands()) {
                if (subCommand.getRequirements().isPlayerOnly()
                        && sender.hasPermission(subCommand.getRequirements().getPermission().node)
                        && subCommand.getVisibility() != CommandVisibility.INVISIBLE) {
                    completions.addAll(subCommand.getAliases());
                }
            }
            return completions;
        }

        // Handle first argument = subcommand
        if (context.args.size() == 1) {
            for (; !commandsList.isEmpty() && !context.args.isEmpty(); context.args.remove(0)) {
                String cmdName = context.args.get(0).toLowerCase();
                boolean found = false;

                for (FCommand fCommand : commandsList) {
                    for (String s : fCommand.getAliases()) {
                        if (s.startsWith(cmdName)) {
                            commandsList = fCommand.getSubCommands();
                            completions.addAll(fCommand.getAliases());
                            found = true;
                            break;
                        }
                    }
                    if (found) break;
                }
            }

            String lastArg = args[args.length - 1].toLowerCase();
            return completions.stream()
                    .filter(name -> name.toLowerCase().startsWith(lastArg))
                    .collect(Collectors.toList());
        }

        // Handle further arguments — walk the subcommand tree using all args except the last
        String currentArg = args[args.length - 1].toLowerCase();
        FCommand resolved = cmdBase;

        for (int i = 0; i < args.length - 1; i++) {
            String part = args[i].toLowerCase();
            FCommand next = null;
            for (FCommand sub : resolved.getSubCommands()) {
                for (String a : sub.getAliases()) {
                    if (a.equalsIgnoreCase(part)) {
                        next = sub;
                        break;
                    }
                }
                if (next != null) break;
            }
            if (next == null) break;
            resolved = next;
        }

        // If the resolved command still has subcommands, complete with those aliases
        if (!resolved.getSubCommands().isEmpty()) {
            for (FCommand sub : resolved.getSubCommands()) {
                completions.addAll(sub.getAliases());
            }
            return completions.stream()
                    .filter(name -> name.toLowerCase().startsWith(currentArg))
                    .collect(Collectors.toList());
        }

        // Determine which positional argument the user is filling based on arg index
        int argIndex = args.length - 2;
        List<String> argNames = new ArrayList<>(resolved.getRequiredArgs());
        argNames.addAll(resolved.getOptionalArgs().keySet());
        String argName = argIndex >= 0 && argIndex < argNames.size()
                ? argNames.get(argIndex).toLowerCase() : "";

        // Player/name completions
        if (argName.contains("player") || argName.contains("target") || argName.contains("member") || argName.contains("name")) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(currentArg)) completions.add(player.getName());
            }
            return completions;
        }

        // Faction completions
        if (argName.contains("faction") || argName.contains("tag")) {
            for (Faction faction : Factions.getInstance().getAllFactions()) {
                String tag = ChatColor.stripColor(faction.getTag());
                if (tag.toLowerCase().startsWith(currentArg)) completions.add(tag);
            }
            return completions;
        }

        // Relation completions
        if (argName.contains("relation") || argName.contains("enemy") || argName.contains("ally") || argName.contains("neutral") || argName.contains("truce")) {
            for (Relation value : Relation.VALUES) {
                if (value.nicename.toLowerCase().startsWith(currentArg)) completions.add(value.nicename);
            }
            return completions;
        }

        // Role completions
        if (argName.contains("role")) {
            for (Role value : Role.VALUES) {
                if (value.nicename.toLowerCase().startsWith(currentArg)) completions.add(value.nicename);
            }
            if (sender instanceof Player) {
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player) sender);
                if (fPlayer != null && fPlayer.hasFaction()) {
                    for (FactionRole value : fPlayer.getFaction().getRoles().values()) {
                        String id = value.getId();
                        String display = ChatColor.stripColor(value.getDisplayName());
                        if (id.toLowerCase().startsWith(currentArg)) completions.add(id);
                        if (display.toLowerCase().startsWith(currentArg)) completions.add(display);
                    }
                }
            }
            return completions;
        }

        // on/off toggle completions
        if (argName.contains("on/off") || argName.contains("yes/no") || argName.contains("on/off/auto")) {
            for (String opt : Arrays.asList("on", "off", "auto", "yes", "no", "flip")) {
                if (opt.startsWith(currentArg) && argName.contains(opt.replace("flip", "on/off"))) completions.add(opt);
            }
            if (argName.contains("on/off/auto")) {
                for (String opt : Arrays.asList("on", "off", "auto")) {
                    if (opt.startsWith(currentArg)) completions.add(opt);
                }
            } else if (argName.contains("yes/no")) {
                for (String opt : Arrays.asList("yes", "no")) {
                    if (opt.startsWith(currentArg)) completions.add(opt);
                }
            } else {
                for (String opt : Arrays.asList("on", "off")) {
                    if (opt.startsWith(currentArg)) completions.add(opt);
                }
            }
            return completions;
        }

        // Chat mode completions
        if (argName.equals("mode")) {
            for (String opt : Arrays.asList("faction", "alliance", "public", "truce")) {
                if (opt.startsWith(currentArg)) completions.add(opt);
            }
            return completions;
        }

        // Top criteria completions
        if (argName.equals("criteria")) {
            for (String opt : Arrays.asList("land", "online", "power", "kills", "deaths", "members")) {
                if (opt.startsWith(currentArg)) completions.add(opt);
            }
            return completions;
        }

        // Amount/number: return hint only (no enumerable values)
        if (argName.contains("amount") || argName.contains("number") || argName.contains("power")
                || argName.contains("radius") || argName.contains("limit") || argName.contains("height")
                || argName.contains("page") || argName.contains("strikes")) {
            return completions;
        }

        // Default: players + factions
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(currentArg)) completions.add(player.getName());
        }
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            String tag = ChatColor.stripColor(faction.getTag());
            if (tag.toLowerCase().startsWith(currentArg)) completions.add(tag);
        }
        return completions;
    }

    // -------------------------------------------- //
    // Functions for other plugins to hook into
    // -------------------------------------------- //

    // If another plugin is handling insertion of chat tags, this should be used to notify Factions
    public void handleFactionTagExternally(boolean notByFactions) {
        Conf.chatTagHandledByAnotherPlugin = notByFactions;
    }

    public FLogManager getFlogManager() {
        return fLogManager;
    }

    public void logFactionEvent(Faction faction, FLogType type, String... arguments) {
        this.fLogManager.log(faction, type, arguments);
    }


    public List<ReserveObject> getFactionReserves() {
        return this.reserveObjects;
    }


    public String getPrimaryGroup(OfflinePlayer player) {
        return perms == null || !perms.hasGroupSupport() ? " " : perms.getPrimaryGroup(Bukkit.getWorlds().get(0).toString(), player);
    }

    public FactionDataHelper getFactionDataHelper() {
        return factionDataHelper;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }


    public FactionsPlayerListener getFactionsPlayerListener() {
        return this.factionsPlayerListener;
    }
}
