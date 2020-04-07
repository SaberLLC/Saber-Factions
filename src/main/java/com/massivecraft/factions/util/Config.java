package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Objects;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/7/2020
 */
public class Config extends YamlConfiguration {
    private String fileName;
    private FactionsPlugin plugin;

    public Config(FactionsPlugin plugin, String fileName) {
        this(plugin, fileName, ".yml");
    }

    public Config(FactionsPlugin plugin, String fileName, String fileExtension) {
        this.plugin = plugin;
        this.fileName = fileName + (fileName.endsWith(fileExtension) ? "" : fileExtension);
        this.createFile();
    }

    public String getFileName() {
        return this.fileName;
    }

    public FactionsPlugin getPlugin() {
        return this.plugin;
    }

    private void createFile() {
        File folder = this.plugin.getDataFolder();
        try {
            File ex = new File(folder, this.fileName);
            if (!ex.exists()) {
                if (this.plugin.getResource(this.fileName) != null) {
                    this.plugin.saveResource(this.fileName, false);
                } else {
                    this.save(ex);
                }
            } else {
                this.load(ex);
                this.save(ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        File folder = this.plugin.getDataFolder();
        try {
            this.save(new File(folder, this.fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Config)) {
            return false;
        }
        Config config = (Config) o;
        if (this.fileName != null) {
            if (this.fileName.equals(config.fileName)) {
                return Objects.equals(this.plugin, config.plugin);
            }
        } else if (config.fileName == null) {
            return Objects.equals(this.plugin, config.plugin);
        }
        return false;
    }

}
