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
import com.massivecraft.factions.cmd.chest.AntiChestListener;
import com.massivecraft.factions.cmd.reserve.ReserveAdapter;
import com.massivecraft.factions.cmd.reserve.ReserveObject;
import com.massivecraft.factions.data.helpers.FactionDataHelper;
import com.massivecraft.factions.listeners.*;
import com.massivecraft.factions.listeners.vspecific.ChorusFruitListener;
import com.massivecraft.factions.missions.MissionHandler;
import com.massivecraft.factions.missions.TributeInventoryHandler;
import com.massivecraft.factions.missions.impl.MissionHandlerModern;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.ClipPlaceholderAPIManager;
import com.massivecraft.factions.util.AutoLeaveTask;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.ReflectionUtils;
import com.massivecraft.factions.util.VersionProtocol;
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
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
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

/**
 * Main plugin class for SaberFactions (Folia-compatible version).
 */
public class FactionsPlugin extends MPlugin {

    // track if load was successful
    private boolean loadSuccessful;
    public boolean isLoadSuccessful() {
        return loadSuccessful;
    }
    public void setLoadSuccessful(boolean loadSuccessful) {
        this.loadSuccessful = loadSuccessful;
    }

    public static FactionsPlugin instance;

    private final Gson gsonSerializer = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .enableComplexMapKeySerialization()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
            .registerTypeAdapter(new TypeToken<Map<Permissable, Map<PermissableAction, Access>>>() {}.getType(), new PermissionsMapTypeAdapter())
            .registerTypeAdapter(LazyLocation.class, new MyLocationTypeAdapter())
            .registerTypeAdapter(new TypeToken<Map<FLocation, Set<String>>>() {}.getType(), new MapFLocToStringSetTypeAdapter())
            .registerTypeAdapter(Inventory.class, new InventoryTypeAdapter())
            .registerTypeAdapter(ReserveObject.class, new ReserveAdapter())
            .registerTypeAdapter(Location.class, new LocationTypeAdapter())
            .registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY)
            .create();

    // Some static references
    public static boolean cachedRadiusClaim;
    public static Permission perms = null;

    // plugin fields
    private Map<String, FactionsAddon> factionsAddonHashMap;
    private final HashMap<Faction, String> shieldStatMap = new HashMap<>();

    public static boolean startupFinished = false;
    public boolean PlaceholderApi;

    // Commands
    public FCmdRoot cmdBase;
    public CmdAutoHelp cmdAutoHelp;
    public short version;
    public List<String> itemList = getConfig().getStringList("fchest.Items-Not-Allowed");
    public boolean hookedPlayervaults;
    public FLogManager fLogManager;
    public List<ReserveObject> reserveObjects;
    public FileManager fileManager;

    /**
     * The TimerManager. Could be null if something fails or never gets assigned.
     */
    public TimerManager timerManager;

    private FactionsPlayerListener factionsPlayerListener;
    private boolean locked = false;

    // For auto-leave repeating task in Folia
    private transient ScheduledTask autoLeaveTask = null;

    private ClipPlaceholderAPIManager clipPlaceholderAPIManager;
    private boolean mvdwPlaceholderAPIManager = false;

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

        // Check for Vault
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            Logger.print("You are missing dependencies!", Logger.PrefixType.FAILED);
            Logger.print("Please verify [Vault] is installed!", Logger.PrefixType.FAILED);
            Conf.save();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // version check
        this.version = Short.parseShort(ReflectionUtils.PackageType.getServerVersion().split("_")[1]);

        if (!preEnable()) {
            this.setLoadSuccessful(false);
            return;
        }

        // Load config from disk
        Conf.load();

        // Load data (Board, Factions, FPlayers) asynchronously
        StartupParameter.initData(this, () -> {

            // If faction flight is enabled, schedule the repeating flight check in Folia
            if (getConfig().getBoolean("enable-faction-flight", true)) {
                Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                    this,
                    scheduledTask -> new FlightEnhance().run(),
                    30L,  // initial delay
                    30L   // period
                );
            }

            VersionProtocol.printVerionInfo();

            // Setup commands
            this.cmdBase = new FCmdRoot();
            this.cmdAutoHelp = new CmdAutoHelp();

            setupPermissions();

            // Optionally load WorldGuard bridging
            if (Conf.worldGuardChecking || Conf.worldGuardBuildPriority) {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
                if (plugin != null) {
                    new WorldGuardBridge().connect(this, true);
                }
            }

            // Start auto-leave repeating task if needed
            startAutoLeaveTask(false);

            // Register event listeners
            Bukkit.getPluginManager().registerEvents(new SaberGUIListener(), this);
            Bukkit.getPluginManager().registerEvents(factionsPlayerListener = new FactionsPlayerListener(), this);

            if (Conf.userSpawnerChunkSystem) {
                Bukkit.getPluginManager().registerEvents(new SpawnerChunkListener(), this);
            }

            if (getConfig().getBoolean("disable-chorus-teleport-in-territory") && this.version > 8) {
                Bukkit.getPluginManager().registerEvents(new ChorusFruitListener(), this);
            }

            FactionDataHelper.init();

            // If we are on MC 1.9+ ...
            if (version > 8) {
                Bukkit.getPluginManager().registerEvents(new MissionHandlerModern(), this);
            }

            // Additional event listeners
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
            }) {
                Bukkit.getPluginManager().registerEvents(eventListener, this);
            }

            // If grace system is used, register the graceTimer if available
            if (Conf.useGraceSystem) {
                if (timerManager != null && timerManager.graceTimer != null) {
                    Bukkit.getPluginManager().registerEvents(timerManager.graceTimer, this);
                } else {
                    Logger.print("Grace system is enabled, but TimerManager or graceTimer is null. Skipping graceTimer listener registration.", Logger.PrefixType.WARNING);
                }
            }

            new AsyncPlayerMap(this);

            setupPlaceholderAPI();

            factionsAddonHashMap = new HashMap<>();
            AddonManager.getAddonManagerInstance().loadAddons();

            // Folia runDelayed for 100 ticks, to do any post-load tasks like tab completion building
            Bukkit.getGlobalRegionScheduler().runDelayed(this, scheduledTask -> {
                if (!factionsAddonHashMap.isEmpty()) {
                    FCmdRoot.instance.addVariableCommands();
                    FCmdRoot.instance.rebuild();
                }
            }, 100L);

            // Register command executor
            this.getCommand(refCommand).setExecutor(cmdBase);
            if (!CommodoreProvider.isSupported()) {
                this.getCommand(refCommand).setTabCompleter(this);
            }

            this.postEnable();
            this.setLoadSuccessful(true);
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
            if (rsp != null) {
                perms = rsp.getProvider();
            }
        } catch (NoClassDefFoundError ignored) {}
    }

    @Override
    public Gson getGson() {
        return this.gsonSerializer;
    }

    @Override
    public void onDisable() {
        // Attempt to safely shut down
        ShutdownParameter.initShutdown(this);

        // Cancel any Folia repeating tasks for auto-leave
        if (this.autoLeaveTask != null && !this.autoLeaveTask.isCancelled()) {
            this.autoLeaveTask.cancel();
            this.autoLeaveTask = null;
        }

        if (TextUtil.AUDIENCES != null) {
            TextUtil.AUDIENCES.close();
        }

        super.onDisable();
    }

    /**
     * Start or restart the auto-leave repeating task in Folia
     *
     * @param restartIfRunning whether to forcibly restart if a task is already running
     */
    public void startAutoLeaveTask(boolean restartIfRunning) {
        // If there's already a scheduled task
        if (autoLeaveTask != null && !autoLeaveTask.isCancelled()) {
            if (!restartIfRunning) return;
            autoLeaveTask.cancel();
            autoLeaveTask = null;
        }

        if (Conf.useAutoLeaveAndDisbandSystem && Conf.autoLeaveRoutineRunsEveryXMinutes > 0.0) {
            long ticks = (long) (20 * 60 * Conf.autoLeaveRoutineRunsEveryXMinutes);

            // Folia repeating task
            autoLeaveTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                this,
                scheduledTask -> new AutoLeaveTask().run(),
                ticks,
                ticks
            );
        }
    }

    @Override
    public void postAutoSave() {
        Conf.save();
    }

    public Economy getEcon() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        return rsp != null ? rsp.getProvider() : null;
    }

    @Override
    public boolean logPlayerCommands() {
        return Conf.logPlayerCommands;
    }

    @Override
    public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly) {
        // If a player is prevented from using a command
        if (sender instanceof Player && FactionsPlayerListener.preventCommand(commandString, (Player) sender)) {
            return true;
        }
        return super.handleCommand(sender, commandString, testOnly);
    }

    // For older MC versions fallback
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return super.onTabComplete(sender, command, alias, args);
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

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public FactionsPlayerListener getFactionsPlayerListener() {
        return this.factionsPlayerListener;
    }
}
