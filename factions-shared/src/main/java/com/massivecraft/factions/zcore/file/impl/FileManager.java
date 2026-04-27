package com.massivecraft.factions.zcore.file.impl;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.file.CustomFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    private final File dataFolder = FactionsPlugin.getInstance().getDataFolder();
    private Map<String, CustomFile> customFiles;

    public FileManager() {
        initFiles();
    }

    private void initFiles() {
        customFiles = new HashMap<>();

        customFiles.put("roster", new CustomFile(getFile("configuration", "roster.yml")));
        customFiles.put("boosters", new CustomFile(getFile("data", "boosters.yml")));
        customFiles.put("timers", new CustomFile(getFile("data", "timers.yml")));
        customFiles.put("fperms", new CustomFile(getFile("configuration", "fperms.yml")));
        customFiles.put("upgrades", new CustomFile(getFile("configuration", "upgrades.yml")));
        customFiles.put("permissions", new CustomFile(getFile("data", "permissions.yml")));
        customFiles.put("corex", new CustomFile(getFile("corex", "corex.yml")));
        customFiles.put("missions", new CustomFile(getFile("configuration", "missions.yml")));
        customFiles.put("banners", new CustomFile(getFile("configuration", "banners.yml")));
    }

    private File getFile(String folder, String fileName) {
        return new File(dataFolder + File.separator + folder + File.separator + fileName);
    }

    public void setupFiles() {
        customFiles.get("timers").setup(true, "data");
        customFiles.get("permissions").setup(true, "data");
        customFiles.get("corex").setup(true, "corex");
        customFiles.get("roster").setup(true, "configuration");
        customFiles.get("fperms").setup(true, "configuration");
        customFiles.get("upgrades").setup(true, "configuration");
        customFiles.get("missions").setup(true, "configuration");
        customFiles.get("banners").setup(true, "configuration");
    }

    public void loadCustomFiles() {
        customFiles.values().forEach(CustomFile::loadFile);
    }

    public CustomFile getFileByKey(String key) {
        return customFiles.getOrDefault(key, null);
    }


    public CustomFile getTimers() {
        return getFileByKey("timers");
    }

    public CustomFile getFperms() {
        return getFileByKey("fperms");
    }

    public CustomFile getUpgrades() {
        return getFileByKey("upgrades");
    }

    public CustomFile getPermissions() {
        return getFileByKey("permissions");
    }

    public CustomFile getCoreX() {
        return getFileByKey("corex");
    }

    public CustomFile getMissions() {
        return getFileByKey("missions");
    }

    public CustomFile getBanners() {
        return getFileByKey("banners");
    }
    public CustomFile getRoster() {
        return getFileByKey("roster");
    }

}