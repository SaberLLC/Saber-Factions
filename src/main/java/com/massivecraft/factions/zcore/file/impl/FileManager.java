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
    private CustomFile shop = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + File.separator + "configuration" + File.separator + "shop.yml"));
    private CustomFile permissions = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + File.separator + "data" + File.separator + "permissions.yml"));
    private CustomFile discord = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + File.separator + "configuration" + File.separator + "discord.yml"));
    private CustomFile corex = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + File.separator + "corex" + File.separator + "corex.yml"));

    public void setupFiles() {
        boosters.setup(true, "data");
        timers.setup(true, "data");
        shop.setup(true, "configuration");
        permissions.setup(true, "data");
        discord.setup(true, "configuration");
        corex.setup(true, "corex");
        fperms.setup(true, "configuration");
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

    public CustomFile getShop() {
        return shop;
    }

    public CustomFile getDiscord() {
        return discord;
    }
}
