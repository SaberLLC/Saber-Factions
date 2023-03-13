package com.massivecraft.factions.zcore.frame.fupgrades.provider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface PluginProvider<P> extends Provider<P> {

    @Nonnull
    String pluginName();

    boolean enabled();

    void enabledOrElse(@Nonnull Consumer<P> plugin, @Nonnull Runnable orElse);

    @Nullable
    @Override
    P provide();
}
