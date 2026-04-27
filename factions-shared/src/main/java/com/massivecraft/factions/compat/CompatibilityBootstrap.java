package com.massivecraft.factions.compat;

import com.massivecraft.factions.util.Logger;

import java.util.ServiceLoader;

public final class CompatibilityBootstrap {

    private static final CompatibilityModule FALLBACK_MODULE = new CompatibilityModule() {
        @Override
        public String getName() {
            return "fallback";
        }

        @Override
        public boolean supports(short minecraftVersion) {
            return true;
        }

        @Override
        public void onEnable(CompatibilityContext context) {
        }
    };

    private CompatibilityBootstrap() {
    }

    public static CompatibilityModule select(short minecraftVersion) {
        CompatibilityModule module = FALLBACK_MODULE;
        for (CompatibilityModule candidate : ServiceLoader.load(CompatibilityModule.class, CompatibilityBootstrap.class.getClassLoader())) {
            if (!candidate.supports(minecraftVersion)) {
                continue;
            }
            if (candidate.getPriority() > module.getPriority()) {
                module = candidate;
            }
        }

        Logger.print("Using compatibility module: " + module.getName(), Logger.PrefixType.DEFAULT);
        return module;
    }
}
