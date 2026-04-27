package com.massivecraft.factions.compat;

public interface CompatibilityModule {

    String getName();

    boolean supports(short minecraftVersion);

    default int getPriority() {
        return 0;
    }

    default boolean supportsBrigadier() {
        return false;
    }

    void onEnable(CompatibilityContext context);
}
