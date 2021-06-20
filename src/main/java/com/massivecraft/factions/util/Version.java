package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.particle.BukkitParticleProvider;
import com.massivecraft.factions.util.particle.PacketParticleProvider;
import com.massivecraft.factions.util.particle.darkblade12.ReflectionUtils;
import org.bukkit.Bukkit;

public class Version {

    public static void versionInfo() {
        short version = Short.parseShort(ReflectionUtils.PackageType.getServerVersion().split("_")[1]);
        switch (version) {
            case 7:
                FactionsPlugin.instance.log("Minecraft Version 1.7 found, disabling banners, itemflags inside GUIs, corners, and Titles.");
                break;
            case 8:
                FactionsPlugin.instance.log("Minecraft Version 1.8 found, Title Fadeouttime etc will not be configurable.");
                break;
            case 13:
                FactionsPlugin.instance.log("Minecraft Version 1.13 found, New Items will be used.");
                break;
            case 14:
                FactionsPlugin.instance.log("Minecraft Version 1.14 found.");
                break;
            case 15:
                FactionsPlugin.instance.log("Minecraft Version 1.15 found.");
                break;
            case 16:
                FactionsPlugin.instance.log("Minecraft Version 1.16 found.");
                break;
            case 17:
                FactionsPlugin.instance.log("Minecraft Version 1.17 found.");
                break;
        }
    }

    public static void initParticleProvider() {
        if (FactionsPlugin.instance.version <= 13) {
            FactionsPlugin.instance.particleProvider = new PacketParticleProvider();
        } else {
            FactionsPlugin.instance.particleProvider = new BukkitParticleProvider();
        }
        Bukkit.getLogger().info(FactionsPlugin.instance.txt.parse("Using %1s as a particle provider", FactionsPlugin.instance.particleProvider.name()));
    }

    public static void initNonPacketParticles() {
        if (FactionsPlugin.instance.version > 8) {
            FactionsPlugin.instance.useNonPacketParticles = true;
            FactionsPlugin.instance.log("Minecraft com.massivecraft.factions.Version 1.9 or higher found, using non packet based particle API");
        }
    }
}
