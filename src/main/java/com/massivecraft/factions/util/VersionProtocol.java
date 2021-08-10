package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.particle.BukkitParticleProvider;
import com.massivecraft.factions.util.particle.PacketParticleProvider;
import com.massivecraft.factions.util.particle.darkblade12.ReflectionUtils;
import org.bukkit.Bukkit;

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
        }
    }

    public static void doBigThingsWithParticlesOMEGALUL() {
        if (FactionsPlugin.instance.version <= 13) {
            FactionsPlugin.instance.particleProvider = new PacketParticleProvider();
        } else {
            FactionsPlugin.instance.particleProvider = new BukkitParticleProvider();
        }

        if (FactionsPlugin.instance.version > 8) {
            FactionsPlugin.instance.useNonPacketParticles = true;
            Logger.print("Minecraft com.massivecraft.factions.Version 1.9 or higher found, using non packet based particle API", Logger.PrefixType.DEFAULT);
        }

        Logger.print(FactionsPlugin.instance.txt.parse("Using %1s as a particle provider", FactionsPlugin.instance.particleProvider.name()), Logger.PrefixType.DEFAULT);
    }
}
