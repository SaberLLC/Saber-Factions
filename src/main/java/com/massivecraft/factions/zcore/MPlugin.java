package com.massivecraft.factions.zcore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.persist.SaveTask;
import com.massivecraft.factions.zcore.util.PermUtil;
import com.massivecraft.factions.zcore.util.Persist;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;


public abstract class MPlugin extends JavaPlugin {

    // Persist related
    public final Gson gson = this.getGsonBuilder().create();
    // Some utils
    public Persist persist;
    public TextUtil txt;
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
    // Listeners
    private MPluginSecretPlayerListener mPluginSecretPlayerListener;

    // Our stored base commands
    private List<MCommand<?>> baseCommands = new ArrayList<>();
    // holds f stuck start times
    private Map<UUID, Long> timers = new HashMap<>();
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
        return this.baseCommands;
    }

    public boolean preEnable() {
        Logger.print("=== ENABLE START ===", Logger.PrefixType.DEFAULT);
        timeEnableStart = System.currentTimeMillis();

        // Ensure basefolder exists!
        this.getDataFolder().mkdirs();

        // Create Utility Instances
        this.perm = new PermUtil(this);
        this.persist = new Persist(this);

        this.txt = new TextUtil();
        initTXT();

        // attempt to get first command defined in plugin.yml as reference command, if any commands are defined in there
        // reference command will be used to prevent "unknown command" console messages
        try {
            Map<String, Map<String, Object>> refCmd = this.getDescription().getCommands();
            if (refCmd != null && !refCmd.isEmpty()) {
                this.refCommand = (String) (refCmd.keySet().toArray()[0]);
            }
        } catch (ClassCastException ex) {
        }

        // Create and register player command listener
        this.mPluginSecretPlayerListener = new MPluginSecretPlayerListener(this);
        getServer().getPluginManager().registerEvents(this.mPluginSecretPlayerListener, this);

        // Register recurring tasks
        if (this.saveTask == null && Conf.saveToFileEveryXMinutes > 0.0) {
            long saveTicks = (long) (1200.0 * Conf.saveToFileEveryXMinutes);
            this.saveTask = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveTask(this), saveTicks, saveTicks).getTaskId();
        }
        loadLang();
        loadSuccessful = true;
        return true;
    }

    public void postEnable() {
        Logger.print("=== ENABLE DONE (Took " + (System.currentTimeMillis() - timeEnableStart) + "ms) ===", Logger.PrefixType.DEFAULT);
    }

    public void loadLang() {
        File lang = new File(getDataFolder(), "lang.yml");
        OutputStream out = null;
        InputStream defLangStream = this.getResource("lang.yml");
        if (!lang.exists()) {
            try {
                getDataFolder().mkdir();
                lang.createNewFile();
                if (defLangStream != null) {
                    out = new FileOutputStream(lang);
                    int read;
                    byte[] bytes = new byte[1024];

                    while ((read = defLangStream.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(defLangStream)));
                    TL.setFile(defConfig);
                }
            } catch (IOException e) {
                e.printStackTrace(); // So they notice
                getLogger().severe("[Factions] Couldn't create language file.");
                getLogger().severe("[Factions] This is a fatal error. Now disabling");
                this.setEnabled(false); // Without it loaded, we can't send them messages
            } finally {
                if (defLangStream != null) {
                    try {
                        defLangStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
        for (TL item : TL.values()) {
            if (conf.getString(item.getPath()) == null) {
                conf.set(item.getPath(), item.getDefault());
            }
        }

        // Remove this here because I'm sick of dealing with bug reports due to bad decisions on my part.
        if (conf.getString(TL.COMMAND_SHOW_POWER.getPath(), "").contains("%5$s")) {
            conf.set(TL.COMMAND_SHOW_POWER.getPath(), TL.COMMAND_SHOW_POWER.getDefault());
            Logger.print( "Removed errant format specifier from f show power.", Logger.PrefixType.DEFAULT);
        }

        TL.setFile(conf);
        try {
            conf.save(lang);
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Factions: Failed to save lang.yml.");
            getLogger().log(Level.WARNING, "Factions: Report this stack trace to Driftay.");
            e.printStackTrace();
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

    public GsonBuilder getGsonBuilder() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
    }

    public Gson getGson() {
        return this.gson;
    }

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

    public void initTXT() {
        TextUtil.init();
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
        boolean noSlash = true;
        if (commandString.startsWith("/")) {
            noSlash = false;
            commandString = commandString.substring(1);
        }

        for (final MCommand<?> command : this.getBaseCommands()) {
            if (noSlash && !command.allowNoSlashAccess) {
                continue;
            }

            for (String alias : command.aliases) {
                // disallow double-space after alias, so specific commands can be prevented (preventing "f home" won't prevent "f  home")
                if (commandString.startsWith(alias + "  ")) {
                    return false;
                }

                if (commandString.startsWith(alias + " ") || commandString.equals(alias)) {
                    final List<String> args = new ArrayList<>(Arrays.asList(commandString.split("\\s+")));
                    args.remove(0);

                    if (testOnly) {
                        return true;
                    }

                    if (async) {
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> command.execute(sender, args));
                    } else {
                        command.execute(sender, args);
                    }

                    return true;
                }
            }
        }
        return false;
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
