package org.saberdev.corex;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;
import com.massivecraft.factions.zcore.file.CustomFile;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CoreX {
    public static CustomFile getConfig() {
        return FactionsPlugin.getInstance().getFileManager().getCoreX();
    }

    public static void init() {
        Logger.print("CoreX Integration Starting!", Logger.PrefixType.DEFAULT);

        List<Listener> initializedFeatures = new ArrayList<>();

        Reflections reflections = new Reflections("org.saberdev.corex.addons");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CoreAddon.class);

        for (Class<?> clazz : annotatedClasses) {
            CoreAddon annotation = clazz.getAnnotation(CoreAddon.class);
            if (annotation != null) {
                String featureName = annotation.configVariable();
                try {
                    Listener listener = (Listener) clazz.newInstance();
                    registerFeature(initializedFeatures, featureName, listener);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!initializedFeatures.isEmpty()) {
            Logger.print("Enabling " + initializedFeatures.size() + " CoreX Features...", Logger.PrefixType.DEFAULT);
            for (Listener eventListener : initializedFeatures) {
                FactionsPlugin.getInstance().getServer().getPluginManager().registerEvents(eventListener, FactionsPlugin.getInstance());
            }
        }
    }

    private static void registerFeature(List<Listener> initializedFeatures, String featureName, Listener listener) {
        if (handleFeatureRegistry(featureName)) {
            initializedFeatures.add(listener);
        }
    }

    public static boolean handleFeatureRegistry(String key) {
        return getConfig().fetchBoolean("Features." + key);
    }
}