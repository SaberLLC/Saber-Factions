package com.massivecraft.factions.zcore.file;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

/**
 * Factions - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 4/4/2020
 */
public class CustomFile {

    private File file;
    private YamlConfiguration fileConfig;
    private HashMap<String, Object> cachedObjects = new HashMap<>();

    public CustomFile(File file) {
        this.file = file;
        loadFile();
    }

    public void setup(boolean loadFromProject, String inFolder) {
        if (!getFile().exists()) {
            if (loadFromProject) {
                if (!inFolder.equalsIgnoreCase("")) {
                    FactionsPlugin.getInstance().saveResource(inFolder + "/" + file.getName(), false);
                } else {
                    FactionsPlugin.getInstance().saveResource(file.getName(), false);
                }
            } else {
                try {
                    getFile().createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        loadFile();
        // Add default values for missing config options
        InputStream resource;
        if (!inFolder.equalsIgnoreCase("")) {
            resource = FactionsPlugin.getInstance().getResource(inFolder + "/" + file.getName());
        } else {
            resource = FactionsPlugin.getInstance().getResource(file.getName());
        }

        if(resource == null)
            return;
        YamlConfiguration defaultConf = YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
        getConfig().setDefaults(defaultConf);
        getConfig().options().copyDefaults(true);
        try {
            getConfig().save(getFile());
            loadFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFile() {
        this.fileConfig = YamlConfiguration.loadConfiguration(file);
        this.cachedObjects.clear(); // remove cached objects
    }

    public void saveFile() {
        try {
            getConfig().save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean containsKey(String key) {
        return getCachedObjects().containsKey(key) || getConfig().contains(key);
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

    public Object getObj(String key, Enum<dataTypes> data) {
        //check for cache first

        if (getCachedObjects().containsKey(key)) {
            return getCachedObjects().get(key);
        }

        if (data.equals(dataTypes.STRING)) {
            String d = getConfig().getString(key);
            this.cachedObjects.put(key, d);
            return d;
        }

        if (data.equals(dataTypes.DOUBLE)) {
            double d = getConfig().getDouble(key);
            this.cachedObjects.put(key, d);
            return d;
        }

        if (data.equals(dataTypes.INT)) {
            int d = getConfig().getInt(key);
            this.cachedObjects.put(key, d);
            return d;
        }

        if (data.equals(dataTypes.BOOLEAN)) {
            boolean d = getConfig().getBoolean(key);
            this.cachedObjects.put(key, d);
            return d;
        }

        if (data.equals(dataTypes.STRINGLIST)) {
            List<String> d = getConfig().getStringList(key);
            this.cachedObjects.put(key, d);
            return d;
        }
        return null;
    }

    public HashMap<String, Object> getCachedObjects() {
        return cachedObjects;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setFileConfig(YamlConfiguration fileConfig) {
        this.fileConfig = fileConfig;
    }

    public YamlConfiguration getConfig() {
        return fileConfig;
    }

    public enum dataTypes {
        STRING, INT, DOUBLE, STRINGLIST, BOOLEAN, MAP
    }
}
