package com.massivecraft.factions;

import cc.javajobs.wgbridge.WorldGuardBridge;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.addon.AddonManager;
import com.massivecraft.factions.addon.FactionsAddon;
import com.massivecraft.factions.cmd.CmdAutoHelp;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.audit.FChestListener;
import com.massivecraft.factions.cmd.audit.FLogManager;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.cmd.banner.listener.BannerListener;
import com.massivecraft.factions.cmd.banner.struct.BannerManager;
import com.massivecraft.factions.cmd.chest.AntiChestListener;
import com.massivecraft.factions.cmd.reserve.ReserveAdapter;
import com.massivecraft.factions.cmd.reserve.ReserveObject;
import com.massivecraft.factions.integration.LunarClientWrapper;
import com.massivecraft.factions.listeners.*;
import com.massivecraft.factions.listeners.vspecific.ChorusFruitListener;
import com.massivecraft.factions.missions.MissionHandler;
import com.massivecraft.factions.missions.TributeInventoryHandler;
import com.massivecraft.factions.missions.impl.MissionHandlerModern;
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
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.frame.fupgrades.UpgradesListener;
import com.massivecraft.factions.zcore.util.ShutdownParameter;
import com.massivecraft.factions.zcore.util.StartupParameter;
import com.massivecraft.factions.zcore.util.TextUtil;
import me.lucko.commodore.CommodoreProvider;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.lang.reflect.Modifier;
import java.util.*;


public class FactionsPlugin extends MPlugin {

    // Our single plugin instance.
    // Single 4 life.
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
    public static boolean cachedRadiusClaim;
    public static Permission perms = null;
    private Map<String, FactionsAddon> factionsAddonHashMap;
    private HashMap<Faction, String> shieldStatMap = new HashMap<>();

    // This plugin sets the boolean true when fully enabled.
    // Plugins can check this boolean while hooking in have
    // a green light to use the api.
    public static boolean startupFinished = false;
    public boolean PlaceholderApi;
    // Commands

    public FCmdRoot cmdBase;
    public CmdAutoHelp cmdAutoHelp;
    private AsyncPlayerMap asyncPlayerMap;
    public short version;
    public boolean useNonPacketParticles = false;
    public List<String> itemList = getConfig().getStringList("fchest.Items-Not-Allowed");
    public boolean hookedPlayervaults;
    public FLogManager fLogManager;
    public List<ReserveObject> reserveObjects;
    public FileManager fileManager;
    public TimerManager timerManager;
    private FactionsPlayerListener factionsPlayerListener;
    private boolean locked = false;
    private Integer AutoLeaveTask = null;
    private ClipPlaceholderAPIManager clipPlaceholderAPIManager;
    private boolean mvdwPlaceholderAPIManager = false;
    private BannerManager bannerManager;
    public LunarClientWrapper lcWrapper;

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

        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            Logger.print("You are missing dependencies!", Logger.PrefixType.FAILED);
            Logger.print("Please verify [Vault] is installed!", Logger.PrefixType.FAILED);
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
                Bukkit.getServer().getScheduler().runTaskTimer(FactionsPlugin.getInstance(), new FlightEnhance(), 30L, 30L);
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

            if (Conf.userSpawnerChunkSystem) {
                Bukkit.getPluginManager().registerEvents(new SpawnerChunkListener(), this);
            }

            if (FactionsPlugin.getInstance().getConfig().getBoolean("disable-chorus-teleport-in-territory") && this.version > 8) {
                Bukkit.getPluginManager().registerEvents(new ChorusFruitListener(), this);
            }


            if (version > 8) {
                Bukkit.getPluginManager().registerEvents(new MissionHandlerModern(), this);
            }

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

            this.asyncPlayerMap = new AsyncPlayerMap(this);

            this.setupPlaceholderAPI();
            factionsAddonHashMap = new HashMap<>();
            AddonManager.getAddonManagerInstance().loadAddons();

            Bukkit.getScheduler().runTaskLater(this, () -> {
                //To Add Addon Commands Into "Tab Completion Format"
                if(factionsAddonHashMap.size() > 0) {
                    FCmdRoot.instance.addVariableCommands();
                    FCmdRoot.instance.rebuild();
                }
            }, 100);

            if (FactionsPlugin.getInstance().getConfig().getBoolean("fbanners.Enabled")) {
                bannerManager = new BannerManager();
                bannerManager.onEnable(this);
                getServer().getPluginManager().registerEvents(new BannerListener(), this);
            }

            this.getCommand(refCommand).setExecutor(cmdBase);

            if (!CommodoreProvider.isSupported()) this.getCommand(refCommand).setTabCompleter(this);


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

    public BannerManager getBannerManager() {
        return bannerManager;
    }

    @Override
    public Gson getGson() {
        return this.gsonSerializer;
    }

    @Override
    public void onDisable() {
        if (this.AutoLeaveTask != null) {
            getServer().getScheduler().cancelTask(this.AutoLeaveTask);
            this.AutoLeaveTask = null;
        }
        if (TextUtil.AUDIENCES != null) {
            TextUtil.AUDIENCES.close();
        }

        if(bannerManager != null) {
            bannerManager.onDisable(this);
        }

        ShutdownParameter.initShutdown(this);

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
        //Board.getInstance().forceSave(); Not sure why this was there as it's called after the board is already saved.
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
        List<FCommand> commandsList = cmdBase.subCommands;
        FCommand commandsEx = cmdBase;
        List<String> completions = new ArrayList<>();
        // Check for "" first arg because spigot is mangled.
        if (context.args.get(0).equals("")) {
            for (FCommand subCommand : commandsEx.subCommands) {
                if (subCommand.requirements.playerOnly && sender.hasPermission(subCommand.requirements.permission.node) && subCommand.visibility != CommandVisibility.INVISIBLE)
                    completions.addAll(subCommand.aliases);
            }
            return completions;
        } else if (context.args.size() == 1) {
            for (; !commandsList.isEmpty() && !context.args.isEmpty(); context.args.remove(0)) {
                String cmdName = context.args.get(0).toLowerCase();
                boolean toggle = false;
                for (FCommand fCommand : commandsList) {
                    for (String s : fCommand.aliases) {
                        if (s.startsWith(cmdName)) {
                            commandsList = fCommand.subCommands;
                            completions.addAll(fCommand.aliases);
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

    public AsyncPlayerMap getAsyncPlayerMap() {
        return asyncPlayerMap;
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

    public LunarClientWrapper getLunarClientWrapper() {
        return lcWrapper;
    }

    public List<ReserveObject> getFactionReserves() {
        return this.reserveObjects;
    }


    public String getPrimaryGroup(OfflinePlayer player) {
        return perms == null || !perms.hasGroupSupport() ? " " : perms.getPrimaryGroup(Bukkit.getWorlds().get(0).toString(), player);
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }


    public FactionsPlayerListener getFactionsPlayerListener() {
        return this.factionsPlayerListener;
    }
}
