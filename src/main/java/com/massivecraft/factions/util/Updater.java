package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class Updater {
    public static double currentVersion = 1.0;
    public static void updateIfNeeded(FileConfiguration conf) {
        double version = conf.getDouble("Config-Version", 0);
        //Previous version
        if (version == 0) {
            //Instructions for this configuration to be updated to current
            FactionsPlugin.getInstance().log("Your config.yml is pre-versioning so we are going to assign it version 1.0 \n Please regenerate your config.yml if you run into any errors relating to config.");
            conf.set("Config-Version", 1.0);
            version = 1.0;
        }
        //End with save + reload
        try {
            conf.save(new File("plugins/Factions/config.yml"));
            FactionsPlugin.getInstance().reloadConfig();
        } catch (IOException e) {e.printStackTrace();}
    }
}
