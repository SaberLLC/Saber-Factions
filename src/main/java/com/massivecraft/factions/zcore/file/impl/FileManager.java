package com.massivecraft.factions.zcore.file.impl;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.zcore.file.CustomFile;

import java.io.File;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/4/2020
 */
public class FileManager {

    private CustomFile boosters = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + File.separator + "data" + File.separator + "boosters.yml"));
    private CustomFile timers = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + File.separator + "data" + File.separator + "timers.yml"));
    private CustomFile fperms = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + File.separator + "configuration" + File.separator + "fperms.yml"));
    private CustomFile upgrades = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + File.separator + "configuration" + File.separator + "upgrades.yml"));
    private CustomFile permissions = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + File.separator + "data" + File.separator + "permissions.yml"));
    private CustomFile corex = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + File.separator + "corex" + File.separator + "corex.yml"));
    private CustomFile missions = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + File.separator + "configuration" + File.separator + "missions.yml"));
    private CustomFile banners = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + File.separator + "configuration" + File.separator + "banners.yml"));

    public void setupFiles() {
        boosters.setup(true, "data");
        timers.setup(true, "data");
        permissions.setup(true, "data");
        corex.setup(true, "corex");
        fperms.setup(true, "configuration");
        upgrades.setup(true, "configuration");
        missions.setup(true, "configuration");
        banners.setup(true, "configuration");
    }


    public void loadCustomFiles() {
        boosters.loadFile();
        timers.loadFile();
        permissions.loadFile();
        corex.loadFile();
        fperms.loadFile();
        upgrades.loadFile();
        missions.loadFile();
        banners.loadFile();
    }


    public CustomFile getBanners() {
        return banners;
    }

    public CustomFile getUpgrades() {
        return upgrades;
    }

    public CustomFile getMissions() {
        return missions;
    }

    public CustomFile getFperms() {
        return fperms;
    }

    public CustomFile getTimers() {
        return timers;
    }

    public CustomFile getBoosters() {
        return boosters;
    }

    public CustomFile getCoreX() {
        return corex;
    }

    public CustomFile getPermissions() {
        return permissions;
    }

}
