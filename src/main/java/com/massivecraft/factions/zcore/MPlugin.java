package com.massivecraft.factions.zcore;

import com.google.gson.Gson;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.persist.MemoryFPlayers;
import com.massivecraft.factions.zcore.persist.SaveTask;
import com.massivecraft.factions.zcore.util.PermUtil;
import com.massivecraft.factions.zcore.util.Persist;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;


public abstract class MPlugin extends JavaPlugin {

    // Some utils
    public Persist persist;
    public PermUtil perm;

    public String refCommand = "";
    //holds f stuck taskids
    public Map<UUID, Integer> stuckMap = new HashMap<>();
    // These are not supposed to be used directly.
    // They are loaded and used through the TextUtil instance for the plugin.
    public Map<String, String> rawTags = new LinkedHashMap<>();
    protected boolean loadSuccessful = false;
    private Integer saveTask = null;
    private boolean autoSave = true;

    // Our stored base commands
    private final Map<String, MCommand<?>> baseCommands = new HashMap<>();

    private static final Pattern ARGUMENT_DELIMITER = Pattern.compile("\\s+");
    // holds f stuck start times
    private final Map<UUID, Long> timers = new HashMap<>();

    // -------------------------------------------- //
    // ENABLE
    // -------------------------------------------- //
    private long timeEnableStart;

    public boolean getAutoSave() {
        return this.autoSave;
    }

    public void setAutoSave(boolean val) {
        this.autoSave = val;
    }

    public List<MCommand<?>> getBaseCommands() {
        return new ArrayList<>(this.baseCommands.values());
    }

