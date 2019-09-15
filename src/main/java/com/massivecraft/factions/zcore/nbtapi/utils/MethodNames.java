package com.massivecraft.factions.zcore.nbtapi.utils;

public class MethodNames {

    private final static MinecraftVersion MINECRAFT_VERSION = MinecraftVersion.getVersion();

    public static String getTileDataMethodName() {
        return MINECRAFT_VERSION == MinecraftVersion.MC1_8_R3 ? "b" : "save";
    }

    public static String getTypeMethodName() {
        return MINECRAFT_VERSION == MinecraftVersion.MC1_8_R3 ? "b" : "d";
    }

    public static String getEntityNbtGetterMethodName() {
        return "b";
    }

    public static String getEntityNbtSetterMethodName() {
        return "a";
    }

    public static String getRemoveMethodName() {
        return MINECRAFT_VERSION == MinecraftVersion.MC1_8_R3 ? "a" : "remove";
    }
}
