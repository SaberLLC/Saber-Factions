package com.massivecraft.factions;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.cmd.*;
import com.massivecraft.factions.cmd.audit.FChestListener;
import com.massivecraft.factions.cmd.audit.FLogManager;
import com.massivecraft.factions.cmd.audit.FLogType;
import com.massivecraft.factions.cmd.check.CheckTask;
import com.massivecraft.factions.cmd.check.WeeWooTask;
import com.massivecraft.factions.cmd.chest.AntiChestListener;
import com.massivecraft.factions.cmd.reserve.ListParameterizedType;
import com.massivecraft.factions.cmd.reserve.ReserveAdapter;
import com.massivecraft.factions.cmd.reserve.ReserveObject;
import com.massivecraft.factions.discord.Discord;
import com.massivecraft.factions.discord.DiscordListener;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.integration.dynmap.EngineDynmap;
import com.massivecraft.factions.listeners.*;
import com.massivecraft.factions.missions.MissionHandler;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.util.adapters.*;
import com.massivecraft.factions.util.particle.BukkitParticleProvider;
import com.massivecraft.factions.util.particle.PacketParticleProvider;
import com.massivecraft.factions.util.particle.ParticleProvider;
import com.massivecraft.factions.util.particle.darkblade12.ReflectionUtils;
import com.massivecraft.factions.util.timer.TimerManager;
import com.massivecraft.factions.util.wait.WaitExecutor;
import com.massivecraft.factions.zcore.CommandVisibility;
import com.massivecraft.factions.zcore.MPlugin;
import com.massivecraft.factions.zcore.file.impl.FileManager;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.frame.fupgrades.UpgradesListener;
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

import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;


public class FactionsPlugin extends MPlugin {

    // Our single plugin instance.
    // Single 4 life.
    public static FactionsPlugin instance;
    public static boolean cachedRadiusClaim;
    public static Permission perms = null;
    // This plugin sets the boolean true when fully enabled.
    // Plugins can check this boolean while hooking in have
    // a green light to use the api.
    public static boolean startupFinished = false;
    public boolean PlaceholderApi;
    // Commands
    public FCmdRoot cmdBase;
    public CmdAutoHelp cmdAutoHelp;
    public boolean mc17 = false;
    public boolean mc18 = false;
    public boolean mc112 = false;
    public boolean mc113 = false;
    public boolean mc114 = false;
    public boolean mc115 = false;
    public boolean mc116 = false;

    public boolean useNonPacketParticles = false;
    public List<String> itemList = getConfig().getStringList("fchest.Items-Not-Allowed");
    SkriptAddon skriptAddon;
    private FactionsPlayerListener factionsPlayerListener;
    private boolean locked = false;
    private boolean spam = false;
    private Integer AutoLeaveTask = null;
    private boolean hookedPlayervaults;
    private ClipPlaceholderAPIManager clipPlaceholderAPIManager;
    private SeeChunkUtil seeChunkUtil;
    private ParticleProvider particleProvider;
    private boolean mvdwPlaceholderAPIManager = false;
    private Listener[] eventsListener;
    private Worldguard wg;
    private FLogManager fLogManager;
    private List<ReserveObject> reserveObjects;
    private FileManager fileManager;
    private TimerManager timerManager;

    public FactionsPlugin() {
        instance = this;
    }

