package com.massivecraft.factions;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.cmd.CmdAutoHelp;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.Essentials;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.integration.dynmap.EngineDynmap;
import com.massivecraft.factions.listeners.*;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.util.Particles.ReflectionUtils;
import com.massivecraft.factions.zcore.MPlugin;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.fupgrades.CropUpgrades;
import com.massivecraft.factions.zcore.fupgrades.EXPUpgrade;
import com.massivecraft.factions.zcore.fupgrades.FUpgradesGUI;
import com.massivecraft.factions.zcore.fupgrades.SpawnerUpgrades;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;


public class P extends MPlugin {

    // Our single plugin instance.
    // Single 4 life.
    public static P p;
    public static Permission perms = null;


    public boolean PlaceholderApi;
    // Commands
    public FCmdRoot cmdBase;
    public CmdAutoHelp cmdAutoHelp;
    public boolean mc17 = false;
    public boolean mc18 = false;
    public boolean mc113 = false;
    public boolean useNonPacketParticles = false;
    public boolean factionsFlight = false;

    // Persistence related
    private boolean locked = false;
    private Integer AutoLeaveTask = null;
    private boolean hookedPlayervaults;
    private ClipPlaceholderAPIManager clipPlaceholderAPIManager;
    private boolean mvdwPlaceholderAPIManager = false;




    public P() {
        p = this;
    }

    public boolean getLocked() {
        return this.locked;
    }

    public void setLocked(boolean val) {
        this.locked = val;
        this.setAutoSave(val);
    }

