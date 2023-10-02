package com.massivecraft.factions.zcore.file;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/4/2020
 */
public class CustomFile {

    private File file;
    private YamlConfiguration fileConfig;
    private final HashMap<String, Object> cachedObjects = new HashMap<>();

    public CustomFile(File file) {
        this.file = file;
        loadFile();
    }

    public void setup(boolean loadFromProject, String inFolder) {
        if (!file.exists()) {
            FactionsPlugin pluginInstance = FactionsPlugin.getInstance();
            String resourcePath = inFolder.isEmpty() ? file.getName() : inFolder + "/" + file.getName();

            if (loadFromProject) {
                pluginInstance.saveResource(resourcePath, false);
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        loadFile();

        // Add default values for missing config options
        InputStream resource = FactionsPlugin.getInstance().getResource(inFolder.isEmpty() ? file.getName() : inFolder + "/" + file.getName());

        if (resource != null) {
            YamlConfiguration defaultConf = YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
            fileConfig.setDefaults(defaultConf);
            fileConfig.options().copyDefaults(true);
            saveFile();
        }
    }

    public void loadFile() {
        this.fileConfig = YamlConfiguration.loadConfiguration(file);
        this.cachedObjects.clear();
    }

    public void saveFile() {
        try {
            fileConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfig() {
        return fileConfig;
    }

    public boolean containsKey(String key) {
        return cachedObjects.containsKey(key) || fileConfig.contains(key);
    }

    public String fetchString(String key) {
        return (String) getObj(key, dataTypes.STRING);
    }

    public int fetchInt(String key) {
        return (int) getObj(key, dataTypes.INT);
    }

    public double fetchDouble(String key) {
        return (double) getObj(key, dataTypes.DOUBLE);
    }

    public List<String> fetchStringList(String key) {
        return (List<String>) getObj(key, dataTypes.STRINGLIST);
    }

    public boolean fetchBoolean(String key) {
        return (boolean) getObj(key, dataTypes.BOOLEAN);
    }

    public Map<String, Object> fetchMap(String key) {
        return (Map<String, Object>) getObj(key, dataTypes.MAP);
    }


    private Object getObj(String key, dataTypes data) {
        if (cachedObjects.containsKey(key)) {
            return cachedObjects.get(key);
        }

        Object result;

        switch (data) {
            case STRING:
                result = fileConfig.getString(key);
                break;
            case DOUBLE:
                result = fileConfig.getDouble(key);
                break;
            case INT:
                result = fileConfig.getInt(key);
                break;
            case BOOLEAN:
                result = fileConfig.getBoolean(key);
                break;
            case STRINGLIST:
                result = fileConfig.getStringList(key);
                break;
            default:
                return null;
        }

        cachedObjects.put(key, result);
        return result;
    }

    public enum dataTypes {
        STRING, INT, DOUBLE, STRINGLIST, BOOLEAN, MAP
    }
}