    public static FactionsPlugin getInstance() {
        return instance;
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

    public boolean getSpam() {
        return this.spam;
    }

    public void setSpam(boolean val) {
        this.spam = val;
        this.setAutoSave(val);
    }

    @Override
    public void onEnable() {
        log("==== Setup ====");

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            divider();
            System.out.println("You are missing dependencies!");
            System.out.println("Please verify [Vault] is installed!");
            Conf.save();
            Bukkit.getPluginManager().disablePlugin(instance);
            divider();
            return;
        }

        int version = Integer.parseInt(ReflectionUtils.PackageType.getServerVersion().split("_")[1]);
        switch (version) {
            case 7:
                FactionsPlugin.instance.log("Minecraft Version 1.7 found, disabling banners, itemflags inside GUIs, corners, and Titles.");
                mc17 = true;
                break;
            case 8:
                FactionsPlugin.instance.log("Minecraft Version 1.8 found, Title Fadeouttime etc will not be configurable.");
                mc18 = true;
                break;
            case 12:
                mc112 = true;
                break;
            case 13:
                FactionsPlugin.instance.log("Minecraft Version 1.13 found, New Items will be used.");
                mc113 = true;
                break;
            case 14:
                FactionsPlugin.instance.log("Minecraft Version 1.14 found.");
                mc114 = true;
                break;
            case 15:
                FactionsPlugin.instance.log("Minecraft Version 1.15 found.");
                mc115 = true;
                break;
            case 16:
                FactionsPlugin.instance.log("Minecraft Version 1.16 found.");
                mc116 = true;
                break;
        }
        migrateFPlayerLeaders();
        log("==== End Setup ====");

        int pluginId = 7013;
        new Metrics(this, pluginId);

        if (!preEnable()) {
            this.loadSuccessful = false;
            return;
        }

        FlightEnhance.get().wipe();

        saveDefaultConfig();
        this.reloadConfig();
        //Start wait task executor
        WaitExecutor.startTask();
        // Load Conf from disk
        Conf.load();

        fileManager = new FileManager();
        fileManager.setupFiles();

        fLogManager = new FLogManager();

        com.massivecraft.factions.integration.Essentials.setup();
        hookedPlayervaults = setupPlayervaults();
        FPlayers.getInstance().load();
        Factions.getInstance().load();

        for (FPlayer fPlayer : FPlayers.getInstance().getAllFPlayers()) {
            Faction faction = Factions.getInstance().getFactionById(fPlayer.getFactionId());
            if (faction == null) {
                log("Invalid faction id on " + fPlayer.getName() + ":" + fPlayer.getFactionId());
                fPlayer.resetFactionData(false);
                continue;
            }
            if (fPlayer.isAlt()) faction.addAltPlayer(fPlayer);
            else faction.addFPlayer(fPlayer);
        }

        Factions.getInstance().getAllFactions().forEach(Faction::refreshFPlayers);

        if (getConfig().getBoolean("enable-faction-flight", true)) {
            FlightEnhance.get().start();
        }


        Board.getInstance().load();
        Board.getInstance().clean();
        //Load command aliases
        Aliases.load();
        // Add Base Commands
        this.cmdBase = new FCmdRoot();
        this.cmdAutoHelp = new CmdAutoHelp();

        Econ.setup();
        setupPermissions();

        if (Conf.worldGuardChecking || Conf.worldGuardBuildPriority) wg = new Worldguard();

        EngineDynmap.getInstance().init();

        // Run before initializing listeners to handle reloads properly.
        if (mc113 || mc112 || mc18 || mc17) { // Before 1.13
            particleProvider = new PacketParticleProvider();
        } else {
            particleProvider = new BukkitParticleProvider();
        }
        getLogger().info(txt.parse("Using %1s as a particle provider", particleProvider.name()));

        if (getConfig().getBoolean("see-chunk.particles")) {
            double delay = Math.floor(getConfig().getDouble("see-chunk.interval") * 20);
            seeChunkUtil = new SeeChunkUtil();
            seeChunkUtil.runTaskTimer(this, 0, (long) delay);
        }


        // start up task which runs the autoLeaveAfterDaysOfInactivity routine
        startAutoLeaveTask(false);

        cachedRadiusClaim = Conf.useRadiusClaimSystem;

        if (version > 8) {
            useNonPacketParticles = true;
            log("Minecraft Version 1.9 or higher found, using non packet based particle API");
        }

        if (getServer().getPluginManager().getPlugin("Skript") != null) {
            log("Skript was found! Registering SaberFactions Addon...");
            skriptAddon = Skript.registerAddon(this);
            try {
                skriptAddon.loadClasses("com.massivecraft.factions.skript", "expressions");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            log("Skript addon registered!");
        }

        if (Conf.useCheckSystem) {
            int minute = 1200;
            this.getServer().getScheduler().runTaskTimerAsynchronously(this, new CheckTask(this, 3), 0L, minute * 3);
            this.getServer().getScheduler().runTaskTimerAsynchronously(this, new CheckTask(this, 5), 0L, minute * 5);
            this.getServer().getScheduler().runTaskTimerAsynchronously(this, new CheckTask(this, 10), 0L, minute * 10);
            this.getServer().getScheduler().runTaskTimerAsynchronously(this, new CheckTask(this, 15), 0L, minute * 15);
            this.getServer().getScheduler().runTaskTimerAsynchronously(this, new CheckTask(this, 30), 0L, minute * 30);
            this.getServer().getScheduler().runTaskTimer(this, CheckTask::cleanupTask, 0L, 1200L);
            this.getServer().getScheduler().runTaskTimerAsynchronously(this, new WeeWooTask(this), 600L, 600L);
        }
        //Setup Discord Bot
        new Discord(this);

        fLogManager.loadLogs(this);

        this.timerManager = new TimerManager(this);
        this.timerManager.reloadTimerData();
        System.out.println("[SABER-FACTIONS] - Loaded " + timerManager.getTimers().size() + " timers into list!");

        getServer().getPluginManager().registerEvents(factionsPlayerListener = new FactionsPlayerListener(), this);

        // Register Event Handlers
        eventsListener = new Listener[]{
                new FactionsChatListener(),
                new FactionsEntityListener(),
                new FactionsExploitListener(),
                new FactionsBlockListener(),
                new UpgradesListener(),
                new MissionHandler(this),
                new FChestListener(),
                new MenuListener(),
                new AntiChestListener()
        };

        for (Listener eventListener : eventsListener)
            getServer().getPluginManager().registerEvents(eventListener, this);

        if (Conf.useGraceSystem) {
            getServer().getPluginManager().registerEvents(timerManager.graceTimer, this);
        }

        this.getCommand(refCommand).setExecutor(cmdBase);

        if (!CommodoreProvider.isSupported()) this.getCommand(refCommand).setTabCompleter(this);

        reserveObjects = new ArrayList<>();
        String path = Paths.get(this.getDataFolder().getAbsolutePath()).toAbsolutePath().toString() + File.separator + "reserves.json";
        File file = new File(path);
        try {
            String json;
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            json = String.join("", Files.readAllLines(Paths.get(file.getPath()))).replace("\n", "").replace("\r", "");
            if (json.equalsIgnoreCase("")) {
                Files.write(Paths.get(path), "[]".getBytes());
                json = "[]";
            }
            reserveObjects = this.getGsonBuilder().create().fromJson(json, new ListParameterizedType(ReserveObject.class));
            if (reserveObjects == null) reserveObjects = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getDescription().getFullName().contains("BETA")) {
            divider();
            System.out.println("You are using a BETA version of the plugin!");
            System.out.println("This comes with risks of small bugs in newer features!");
            System.out.println("For support head to: https://github.com/Driftay/Saber-Factions/issues");
            divider();
        }

        this.setupPlaceholderAPI();
        this.postEnable();
        this.loadSuccessful = true;
        // Set startup finished to true. to give plugins hooking in a greenlight
        FactionsPlugin.startupFinished = true;
    }


    private void setupPlaceholderAPI() {
        Plugin clip = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if (clip != null && clip.isEnabled()) {
            this.clipPlaceholderAPIManager = new ClipPlaceholderAPIManager();
            if (this.clipPlaceholderAPIManager.register()) {
                PlaceholderApi = true;
                log(Level.INFO, "Successfully registered placeholders with PlaceholderAPI.");
            } else {
                PlaceholderApi = false;
            }
        } else {
            PlaceholderApi = false;
        }

        Plugin mvdw = getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI");
        if (mvdw != null && mvdw.isEnabled()) {
            this.mvdwPlaceholderAPIManager = true;
            log(Level.INFO, "Found MVdWPlaceholderAPI. Adding hooks.");
        }
    }

    public List<String> replacePlaceholders(List<String> lore, Placeholder... placeholders) {
        for (Placeholder placeholder : placeholders) {
            for (int x = 0; x <= lore.size() - 1; x++)
                lore.set(x, lore.get(x).replace(placeholder.getTag(), placeholder.getReplace()));
        }
        return lore;
    }

    private void migrateFPlayerLeaders() {
        List<String> lines = new ArrayList<>();
        File fplayerFile = new File("plugins" + File.pathSeparator + "Factions" + File.pathSeparator + "players.json");

        try {
            BufferedReader br = new BufferedReader(new FileReader(fplayerFile));
            System.out.println("Migrating old players.json file.");
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("\"role\": \"ADMIN\"")) {
                    line = line.replace("\"role\": \"ADMIN\"", "\"role\": " + "\"LEADER\"");
                }
                lines.add(line);
            }
            br.close();
            BufferedWriter bw = new BufferedWriter(new FileWriter(fplayerFile));
            for (String newLine : lines) {
                bw.write(newLine + "\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            System.out.println("File was not found for players.json, assuming"
                    + " there is no need to migrate old players.json file.");
        }
    }