    public boolean preEnable() {
        Logger.print("=== ENABLE START ===", Logger.PrefixType.DEFAULT);
        timeEnableStart = System.nanoTime();

        // Ensure basefolder exists!
        this.saveDefaultConfig();

        // Create Utility Instances
        this.perm = new PermUtil(this);
        this.persist = new Persist(this);

        TextUtil.init();

        // attempt to get first command defined in plugin.yml as reference command, if any commands are defined in there
        // reference command will be used to prevent "unknown command" console messages
        try {
            Map<String, Map<String, Object>> refCmd = this.getDescription().getCommands();
            if (refCmd != null && !refCmd.isEmpty()) {
                this.refCommand = (String) (refCmd.keySet().toArray()[0]);
            }
        } catch (ClassCastException ignored) {
        }

        // Create and register player command listener
        // Listeners
        Bukkit.getPluginManager().registerEvents(new MPluginSecretPlayerListener(this), this);

        // Register recurring tasks
        if (this.saveTask == null && Conf.saveToFileEveryXMinutes > 0.0) {
            long saveTicks = (long) (1200.0 * Conf.saveToFileEveryXMinutes);
            this.saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new SaveTask(this), saveTicks, saveTicks).getTaskId();
        }
        loadLang();
        loadSuccessful = true;
        return true;
    }

    public void postEnable() {
        Logger.print("=== ENABLE DONE (Took " + DecimalFormat.getInstance().format((System.nanoTime() - timeEnableStart) / 1_000_000.0D) + "ms) ===", Logger.PrefixType.DEFAULT);
    }

    public void loadLang() {
        Path langPath = Paths.get(getDataFolder().getPath(), "lang.yml");

        InputStream defaultLangStream = this.getResource("lang.yml");
        if (defaultLangStream == null) {
            getLogger().severe("[Factions] Couldn't load default language file from resources.");
            getLogger().severe("[Factions] This is a fatal error. Now disabling");
            this.setEnabled(false);
            return;
        }

        YamlConfiguration defaultLangConfig;
        try (InputStreamReader isr = new InputStreamReader(defaultLangStream)) {
            defaultLangConfig = YamlConfiguration.loadConfiguration(isr);
        } catch (IOException exception) {
            getLogger().log(Level.WARNING, "Factions: Failed to load default lang.yml.");
            getLogger().log(Level.WARNING, "Factions: Report this stack trace to Driftay.");
            exception.printStackTrace();
            return;
        }

        YamlConfiguration langConfig = new YamlConfiguration();
        if (Files.exists(langPath)) {
            try {
                langConfig.load(langPath.toFile());
            } catch (IOException | InvalidConfigurationException exception) {
                getLogger().log(Level.WARNING, "Factions: Failed to load lang.yml.");
                getLogger().log(Level.WARNING, "Factions: Report this stack trace to Driftay.");
                exception.printStackTrace();
            }
        } else {
            try {
                Files.createDirectories(langPath.getParent());
                Files.createFile(langPath);
                langConfig.save(langPath.toFile());
            } catch (IOException exception) {
                getLogger().log(Level.WARNING, "Factions: Failed to create lang.yml.");
                getLogger().log(Level.WARNING, "Factions: Report this stack trace to Driftay.");
                exception.printStackTrace();
            }
        }

        for (TL item : TL.VALUES) {
            String path = item.getPath();
            if (langConfig.get(path) == null) {
                langConfig.set(path, defaultLangConfig.getString(path, item.getDefault()));
            }
        }

        if (langConfig.getString(TL.COMMAND_SHOW_POWER.getPath(), "").contains("%5$s")) {
            langConfig.set(TL.COMMAND_SHOW_POWER.getPath(), TL.COMMAND_SHOW_POWER.getDefault());
            getLogger().log(Level.INFO, "Removed errant format specifier from f show power.");
        }

        TL.setFile(langConfig);
        try {
            langConfig.save(langPath.toFile());
        } catch (IOException exception) {
            getLogger().log(Level.WARNING, "Factions: Failed to save lang.yml.");
            getLogger().log(Level.WARNING, "Factions: Report this stack trace to Driftay.");
            exception.printStackTrace();
        }
    }

    public void onDisable() {
        if (saveTask != null) {
            this.getServer().getScheduler().cancelTask(saveTask);
            saveTask = null;
        }
        // only save data if plugin actually loaded successfully
        if (loadSuccessful) {
            Factions.getInstance().forceSave();
            FPlayers.getInstance().forceSave();
            Board.getInstance().forceSave();
        }
        ((MemoryFPlayers) FPlayers.getInstance()).wipeOnlinePlayers();

        Logger.print("Shutdown Successful!", Logger.PrefixType.DEFAULT);
    }

    // -------------------------------------------- //
    // Some inits...
    // You are supposed to override these in the plugin if you aren't satisfied with the defaults
    // The goal is that you always will be satisfied though.
    // -------------------------------------------- //

    public void suicide() {
        Logger.print("Plugin Suicide Initiating!", Logger.PrefixType.DEFAULT);
        this.getServer().getPluginManager().disablePlugin(this);
    }

    // -------------------------------------------- //
    // LANG AND TAGS
    // -------------------------------------------- //

    public abstract Gson getGson();

    public void addRawTags() {
        this.rawTags.put("l", "<green>"); // logo
        this.rawTags.put("a", "<gold>"); // art
        this.rawTags.put("n", "<silver>"); // notice
        this.rawTags.put("i", "<yellow>"); // info
        this.rawTags.put("g", "<lime>"); // good
        this.rawTags.put("b", "<rose>"); // bad
        this.rawTags.put("h", "<pink>"); // highligh
        this.rawTags.put("c", "<aqua>"); // command
        this.rawTags.put("plugin", "<teal>"); // parameter
    }

    // -------------------------------------------- //
    // COMMAND HANDLING
    // -------------------------------------------- //

    // can be overridden by FactionsPlugin method, to provide option
    public boolean logPlayerCommands() {
        return true;
    }

    public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly) {
        return handleCommand(sender, commandString, testOnly, false);
    }

    public boolean handleCommand(final CommandSender sender, String commandString, boolean testOnly, boolean async) {
        commandString = ARGUMENT_DELIMITER.matcher((commandString.startsWith("/") ? commandString.substring(1) : commandString)).replaceAll(" ");

        String[] arguments = ARGUMENT_DELIMITER.split(commandString);
        MCommand<?> command = this.baseCommands.get(arguments[0]);
        if (command == null) {
            return false;
        }
        if (testOnly) {
            return true;
        }

        List<String> args = Arrays.asList(arguments).subList(1, arguments.length);
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> command.execute(sender, args));
        } else {
            command.execute(sender, args);
        }
        return true;
    }

    public boolean handleCommand(CommandSender sender, String commandString) {
        return this.handleCommand(sender, commandString, false);
    }

    // -------------------------------------------- //
    // HOOKS
    // -------------------------------------------- //
    public void preAutoSave() {

    }

    public void postAutoSave() {

    }

    public Map<UUID, Integer> getStuckMap() {
        return this.stuckMap;
    }

    public Map<UUID, Long> getTimers() {
        return this.timers;
    }

}
