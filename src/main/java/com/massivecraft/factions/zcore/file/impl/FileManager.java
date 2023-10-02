package com.massivecraft.factions.zcore.file.impl;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.file.CustomFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    private final Map<String, CustomFile> customFiles = new HashMap<>();

    public FileManager() {
        addCustomFile("boosters", "data");
        addCustomFile("timers", "data");
        addCustomFile("permissions", "data");
        addCustomFile("corex", "corex");
        addCustomFile("fperms", "configuration");
        addCustomFile("upgrades", "configuration");
        addCustomFile("missions", "configuration");
        addCustomFile("banners", "configuration");
    }

    private void addCustomFile(String filename, String folder) {
        String filePath = FactionsPlugin.getInstance().getDataFolder() + File.separator + folder + File.separator + filename + ".yml";
        customFiles.put(filename, new CustomFile(new File(filePath)));
    }

    public void setupFiles() {
        customFiles.forEach((name, customFile) -> {
            String folder = name.equals("corex") ? "corex" : (name.endsWith("s") ? "data" : "configuration");
            customFile.setup(true, folder);
        });
    }

    public void loadCustomFiles() {
        customFiles.values().forEach(CustomFile::loadFile);
    }

    public CustomFile getCustomFile(String name) {
        return customFiles.get(name);
    }

    // Individual getters, for direct access
    public CustomFile getBanners() {
        return getCustomFile("banners");
    }

    public CustomFile getUpgrades() {
        return getCustomFile("upgrades");
    }

    public CustomFile getMissions() {
        return getCustomFile("missions");
    }

    public CustomFile getFperms() {
        return getCustomFile("fperms");
    }

    public CustomFile getTimers() {
        return getCustomFile("timers");
    }

    public CustomFile getBoosters() {
        return getCustomFile("boosters");
    }

    public CustomFile getCoreX() {
        return getCustomFile("corex");
    }

    public CustomFile getPermissions() {
        return getCustomFile("permissions");
    }

}