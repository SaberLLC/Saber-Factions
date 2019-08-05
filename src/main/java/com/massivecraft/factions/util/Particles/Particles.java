package com.massivecraft.factions.util.Particles;

import com.massivecraft.factions.P;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

public enum Particles {


    // Gotta use Strings or runtime errors on 1.8, the Particle class does not exist
    CLOUD(ParticleEffect.CLOUD, "CLOUD"),
    REDSTONE(ParticleEffect.REDSTONE, "REDSTONE"),
    NOTE(ParticleEffect.NOTE, "NOTE");


    private ParticleEffect sub18;
    private String over19;

    Particles(ParticleEffect sub18, String over19) {
        this.sub18 = sub18;
        this.over19 = over19;
    }


    public void displayAtLocation(Location location, int amt) {
        if (P.p.useNonPacketParticles) {
            // 1.9+ based servers will use the built in particleAPI instead of packet based.
            // any particle amount higher than 0 made them go everywhere, and the offset at 0 was not working.
            // So setting the amount to 0 spawns 1 in the precise location
            location.getWorld().spawnParticle(Particle.valueOf(over19), location, 0);
        } else {
            sub18.display((float) 0, (float) 0, (float) 0, (float) 0, amt, location, 16);
        }
    }

    public void displayAtLocation(Location location, int amt, ParticleEffect.OrdinaryColor color) {
        if (P.p.useNonPacketParticles) {
            // 1.9-1.11 & 1.13+ based servers will use the built in particleAPI instead of packet based.
            // any particle amount higher than 0 made them go everywhere, and the offset at 0 was not working.
            // So setting the amount to 0 spawns 1 in the precise location


            // Gotta do this so colorable ones have their data :P
            if (this == Particles.REDSTONE || this == Particles.CLOUD || this == Particles.NOTE) {
                if (P.p.mc112) {
                    location.getWorld().spawnParticle(Particle.valueOf(over19), location, 0);
                } else {
                    location.getWorld().spawnParticle(Particle.valueOf(over19), location, 0, new Particle.DustOptions(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()), 1));
                }
            } else {
                location.getWorld().spawnParticle(Particle.valueOf(over19), location, 0);
            }
        } else {
            sub18.display(color, location, 16);

        }
    }


}
