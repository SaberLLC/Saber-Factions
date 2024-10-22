package com.massivecraft.factions;

import cc.javajobs.wgbridge.WorldGuardBridge;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.addon.AddonManager;
import com.massivecraft.factions.addon.FactionsAddon;
import com.massivecraft.factions.apollo.core.RecipientsUpdaterListener;
import com.massivecraft.factions.apollo.fteam.ApolloFTeam;
import com.massivecraft.factions.apollo.fteam.ApolloFTeamTask;
import com.massivecraft.factions.cmd.CmdAutoHelp;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.reserve.ReserveAdapter;
import com.massivecraft.factions.cmd.reserve.ReserveObject;
import com.massivecraft.factions.data.helpers.FactionDataHelper;
import com.massivecraft.factions.listeners.*;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.util.adapters.*;
import com.massivecraft.factions.util.flight.FlightEnhance;
import com.massivecraft.factions.util.flight.stuct.AsyncPlayerMap;
import com.massivecraft.factions.zcore.CommandVisibility;
import com.massivecraft.factions.zcore.MPlugin;
import com.massivecraft.factions.zcore.file.impl.FileManager;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.frame.fupgrades.UpgradesListener;
import com.massivecraft.factions.zcore.util.ShutdownParameter;
import com.massivecraft.factions.zcore.util.StartupParameter;
import com.massivecraft.factions.zcore.util.TextUtil;
import io.papermc.lib.PaperLib;
import io.papermc.lib.environments.Environment;
import me.lucko.commodore.CommodoreProvider;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.lang.reflect.Modifier;
import java.util.*;


public class FactionsPlugin extends MPlugin {

