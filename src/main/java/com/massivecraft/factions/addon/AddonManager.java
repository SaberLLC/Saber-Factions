package com.massivecraft.factions.addon;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.Logger;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class is responsible for managing the addons that extend the functionality of Factions.
 *
 * @author SavageLabs Team
 */
public final class AddonManager {

    /**
     * The instance of the AddonManager.
     */
    private static AddonManager addonManagerInstance;

    /**
     * The folder where the addons are stored.
     */
    private final File addonFolder;

    /**
     * The FactionsPlugin instance.
     */
    private final FactionsPlugin plugin;

    /**
     * Creates a new AddonManager instance.
     *
     * @param plugin The FactionsPlugin instance.
     */
    private AddonManager(final FactionsPlugin plugin) {
        this.plugin = plugin;
        this.addonFolder = new File(plugin.getDataFolder(), "addons");
        createFoldersIfNeeded();
    }

    /**
     * Gets the instance of the AddonManager.
     *
     * @return The instance of the AddonManager.
     */
    public static synchronized AddonManager getAddonManagerInstance() {
        if (addonManagerInstance == null) {
            addonManagerInstance = new AddonManager(FactionsPlugin.getInstance());
        }
        return addonManagerInstance;
    }

    /**
     * Creates the necessary folders if they don't exist.
     */
    private void createFoldersIfNeeded() {
        createFolderIfNotExists(addonFolder);
    }

    /**
     * Creates a folder if it doesn't exist.
     *
     * @param folder The folder to create.
     */
    private void createFolderIfNotExists(File folder) {
        if (!folder.exists() &&!folder.mkdirs()) {
            throw new RuntimeException("Failed to create folder: " + folder.getAbsolutePath());
        }
    }

    /**
     * Loads all the addons from the addons folder.
     */
    public void loadAddons() {
        for (File addon : loadAddonFiles()) {
            Class<?> addonMainClass = getAddonMainClass(addon);
            if (addonMainClass != null) {
                Constructor<?> constructor;
                FactionsAddon factionsAddon;
                try {
                    constructor = addonMainClass.getConstructor(FactionsPlugin.class);
                    factionsAddon = (FactionsAddon) constructor.newInstance(plugin);
                    plugin.getFactionsAddonHashMap().put(factionsAddon.getAddonName(), factionsAddon);
                    factionsAddon.initializeAddon();
                } catch (Exception e) {
                    Logger.print("Error instantiating addon: " + e.getMessage(), Logger.PrefixType.FAILED);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Loads all the addon files from the addons folder.
     *
     * @return The addon files.
     */
    private File[] loadAddonFiles() {
        return addonFolder.listFiles(file -> file.isFile() && file.getName().endsWith(".jar"));
    }

    /**
     * Gets the main class of an addon.
     *
     * @param addon The addon file.
     * @return The main class of the addon, or null if the addon is invalid.
     */
    private Class<?> getAddonMainClass(final File addon) {
        //Setup this so we go deep into directories
        Class<?> mainClass = null;
        try {
            URLClassLoader child = new URLClassLoader(new URL[]{addon.toURI().toURL()}, this.getClass().getClassLoader());
            JarFile jarFile = new JarFile(addon);
            Enumeration<JarEntry> allEntries = jarFile.entries();
            while (allEntries.hasMoreElements()) {
                JarEntry entry = allEntries.nextElement();
                if (!entry.getName().endsWith(".class")) continue;
                String className = entry.getName().replace(".class", "");
                className = className.replace("/", ".");
                Class<?> clazz = child.loadClass(className);
                if (FactionsAddon.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
                    mainClass = clazz;
                    break;
                }
            }
        } catch (Exception e) {
            Logger.print("Error loading addon file: " + e.getMessage(), Logger.PrefixType.FAILED);
            e.printStackTrace();
        }
        return mainClass;
    }
}