    public boolean isClipPlaceholderAPIHooked() {
        return this.clipPlaceholderAPIManager != null;
    }

    public boolean isMVdWPlaceholderAPIHooked() {
        return this.mvdwPlaceholderAPIManager;
    }

    private boolean setupPermissions() {
        try {
            RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
            if (rsp != null) perms = rsp.getProvider();
        } catch (NoClassDefFoundError ex) {
            return false;
        }
        return perms != null;
    }

    private boolean setupPlayervaults() {
        Plugin plugin = getServer().getPluginManager().getPlugin("PlayerVaults");
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public GsonBuilder getGsonBuilder() {
        Type mapFLocToStringSetType = new TypeToken<Map<FLocation, Set<String>>>() {
        }.getType();

        Type accessTypeAdatper = new TypeToken<Map<Permissable, Map<PermissableAction, Access>>>() {
        }.getType();

        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().enableComplexMapKeySerialization().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
                .registerTypeAdapter(accessTypeAdatper, new PermissionsMapTypeAdapter())
                .registerTypeAdapter(LazyLocation.class, new MyLocationTypeAdapter())
                .registerTypeAdapter(mapFLocToStringSetType, new MapFLocToStringSetTypeAdapter())
                .registerTypeAdapter(Inventory.class, new InventoryTypeAdapter())
                .registerTypeAdapter(ReserveObject.class, new ReserveAdapter())
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY);
    }

