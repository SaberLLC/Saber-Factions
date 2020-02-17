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
import com.massivecraft.factions.cmd.reserve.ReserveObject;
import com.massivecraft.factions.discord.Discord;
import com.massivecraft.factions.discord.DiscordListener;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.integration.dynmap.EngineDynmap;
import com.massivecraft.factions.listeners.*;
import com.massivecraft.factions.missions.MissionHandler;
import com.massivecraft.factions.shop.ShopConfig;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.util.Particles.ReflectionUtils;
import com.massivecraft.factions.zcore.CommandVisibility;
import com.massivecraft.factions.zcore.MPlugin;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.fupgrades.UpgradesListener;
import com.massivecraft.factions.zcore.util.TextUtil;
import me.lucko.commodore.CommodoreProvider;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.stream.Collectors;


public class FactionsPlugin extends MPlugin {

    // Our single plugin instance.
    // Single 4 life.
    public static FactionsPlugin instance;
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
    public boolean useNonPacketParticles = false;
    public boolean factionsFlight = false;
    SkriptAddon skriptAddon;
    private FactionsPlayerListener factionsPlayerListener;
    private boolean locked = false;
    private boolean spam = false;
    private Integer AutoLeaveTask = null;
    private boolean hookedPlayervaults;
    private ClipPlaceholderAPIManager clipPlaceholderAPIManager;
    private boolean mvdwPlaceholderAPIManager = false;
    private Listener[] eventsListener;
    public List<String> itemList = getConfig().getStringList("fchest.Items-Not-Allowed");
    private Worldguard wg;
    private FLogManager fLogManager;
    private List<ReserveObject> reserveObjects;


    public FactionsPlugin() {
        instance = this;
    }

