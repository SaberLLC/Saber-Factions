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
    private CustomFile permissions = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + "/permissions.yml"));
    private CustomFile discord = new CustomFile(new File(FactionsPlugin.getInstance().getDataFolder() + "/discord.yml"));

    public void setupFiles() {
        shop.setup(true, "");
        permissions.setup(true, "");
        discord.setup(true, "");
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
