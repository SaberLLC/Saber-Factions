package com.massivecraft.factions.util;

public class VersionProtocol {

    public static void printVerionInfo() {
        short version = Short.parseShort(ReflectionUtils.PackageType.getServerVersion().split("_")[1]);
        switch (version) {
            case 7:
                Logger.print("Minecraft Version 1.7 found, disabling banners, itemflags inside GUIs, corners, and Titles.", Logger.PrefixType.DEFAULT);
                break;
            case 8:
                Logger.print("Minecraft Version 1.8 found, Title Fadeouttime etc will not be configurable.", Logger.PrefixType.DEFAULT);
                break;
            case 13:
                Logger.print("Minecraft Version 1.13 found, New Items will be used.", Logger.PrefixType.DEFAULT);
                break;
            case 14:
                Logger.print("Minecraft Version 1.14 found.", Logger.PrefixType.DEFAULT);
                break;
            case 15:
                Logger.print("Minecraft Version 1.15 found.", Logger.PrefixType.DEFAULT);
                break;
            case 16:
                Logger.print("Minecraft Version 1.16 found.", Logger.PrefixType.DEFAULT);
                break;
            case 17:
                Logger.print("Minecraft Version 1.17 found.", Logger.PrefixType.DEFAULT);
                break;
            case 18:
                Logger.print("Minecraft Version 1.18 found.", Logger.PrefixType.DEFAULT);
                break;
        }
    }
}