    public static FactionsPlugin getInstance() {
        return instance;
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

    public void playSoundForAll(String sound) {
        for (Player pl : Bukkit.getOnlinePlayers()) playSound(pl, sound);
    }

    public void playSoundForAll(List<String> sounds) {
        for (Player pl : Bukkit.getOnlinePlayers()) playSound(pl, sounds);
    }

    public void playSound(Player p, List<String> sounds) {
        for (String sound : sounds) playSound(p, sound);
    }

    public void playSound(Player p, String sound) {
        float pitch = Float.parseFloat(sound.split(":")[1]);
        sound = sound.split(":")[0];
        p.playSound(p.getLocation(), Sound.valueOf(sound), pitch, 5.0F);
    }

    @Override
    public void onEnable() {
        log("==== Setup ====");

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
        }
        migrateFPlayerLeaders();
        log("==== End Setup ====");

        if (!preEnable()) return;
        this.loadSuccessful = false;

        if (!new File(this.getDataFolder() + "/config.yml").exists()) {
            this.saveResource("config.yml", false);
            this.reloadConfig();
        }
        //Attempt to generate a permission list
        PermissionList.generateFile();
        // Load Conf from disk
        Conf.load();
        fLogManager = new FLogManager();
        //Dependency checks
        if (Conf.dependencyCheck && (!Bukkit.getPluginManager().isPluginEnabled("Vault"))) {
            divider();
            System.out.println("You are missing dependencies!");
            System.out.println("Please verify and Vault are installed!");
            Conf.save();
            Bukkit.getPluginManager().disablePlugin(instance);
            divider();
            return;
        }
        //Update their config if needed
        // Updater.updateIfNeeded(getConfig());
        RegisteredServiceProvider<Economy> rsp = FactionsPlugin.this.getServer().getServicesManager().getRegistration(Economy.class);
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

        // start up task which runs the autoLeaveAfterDaysOfInactivity routine
        startAutoLeaveTask(false);

        if (version > 8) {
            useNonPacketParticles = true;
            log("Minecraft Version 1.9 or higher found, using non packet based particle API");
        }

        if (getConfig().getBoolean("enable-faction-flight")) factionsFlight = true;

        if (getServer().getPluginManager().getPlugin("Skript") != null) {
            log("Skript was found! Registering FactionsPlugin Addon...");
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

        ShopConfig.setup();
        fLogManager.loadLogs(this);

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

    public SkriptAddon getSkriptAddon() {
        return skriptAddon;
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
        try {
            String path = Paths.get(getDataFolder().getAbsolutePath()).toAbsolutePath().toString() + File.separator + "reserves.json";
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            Files.write(Paths.get(file.getPath()),getGsonBuilder().create().toJson(reserveObjects).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // only save data if plugin actually completely loaded successfully
        if (this.loadSuccessful) Conf.saveSync();


        if (AutoLeaveTask != null) {
            this.getServer().getScheduler().cancelTask(AutoLeaveTask);
            AutoLeaveTask = null;
        }
        DiscordListener.saveGuilds();
        if (Discord.jda != null) {
            Discord.jda.shutdownNow();
        }
        super.onDisable();
        try {
            fLogManager.saveLogs();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public ItemStack createItem(Material material, int amount, short datavalue, String name, List<String> lore) {
        ItemStack item = new ItemStack(XMaterial.matchXMaterial(material.toString()).get().parseMaterial(), amount, datavalue);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(name));
        meta.setLore(colorList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createLazyItem(Material material, int amount, short datavalue, String name, String lore) {
        ItemStack item = new ItemStack(material, amount, datavalue);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(FactionsPlugin.instance.getConfig().getString(name)));
        meta.setLore(colorList(FactionsPlugin.instance.getConfig().getStringList(lore)));
        item.setItemMeta(meta);
        return item;
    }

    public Economy getEcon() {
        RegisteredServiceProvider<Economy> rsp = FactionsPlugin.instance.getServer().getServicesManager().getRegistration(Economy.class);
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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (split.length == 0) return handleCommand(sender, "/f help", false);

        // otherwise, needs to be handled; presumably another plugin directly ran the command
        String cmd = Conf.baseCommandAliases.isEmpty() ? "/f" : "/" + Conf.baseCommandAliases.get(0);
        return handleCommand(sender, cmd + " " + TextUtil.implode(Arrays.asList(split), " "), false);
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

    // This value will be updated whenever new hooks are added
    public int hookSupportVersion() {
        return 3;
    }

    // If another plugin is handling insertion of chat tags, this should be used to notify Factions
    public void handleFactionTagExternally(boolean notByFactions) {
        Conf.chatTagHandledByAnotherPlugin = notByFactions;
    }

    // Simply put, should this chat event be left for Factions to handle? For now, that means players with Faction Chat
    // enabled or use of the Factions f command without a slash; combination of isPlayerFactionChatting() and isFactionsCommand()

    public boolean shouldLetFactionsHandleThisChat(AsyncPlayerChatEvent event) {
        return event != null && (isPlayerFactionChatting(event.getPlayer()) || isFactionsCommand(event.getMessage()));
    }


    // Does player have Faction Chat enabled? If so, chat plugins should preferably not do channels,
    // local chat, or anything else which targets individual recipients, so Faction Chat can be done
    public boolean isPlayerFactionChatting(Player player) {
        if (player == null) return false;
        FPlayer me = FPlayers.getInstance().getByPlayer(player);
        return me != null && me.getChatMode().isAtLeast(ChatMode.ALLIANCE);
    }

    // Is this chat message actually a Factions command, and thus should be left alone by other plugins?

    // TODO: GET THIS BACK AND WORKING

    public boolean isFactionsCommand(String check) {
        return !(check == null || check.isEmpty()) && this.handleCommand(null, check, true);
    }

    // Get a player's faction tag (faction name), mainly for usage by chat plugins for local/channel chat
    public String getPlayerFactionTag(Player player) {
        return getPlayerFactionTagRelation(player, null);
    }

    // Same as above, but with relation (enemy/neutral/ally) coloring potentially added to the tag
    public String getPlayerFactionTagRelation(Player speaker, Player listener) {
        String tag = "~";

        if (speaker == null) return tag;


        FPlayer me = FPlayers.getInstance().getByPlayer(speaker);
        if (me == null) return tag;
        // if listener isn't set, or config option is disabled, give back uncolored tag
        if (listener == null || !Conf.chatTagRelationColored) {
            tag = me.getChatTag().trim();
        } else {
            FPlayer you = FPlayers.getInstance().getByPlayer(listener);
            if (you == null) {
                tag = me.getChatTag().trim();
            } else { // everything checks out, give the colored tag
                tag = me.getChatTag(you).trim();
            }
        }
        if (tag.isEmpty()) tag = "~";
        return tag;
    }


    public FLogManager getFlogManager() {
        return fLogManager;
    }

    public void logFactionEvent(Faction faction, FLogType type, String... arguments) {
        this.fLogManager.log(faction, type, arguments);
    }

    // Get a player's title within their faction, mainly for usage by chat plugins for local/channel chat
    public String getPlayerTitle(Player player) {
        if (player == null) return "";
        FPlayer me = FPlayers.getInstance().getByPlayer(player);
        if (me == null) return "";
        return me.getTitle().trim();
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

    // Get a list of all faction tags (names)
    public Set<String> getFactionTags() {
        return Factions.getInstance().getFactionTags();
    }

    public List<ReserveObject> getFactionReserves() {
        return this.reserveObjects;
    }

    // Get a list of all players in the specified faction
    public Set<String> getPlayersInFaction(String factionTag) {
        Set<String> players = new HashSet<>();
        Faction faction = Factions.getInstance().getByTag(factionTag);
        if (faction != null) {
            for (FPlayer fplayer : faction.getFPlayers()) players.add(fplayer.getName());
        }
        return players;
    }

    // Get a list of all online players in the specified faction
    public Set<String> getOnlinePlayersInFaction(String factionTag) {
        Set<String> players = new HashSet<>();
        Faction faction = Factions.getInstance().getByTag(factionTag);
        if (faction != null) {
            for (FPlayer fplayer : faction.getFPlayersWhereOnline(true)) players.add(fplayer.getName());
        }
        return players;
    }

    public boolean isHookedPlayervaults() {
        return hookedPlayervaults;
    }

    public String getPrimaryGroup(OfflinePlayer player) {
        AtomicReference<String> primaryGroup = new AtomicReference<>();

        if (perms == null || !perms.hasGroupSupport()) return " ";
        else {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> primaryGroup.set(perms.getPrimaryGroup(Bukkit.getWorlds().get(0).toString(), player)));
            return primaryGroup.get();
        }
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

    public Worldguard getWg() {
        return wg;
    }
}