    public void divider() {
        System.out.println("  .-.-.   .-.-.   .-.-.   .-.-.   .-.-.   .-.-.   .-.-.   .-.-");
        System.out.println(" / / \\ \\ / / \\ \\ / / \\ \\ / / \\ \\ / / \\ \\ / / \\ \\ / / \\ \\ / / \\");
        System.out.println("`-'   `-`-'   `-`-'   `-`-'   `-`-'   `-`-'   `-`-'   `-`-'");
    }

    @Override
    public void onDisable() {
        if (this.AutoLeaveTask != null) {
            getServer().getScheduler().cancelTask(this.AutoLeaveTask);
            this.AutoLeaveTask = null;
        }

        Conf.saveSync();
        timerManager.saveTimerData();
        DiscordListener.saveGuilds();

        if (Discord.jda != null) Discord.jda.shutdownNow();

        fLogManager.saveLogs();

        try {
            String path = Paths.get(getDataFolder().getAbsolutePath()).toAbsolutePath().toString() + File.separator + "reserves.json";
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            Files.write(Paths.get(file.getPath()), getGsonBuilder().create().toJson(reserveObjects).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onDisable();
    }

    public void startAutoLeaveTask(boolean restartIfRunning) {
        if (AutoLeaveTask != null) {
            if (!restartIfRunning) return;
            this.getServer().getScheduler().cancelTask(AutoLeaveTask);
        }

        if (Conf.autoLeaveRoutineRunsEveryXMinutes > 0.0) {
            long ticks = (long) (20 * 60 * Conf.autoLeaveRoutineRunsEveryXMinutes);
            AutoLeaveTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoLeaveTask(), ticks, ticks);
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
            completions = completions.stream()
                    .filter(m -> m.toLowerCase().startsWith(lastArg))
                    .collect(Collectors.toList());
            return completions;
        } else {
            String lastArg = args[args.length - 1].toLowerCase();
            for (Role value : Role.values()) completions.add(value.nicename);
            for (Relation value : Relation.values()) completions.add(value.nicename);
            // The stream and foreach from the old implementation looped 2 times, by looping all players -> filtered -> looped filter and added -> filtered AGAIN at the end.
            // This loops them once and just adds, because we are filtering the arguments at the end anyways
            for (Player player : Bukkit.getServer().getOnlinePlayers()) completions.add(player.getName());
            for (Faction faction : Factions.getInstance().getAllFactions())
                completions.add(ChatColor.stripColor(faction.getTag()));
            completions = completions.stream().filter(m -> m.toLowerCase().startsWith(lastArg)).collect(Collectors.toList());
            return completions;
        }
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

    public String color(String line) {
        line = ChatColor.translateAlternateColorCodes('&', line);
        return line;
    }

    //colors a string list
    public List<String> colorList(List<String> lore) {
        for (int i = 0; i <= lore.size() - 1; i++) lore.set(i, color(lore.get(i)));
        return lore;
    }

    public List<ReserveObject> getFactionReserves() {
        return this.reserveObjects;
    }


    public String getPrimaryGroup(OfflinePlayer player) {
        return perms == null || !perms.hasGroupSupport() ? " " : perms.getPrimaryGroup(Bukkit.getWorlds().get(0).toString(), player);
    }

    public ParticleProvider getParticleProvider() {
        return particleProvider;
    }

    public SeeChunkUtil getSeeChunkUtil() {
        return seeChunkUtil;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public void debug(Level level, String s) {
        if (getConfig().getBoolean("debug", false)) getLogger().log(level, s);
    }

    public FactionsPlayerListener getFactionsPlayerListener() {
        return this.factionsPlayerListener;
    }

    public void debug(String s) {
        debug(Level.INFO, s);
    }

}
