package com.massivecraft.factions.addon;

import com.massivecraft.factions.FactionsPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author SavageLabs Team
 */

public final class AddonManager {

    private static AddonManager addonManagerInstance;

    private final File addonFolder;
    private final File addonConfigFolder;
    private final FactionsPlugin plugin;

    private AddonManager(final FactionsPlugin plugin) {
        this.plugin = plugin;
        this.addonFolder = new File(plugin.getDataFolder(), "addons");
        this.addonConfigFolder = new File(plugin.getDataFolder(), "configuration/addons");
        createFoldersIfNeeded();
    }

    public static synchronized AddonManager getAddonManagerInstance() {
        if (addonManagerInstance == null) {
            addonManagerInstance = new AddonManager(FactionsPlugin.getInstance());
        }
        return addonManagerInstance;
    }

    private void createFoldersIfNeeded() {
        createFolderIfNotExists(addonFolder);
        createFolderIfNotExists(addonConfigFolder);
    }

    private void createFolderIfNotExists(File folder) {
        if (!folder.exists() && !folder.mkdirs()) {
            throw new RuntimeException("Failed to create folder: " + folder.getAbsolutePath());
        }
    }

    private File[] loadAddonFiles() {
        return addonFolder.listFiles(file -> file.isFile() && file.getName().endsWith(".jar"));
    }

    public void loadAddons() {
        for (File addon : loadAddonFiles()) {
            Class<? extends FactionsAddon> addonMainClass = getAddonMainClass(addon);
            if (addonMainClass != null) {
                try {
                    Constructor<? extends FactionsAddon> constructor = addonMainClass.getConstructor(FactionsPlugin.class);
                    FactionsAddon factionsAddon = constructor.newInstance(plugin);
                    FactionsPlugin.getInstance().getFactionsAddonHashMap().put(factionsAddon.getAddonName(), factionsAddon);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    System.out.println("[Factions] Error instantiating addon: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private Class<? extends FactionsAddon> getAddonMainClass(File addon) {
        Class<? extends FactionsAddon> mainClass = null;
        try (URLClassLoader child = new URLClassLoader(new URL[]{addon.toURI().toURL()}, getClass().getClassLoader());
             JarFile jarFile = new JarFile(addon)) {

            Enumeration<JarEntry> allEntries = jarFile.entries();
            while (allEntries.hasMoreElements()) {
                JarEntry entry = allEntries.nextElement();
                if (!entry.getName().endsWith(".class")) {
                    continue;
                }
                String className = entry.getName().replace(".class", "").replace("/", ".");
                Class<?> clazz = child.loadClass(className);
                if (FactionsAddon.class.isAssignableFrom(clazz)) {
                    mainClass = clazz.asSubclass(FactionsAddon.class);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mainClass;
    }
}

