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
import io.papermc.paper.threadedregions.scheduler.ScheduledTask; // Folia
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
    public transient Map<UUID, ScheduledTask> stuckMap = new HashMap<>();
    public Map<UUID, Long> timers = new HashMap<>();

    // Base commands
    private final Map<String, MCommand<?>> baseCommands = new HashMap<>();

    // Instead of Integer for saveTask, store a ScheduledTask
    private transient ScheduledTask saveTask = null;

    private boolean loadSuccessful = false;
    private boolean autoSave = true;

    private static final Pattern ARGUMENT_DELIMITER = Pattern.compile("\\s+");

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

        // attempt to get first command defined in plugin.yml as reference command, if any commands are defined
        try {
            Map<String, Map<String, Object>> refCmd = this.getDescription().getCommands();
            if (refCmd != null && !refCmd.isEmpty()) {
                this.refCommand = (String) (refCmd.keySet().toArray()[0]);
            }
        } catch (ClassCastException ignored) {}

        // Register a secret player listener
        Bukkit.getPluginManager().registerEvents(new MPluginSecretPlayerListener(this), this);

        // Register recurring tasks
        if (this.saveTask == null && Conf.saveToFileEveryXMinutes > 0.0) {
            // Convert minutes to ticks, then to ms for Folia
            long saveTicks = (long) (1200.0 * Conf.saveToFileEveryXMinutes); 
            long saveMs = saveTicks * 50L; // 1 tick = 50 ms

            // Use AsyncScheduler for asynchronous repeating tasks
            this.saveTask = Bukkit.getAsyncScheduler().runAtFixedRate(
                this,
                scheduledTask -> {
                    new SaveTask(this).run();
                },
                saveMs, // initial delay in ms
                saveMs, // repeat period in ms
                java.util.concurrent.TimeUnit.MILLISECONDS
            );
        }

        loadLang();
        loadSuccessful = true;
        return true;
    }

    public void postEnable() {
        Logger.print("=== ENABLE DONE (Took " 
            + DecimalFormat.getInstance().format((System.nanoTime() - timeEnableStart) / 1_000_000.0D) 
            + "ms) ===", 
            Logger.PrefixType.DEFAULT);
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

    @Override
    public void onDisable() {
        // If we had a repeating task, cancel it
        if (saveTask != null && !saveTask.isCancelled()) {
            saveTask.cancel();
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

    public void preAutoSave() {
    }

    public void postAutoSave() {
    }

    public void suicide() {
        Logger.print("Plugin Suicide Initiating!", Logger.PrefixType.DEFAULT);
        this.getServer().getPluginManager().disablePlugin(this);
    }

    public abstract Gson getGson();

    public boolean logPlayerCommands() {
        return true;
    }

    // Command handling
    public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly) {
        return handleCommand(sender, commandString, testOnly, false);
    }

    public boolean handleCommand(final CommandSender sender, String commandString, boolean testOnly, boolean async) {
        commandString = commandString.startsWith("/") ? commandString.substring(1) : commandString;
        commandString = commandString.trim().replaceAll("\\s+", " ");

        String[] arguments = commandString.split("\\s+");
        MCommand<?> command = this.baseCommands.get(arguments[0]);
        if (command == null) {
            return false;
        }
        if (testOnly) {
            return true;
        }

        List<String> args = Arrays.asList(arguments).subList(1, arguments.length);
        if (async) {
            Bukkit.getAsyncScheduler().runDelayed(this, scheduledTask -> command.execute(sender, args), 0L, java.util.concurrent.TimeUnit.MILLISECONDS);
        } else {
            command.execute(sender, args);
        }
        return true;
    }

    public boolean handleCommand(CommandSender sender, String commandString) {
        return this.handleCommand(sender, commandString, false);
    }

    public Map<UUID, ScheduledTask> getStuckMap() {
        return this.stuckMap;
    }

    public Map<UUID, Long> getTimers() {
        return this.timers;
    }

    public List<MCommand<?>> getBaseCommandsList() {
        return new ArrayList<>(this.baseCommands.values());
    }
}
