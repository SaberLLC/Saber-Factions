package com.massivecraft.factions.addon;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.util.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents an addon for the Factions plugin.
 *
 * @author Your Name
 */
public abstract class FactionsAddon {

    private final String addonName;
    private final FactionsPlugin plugin;

    private File configFile;
    private FileConfiguration config;

    private boolean configLoaded = false;

    /**
     * Creates a new FactionsAddon instance.
     *
     * @param plugin The FactionsPlugin instance.
     */
    public FactionsAddon(final FactionsPlugin plugin) {
        this.plugin = plugin;
        this.addonName = getFriendlyName();
    }

    /**
     * Initializes the addon.
     */
    public void initializeAddon() {
        loadConfig();
        onEnable();
        registerListeners();
        registerFCommands();
        Logger.print("Addon: " + getAddonName() + " loaded successfully!", Logger.PrefixType.DEFAULT);
    }

    /**
     * Terminates the addon.
     */
    public void terminateAddon() {
        unregisterListeners();
        onDisable();
        // Save config only if it was loaded before
        if (configLoaded) {
            saveConfig();
        }
    }

    /**
     * This method is called when the addon is enabled.
     */
    protected abstract void onEnable();

    /**
     * This method is called when the addon is disabled.
     */
    protected abstract void onDisable();

    /**
     * Returns the friendly name of the addon.
     *
     * @return The friendly name of the addon.
     */
    protected abstract String getFriendlyName();

    /**
     * Returns a set of listeners that should be registered for this addon.
     *
     * @return A set of listeners.
     */
    protected Set<Listener> listenersToRegister() {
        return new HashSet<>();
    }

    /**
     * Returns a set of FCommands that should be registered for this addon.
     *
     * @return A set of FCommands.
     */
    protected Set<FCommand> fCommandsToRegister() {
        return new HashSet<>();
    }

    /**
     * Returns the name of the addon.
     *
     * @return The name of the addon.
     */
    public String getAddonName() {
        return addonName;
    }

    /**
     * Returns the FactionsPlugin instance.
     *
     * @return The FactionsPlugin instance.
     */
    public FactionsPlugin getPlugin() {
        return plugin;
    }

    /**
     * Registers the listeners for this addon.
     */
    private void registerListeners() {
        for (Listener listener : listenersToRegister()) {
            if (listener!= null) {
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
            }
        }
    }

    /**
     * Unregisters the listeners for this addon.
     */
    private void unregisterListeners() {
        for (Listener listener : listenersToRegister()) {
            HandlerList.unregisterAll(listener);
        }
    }

    /**
     * Registers the FCommands for this addon.
     */
    private void registerFCommands() {
        for (FCommand fCommand : fCommandsToRegister()) {
            if (fCommand!= null) {
                plugin.cmdBase.addSubCommand(fCommand);
            }
        }
    }

    /**
     * Loads the configuration for this addon.
     */
    public void loadConfig() {
        if (!configLoaded) {
            Path path = Paths.get(plugin.getDataFolder().toString(), "configuration/addons", getAddonName().toLowerCase() + ".yml");
            configFile = path.toFile();

            if (!Files.exists(path)) {
                try {
                    exportConfig("/" + getAddonName().toLowerCase() + ".yml");
                } catch (Exception e) {
                    Logger.print("Error transferring config for " + getAddonName() + ": " + e.getMessage(), Logger.PrefixType.FAILED);
                    e.printStackTrace();
                }
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            configLoaded = true;
        }
    }

    /**
     * Exports the default configuration for this addon.
     *
     * @param resourceName The name of the resource to export.
     * @throws Exception If an error occurs while exporting the configuration.
     */
    private void exportConfig(String resourceName) throws Exception {
        try (InputStream stream = this.getClass().getResourceAsStream(resourceName)) {
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            // Prepare the output file path
            Path outputPath = Paths.get(plugin.getDataFolder().toString(), "configuration/addons", resourceName.toLowerCase());

            // Create directories if they don't exist
            Files.createDirectories(outputPath.getParent());

            try (OutputStream resStreamOut = Files.newOutputStream(outputPath)) {
                byte[] buffer = new byte[4096];
                int readBytes;
                while ((readBytes = stream.read(buffer)) > 0) {
                    resStreamOut.write(buffer, 0, readBytes);
                }
            }

            Logger.print(getAddonName() + " config file successfully transferred!", Logger.PrefixType.DEFAULT);
        }
    }
    /**
     * Saves the configuration for this addon.
     */
    public void saveConfig() {
        if (config == null || configFile == null) return;
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            Logger.print("Error saving config for " + getAddonName() + ": " + e.getMessage(), Logger.PrefixType.FAILED);
            e.printStackTrace();
        }
    }

    /**
     * Returns the configuration for this addon.
     *
     * @return The configuration for this addon.
     */
    public FileConfiguration getConfig() {
        if (!configLoaded) {
            loadConfig();
        }
        return config;
    }
}