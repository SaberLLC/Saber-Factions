package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PermissionList {
    public static void generateFile() {
        File file = new File(FactionsPlugin.getInstance().getDataFolder().toString() + "/" + "permissions.yml");
        if (!file.exists()) {
            try {
                FactionsPlugin.getInstance().log("Generating a file with all permissions...");
                file.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                for (Permission p : Permission.values()) {
                    config.set(p.node, p.name());
                }
                config.save(file);
                FactionsPlugin.getInstance().log("Generation complete you can find this file at " + FactionsPlugin.getInstance().getDataFolder().toString() + "/" + "permissions.yml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
