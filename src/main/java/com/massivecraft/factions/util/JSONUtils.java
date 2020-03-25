package com.massivecraft.factions.util;

/**
 * @author Saser
 */

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;

import java.io.*;
import java.lang.reflect.Type;

public class JSONUtils {
    public static Gson gson = (new GsonBuilder()).enableComplexMapKeySerialization().create();

    public JSONUtils() {
    }

    public static File getOrCreateFile(File parent, String string) throws IOException {
        if (!parent.exists()) {
            parent.mkdir();
            Bukkit.getLogger().info("Creating directory " + parent.getName());
        }

        File f = new File(parent, string);
        if (!f.exists()) {
            Bukkit.getLogger().info("Creating new file " + string + " due to it not existing!");
            f.createNewFile();
        }
        return f;
    }

    public static File getOrCreateFile(String fileName) throws IOException {
        File f = new File(fileName);
        if (!f.exists()) {
            Bukkit.getLogger().info("Creating new file " + fileName + " due to it not existing!");
            f.createNewFile();
        }
        return f;
    }

    public static Object fromJson(String fileName, Object token) throws IOException {
        File f = getOrCreateFile(fileName);
        return fromJson(f, token);
    }

    public static Object fromJson(File f, Object clazz) throws FileNotFoundException {
        return gson.fromJson(new FileReader(f), getTypeFromObject(clazz));
    }

    public static Object fromJson(File f, Object clazz, Object defaultObj) throws FileNotFoundException {
        Object retr = gson.fromJson(new FileReader(f), getTypeFromObject(clazz));
        return retr == null ? defaultObj : retr;
    }

    public static Object fromJson(File f, Type token) throws FileNotFoundException {
        return fromJson(f, token, gson);
    }

    public static Object fromJson(File f, Type token, Gson gson) throws FileNotFoundException {
        return gson.fromJson(new FileReader(f), token);
    }

    public static String toJSON(Object object, Object token) {
        return toJSON(object, token, gson);
    }

    public static String toJSON(Object object, Object token, Gson gson) {
        return gson.toJson(object, getTypeFromObject(token));
    }

    public static boolean saveJSONToFile(String fileName, Object toSave, Object token) throws IOException {
        return saveJSONToFile(getOrCreateFile(fileName), toSave, token);
    }

    public static boolean saveJSONToFile(File f, Object toSave, Object token, Gson gson) throws IOException {
        String str = toJSON(toSave, token, gson);
        FileWriter writer = new FileWriter(f);
        writer.write(str);
        writer.flush();
        writer.close();
        return true;
    }

    public static boolean saveJSONToFile(File f, Object toSave, Object token) throws IOException {
        return saveJSONToFile(f, toSave, token, gson);
    }

    private static Type getTypeFromObject(Object object) {
        return object instanceof Type ? (Type) object : getTypeFromClass(object.getClass());
    }

    private static Type getTypeFromClass(Class<?> clazz) {
        return TypeToken.of(clazz).getType();
    }
}
