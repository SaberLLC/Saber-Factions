package com.massivecraft.factions.zcore.frame.fupgrades.provider.stackers;

import com.massivecraft.factions.zcore.frame.fupgrades.provider.PluginProvider;
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.stack.StackedSpawner;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class RoseStackerProvider implements PluginProvider<RoseStackerAPI> {

    private static final String NAME = "RoseStacker";

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
    public void enabledOrElse(@Nonnull Consumer<RoseStackerAPI> plugin, @Nonnull Runnable orElse) {
        if (enabled()) {
            plugin.accept(get());
        } else {
            orElse.run();
        }
    }

    @Nonnull
    @Override
    public RoseStackerAPI provide() {
        return RoseStackerAPI.getInstance();
    }

    public boolean setDelay(@Nonnull Block block, int delay) {
        StackedSpawner stackedSpawner = get().getStackedSpawner(block);
        if (stackedSpawner == null) {
            return false;
        }
        stackedSpawner.getSpawner().setDelay(delay);
        return true;
    }
}