    public static FactionsPlugin instance;
    private final Gson gsonSerializer = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().enableComplexMapKeySerialization().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
            .registerTypeAdapter(new TypeToken<Map<Permissable, Map<PermissableAction, Access>>>() {
            }.getType(), new PermissionsMapTypeAdapter())
            .registerTypeAdapter(LazyLocation.class, new MyLocationTypeAdapter())
            .registerTypeAdapter(new TypeToken<Map<FLocation, Set<String>>>() {
            }.getType(), new MapFLocToStringSetTypeAdapter())
            .registerTypeAdapter(Inventory.class, new InventoryTypeAdapter())
            .registerTypeAdapter(ReserveObject.class, new ReserveAdapter())
            .registerTypeAdapter(Location.class, new LocationTypeAdapter())
            .registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY)
            .create();

    //TODO REDO
    public static boolean cachedRadiusClaim;

    public static Permission perms = null;
    private Map<String, FactionsAddon> factionsAddonHashMap;

    // This plugin sets the boolean true when fully enabled.
    // Plugins can check this boolean while hooking in have
    // a green light to use the api.
    public static boolean startupFinished = false;
    public boolean PlaceholderApi;

    // Commands
    public FCmdRoot cmdBase;
    public CmdAutoHelp cmdAutoHelp;
    public short version;
    public List<ReserveObject> reserveObjects;
    public FileManager fileManager;
    private FactionsPlayerListener factionsPlayerListener;
    private boolean locked = false;
    private Integer AutoLeaveTask = null;
    private ClipPlaceholderAPIManager clipPlaceholderAPIManager;

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

    public void setLocked(boolean val) {
        this.locked = val;
        this.setAutoSave(val);
    }

    @Override
    public void onEnable() {

        Environment env = PaperLib.getEnvironment();
        if (!env.isImanity()) {
            Logger.print("This fork of Factions need the server Running on ImanitySpigot 3", Logger.PrefixType.FAILED);
            Logger.print("Please verify you are using Imanity!", Logger.PrefixType.FAILED);
            Conf.save();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final PluginManager pluginManager = Bukkit.getPluginManager();

        if (pluginManager.getPlugin("Vault") == null && pluginManager.getPlugin("Apollo-Bukkit") != null) {
            Logger.print("You are missing dependencies!", Logger.PrefixType.FAILED);
            Logger.print("Please verify [Vault] AND [Apollo-Bukkit] is installed!", Logger.PrefixType.FAILED);
            Conf.save();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.version = Short.parseShort(ReflectionUtils.PackageType.getServerVersion().split("_")[1]);

        if (!preEnable()) {
            this.loadSuccessful = false;
            return;
        }

        // Load Conf from disk
        Conf.load();

        StartupParameter.initData(this, () -> {
            if (getConfig().getBoolean("enable-faction-flight", true)) {
                Bukkit.getServer().getScheduler().runTaskTimer(FactionsPlugin.getInstance(), new FlightEnhance(getConfig().getBoolean("ffly.AutoEnable", false)), 30L, 30L);
            }

            VersionProtocol.printVerionInfo();
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

            FactionDataHelper.init();

            for (Listener eventListener : new Listener[]{
                    new FactionsChatListener(),
                    new FactionsEntityListener(),
                    new FactionsExploitListener(),
                    new FactionsBlockListener(),
                    new UpgradesListener(),
                    new MenuListener(),
            })
                Bukkit.getPluginManager().registerEvents(eventListener, this);

            new AsyncPlayerMap(this);

            this.setupPlaceholderAPI();
            factionsAddonHashMap = new HashMap<>();
            AddonManager.getAddonManagerInstance().loadAddons();

            Bukkit.getScheduler().runTaskLater(this, () -> {
                //To Add Addon Commands Into "Tab Completion Format"
                if (!factionsAddonHashMap.isEmpty()) {
                    FCmdRoot.instance.addVariableCommands();
                    FCmdRoot.instance.rebuild();
                }
            }, 100);

            this.getCommand(refCommand).setExecutor(cmdBase);
            if (!CommodoreProvider.isSupported()) this.getCommand(refCommand).setTabCompleter(this);

            if (Conf.enableApolloIntegration) {
                new ApolloFTeam();
                Bukkit.getPluginManager().registerEvents(new RecipientsUpdaterListener(), this);
                Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ApolloFTeamTask(), 100, 100);
            }

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

    }


    public Map<String, FactionsAddon> getFactionsAddonHashMap() {
        return factionsAddonHashMap;
    }

    public boolean isClipPlaceholderAPIHooked() {
        return this.clipPlaceholderAPIManager != null;
    }

    private void setupPermissions() {
        try {
            RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
            if (rsp != null) perms = rsp.getProvider();
        } catch (NoClassDefFoundError ignored) {
        }
    }

    @Override
    public Gson getGson() {
        return this.gsonSerializer;
    }

    @Override
    public void onDisable() {
        ShutdownParameter.initShutdown(this);

        if (this.AutoLeaveTask != null) {
            getServer().getScheduler().cancelTask(this.AutoLeaveTask);
            this.AutoLeaveTask = null;
        }
        if (TextUtil.AUDIENCES != null) {
            TextUtil.AUDIENCES.close();
        }

        super.onDisable();
    }

    public void startAutoLeaveTask(boolean restartIfRunning) {
        if (AutoLeaveTask != null) {
            if (!restartIfRunning) return;
            this.getServer().getScheduler().cancelTask(AutoLeaveTask);
        }

        if (Conf.useAutoLeaveAndDisbandSystem) {
            if (Conf.autoLeaveRoutineRunsEveryXMinutes > 0.0) {
                long ticks = (long) (20 * 60 * Conf.autoLeaveRoutineRunsEveryXMinutes);
                AutoLeaveTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoLeaveTask(), ticks, ticks);
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


    // This method must stay for < 1.12 versions
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Must be a LinkedList to prevent UnsupportedOperationException.
        List<String> argsList = new LinkedList<>(Arrays.asList(args));
        CommandContext context = new CommandContext(sender, argsList, alias);
        List<FCommand> commandsList = cmdBase.getSubCommands();
        FCommand commandsEx = cmdBase;
        List<String> completions = new ArrayList<>();
        // Check for "" first arg because spigot is mangled.
        if (context.args.get(0).equals("")) {
            for (FCommand subCommand : commandsEx.getSubCommands()) {
                if (subCommand.getRequirements().isPlayerOnly() && sender.hasPermission(subCommand.getRequirements().getPermission().node) && subCommand.getVisibility() != CommandVisibility.INVISIBLE)
                    completions.addAll(subCommand.getAliases());
            }
            return completions;
        } else if (context.args.size() == 1) {
            for (; !commandsList.isEmpty() && !context.args.isEmpty(); context.args.remove(0)) {
                String cmdName = context.args.get(0).toLowerCase();
                boolean toggle = false;
                for (FCommand fCommand : commandsList) {
                    for (String s : fCommand.getAliases()) {
                        if (s.startsWith(cmdName)) {
                            commandsList = fCommand.getSubCommands();
                            completions.addAll(fCommand.getAliases());
                            toggle = true;
                            break;
                        }
                    }
                    if (toggle) break;
                }
            }
            String lastArg = args[args.length - 1].toLowerCase();
            List<String> filteredCompletions = new ArrayList<>(completions.size());
            for (String completion : completions) {
                if (completion.toLowerCase().startsWith(lastArg)) {
                    filteredCompletions.add(completion);
                }
            }
            return filteredCompletions;
        } else {
            String lastArg = args[args.length - 1].toLowerCase();
            for (Role value : Role.VALUES) completions.add(value.nicename);
            for (Relation value : Relation.VALUES) completions.add(value.nicename);
            // The stream and foreach from the old implementation looped 2 times, by looping all players -> filtered -> looped filter and added -> filtered AGAIN at the end.
            // This loops them once and just adds, because we are filtering the arguments at the end anyways
            for (Player player : Bukkit.getServer().getOnlinePlayers()) completions.add(player.getName());
            for (Faction faction : Factions.getInstance().getAllFactions())
                completions.add(ChatColor.stripColor(faction.getTag()));
            List<String> filteredCompletions = new ArrayList<>(completions.size());
            for (String completion : completions) {
                if (completion.toLowerCase().startsWith(lastArg)) {
                    filteredCompletions.add(completion);
                }
            }
            return filteredCompletions;
        }
    }

    // -------------------------------------------- //
    // Functions for other plugins to hook into
    // -------------------------------------------- //

    // If another plugin is handling insertion of chat tags, this should be used to notify Factions
    public void handleFactionTagExternally(boolean notByFactions) {
        Conf.chatTagHandledByAnotherPlugin = notByFactions;
    }

    public List<ReserveObject> getFactionReserves() {
        return this.reserveObjects;
    }

    public String getPrimaryGroup(OfflinePlayer player) {
        return perms == null || !perms.hasGroupSupport() ? " " : perms.getPrimaryGroup(Bukkit.getWorlds().get(0).toString(), player);
    }

    public FactionsPlayerListener getFactionsPlayerListener() {
        return this.factionsPlayerListener;
    }
}
