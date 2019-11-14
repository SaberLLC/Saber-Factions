package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Updater {
    public static double currentVersion = 1.1;
    public static void updateIfNeeded(FileConfiguration conf) {
        double version = conf.getDouble("Config-Version", 0);
        //Previous version
        if (version == 0) {
            //Instructions for this configuration to be updated to current
            FactionsPlugin.getInstance().log("Your config.yml is pre-versioning so we are going to assign it version 1.0 \n Please regenerate your config.yml if you run into any errors relating to config.");
            conf.set("Config-Version", 1.0);
            version = 1.0;
        }
        if (version == 1.0) {
            FactionsPlugin.getInstance().log("Updating config from version 1.0 to 1.1");
            FactionsPlugin.getInstance().log("Adding randomization support for f missions...");
            conf.set("Randomization.Enabled", false);
            conf.set("Randomization.Start-Item.Allowed.Name", "&aStart!");
            conf.set("Randomization.Start-Item.Allowed.Material", "GREEN_STAINED_GLASS_PANE");
            List<String> lore = new ArrayList<>();
            lore.add("&aStart a new mission!");
            conf.set("Randomization.Start-Item.Allowed.Lore", lore);
            conf.set("Randomization.Start-Item.Disallowed.Name", "&4Cannot start new mission");
            conf.set("Randomization.Start-Item.Disallowed.Material", "GRAY_STAINED_GLASS_PANE");
            lore.clear();
            lore.add("&4%reason%");
            conf.set("Randomization.Start-Item.Disallowed.Lore", lore);
            conf.set("Randomization.Start-Item.Slot", 23);
            conf.set("Config-Version", 1.1);
            version = 1.1;

        }
        //End with save + reload
        try {
            conf.save(new File("plugins/Factions/config.yml"));
            FactionsPlugin.getInstance().reloadConfig();
        } catch (IOException e) {e.printStackTrace();}
    }
}
