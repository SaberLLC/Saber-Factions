package com.massivecraft.factions.compat;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public final class CompatibilityContext {

    private final FactionsPlugin plugin;

    public CompatibilityContext(FactionsPlugin plugin) {
        this.plugin = plugin;
    }

    public FactionsPlugin getPlugin() {
        return plugin;
    }

    public short getMinecraftVersion() {
        return plugin.version;
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
}
