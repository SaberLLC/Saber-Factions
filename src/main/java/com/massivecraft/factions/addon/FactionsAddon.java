package com.massivecraft.factions.addon;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.util.Logger;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.Set;

/**
 * @author SavageLabs Team
 */

public abstract class FactionsAddon {

    private final String addonName;
    private final FactionsPlugin plugin;

    public FactionsAddon(final FactionsPlugin plugin) {
        this.plugin = plugin;
        this.addonName = getClass().getName();
        enableAddon();
    }

    private void enableAddon() {
        onEnable();
        registerListeners();
        registerFCommands();
        Logger.print("Addon: " + getAddonName() + " loaded successfully!", Logger.PrefixType.DEFAULT);
    }

    public void disableAddon() {
        unregisterListeners();
        onDisable();
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public Set<Listener> listenersToRegister() {
        return Collections.emptySet();
    }

    public Set<FCommand> fCommandsToRegister() {
        return Collections.emptySet();
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
}
