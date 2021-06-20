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

    private CustomFile shop = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + "/shop.yml"));
    private CustomFile permissions = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + File.separator + "data" + File.separator + "permissions.yml"));
    private CustomFile discord = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + "/discord.yml"));
    private CustomFile corex = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + File.separator + "corex" + File.separator + "corex.yml"));

    public void setupFiles() {
        shop.setup(true, "");
        permissions.setup(true, "data");
        discord.setup(true, "");
        corex.setup(true, "corex");
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
