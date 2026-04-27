package com.massivecraft.factions.compat.legacy;

import com.massivecraft.factions.compat.CompatibilityContext;
import com.massivecraft.factions.compat.CompatibilityModule;

public final class LegacyCompatibilityModule implements CompatibilityModule {

    @Override
    public String getName() {
        return "legacy";
    }

    @Override
    public boolean supports(short minecraftVersion) {
        return minecraftVersion <= 8;
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public void onEnable(CompatibilityContext context) {
    }
}
