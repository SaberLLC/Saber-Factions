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

    private File addonFolder;
    private File addonConfigFolder;
    private FactionsPlugin plugin;

    private AddonManager(final FactionsPlugin plugin) {

        this.plugin = plugin;

        addonFolder = new File(plugin.getDataFolder() + "/addons");

        if (!addonFolder.exists()) {
            addonFolder.mkdir();
        }

        addonConfigFolder = new File(plugin.getDataFolder() + "/configuration/addons");
        if (!addonConfigFolder.exists()) {
            addonConfigFolder.mkdir();
        }
    }

    public static AddonManager getAddonManagerInstance() {
        if (addonManagerInstance == null) {
            addonManagerInstance = new AddonManager(FactionsPlugin.getInstance());
        }
        return addonManagerInstance;
    }

    private File[] loadAddonFiles() {
        return addonFolder.listFiles(file -> !file.isDirectory() && file.getName().endsWith("jar"));
    }


    public void loadAddons() {
        for (File addon : loadAddonFiles()) {
            Class<?> addonMainClass = getAddonMainClass(addon);
            if (addonMainClass != null) {
                Constructor<?> constructor;
                FactionsAddon factionsAddon;
                try {
                    constructor = addonMainClass.getConstructor(FactionsPlugin.class);
                    factionsAddon = (FactionsAddon) constructor.newInstance(plugin);
                    FactionsPlugin.getInstance().getFactionsAddonHashMap().put(factionsAddon.getAddonName(), factionsAddon);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    System.out.println("[Factions] Error instantiating addon: " + e.getMessage());
                }
            }
        }
    }


    private Class<?> getAddonMainClass(final File addon) {
        //Setup this so we go deep into directories
        Class<?> mainClass = null;
        try {
            URLClassLoader child = new URLClassLoader(
                    new URL[]{addon.toURI().toURL()},
                    this.getClass().getClassLoader());
            JarFile jarFile = new JarFile(addon);
            Enumeration<JarEntry> allEntries = jarFile.entries();
            while (allEntries.hasMoreElements()) {
                JarEntry entry = allEntries.nextElement();
                if (!entry.getName().endsWith(".class")) continue;
                String className = entry.getName().replace(".class", "");
                className = className.replace("/", ".");
                Class<?> clazz = child.loadClass(className);
                if (clazz.getSuperclass().equals(FactionsAddon.class)) {
                    mainClass = clazz;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mainClass;
    }
    //private Class<?> getAddonMainClass(final File addon) {
    //    Class<?> mainClass = null;
    //    try (URLClassLoader child = new URLClassLoader(
    //            new URL[]{addon.toURI().toURL()},
    //            this.getClass().getClassLoader());
    //         JarFile jarFile = new JarFile(addon)) {
    //        Enumeration<JarEntry> allEntries = jarFile.entries();
    //        while (allEntries.hasMoreElements() && mainClass == null) {
    //            JarEntry entry = allEntries.nextElement();
    //            if (!entry.getName().endsWith(".class")) continue;
    //            String className = entry.getName().replace("/", ".").replace(".class", "");
    //            try {
    //                Class<?> clazz = child.loadClass(className);
    //                if (clazz.getSuperclass().equals(FactionsAddon.class)) {
    //                    mainClass = clazz;
    //                }
    //            } catch (ClassNotFoundException ignored) {} //continue to next entry
    //        }
    //    } catch (IOException exception) {
    //        exception.printStackTrace();
    //    }
    //    return mainClass;
    //}
}
