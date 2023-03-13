package com.massivecraft.factions.zcore.frame.fupgrades.provider.stackers;

import com.bgsoftware.wildstacker.api.WildStacker;
import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.massivecraft.factions.zcore.frame.fupgrades.provider.PluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.block.CreatureSpawner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class WildStackerProvider implements PluginProvider<WildStacker> {

    private static final String NAME = "WildStacker";

    @Nonnull
    @Override
    public String pluginName() {
        return NAME;
    }

    @Override
    public boolean enabled() {
        return Bukkit.getPluginManager().isPluginEnabled(NAME);
    }

    @Override
    public void enabledOrElse(@Nonnull Consumer<WildStacker> plugin, @Nonnull Runnable orElse) {
        if (enabled()) {
            plugin.accept(get());
        } else {
            orElse.run();
        }
    }

    @Nullable
    @Override
    public WildStacker provide() {
        WildStacker stacker = WildStackerAPI.getWildStacker();
        if (stacker == null) {
            stacker = (WildStacker) Bukkit.getPluginManager().getPlugin(NAME);
            if (stacker != null) {
                WildStackerAPI.setPluginInstance(stacker);
            }
        }
        return stacker;
    }

    public boolean setDelay(@Nonnull CreatureSpawner spawner, int delay) {
        WildStacker wildStacker = get();
        if (wildStacker == null) {
            return false;
        }
        wildStacker.getSystemManager().getStackedSpawner(spawner).getSpawner().setDelay(delay);
        return true;
    }
}