    public void playSoundForAll(String sound) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            playSound(pl, sound);
        }
    }

    public void playSoundForAll(List<String> sounds) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            playSound(pl, sounds);
        }
    }

    public void playSound(Player p, List<String> sounds) {
        for (String sound : sounds) {
            playSound(p, sound);
        }
    }

    public void playSound(Player p, String sound) {
        float pitch = Float.valueOf(sound.split(":")[1]);
        sound = sound.split(":")[0];
        p.playSound(p.getLocation(), Sound.valueOf(sound), pitch, 5.0F);
    }

    @Override
    public void onEnable() {
        if (!preEnable()) {
            return;
        }
        this.loadSuccessful = false;
        saveDefaultConfig();

        // Load Conf from disk
        Conf.load();
        Essentials.setup();
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
            faction.addFPlayer(fPlayer);
        }
        Board.getInstance().load();
        Board.getInstance().clean();

        //inspect stuff


        // Add Base Commands
        this.cmdBase = new FCmdRoot();
        this.cmdAutoHelp = new CmdAutoHelp();
        this.getBaseCommands().add(cmdBase);

        Econ.setup();
        setupPermissions();

        if (Conf.worldGuardChecking || Conf.worldGuardBuildPriority) {
            Worldguard.init(this);
        }

        EngineDynmap.getInstance().init();

        // start up task which runs the autoLeaveAfterDaysOfInactivity routine
        startAutoLeaveTask(false);

        //massive stats
        MassiveStats massive = new MassiveStats(this);


        int version = Integer.parseInt(ReflectionUtils.PackageType.getServerVersion().split("_")[1]);
        if (version == 7) {
            P.p.log("Minecraft Version 1.7 found, disabling banners, itemflags inside GUIs, and Titles.");
            mc17 = true;
        } else if (version == 8) {
            P.p.log("Minecraft Version 1.8 found, Title Fadeouttime etc will not be configurable.");
            mc18 = true;
        } else if (version == 13) {
            P.p.log("Minecraft Version 1.13 found, New Items will be used.");
            mc113 = true;
            changeItemIDSInConfig();
        }

        if (version > 8) {
            useNonPacketParticles = true;
            P.p.log("Minecraft Version 1.9 or higher found, using non packet based particle API");
        }

        if (P.p.getConfig().getBoolean("enable-faction-flight")) {
            factionsFlight = true;
        }

        setupMultiversionMaterials();

        // Register Event Handlers
        getServer().getPluginManager().registerEvents(new FactionsPlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new FactionsChatListener(this), this);
        getServer().getPluginManager().registerEvents(new FactionsEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new FactionsExploitListener(), this);
        getServer().getPluginManager().registerEvents(new FactionsBlockListener(this), this);
        getServer().getPluginManager().registerEvents(new FUpgradesGUI(), this);
        getServer().getPluginManager().registerEvents(new EXPUpgrade(), this);
        getServer().getPluginManager().registerEvents(new CropUpgrades(), this);
        getServer().getPluginManager().registerEvents(new SpawnerUpgrades(), this);
        // since some other plugins execute commands directly through this command interface, provide it
        this.getCommand(this.refCommand).setExecutor(this);

        if (P.p.getDescription().getFullName().contains("BETA")) {
            divider();
            System.out.println("You are using a BETA version of the plugin!");
            System.out.println("This comes with risks of small bugs in newer features!");
            System.out.println("For support head to: https://github.com/ProSavage/SavageFactions/issues");
            divider();
        }




        setupPlaceholderAPI();
        postEnable();
        this.loadSuccessful = true;
    }


    //multiversion material fields
    public Material SUGAR_CANE_BLOCK, BANNER, CROPS, REDSTONE_LAMP_ON,
            STAINED_GLASS, STATIONARY_WATER, STAINED_CLAY, WOOD_BUTTON,
            SOIL, MOB_SPANWER, THIN_GLASS, IRON_FENCE, NETHER_FENCE, FENCE,
            WOODEN_DOOR, TRAP_DOOR, FENCE_GATE, BURNING_FURNACE, DIODE_BLOCK_OFF,
            DIODE_BLOCK_ON, ENCHANTMENT_TABLE, FIREBALL;


    private void setupMultiversionMaterials() {
        if (mc113) {
            BANNER = Material.valueOf("LEGACY_BANNER");
            CROPS = Material.valueOf("LEGACY_CROPS");
            SUGAR_CANE_BLOCK = Material.valueOf("LEGACY_SUGAR_CANE_BLOCK");
            REDSTONE_LAMP_ON = Material.valueOf("LEGACY_REDSTONE_LAMP_ON");
            STAINED_GLASS = Material.valueOf("LEGACY_STAINED_GLASS");
            STATIONARY_WATER = Material.valueOf("LEGACY_STATIONARY_WATER");
            STAINED_CLAY = Material.valueOf("LEGACY_STAINED_CLAY");
            WOOD_BUTTON = Material.valueOf("LEGACY_WOOD_BUTTON");
            SOIL = Material.valueOf("LEGACY_SOIL");
            MOB_SPANWER = Material.valueOf("LEGACY_MOB_SPAWNER");
            THIN_GLASS = Material.valueOf("LEGACY_THIN_GLASS");
            IRON_FENCE = Material.valueOf("LEGACY_IRON_FENCE");
            NETHER_FENCE = Material.valueOf("LEGACY_NETHER_FENCE");
            FENCE = Material.valueOf("LEGACY_FENCE");
            WOODEN_DOOR = Material.valueOf("LEGACY_WOODEN_DOOR");
            TRAP_DOOR = Material.valueOf("LEGACY_TRAP_DOOR");
            FENCE_GATE = Material.valueOf("LEGACY_FENCE_GATE");
            BURNING_FURNACE = Material.valueOf("LEGACY_BURNING_FURNACE");
            DIODE_BLOCK_OFF = Material.valueOf("LEGACY_DIODE_BLOCK_OFF");
            DIODE_BLOCK_ON = Material.valueOf("LEGACY_DIODE_BLOCK_ON");
            ENCHANTMENT_TABLE = Material.valueOf("LEGACY_ENCHANTMENT_TABLE");
            FIREBALL = Material.valueOf("LEGACY_FIREBALL");

        } else {
            BANNER = Material.valueOf("BANNER");
            CROPS = Material.valueOf("CROPS");
            SUGAR_CANE_BLOCK = Material.valueOf("SUGAR_CANE_BLOCK");
            REDSTONE_LAMP_ON = Material.valueOf("REDSTONE_LAMP_ON");
            STAINED_GLASS = Material.valueOf("STAINED_GLASS");
            STATIONARY_WATER = Material.valueOf("STATIONARY_WATER");
            STAINED_CLAY = Material.valueOf("STAINED_CLAY");
            WOOD_BUTTON = Material.valueOf("WOOD_BUTTON");
            SOIL = Material.valueOf("SOIL");
            MOB_SPANWER = Material.valueOf("MOB_SPAWNER");
            THIN_GLASS = Material.valueOf("THIN_GLASS");
            IRON_FENCE = Material.valueOf("IRON_FENCE");
            NETHER_FENCE = Material.valueOf("NETHER_FENCE");
            FENCE = Material.valueOf("FENCE");
            WOODEN_DOOR = Material.valueOf("WOODEN_DOOR");
            TRAP_DOOR = Material.valueOf("TRAP_DOOR");
            FENCE_GATE = Material.valueOf("FENCE_GATE");
            BURNING_FURNACE = Material.valueOf("BURNING_FURNACE");
            DIODE_BLOCK_OFF = Material.valueOf("DIODE_BLOCK_OFF");
            DIODE_BLOCK_ON = Material.valueOf("DIODE_BLOCK_ON");
            ENCHANTMENT_TABLE = Material.valueOf("ENCHANTMENT_TABLE");
            FIREBALL = Material.valueOf("FIREBALL");
        }


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


    public void changeItemIDSInConfig() {


        P.p.log("Starting conversion of legacy material in config to 1.13 materials.");


        replaceStringInConfig("fperm-gui.relation.materials.recruit", "WOOD_SWORD", "WOODEN_SWORD");

        replaceStringInConfig("fperm-gui.relation.materials.normal", "GOLD_SWORD", "GOLDEN_SWORD");

        replaceStringInConfig("fperm-gui.relation.materials.ally", "GOLD_AXE", "GOLDEN_AXE");

        replaceStringInConfig("fperm-gui.relation.materials.neutral", "WOOD_AXE", "WOODEN_AXE");

        ConfigurationSection actionMaterialsConfigSection = getConfig().getConfigurationSection("fperm-gui.action.materials");

        Set<String> actionMaterialKeys = actionMaterialsConfigSection.getKeys(true);


        for (String key : actionMaterialKeys) {
            replaceStringInConfig("fperm-gui.action.materials." + key, "STAINED_GLASS", "GRAY_STAINED_GLASS");
        }

        replaceStringInConfig("fperm-gui.dummy-items.0.material", "STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE");

        replaceStringInConfig("fwarp-gui.dummy-items.0.material", "STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE");

        replaceStringInConfig("fupgrades.MainMenu.DummyItem.Type", "STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE");

        replaceStringInConfig("fupgrades.MainMenu.EXP.EXPItem.Type", "EXP_BOTTLE", "EXPERIENCE_BOTTLE");

        replaceStringInConfig("fupgrades.MainMenu.Spawners.SpawnerItem.Type", "MOB_SPAWNER", "SPAWNER");

        replaceStringInConfig("fperm-gui.action.access.allow", "LIME", "LIME_STAINED_GLASS");

        replaceStringInConfig("fperm-gui.action.access.deny", "RED", "RED_STAINED_GLASS");

        replaceStringInConfig("fperm-gui.action.access.undefined", "CYAN", "CYAN_STAINED_GLASS");


    }

    public void replaceStringInConfig(String path, String stringToReplace, String replacementString) {
        if (getConfig().getString(path).equals(stringToReplace)) {
            P.p.log("Replacing legacy material '" + stringToReplace + "' with '" + replacementString + "' for config node '" + path + "'.");
            getConfig().set(path, replacementString);
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
            if (rsp != null) {
                perms = rsp.getProvider();
            }
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
                .registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY);
    }

    private void divider() {
        System.out.println("  .-.-.   .-.-.   .-.-.   .-.-.   .-.-.   .-.-.   .-.-.   .-.-");
        System.out.println(" / / \\ \\ / / \\ \\ / / \\ \\ / / \\ \\ / / \\ \\ / / \\ \\ / / \\ \\ / / \\");
        System.out.println("`-'   `-`-'   `-`-'   `-`-'   `-`-'   `-`-'   `-`-'   `-`-'");
    }

    @Override
    public void onDisable() {
        // only save data if plugin actually completely loaded successfully
        if (this.loadSuccessful) {
            Conf.save();
        }

        if (AutoLeaveTask != null) {
            this.getServer().getScheduler().cancelTask(AutoLeaveTask);
            AutoLeaveTask = null;
        }

        super.onDisable();
    }

    public void startAutoLeaveTask(boolean restartIfRunning) {
        if (AutoLeaveTask != null) {
            if (!restartIfRunning) {
                return;
            }
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
        ItemStack item = new ItemStack(material, amount, datavalue);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(name));
        meta.setLore(colorList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createLazyItem(Material material, int amount, short datavalue, String name, String lore) {
        ItemStack item = new ItemStack(material, amount, datavalue);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(P.p.getConfig().getString(name)));
        meta.setLore(colorList(P.p.getConfig().getStringList(lore)));
        item.setItemMeta(meta);
        return item;
    }

    public Economy getEcon() {
        RegisteredServiceProvider<Economy> rsp = P.p.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = rsp.getProvider();
        return econ;
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
        if (split.length == 0) {
            return handleCommand(sender, "/f help", false);
        }

        // otherwise, needs to be handled; presumably another plugin directly ran the command
        String cmd = Conf.baseCommandAliases.isEmpty() ? "/f" : "/" + Conf.baseCommandAliases.get(0);
        return handleCommand(sender, cmd + " " + TextUtil.implode(Arrays.asList(split), " "), false);
    }

    public void createTimedHologram(final Location location, String text, Long timeout) {
        ArmorStand as = (ArmorStand) location.add(0.5, 1, 0.5).getWorld().spawnEntity(location, EntityType.ARMOR_STAND); //Spawn the ArmorStand
        as.setVisible(false); //Makes the ArmorStand invisible
        as.setGravity(false); //Make sure it doesn't fall
        as.setCanPickupItems(false); //I'm not sure what happens if you leave this as it is, but you might as well disable it
        as.setCustomName(P.p.color(text)); //Set this to the text you want
        as.setCustomNameVisible(true); //This makes the text appear no matter if your looking at the entity or not
        final ArmorStand armorStand = as;
        Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, new Runnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage("removing stand");
                armorStand.remove();
            }
        }, timeout * 20);

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
        if (player == null) {
            return false;
        }
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

        if (speaker == null) {
            return tag;
        }

        FPlayer me = FPlayers.getInstance().getByPlayer(speaker);
        if (me == null) {
            return tag;
        }

        // if listener isn't set, or config option is disabled, give back uncolored tag
        if (listener == null || !Conf.chatTagRelationColored) {
            tag = me.getChatTag().trim();
        } else {
            FPlayer you = FPlayers.getInstance().getByPlayer(listener);
            if (you == null) {
                tag = me.getChatTag().trim();
            } else  // everything checks out, give the colored tag
            {
                tag = me.getChatTag(you).trim();
            }
        }
        if (tag.isEmpty()) {
            tag = "~";
        }

        return tag;
    }

    // Get a player's title within their faction, mainly for usage by chat plugins for local/channel chat
    public String getPlayerTitle(Player player) {
        if (player == null) {
            return "";
        }

        FPlayer me = FPlayers.getInstance().getByPlayer(player);
        if (me == null) {
            return "";
        }

        return me.getTitle().trim();
    }

    public String color(String line) {
        line = ChatColor.translateAlternateColorCodes('&', line);
        return line;
    }

    //colors a string list
    public List<String> colorList(List<String> lore) {
        for (int i = 0; i <= lore.size() - 1; i++) {
            lore.set(i, color(lore.get(i)));
        }
        return lore;
    }

    // Get a list of all faction tags (names)
    public Set<String> getFactionTags() {
        return Factions.getInstance().getFactionTags();
    }

    // Get a list of all players in the specified faction
    public Set<String> getPlayersInFaction(String factionTag) {
        Set<String> players = new HashSet<>();
        Faction faction = Factions.getInstance().getByTag(factionTag);
        if (faction != null) {
            for (FPlayer fplayer : faction.getFPlayers()) {
                players.add(fplayer.getName());
            }
        }
        return players;
    }

    // Get a list of all online players in the specified faction
    public Set<String> getOnlinePlayersInFaction(String factionTag) {
        Set<String> players = new HashSet<>();
        Faction faction = Factions.getInstance().getByTag(factionTag);
        if (faction != null) {
            for (FPlayer fplayer : faction.getFPlayersWhereOnline(true)) {
                players.add(fplayer.getName());
            }
        }
        return players;
    }

    public boolean isHookedPlayervaults() {
        return hookedPlayervaults;
    }

    public String getPrimaryGroup(OfflinePlayer player) {
        return perms == null || !perms.hasGroupSupport() ? " " : perms.getPrimaryGroup(Bukkit.getWorlds().get(0).toString(), player);
    }

    public void debug(Level level, String s) {
        if (getConfig().getBoolean("debug", false)) {
            getLogger().log(level, s);
        }
    }

    public void debug(String s) {
        debug(Level.INFO, s);
    }
}
