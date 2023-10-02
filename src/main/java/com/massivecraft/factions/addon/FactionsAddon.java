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

public abstract class FactionsAddon {

    private final String addonName;
    private final FactionsPlugin plugin;

    private File configFile;
    private FileConfiguration config;

    public FactionsAddon(final FactionsPlugin plugin) {
        this.plugin = plugin;
        this.addonName = getFriendlyName();
    }

    public void initializeAddon() {
        loadConfig();
        onEnable();
        registerListeners();
        registerFCommands();
        Logger.print("Addon: " + getAddonName() + " loaded successfully!", Logger.PrefixType.DEFAULT);
    }

    public void terminateAddon() {
        unregisterListeners();
        onDisable();
    }

    protected abstract void onEnable();

    protected abstract void onDisable();

    protected abstract String getFriendlyName();

    protected Set<Listener> listenersToRegister() {
        return new HashSet<>();
    }

    protected Set<FCommand> fCommandsToRegister() {
        return new HashSet<>();
    }

    public String getAddonName() {
        return addonName;
    }

    public FactionsPlugin getPlugin() {
        return plugin;
    }

    private void registerListeners() {
        for (Listener listener : listenersToRegister()) {
            if (listener != null) {
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
            }
        }
    }

    private void unregisterListeners() {
        for (Listener listener : listenersToRegister()) {
            HandlerList.unregisterAll(listener);
        }
    }

    private void registerFCommands() {
        for (FCommand fCommand : fCommandsToRegister()) {
            if (fCommand != null) {
                plugin.cmdBase.addSubCommand(fCommand);
            }
        }
    }

    public void loadConfig() {
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
    }

    private void exportConfig(String resourceName) throws Exception {
        try (InputStream stream = this.getClass().getResourceAsStream(resourceName);
             OutputStream resStreamOut = Files.newOutputStream(Paths.get(plugin.getDataFolder().toString(), "configuration/addons", resourceName.toLowerCase()))) {

            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            byte[] buffer = new byte[4096];
            int readBytes;
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
            Logger.print(getAddonName() + " config file successfully transferred!", Logger.PrefixType.DEFAULT);
        }
    }

    public void saveConfig() {
        if (config == null || configFile == null) return;
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            Logger.print("Error saving config for " + getAddonName() + ": " + e.getMessage(), Logger.PrefixType.FAILED);
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }
}
